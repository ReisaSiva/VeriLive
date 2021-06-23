package com.reisa.verilive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    EditText emailBox, passwordBox;
    Button loginBTn, signupBtn;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailBox = findViewById(R.id.emailBox);
        passwordBox = findViewById(R.id.passwordBox);
        loginBTn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.createBtn);
        auth = FirebaseAuth.getInstance();
        loginBTn.setOnClickListener(view -> {
            String email, password;
            email = emailBox.getText().toString();
            password = passwordBox.getText().toString();

            if (TextUtils.isEmpty(email)) {
                emailBox.setError("Email tidak boleh kosong");
                emailBox.setFocusable(true);
            }
            else if (!emailValidator(email)){
                emailBox.setError("Masukan email yang benar!");
                emailBox.setFocusable(true);
            }
            else if (TextUtils.isEmpty(password)) {
                passwordBox.setError("Password tidak boleh kosong");
                passwordBox.setFocusable(true);
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                        Intent moveDashboard = new Intent(LoginActivity.this, DashboardActivity.class);
                        startActivity(moveDashboard);

                    } else {
                        Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        signupBtn.setOnClickListener(view -> {
            Intent moveSignup = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(moveSignup);
        });
    }
        private boolean emailValidator(String email) {
            String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            Pattern pattern = Pattern.compile(emailPattern);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();

    }
}