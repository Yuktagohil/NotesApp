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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailText, passwordText;
    Button loginBtn;
    ProgressBar progressBar;
    TextView signupText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        loginBtn = findViewById(R.id.loginBtn);
        progressBar = findViewById(R.id.progressBar);
        signupText = findViewById(R.id.signupText);

        TextView textForgotPassword = findViewById(R.id.textForgotPassword);
        textForgotPassword.setOnClickListener( v -> forgotPassword());


        loginBtn.setOnClickListener(v -> logIn());
        signupText.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,SignUpActivity.class)));

    }

    private void forgotPassword() {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }

    private void logIn() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (isValidated(email,password)){
            userAuth(email,password);
        }
    }

    private void userAuth(String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {

                    @Override
                    public void onSuccess(AuthResult authResult) {
                        changeInProgress(false);
                        if (mAuth.getCurrentUser().isEmailVerified()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            showToast("Please Verify the Email");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        changeInProgress(false);
                        Log.e(TAG,"onFailure: "+e.getMessage());
                        showToast(e.getLocalizedMessage());
                    }
                });
    }

    void changeInProgress(boolean inProgress) {
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }

    private boolean isValidated(String email, String password) {

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
            passwordText.setError("Password length should be greater than 7 characters");
            passwordText.requestFocus();
            return false;
        }
        return true;
    }

    private void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }
}
