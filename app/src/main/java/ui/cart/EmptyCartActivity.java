package ui.cart;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.munche.R;

public class EmptyCartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_cart);

        init();

    }

    @SuppressLint("SetTextI18n")
    private void init() {
        ImageView mGoBackImg = findViewById(R.id.cartBackBtn);
        mGoBackImg.setOnClickListener(view -> this.onBackPressed());
    }

}