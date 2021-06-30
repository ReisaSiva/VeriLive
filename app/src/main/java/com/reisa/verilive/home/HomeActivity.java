package com.reisa.verilive.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.reisa.verilive.R;
import com.reisa.verilive.loan.ListLoan;
import com.reisa.verilive.register.SignupActivity;

public class HomeActivity extends AppCompatActivity {
    private Button lihatBTn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        lihatBTn = findViewById(R.id.lihatPinjaman);


       lihatBTn.setOnClickListener(view -> {
            Intent moveRegister = new Intent(HomeActivity.this, ListLoan.class);
            startActivity(moveRegister);
        });
    }
}