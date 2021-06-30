package com.reisa.verilive.loan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reisa.verilive.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddLoanActivity extends AppCompatActivity {
    private Button saveBtn, backBtn;
    private EditText nameField, amountField, objectiveField, loanDate;
    private Calendar mCalendar;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addloan);
        saveBtn = findViewById(R.id.saveButton);
        backBtn = findViewById(R.id.backButton);
        nameField = findViewById(R.id.nameField);
        amountField = findViewById(R.id.jumlahField);
        objectiveField = findViewById(R.id.objectiveField);
        loanDate = findViewById(R.id.loanDateField);
        mCalendar = Calendar.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        DatePickerDialog.OnDateSetListener date = (datePicker, year, monthOfYear, dayOfMonth) -> {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };


    }

    private void updateLabel() {
        String myFormat = "dd/MM/YYYY";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.UK);

        loanDate.setText(simpleDateFormat.format(mCalendar.getTime()));
    }
}