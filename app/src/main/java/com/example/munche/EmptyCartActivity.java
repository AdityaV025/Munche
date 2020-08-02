package com.example.munche;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;

public class EmptyCartActivity extends AppCompatActivity {

    private AppBarLayout mToolBar;
    private TextView mEmptyCartToolBarText;
    private ImageView mGoBackImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_cart);

        init();

    }

    @SuppressLint("SetTextI18n")
    private void init() {
        mToolBar = findViewById(R.id.emptyCartItemToolBar);
        mEmptyCartToolBarText = findViewById(R.id.confirmOrderText);
        mEmptyCartToolBarText.setText("Empty Cart");
        mGoBackImg = findViewById(R.id.cartBackBtn);
        mGoBackImg.setOnClickListener(view -> {
            this.onBackPressed();
        });
        
    }

}