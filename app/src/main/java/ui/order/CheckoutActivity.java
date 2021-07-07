package ui.order;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.munche.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import utils.GenerateRandomNum;
import utils.JSONParser;

public class CheckoutActivity extends AppCompatActivity implements View.OnClickListener, PaymentStatusListener, PaytmPaymentTransactionCallback {

    private String mTotalAmount;
    private LinearLayout mCODView,mCardView,mUpiView;
    private FirebaseFirestore db;
    private String USER_LIST = "UserList";
    private String CART_ITEMS = "CartItems";
    private String USER_ORDERS = "UserOrders";
    private String RES_LIST = "RestaurantList";
    private String RES_ORDERS = "RestaurantOrders";
    private String[] getItemsArr, getOrderedItemsArr;
    private String upiID,resName,resUid,userAddress,mid,extraInst,userPhone,uid,userName,resSpotImage,resDelTime;
    private long customerID;
    private long orderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        ImageView mGoBackBtn = findViewById(R.id.cartBackBtn);
        resSpotImage = getIntent().getStringExtra("RES_IMAGE");
        getItemsArr = getIntent().getStringArrayExtra("ITEM_NAMES");
        getOrderedItemsArr = getIntent().getStringArrayExtra("ITEM_ORDERED_NAME");
        resName = getIntent().getStringExtra("RES_NAME");
        resUid = getIntent().getStringExtra("RES_UID");
        userAddress = getIntent().getStringExtra("USER_ADDRESS");
        resDelTime = getIntent().getStringExtra("DELIVERY_TIME");
        extraInst = getIntent().getStringExtra("EXTRA_INS");
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        userName = getIntent().getStringExtra("USER_NAME");
        userPhone = getIntent().getStringExtra("USER_PHONE");
        db = FirebaseFirestore.getInstance();
        mTotalAmount = getIntent().getStringExtra("TOTAL_AMOUNT");
        TextView mAmountText = findViewById(R.id.totalAmountItems);
        mAmountText.setText("Amount to be paid \u20b9" + mTotalAmount);
        showResPaymentMethods();
        mCODView = findViewById(R.id.cashMethodContainer);
        mCardView = findViewById(R.id.creditCardMethodContainer);
        mUpiView=  findViewById(R.id.upiMethodContainer);
        mCODView.setOnClickListener(this);
        mCardView.setOnClickListener(this);
        mUpiView.setOnClickListener(this);

        mid = "YOUR_OWN_MID";
        customerID = Long.parseLong(GenerateRandomNum.Companion.generateRandNum());
        orderID = Long.parseLong(GenerateRandomNum.Companion.generateRandNum());

