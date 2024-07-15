package com.example.notesapplication;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    EditText emailText, passwordText, confirmPassText;
    Button signupBtn;
    ProgressBar progressBar;
    TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        confirmPassText = findViewById(R.id.confirmPassText);
        signupBtn = findViewById(R.id.signupBtn);
        progressBar = findViewById(R.id.progressBar);
        loginText = findViewById(R.id.loginText);

        signupBtn.setOnClickListener(v -> signUp());
        loginText.setOnClickListener(v -> startActivity(new Intent(SignUpActivity.this, LoginActivity.class)));

    }

    private void signUp() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String confirmPassword = confirmPassText.getText().toString();

        if (isValidated(email,password,confirmPassword)){
            userAuth(email,password);
        }
    }

    private void userAuth(String email, String password) {
        changeInProgress(true);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                showToast("Verify Email");
                                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                            } else {
                                                showToast(task.getException().getMessage());
                                            }
                                        }
                                    });
                        } else {
                            try{
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e){
                                passwordText.setError("Password is too weak");
                                passwordText.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                emailText.setError("Email ID is invalid or already in use..");
                                emailText.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e){
                                emailText.setError("Email ID is already registered...");
                                emailText.requestFocus();
                            }catch (Exception e){
                                Log.e(TAG, e.getMessage());
                                showToast(e.getMessage());
                            }
                            showToast(task.getException().getMessage());
                        }
                    }
                });
    }

    void changeInProgress(boolean inProgress) {
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            signupBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            signupBtn.setVisibility(View.VISIBLE);
        }
    }

    private boolean isValidated(String email, String password, String confirmPassword) {

        if(TextUtils.isEmpty(email)){
            emailText.setError("Email ID cannot be empty");
            emailText.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Email is invalid");
            emailText.requestFocus();
            return false;
        }
        if(TextUtils.isEmpty(password)){
            passwordText.setError("Password cannot be empty");
            passwordText.requestFocus();
            return false;
        }
        if (password.length()<7) {
            passwordText.setError("Password length should be less than 7 characters");
            passwordText.requestFocus();
            return false;
        }
        if(TextUtils.isEmpty(confirmPassword)){
            confirmPassText.setError("Confirm Password cannot be empty");
            confirmPassText.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPassText.setError("Password not matched");
            confirmPassText.requestFocus();
            return false;
        }
        return true;
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
