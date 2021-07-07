package ui.review;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.munche.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import models.ReviewDetails;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewsActivity extends AppCompatActivity {

    private RecyclerView mUserReviewRecyclerView;
    LinearLayoutManager linearLayoutManager;
    private FirebaseFirestore db;
    private String uid,resUid,resName,resPrice,resNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        init();
        fetchReviews();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        fetchResDetails();
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        db = FirebaseFirestore.getInstance();
        LinearLayout mWriteNewReview = findViewById(R.id.newReviewContainer);
        mWriteNewReview.setVisibility(View.VISIBLE);
        mWriteNewReview.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewReviewActivity.class);
            intent.putExtra("UID", uid);
            intent.putExtra("RES_NAME",resName);
            intent.putExtra("RUID",resUid);
            startActivity(intent);
        });
        ImageView mGoBackBtn = findViewById(R.id.cartBackBtn);
        mGoBackBtn.setOnClickListener(view -> {
            onBackPressed();
        });
        mUserReviewRecyclerView = findViewById(R.id.userReviewRecyclerView);
        TextView mResReviewName = findViewById(R.id.resReviewText);
        mResReviewName.setText(resName);
        TextView mResReviewPrice = findViewById(R.id.resReviewPrice);
        mResReviewPrice.setText("Cost for one - \u20b9" + resPrice);
        ImageView mCallResView = findViewById(R.id.callResBtn);
        mCallResView.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + resNum));
            startActivity(intent);
        });

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mUserReviewRecyclerView.setLayoutManager(linearLayoutManager);

    }

    private void fetchResDetails() {
        resUid = getIntent().getStringExtra("RUID");
        resName = getIntent().getStringExtra("NAME");
        resPrice = getIntent().getStringExtra("PRICE");
        resNum = getIntent().getStringExtra("NUM");
    }

    private void fetchReviews() {
        Query query = db.collection("RestaurantList").document(resUid).collection("Reviews");
        FirestoreRecyclerOptions<ReviewDetails> reviewModel = new FirestoreRecyclerOptions.Builder<ReviewDetails>()
                .setQuery(query, ReviewDetails.class)
                .build();
        FirestoreRecyclerAdapter<ReviewDetails, ReviewsHolder> adapter = new FirestoreRecyclerAdapter<ReviewDetails, ReviewsHolder>(reviewModel) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(@NonNull ReviewsHolder holder, int position, @NonNull ReviewDetails model) {

                Glide.with(getApplicationContext())
                        .load(model.getUser_image())
                        .apply(new RequestOptions().override(37, 37))
                        .placeholder(R.drawable.user_placeholder)
                        .into(holder.mUserImage);

                holder.mUserName.setText(model.getUser_name());
                holder.mReview.setText(model.getReview());

                String recommend = model.getRecommended();
                if (recommend.equals("YES")) {
                    holder.mRecommendLabel.setText("Recommended");
                    holder.mRecommendLabel.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.recommended_background));
                } else if (recommend.equals("NO")) {
                    holder.mRecommendLabel.setText("Not Recommended");
                    holder.mRecommendLabel.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.not_recommended_background));
                }

            }

            @NotNull
            @Override
            public ReviewsHolder onCreateViewHolder(@NotNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.custom_review_layout, group, false);
                return new ReviewsHolder(view);
            }

            @SuppressLint("LogNotTimber")
            @Override
            public void onError(@NotNull FirebaseFirestoreException e) {
                Log.e("error", Objects.requireNonNull(e.getMessage()));
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        mUserReviewRecyclerView.setAdapter(adapter);
    }

    public static class ReviewsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.userReviewProfileImage)
        CircleImageView mUserImage;
        @BindView(R.id.userReviewName)
        TextView mUserName;
        @BindView(R.id.userReviewText)
        TextView mReview;
        @BindView(R.id.recommendTextLabel)
        TextView mRecommendLabel;

        public ReviewsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}