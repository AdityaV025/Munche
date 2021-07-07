package ui.order;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.munche.R;

public class OrderSuccessfulActivity extends AppCompatActivity {

    private LottieAnimationView mSuccessAnimation;
    private static int TIME_OUT = 3000;
    private MediaPlayer mp;
    private String resUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_successful);

        init();
        changestatusbarcolor();
    }

    private void init() {
        resUid = getIntent().getStringExtra("RES_UID");
        mp = MediaPlayer.create(this, R.raw.success_sound);
        mSuccessAnimation = findViewById(R.id.successAnim);
        mSuccessAnimation.setSpeed(0.8f);
        mSuccessAnimation.playAnimation();
        if(!mp.isPlaying()){
            mp.start();
        }else {
            mp.stop();
        }
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
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(OrderSuccessfulActivity.this, CurrentOrderActivity.class);
            intent.putExtra("RES_UID", resUid);
            startActivity(intent);
            mp.reset();
            mp.release();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
        }, TIME_OUT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSuccessAnimation.cancelAnimation();
    }
}