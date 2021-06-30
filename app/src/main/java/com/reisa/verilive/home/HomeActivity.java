package com.reisa.verilive.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.reisa.verilive.R;
import com.reisa.verilive.loan.AddLoanActivity;
import com.reisa.verilive.loan.ListLoan;

public class HomeActivity extends AppCompatActivity {
    private Button lihatBTn, buatBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        lihatBTn = findViewById(R.id.lihatPinjaman);
        buatBtn = findViewById(R.id.buatPinjaman);

        lihatBTn.setOnClickListener(view -> {
            Intent moveRegister = new Intent(HomeActivity.this, ListLoan.class);
            startActivity(moveRegister);
        });

        buatBtn.setOnClickListener(view -> {
            Intent moveRegister = new Intent(HomeActivity.this, AddLoanActivity.class);
            startActivity(moveRegister);
        });
    }
}