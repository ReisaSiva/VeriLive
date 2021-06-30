package com.reisa.verilive.register;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.reisa.verilive.R;
import com.reisa.verilive.login.LoginActivity;
import com.reisa.verilive.register.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private Button saveBtn, backBtn, buttonUploadID, buttonUploadCU;
    private EditText personalName, idNumber, phoneNumber, birthdayField, address, city, zipCode;
    private Calendar mCalendar;
    private RadioGroup genderRadioGroup;
    private RadioButton genderRadioButton;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private ImageView idPict, cuPict;
    private static int IMAGE_PICK_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        saveBtn = findViewById(R.id.saveButton);
        backBtn = findViewById(R.id.buttonBack);
        buttonUploadID = findViewById(R.id.buttonUploadID);
        personalName = findViewById(R.id.nameField);
        idNumber = findViewById(R.id.idNumField);
        phoneNumber = findViewById(R.id.phoneield);
        birthdayField = findViewById(R.id.birthField);
        genderRadioGroup = findViewById(R.id.sex_radio_group);
        address = findViewById(R.id.addressField);
        city = findViewById(R.id.cityField);
        zipCode = findViewById(R.id.zipField);
        idPict = findViewById(R.id.idPict);

        int getRadioButtonID = genderRadioGroup.getCheckedRadioButtonId();
        genderRadioButton = findViewById(getRadioButtonID);

        mCalendar = Calendar.getInstance();

        String getEmail = getIntent().getStringExtra("sendEmail");
        String getPassword = getIntent().getStringExtra("sendPassword");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        DatePickerDialog.OnDateSetListener date = (datePicker, year, monthOfYear, dayOfMonth) -> {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        buttonUploadID.setOnClickListener(view -> {
                        selectImage(idPict);
        });

        buttonUploadCU.setOnClickListener(view -> {
            selectImage(cuPict);
        });



        saveBtn.setOnClickListener(view -> {
            String getPersonalName = personalName.getText().toString();
            String getIdNumber = idNumber.getText().toString();
            String getPhoneNumber = phoneNumber.getText().toString();
            String getBirthdayField = birthdayField.getText().toString();
            String getValueGender = genderRadioButton.getText().toString();
            String getAddress = address.getText().toString();
            String getCity = city.getText().toString();
            String getZipCode = zipCode.getText().toString();

            if (TextUtils.isEmpty(getPersonalName)) {
                personalName.setError("Nama tidak boleh kosong");
                personalName.setFocusable(true);
            } else if (TextUtils.isEmpty(getIdNumber)) {

            } else if (getIdNumber.length() < 15) {
                idNumber.setError("Minimal ID number 16 karakter");
                idNumber.setFocusable(true);

            } else if (TextUtils.isEmpty(getPhoneNumber)) {
                phoneNumber.setError("Phone number tidak boleh kosong");
                phoneNumber.setFocusable(true);

            } else if (TextUtils.isEmpty(getBirthdayField)) {
                birthdayField.setError("Tanggal lahir tidak boleh kosong");
                birthdayField.setFocusable(true);

            } else if (TextUtils.isEmpty(getValueGender)) {
                genderRadioButton.setError("Gendertidak boleh kosong");
                genderRadioButton.setFocusable(true);

            } else if (TextUtils.isEmpty(getAddress)) {
                address.setError("Alamat tidak boleh kosong");
                address.setFocusable(true);

            } else if (TextUtils.isEmpty(getCity)) {
                city.setError("Kotatidak boleh kosong");
                city.setFocusable(true);

            } else if (TextUtils.isEmpty((getZipCode))) {
                zipCode.setError("Kode pos tidak boleh kosong");
                zipCode.setFocusable(true);

            } else if (getZipCode.length() < 4) {
                zipCode.setError("ZIP Code number 5 karakter");
                zipCode.setFocusable(true);

            } else {
                insertToDatabase(firebaseAuth, firebaseFirestore, getEmail, getPassword, getPersonalName, getIdNumber, getPhoneNumber, getBirthdayField, getValueGender, getAddress, getCity, getZipCode);

            }
        });

        backBtn.setOnClickListener(view -> onBackPressed());

        birthdayField.setOnClickListener(view -> new DatePickerDialog(RegisterActivity.this, date, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show());

    }

    private void selectImage(ImageView idPict ) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, IMAGE_PICK_CODE);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    private void updateLabel() {
        String myFormat = "dd/MM/YYYY";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.UK);

        birthdayField.setText(simpleDateFormat.format(mCalendar.getTime()));
    }


    private void insertToDatabase(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore, String email, String password, String personalName, String idNumber, String phoneNumber, String birthdayField, String genderUser, String address, String city, String zipCode) {
        String getCurrentUser = firebaseAuth.getCurrentUser().getUid();

        User insertUser = new User();
        insertUser.setEmail(email);
        insertUser.setPassword(password);
        insertUser.setPersonalName(personalName);
        insertUser.setIdNumber(idNumber);
        insertUser.setPhoneNumber(phoneNumber);
        insertUser.setBirthdayField(birthdayField);
        insertUser.setGenderUser(genderUser);
        insertUser.setAddress(address);
        insertUser.setCity(city);
        insertUser.setZipCode(zipCode);

        KProgressHUD progressHUD = new KProgressHUD(RegisterActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please Wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        firebaseFirestore.collection("Users").document(getCurrentUser).set(insertUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressHUD.dismiss();
                Toast.makeText(this, "Berhasil membuat akun", Toast.LENGTH_SHORT).show();
                Intent moveLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(moveLogin);
                finish();
            } else {
                progressHUD.dismiss();
                Toast.makeText(this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
//        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
//        switch(requestCode) {
//            case 0:
//                if(resultCode == RESULT_OK){
//                    Uri selectedImage = imageReturnedIntent.getData();
//                    idPict.setImageURI(selectedImage);
//                }
//
//                break;
//            case 1:
//                if(resultCode == RESULT_OK){
//                    Uri selectedImage = imageReturnedIntent.getData();
//                    idPict.setImageURI(selectedImage);
//                }
//                break;
//        }
//    }
    private void selectImage(Context context) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        idPict.setImageBitmap(selectedImage);

                    }
                    break;
                case 1:

                        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
                            Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                            idPict.setImageBitmap(selectedImage);

                        break;
                            }
                        }

                    }

            }


        }
