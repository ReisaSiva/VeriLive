package com.reisa.verilive.loan.dummy;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import com.reisa.verilive.R;

public class loan_item extends AppCompatActivity {
    private Button verifyBTn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loan_item);
        verifyBTn = findViewById(R.id.btnVerify);
        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b!=null)
        {
            String j =(String) b.get("status");
            if(j == "Belum terverifikasi") {

                verifyBTn.setVisibility(View.VISIBLE);
            } else {
                verifyBTn.setVisibility(View.GONE);

            }
        }


    }
}
