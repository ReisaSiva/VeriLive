package com.reisa.verilive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {
    FirebaseFirestore database;
    EditText name, email, password;
    Button createAccount, alreadyAccount;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        name = findViewById(R.id.NameText);
        email = findViewById(R.id.emailBox1);
        password = findViewById(R.id.passwordBox1);

        createAccount = findViewById(R.id.createBtn1);
        alreadyAccount = findViewById(R.id.loginBtn1);

        alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));

            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Semail, Spass, Sname;
                Semail = email.getText().toString();
                Spass = password.getText().toString();
                Sname = name.getText().toString();
                final User user = new User();
                user.setEmail(Semail);
                user.setPassword(Spass);
                user.setName(Sname);
                auth.createUserWithEmailAndPassword(Semail, Spass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            database.collection("Users")
                                    .document().set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    startActivity(new Intent(SignupActivity.this, RegisterActivity.class));
                                }
                            });
                            Toast.makeText(SignupActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignupActivity.this, RegisterActivity.class));
                        } else {
                            Toast.makeText(SignupActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }
}