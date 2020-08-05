package com.example.munche;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.airbnb.lottie.LottieAnimationView;

import Fragments.RestaurantFragment;

public class OrderSuccessfulActivity extends AppCompatActivity {

    private LottieAnimationView mSuccessAnimation;
    private static int TIME_OUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_successful);

        init();
        changestatusbarcolor();
    }

    private void init() {
        mSuccessAnimation = findViewById(R.id.successAnim);
        mSuccessAnimation.playAnimation();
        mSuccessAnimation.setSpeed(0.8f);
        moveToOrdersScreen();
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

    private void moveToOrdersScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(OrderSuccessfulActivity.this, OrdersActivity.class);
                startActivity(intent);
                finish();
            }
        }, TIME_OUT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSuccessAnimation.cancelAnimation();
    }
}