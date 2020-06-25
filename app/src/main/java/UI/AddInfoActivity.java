package UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.munche.MainActivity;
import com.example.munche.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Utils.ChangeStatusBarColor;
import Utils.GPSTracker;

public class AddInfoActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String phoneNum,uid,devicetoken, address,city,state,country,postalCode,knownName,subLocality,subAdminArea,finalAddress;
    private Double latitude,longitude;
    private EditText mUserName, mUserEmail, mUserAddress;
    private Button mSaveInfoBtn, mGetLocationBtn;
    private List<Address> addresses;
    private Geocoder geocoder;
    private GPSTracker gpsTracker;
//    private ChangeStatusBarColor StatusBarColor = new ChangeStatusBarColor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);

//        StatusBarColor.changestatusbarcolor();
        changestatusbarcolor();
        init();
        checkPermission();
        getLocation();

        db = FirebaseFirestore.getInstance();
        phoneNum = getIntent().getStringExtra("PHONENUMBER");
        uid = getIntent().getStringExtra("UID");
        devicetoken = getIntent().getStringExtra("TOKEN");

        mSaveInfoBtn.setOnClickListener(view -> {

            String name = mUserName.getText().toString();
            String email = mUserEmail.getText().toString();

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please Enter Valid Info", Toast.LENGTH_SHORT).show();
            } else{
                Map userData = new HashMap<>();
                userData.put("uid", uid);
                userData.put("phonenumber", phoneNum);
                userData.put("name", name);
                userData.put("email", email);
                userData.put("devicetoken", devicetoken);
                userData.put("latitude", latitude);
                userData.put("longitude",longitude);
                userData.put("address", finalAddress);
                userData.put("knownname", knownName);
                userData.put("sublocality", subLocality);
                userData.put("city", city);
                userData.put("postalcode", postalCode);

                db.collection("UserList").document(uid).set(userData).addOnSuccessListener(o -> {

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Something went Wrong!", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void checkPermission() {
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getLocation() {
        gpsTracker = new GPSTracker(AddInfoActivity.this);
        if(gpsTracker.canGetLocation()){
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

            geocoder = new Geocoder(AddInfoActivity.this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }

            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName();
            subLocality = addresses.get(0).getSubLocality();
            subAdminArea = addresses.get(0).getSubAdminArea();

            finalAddress = knownName + ", " + subLocality +  ", " + city + ", " + postalCode;

        }else{
            gpsTracker.showSettingsAlert();
        }
    }

    private void init() {
        mUserName = findViewById(R.id.userName);
        mUserEmail = findViewById(R.id.userEmail);
        mSaveInfoBtn = findViewById(R.id.addInfo);
        mUserAddress = findViewById(R.id.userAddress);
        mGetLocationBtn = findViewById(R.id.getLocationBtn);
    }

    private void changestatusbarcolor() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        gpsTracker.stopUsingGPS();
    }

}