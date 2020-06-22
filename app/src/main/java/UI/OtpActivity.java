package UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.munche.MainActivity;
import com.example.munche.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.installations.FirebaseInstallationsRegistrar;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class OtpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private String mAuthVerificationId, phoneNum;
    private OtpTextView mOtpText;
    private Button mVerifyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        init();
        mAuthVerificationId = getIntent().getStringExtra("AuthCredentials");
        phoneNum = getIntent().getStringExtra("PhoneNumber");

        mOtpText.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {

//                otp = mOtpText.getOTP();
//                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mAuthVerificationId, otp);
//                signInWithPhoneAuthCredential(credential);
            }
        });

        mVerifyBtn.setOnClickListener(view -> {
            String otp = mOtpText.getOTP();
            if (otp.isEmpty()){
                Toast.makeText(this, "Please enter correct OTP", Toast.LENGTH_LONG).show();
            }else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mAuthVerificationId, otp);
                signInWithPhoneAuthCredential(credential);
            }
        });

    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mOtpText = findViewById(R.id.otpView);
        mVerifyBtn = findViewById(R.id.verifyOTP);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = task.getResult().getUser();
                        String uid = user.getUid();
                        final FirebaseFirestore db = FirebaseFirestore.getInstance();
                        final DocumentReference docRef = db.collection("UserList").document(uid);

                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        docRef.get().addOnSuccessListener(documentSnapshot -> {

                            if (documentSnapshot.exists()) {
                                sendUserToMain();
                            }else {
                                Intent intent = new Intent(OtpActivity.this, AddInfoActivity.class);
                                intent.putExtra("PHONENUMBER", phoneNum);
                                intent.putExtra("UID", uid);
                                intent.putExtra("TOKEN", deviceToken);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }

                        });

                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mCurrentUser != null) {
           sendUserToMain();
        }
    }

    public void sendUserToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}

