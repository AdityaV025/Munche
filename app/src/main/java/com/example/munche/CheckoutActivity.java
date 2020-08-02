package com.example.munche;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CheckoutActivity extends AppCompatActivity {

    private String mTotalAmount;
    private TextView mAmountText;
    private LinearLayout mCODView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {

        mTotalAmount = getIntent().getStringExtra("TOTAL_AMOUNT");
        mAmountText = findViewById(R.id.totalAmountItems);
        mAmountText.setText("Amount to be paid \u20b9" + mTotalAmount);
        mCODView = findViewById(R.id.cashMethodContainer);
        mCODView.setOnClickListener(view -> {

            Intent intent =  new Intent(this, OrderSuccessfulActivity.class);
            startActivity(intent);

        });

    }

}