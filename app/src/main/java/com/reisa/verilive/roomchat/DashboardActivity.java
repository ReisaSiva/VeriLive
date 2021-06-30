package com.reisa.verilive.roomchat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reisa.verilive.R;
import com.reisa.verilive.login.LoginActivity;
import com.reisa.verilive.roomchat.model.History;

import org.jetbrains.annotations.NotNull;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class DashboardActivity extends AppCompatActivity {

    private EditText secretCodeBox;
    private Button JoinBtn;
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String getLongitude, getLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        secretCodeBox = findViewById(R.id.secretCodeBox);
        JoinBtn = findViewById(R.id.JoinBtn);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission()) {
                requestPermission();
            } else {
                getCurrentLocation();
            }
        }

        try {
            URL serverURL = new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultOptions =
                    new JitsiMeetConferenceOptions.Builder()
                            .setServerURL(serverURL)
                            .setWelcomePageEnabled(false)
                            .build();

            JitsiMeet.setDefaultConferenceOptions(defaultOptions);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JoinBtn.setOnClickListener(v -> {
            String getSecretCode = secretCodeBox.getText().toString();

            if (TextUtils.isEmpty(getSecretCode)) {
                secretCodeBox.setError("Secret Code tidak boleh kosong");
                secretCodeBox.setFocusable(true);
            } else {
                afterJoin(firebaseAuth, firebaseFirestore, getSecretCode, getLatitude, getLongitude);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.logout) {

              logout();

                Intent moveToLogin = new Intent(DashboardActivity.this, LoginActivity.class);
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

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    public void onRequestPermissionsResult(int requestCode, String permission[], int grantResults[]) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (permissionAccepted) {
                    Toast.makeText(DashboardActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    getCurrentLocation();
                } else {
                    Toast.makeText(DashboardActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            displayAlertMessage("You need to allow access for both permissions", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(DashboardActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void afterJoin(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore, String secretCode, String latitude, String longitude) {
        // Push ke database then join jitsi
        String getCurrentUser = firebaseAuth.getCurrentUser().getUid();
        String getRandomUid = firebaseFirestore.collection("History Login").document().getId();

        History insertHistory = new History();
        insertHistory.setLatitude(latitude);
        insertHistory.setLongitude(longitude);
        insertHistory.setCode(secretCode);
//        InsertHistory Date
        firebaseFirestore.collection("Users").document(getCurrentUser).collection("History Login").document(getRandomUid).set(insertHistory).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                        .setRoom(secretCode)
                        .setWelcomePageEnabled(false)
                        .build();

                JitsiMeetActivity.launch(DashboardActivity.this, options);
            } else {

                Toast.makeText(this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(DashboardActivity.this);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                Location location = task.getResult();
                if (location != null) {
                    getLongitude = location.getLongitude() + "";
                    getLatitude = location.getLatitude() + "";
                } else {
                    LocationRequest locationRequest = new LocationRequest()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(10000)
                            .setFastestInterval(1000)
                            .setNumUpdates(1);

                    LocationCallback locationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);

                            Location locations = locationResult.getLastLocation();
                            getLongitude = locations.getLongitude() + "";
                            getLatitude = locations.getLatitude() + "";
                        }
                    };

                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                }
            });
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}