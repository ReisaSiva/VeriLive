package com.reisa.verilive.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.reisa.verilive.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    private EditText email, password;
    private Button createAccount, alreadyAccount;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        email = findViewById(R.id.emailBox1);
        password = findViewById(R.id.passwordBox1);
        createAccount = findViewById(R.id.createBtn1);
        alreadyAccount = findViewById(R.id.loginBtn1);

        auth = FirebaseAuth.getInstance();

        alreadyAccount.setOnClickListener(view -> {
            onBackPressed();
        });

        createAccount.setOnClickListener(view -> {
            createAccount();
        });
    }

    private void createAccount() {
        String getEmail = email.getText().toString();
        String getPassword = password.getText().toString();

        if (TextUtils.isEmpty(getEmail)) {
            email.setError("Email tidak boleh kosong");
            email.setFocusable(true);
        } else if (!emailValidator(getEmail)) {
            email.setError("Masukkan email dengan benar");
            email.setFocusable(true);
        } else if (TextUtils.isEmpty(getPassword)) {
            password.setError("Password tidak boleh kosong");
            password.setFocusable(true);
        } else {
            auth.createUserWithEmailAndPassword(getEmail, getPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent moveNextRegister = new Intent(SignupActivity.this, RegisterActivity.class);
                    moveNextRegister.putExtra("sendEmail", getEmail);
                    moveNextRegister.putExtra("sendPassword", getPassword);
                    startActivity(moveNextRegister);
                } else {
                    Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean emailValidator(String email) {
        String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}