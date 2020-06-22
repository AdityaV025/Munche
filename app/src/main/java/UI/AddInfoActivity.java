package UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.munche.MainActivity;
import com.example.munche.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddInfoActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String phoneNum,uid,devicetoken;
    private EditText mUserName, mUserEmail;
    private Button mSaveInfoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);

        init();

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

                db.collection("UserList").document(uid).set(userData).addOnSuccessListener(o -> {

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("UID", uid);
                    startActivity(intent);
                    finish();

                }).addOnFailureListener(e -> {

                    Toast.makeText(this, "Something went Wrong!", Toast.LENGTH_SHORT).show();

                });

            }

        });

    }

    private void init() {
        mUserName = findViewById(R.id.userName);
        mUserEmail = findViewById(R.id.userEmail);
        mSaveInfoBtn = findViewById(R.id.addInfo);
    }

}