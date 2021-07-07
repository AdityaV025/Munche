package ui.review;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.munche.MainActivity;
import com.example.munche.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hsalf.smileyrating.SmileyRating;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NewReviewActivity extends AppCompatActivity {

    private String uid;
    private String userName;
    private String userImage;
    private String recommendText;
    private String resUid;
    private SmileyRating ratingBar;
    private EditText mReviewEditText;
    private RadioButton mRecommendBtn,mNotRecommendBtn;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_review);

        changestatusbarcolor();
        init();
        fetchUserDetails();

    }

    @SuppressLint("SetTextI18n")
    private void init() {
        uid = getIntent().getStringExtra("UID");
        resUid = getIntent().getStringExtra("RUID");
        String resName = getIntent().getStringExtra("RES_NAME");
        db = FirebaseFirestore.getInstance();
        ratingBar = findViewById(R.id.smiley_rating);
        mReviewEditText = findViewById(R.id.reviewEditText);
        ImageView mGoBackBtn = findViewById(R.id.cartBackBtn);
        mGoBackBtn.setOnClickListener(view -> {
            onBackPressed();
        });
        TextView mRatingResName = findViewById(R.id.recommendLabel);
        mRatingResName.setText("Would you recommend " + resName + " ?");
        mRecommendBtn = findViewById(R.id.recommend);
        mNotRecommendBtn = findViewById(R.id.notrecommend);
        Button mSaveReviewBtn = findViewById(R.id.saveReviewBtn);
        mSaveReviewBtn.setOnClickListener(view -> {
            uploadReviewDetails();
        });
    }

    private void fetchUserDetails() {
        db.collection("UserList").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot docRef = task.getResult();
                userName = (String) Objects.requireNonNull(docRef).get("name");
                userImage = (String) docRef.get("user_profile_image");
            }
        });
    }

    private void uploadReviewDetails() {
        if (TextUtils.isEmpty(mReviewEditText.getText()) || ratingBar.getSelectedSmiley().getRating() == -1 || (!mRecommendBtn.isChecked() && !mNotRecommendBtn.isChecked())){
            Toast.makeText(this, "Please fill the review properly", Toast.LENGTH_SHORT).show();
        }else {
            if(mRecommendBtn.isChecked()){
                recommendText = "YES";
            }else if(mNotRecommendBtn.isChecked()){
                recommendText = "NO";
            }
            int rating = ratingBar.getSelectedSmiley().getRating();
            String review = mReviewEditText.getText().toString();

            HashMap<String, String> uploadReviewMap = new HashMap<>();
            uploadReviewMap.put("user_name",userName);
            uploadReviewMap.put("user_image", userImage);
            uploadReviewMap.put("uid", uid);
            uploadReviewMap.put("rating", String.valueOf(rating));
            uploadReviewMap.put("recommended", recommendText);
            uploadReviewMap.put("review", review);

            db.collection("RestaurantList")
                    .document(resUid)
                    .collection("Reviews")
                    .document()
                    .set(uploadReviewMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(getApplicationContext(), "Review Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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

}