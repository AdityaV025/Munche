package com.example.munche;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class CheckoutActivity extends AppCompatActivity {

    private String mTotalAmount;
    private TextView mAmountText;
    private LinearLayout mCODView;
    private String uid;
    private FirebaseFirestore db;
    private String USER_LIST = "UserList";
    private String CART_ITEMS = "CartItems";
    private String[] CartItem = {"Honey Hut Crunch","Fruit Overload","Belgian Bliss","Mint Milk Chocolate"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        db = FirebaseFirestore.getInstance();
        mTotalAmount = getIntent().getStringExtra("TOTAL_AMOUNT");
        mAmountText = findViewById(R.id.totalAmountItems);
        mAmountText.setText("Amount to be paid \u20b9" + mTotalAmount);
        mCODView = findViewById(R.id.cashMethodContainer);
        mCODView.setOnClickListener(view -> {
            deleteCartItems();
            Intent intent =  new Intent(this, OrderSuccessfulActivity.class);
            startActivity(intent);

        });

    }

    private void deleteCartItems() {
        for (int i = 0; i < CartItem.length ; i++){
            db.collection(USER_LIST).document(uid).collection(CART_ITEMS).document(CartItem[i]).delete().addOnSuccessListener(aVoid -> {
            });
        }
        Toast.makeText(this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
    }

}