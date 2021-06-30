package com.reisa.verilive.register;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.reisa.verilive.R;
import com.reisa.verilive.login.LoginActivity;
import com.reisa.verilive.register.model.User;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
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
    private final static int CAMERA_REQUEST_CODE = 200;
    private final static int STORAGE_REQUEST_CODE = 400;
    private final static int IMAGE_PICK_GALLERY_CODE = 1000;
    private final static int IMAGE_PICK_CAMERA_CODE = 2001;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        saveBtn = findViewById(R.id.saveButton);
        backBtn = findViewById(R.id.buttonBack);
        buttonUploadID = findViewById(R.id.buttonUploadID);
        buttonUploadCU = findViewById(R.id.buttonUploadCU);
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
            selectImage();
        });

        buttonUploadCU.setOnClickListener(view -> {
            selectImage();
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

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    pickCamera();
                }
            } else if (options[item].equals("Choose from Gallery")) {
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickFromGallery();
                }
            } else {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void requestCameraPermission() {
        String[] cameraPermission = {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(RegisterActivity.this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private void requestStoragePermission() {
        String[] storagePermission = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(RegisterActivity.this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean results = ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result & results;
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void pickFromGallery() {
        Intent moveGallery = new Intent(Intent.ACTION_PICK);
        moveGallery.setType("image/*");
        startActivityForResult(moveGallery, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "NewPick");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent moveCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        moveCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(moveCamera, IMAGE_PICK_CAMERA_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickCamera();
                    } else {
                        Toast.makeText(this, "Permisiion denied", Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case STORAGE_REQUEST_CODE:
                boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (writeStorageAccepted) {
                    pickFromGallery();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.getDefault());

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                try {
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    idPict.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.getLocalizedMessage();
                }
            } else {
                try {
                    Uri getImageUri = data.getData();
                    InputStream imageStream = getContentResolver().openInputStream(getImageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    idPict.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.getLocalizedMessage();
                }
            }
        }
    }
}
