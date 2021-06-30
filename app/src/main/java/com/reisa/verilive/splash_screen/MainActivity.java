package com.reisa.verilive.splash_screen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.reisa.verilive.login.LoginActivity;
import com.reisa.verilive.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(() -> startActivity(new Intent(MainActivity.this, LoginActivity.class)),2000);
    }
}