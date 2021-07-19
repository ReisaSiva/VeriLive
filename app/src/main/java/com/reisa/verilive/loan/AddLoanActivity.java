package com.reisa.verilive.loan;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.reisa.verilive.R;
import com.reisa.verilive.home.HomeActivity;
import com.reisa.verilive.loan.dummy.Loan;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
        nameField = findViewById(R.id.nameFieldLoan);
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

        loanDate.setOnClickListener(view -> new DatePickerDialog(AddLoanActivity.this, date, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show());
        backBtn.setOnClickListener(view -> onBackPressed());
        saveBtn.setOnClickListener(view -> {
            String getName = nameField.getText().toString();
            String getAmount = amountField.getText().toString();
            String getObjective = objectiveField.getText().toString();
            String getLoanDate = loanDate.getText().toString();
            int min = 100000;
            int max = 999999;
            int random_int = (int)Math.floor(Math.random()*(max-min+1)+min);
            String getCode = String.valueOf(random_int);
            String getUser_id = firebaseAuth.getCurrentUser().getUid();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String getCurrentDateTime = dateFormat.format(new Date()); // Find todays date


            if (TextUtils.isEmpty(getName)) {
                nameField.setError("Nama tidak boleh kosong");
                nameField.setFocusable(true);
            } else if (TextUtils.isEmpty(getAmount)) {
                amountField.setError("Jumlah tidak boleh kosong");
                amountField.setFocusable(true);
            } else if (TextUtils.isEmpty(getObjective)) {
                objectiveField.setError("Tujuan tidak boleh kosong");
                objectiveField.setFocusable(true);
            } else if (TextUtils.isEmpty(getLoanDate)) {
                objectiveField.setError("Tanggal tidak boleh kosong");
                objectiveField.setFocusable(true);
            }else  {

                afterJoin(firebaseAuth, firebaseFirestore, getName, getAmount, getObjective, getLoanDate, getCode, getUser_id, getCurrentDateTime);
            }

        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.getDefault());
        loanDate.setText(simpleDateFormat.format(mCalendar.getTime()));
    }


    private void afterJoin(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore, String name, String amount, String objective, String date, String code, String user_id, String currentDateTime) {
        String getCurrentUser = firebaseAuth.getCurrentUser().getUid();
        String getRandomUid = firebaseFirestore.collection("Loan Information").document().getId();
        Loan insertLoan = new Loan();
        insertLoan.setCurrentDateTime(currentDateTime);
        insertLoan.setUser_id(user_id);
        insertLoan.setName(name);
        insertLoan.setAmount(amount);
        insertLoan.setObjective(objective);
        insertLoan.setDate(date);
        insertLoan.setCode(code);
        insertLoan.setStatus("Belum terverifikasi");

        KProgressHUD progressHUD = new KProgressHUD(AddLoanActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please Wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        firebaseFirestore.collection("Loan Information").document(getRandomUid).set(insertLoan).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressHUD.dismiss();
                Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show();
                Intent moveLogin = new Intent(AddLoanActivity.this, HomeActivity.class);
                startActivity(moveLogin);
                finish();
            } else {
                progressHUD.dismiss();
                Toast.makeText(this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}