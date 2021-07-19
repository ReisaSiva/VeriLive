package com.reisa.verilive.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reisa.verilive.R;
import com.reisa.verilive.loan.AddLoanActivity;
import com.reisa.verilive.loan.ListLoan;
import com.reisa.verilive.login.LoginActivity;

public class HomeActivity extends AppCompatActivity {
    private Button lihatBTn, buatBtn;
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        lihatBTn = findViewById(R.id.lihatPinjaman);
        buatBtn = findViewById(R.id.buatPinjaman);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        lihatBTn.setOnClickListener(view -> {
            Intent moveRegister = new Intent(HomeActivity.this, ListLoan.class);
            startActivity(moveRegister);
        });

        buatBtn.setOnClickListener(view -> {
            Intent moveRegister = new Intent(HomeActivity.this, AddLoanActivity.class);
            startActivity(moveRegister);
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.logout) {

                logout();

                Intent moveToLogin = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(moveToLogin);
                finish();
                return true;
            }

            return true;
        });
    }

    public void logout() {
        {
            firebaseAuth.getInstance()
                    .signOut();
        }
    }
    }