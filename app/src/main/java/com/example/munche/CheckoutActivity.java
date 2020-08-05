package com.example.munche;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;

import java.util.Objects;

public class CheckoutActivity extends AppCompatActivity implements View.OnClickListener, PaymentStatusListener {

    private String mTotalAmount;
    private TextView mAmountText;
    private LinearLayout mCODView,mCardView,mUpiView;
    private String uid;
    private FirebaseFirestore db;
    private String USER_LIST = "UserList";
    private String CART_ITEMS = "CartItems";
    private String RES_LIST = "RestaurantList";
    private String[] getItemsArr;
    private String upiID,resName;
    private EasyUpiPayment mEasyUPIPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        getItemsArr = getIntent().getStringArrayExtra("ITEM_NAMES");
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        db = FirebaseFirestore.getInstance();
        mTotalAmount = getIntent().getStringExtra("TOTAL_AMOUNT");
        mAmountText = findViewById(R.id.totalAmountItems);
        mAmountText.setText("Amount to be paid \u20b9" + mTotalAmount);
        showResPaymentMethods();
        mCODView = findViewById(R.id.cashMethodContainer);
        mCardView = findViewById(R.id.creditCardMethodContainer);
        mUpiView=  findViewById(R.id.upiMethodContainer);
        mCODView.setOnClickListener(this);
        mCardView.setOnClickListener(this);
        mUpiView.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(CheckoutActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CheckoutActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.cashMethodContainer:
                deleteCartItems();
                break;

            case R.id.creditCardMethodContainer:
                deleteCartItems();
                break;

            case R.id.upiMethodContainer:
                if (resName != null){
                    upiPaymentMethod();
                }
                break;

        }

    }

    private void showResPaymentMethods() {
        DocumentReference restaurantRef = db.collection(USER_LIST).document(uid);
        restaurantRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();
                String rUID = Objects.requireNonNull(Objects.requireNonNull(documentSnapshot).get("restaurant_cart_uid")).toString();

                DocumentReference payRef = db.collection(RES_LIST).document(rUID);
                payRef.get().addOnCompleteListener(task1 -> {

                    DocumentSnapshot documentSnapshot1 = task1.getResult();
                    String codPay = Objects.requireNonNull(Objects.requireNonNull(documentSnapshot1).get("cod_payment")).toString();
                    String cardPay = Objects.requireNonNull(Objects.requireNonNull(documentSnapshot1).get("card_payment")).toString();
                    String upiPay = Objects.requireNonNull(Objects.requireNonNull(documentSnapshot1).get("upi_payment")).toString();
                    resName = Objects.requireNonNull(Objects.requireNonNull(documentSnapshot1).get("restaurant_name")).toString();

                    if (codPay.equals("YES") || cardPay.equals("YES") || !upiPay.equals("NO")){

                        mCODView.setVisibility(View.VISIBLE);
                        mCardView.setVisibility(View.VISIBLE);
                        mUpiView.setVisibility(View.VISIBLE);
                        upiID = upiPay;
                    }else {
                        mCODView.setVisibility(View.GONE);
                        mCardView.setVisibility(View.GONE);
                        mUpiView.setVisibility(View.GONE);
                        upiID = "NO";
                    }

                });

            }

        });

    }
    private void upiPaymentMethod() {
            long trID = (long) Math.floor(Math.random() * 9000000000000L) + 1000000000000L;
            long trfID = (long) Math.floor(Math.random() * 9000000000000L) + 1000000000000L;
            mEasyUPIPayment = new EasyUpiPayment.Builder()
                    .with(this)
                    .setPayeeVpa(upiID)
                    .setPayeeName(resName)
                    .setTransactionId(String.valueOf(trID))
                    .setTransactionRefId(String.valueOf(trfID))
                    .setDescription("Payment to " + resName + " for food ordering")
                    .setAmount(mTotalAmount + ".00")
                    .build();

            mEasyUPIPayment.setPaymentStatusListener(this);
            mEasyUPIPayment.startPayment();

        }

    private void deleteCartItems() {
        for (int i = 0; i < Objects.requireNonNull(getItemsArr).length ; i++){
            db.collection(USER_LIST).document(uid).collection(CART_ITEMS).document(getItemsArr[i]).delete().addOnSuccessListener(aVoid -> {
            });
            Intent intent =  new Intent(this, OrderSuccessfulActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onTransactionCompleted(TransactionDetails transactionDetails) {
        Log.d("TRDETAILS", String.valueOf(transactionDetails));
    }

    @Override
    public void onTransactionSuccess() {
        deleteCartItems();
        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTransactionSubmitted() {
        Log.d("PENDING", "PENDINGGGGGG");
    }

    @Override
    public void onTransactionFailed() {
        Toast.makeText(this, "Transaction Has Failed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTransactionCancelled() {
        Toast.makeText(this, "Transaction Cancelled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAppNotFound() {
        Toast.makeText(this, "NO UPI Apps Found On Your Device", Toast.LENGTH_SHORT).show();
    }
}