        if (ContextCompat.checkSelfPermission(CheckoutActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CheckoutActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }

        mGoBackBtn.setOnClickListener(view -> {
            this.onBackPressed();
        });

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.cashMethodContainer:
                uploadOrderDetails("COD");
                deleteCartItems();
                break;

            case R.id.creditCardMethodContainer:
                paytmGateway();
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

    private void paytmGateway() {
        sendUserDetailToServer dl = new sendUserDetailToServer();
        dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void upiPaymentMethod() {
        long transactionId = Long.parseLong(GenerateRandomNum.Companion.generateRandNum());
        long transactionRefId = Long.parseLong(GenerateRandomNum.Companion.generateRandNum());
        EasyUpiPayment mEasyUPIPayment = new EasyUpiPayment.Builder()
                .with(this)
                .setPayeeVpa(upiID)
                .setPayeeName(resName)
                .setTransactionId(String.valueOf(transactionId))
                .setTransactionRefId(String.valueOf(transactionRefId))
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
            intent.putExtra("RES_UID", resUid);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class sendUserDetailToServer extends AsyncTask<ArrayList<String>, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(CheckoutActivity.this);
        //private String orderId , mid, custid, amt;
        String url ="GENERATE_CHECKSUM_URL";
        String verifyurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
        // "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID"+orderId;
        String CHECKSUMHASH ="";
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait...");
            this.dialog.show();
        }
        protected String doInBackground(ArrayList<String>... alldata) {
            JSONParser jsonParser = new JSONParser(CheckoutActivity.this);
            String param=
                    "MID="+mid+
                            "&ORDER_ID=" + orderID +
                            "&CUST_ID="+customerID+
                            "&CHANNEL_ID=WAP&TXN_AMOUNT=" + mTotalAmount +"&WEBSITE=WEBSTAGING"+
                            "&CALLBACK_URL="+ verifyurl+"&INDUSTRY_TYPE_ID=Retail";
            JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",param);

            /*
                This will receive checksum and order_id
             */
            Log.e("CheckSum result >>",jsonObject.toString());
            Log.e("CheckSum result >>",jsonObject.toString());
            try {
                CHECKSUMHASH=jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
                Log.e("CheckSum result >>",CHECKSUMHASH);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return CHECKSUMHASH;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ","  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            PaytmPGService Service = PaytmPGService.getStagingService("YOUR_PAYTM_TEST_URL");
            // when app is ready to publish use production service
            // PaytmPGService  Service = PaytmPGService.getProductionService();
            // now call paytm service here
            //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
            HashMap<String, String> paramMap = new HashMap<String, String>();
            //these are mandatory parameters
            paramMap.put("MID", mid); //MID provided by paytm
            paramMap.put("ORDER_ID", String.valueOf(orderID));
            paramMap.put("CUST_ID", String.valueOf(customerID));
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", mTotalAmount);
            paramMap.put("WEBSITE", "WEBSTAGING");
            paramMap.put("CALLBACK_URL" ,verifyurl);
            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");
            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param "+ paramMap.toString());
            Service.initialize(Order,null);
            // start payment service call here
            Service.startPaymentTransaction(CheckoutActivity.this, true, true,
                    CheckoutActivity.this);
        }
    }

    private void uploadOrderDetails(String paymentMethod) {
        @SuppressLint("SimpleDateFormat") String timeStampDate1 = new SimpleDateFormat("dd MMM yyyy").format(Calendar.getInstance().getTime());
        @SuppressLint("SimpleDateFormat") String timeStampDate2 = new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime());

        String orderID = GenerateRandomNum.Companion.generateRandNum();

        Map<String, Object> orderedItemsMap = new HashMap<>();
        orderedItemsMap.put("ordered_items", FieldValue.arrayUnion((Object[]) getOrderedItemsArr));
        orderedItemsMap.put("total_amount", "\u20b9" + mTotalAmount);
        orderedItemsMap.put("ordered_time", timeStampDate1 + " at " + timeStampDate2);
        orderedItemsMap.put("ordered_restaurant_name", resName);
        orderedItemsMap.put("ordered_restaurant_spotimage", resSpotImage);
        orderedItemsMap.put("ordered_restaurant_delivery_time", resDelTime);
        orderedItemsMap.put("ordered_id", orderID);
        db.collection(USER_LIST).document(uid).collection(USER_ORDERS).document(orderID).set(orderedItemsMap).addOnCompleteListener(task -> {
        });

        Map<String, Object> orderedRestaurantName = new HashMap<>();
        orderedRestaurantName.put("ordered_items", FieldValue.arrayUnion((Object[]) getOrderedItemsArr));
        orderedRestaurantName.put("ordered_at",timeStampDate1 + " at " + timeStampDate2);
        orderedRestaurantName.put("short_time", timeStampDate2);
        orderedRestaurantName.put("total_amount", "\u20b9" + mTotalAmount);
        orderedRestaurantName.put("payment_method", paymentMethod);
        orderedRestaurantName.put("delivery_address", userAddress);
        orderedRestaurantName.put("order_id", orderID);
        orderedRestaurantName.put("customer_name", userName);
        orderedRestaurantName.put("customer_uid", uid);
        orderedRestaurantName.put("extra_instructions", extraInst);
        orderedRestaurantName.put("customer_phonenumber", userPhone);
        orderedRestaurantName.put("delivery_time", resDelTime);
        db.collection(RES_LIST).document(resUid).collection(RES_ORDERS).document(orderID).set(orderedRestaurantName).addOnCompleteListener(task -> {
        });
    }

/**
    ==========UPI Callbacks Starts Here===========
*/
    @Override
    public void onTransactionCompleted(TransactionDetails transactionDetails) {

    }

    @Override
    public void onTransactionSuccess() {
        uploadOrderDetails("PAID");
        deleteCartItems();
        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTransactionSubmitted() {

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

/**
    ==============Paytm Callback Starts From Here==============
 */
    @Override
    public void onTransactionResponse(Bundle inResponse) {
        uploadOrderDetails("PAID");
        deleteCartItems();
    }

    @Override
    public void networkNotAvailable() {
        Toast.makeText(this, "Network Not Available", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {
        Toast.makeText(this, "Client Authentication Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {
        Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
        Toast.makeText(this, "Transaction Failed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressedCancelTransaction() {
        Toast.makeText(this, "Transaction Cancelled", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
        Toast.makeText(this, "Transaction Cancelled", Toast.LENGTH_SHORT).show();
    }
}