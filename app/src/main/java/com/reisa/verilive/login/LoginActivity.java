package com.reisa.verilive.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.reisa.verilive.R;
import com.reisa.verilive.home.HomeActivity;
import com.reisa.verilive.register.SignupActivity;
import com.reisa.verilive.roomchat.DashboardActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private EditText emailBox, passwordBox;
    private Button loginBTn, signupBtn;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailBox = findViewById(R.id.emailBox);
        passwordBox = findViewById(R.id.passwordBox);
        loginBTn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.createBtn);
        auth = FirebaseAuth.getInstance();

        checkAuth();

        loginBTn.setOnClickListener(view -> {
            String email = emailBox.getText().toString();
            String password = passwordBox.getText().toString();

            KProgressHUD progressHUD = new KProgressHUD(LoginActivity.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please Wait")
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();

            if (TextUtils.isEmpty(email)) {
                emailBox.setError("Email tidak boleh kosong");
                emailBox.setFocusable(true);
            } else if (!emailValidator(email)) {
                emailBox.setError("Masukkan email dengan benar");
                emailBox.setFocusable(true);
            } else if (TextUtils.isEmpty(password)) {
                passwordBox.setError("Password tidak boleh kosong");
                passwordBox.setFocusable(true);
            }  else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressHUD.dismiss();
                        Toast.makeText(LoginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                        Intent moveDashboard = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(moveDashboard);
                        finish();
                    } else {
                        progressHUD.dismiss();
                        Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        signupBtn.setOnClickListener(view -> {
            Intent moveRegister = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(moveRegister);
        });
    }

    private boolean emailValidator(String email) {
        String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void checkAuth() {
        FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                Intent moveDashboard = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(moveDashboard);
                finish();
            }
        };

        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }
}