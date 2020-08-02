package com.example.munche;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import Models.RestaurantMenuItems;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainRestaurantPageActivity extends AppCompatActivity {

    private AppBarLayout mToolBar;
    private String mRestaurantUid, mResName, mResDistance, mResPrice, mResDeliveryTime;
    private TextView mResNameToolBar, mResNameText, mResDistanceText,mResAvgPriceText, mResDeliveryTimeText;
    private ImageView mBackBtnView;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<RestaurantMenuItems, MenuItemHolder> adapter;
    LinearLayoutManager linearLayoutManager;
    private RecyclerView mMenuItemRecyclerView;
    private NestedScrollView mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_restaurant_page);

        Intent intent = getIntent();
        if (intent != null){
            mRestaurantUid = intent.getStringExtra("RUID");
            mResName = intent.getStringExtra("NAME");
            mResDistance = intent.getStringExtra("DISTANCE");
            mResPrice = intent.getStringExtra("PRICE");
            mResDeliveryTime = intent.getStringExtra("TIME");
        }

        init();
        getMenuItems();

    }

    @SuppressLint("SetTextI18n")
    private void init() {

        mRootView = (NestedScrollView) findViewById(R.id.content1);
        db = FirebaseFirestore.getInstance();
        mToolBar = findViewById(R.id.mainResToolBar);
        mResNameToolBar = findViewById(R.id.restaurantTitleToolbar);
        mResNameText = findViewById(R.id.mainResName);
        mResDistanceText = findViewById(R.id.mainResDistance);
        mResAvgPriceText = findViewById(R.id.restaurantAvgPrice);
        mResDeliveryTimeText = findViewById(R.id.restaurantDeliveryTime);
        mResNameToolBar.setText(mResName);
        mResNameText.setText(mResName);
        mResDeliveryTimeText.setText(mResDeliveryTime + " mins");
        mResAvgPriceText.setText("\u20b9" + mResPrice + "/person");
        mResDistanceText.setText(mResDistance + " kms");
        mBackBtnView = findViewById(R.id.backBtn);
        mBackBtnView.setOnClickListener(view -> {
            this.onBackPressed();
            this.overridePendingTransition(0,0);
        });

        mMenuItemRecyclerView = findViewById(R.id.menuItemRecylerView);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMenuItemRecyclerView.setLayoutManager(linearLayoutManager);

    }

    private void getMenuItems() {
        Query query = db.collection("Menu").document(mRestaurantUid).collection("MenuItems");
        FirestoreRecyclerOptions<RestaurantMenuItems> menuItemModel = new FirestoreRecyclerOptions.Builder<RestaurantMenuItems>()
                .setQuery(query, RestaurantMenuItems.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<RestaurantMenuItems, MenuItemHolder>(menuItemModel) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(@NonNull MenuItemHolder holder, int position, @NonNull RestaurantMenuItems model) {

                holder.mItemName.setText(model.getName());
                holder.mItemCategory.setText(model.getCategory());
                String specImage = String.valueOf(model.getSpecification());
                if (specImage.equals("Veg")){
                    Glide.with(Objects.requireNonNull(getApplicationContext()))
                            .load(R.drawable.veg_symbol).into(holder.foodSpecification);
                }else {
                    Glide.with(Objects.requireNonNull(getApplicationContext()))
                            .load(R.drawable.non_veg_symbol).into(holder.foodSpecification);
                }
                holder.mItemPrice.setText("\u20B9 " + model.getPrice());
                holder.itemView.setOnClickListener(v -> {
                });

                String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                holder.mItemAddBtn.setOnClickListener(view -> {

                    String selectedItemName = holder.mItemName.getText().toString();
                    addItemToCart(selectedItemName,model,uid);

                });
                holder.mQtyPicker.setOnValueChangeListener((view, oldValue, newValue) -> {
                    if(newValue <= 0) {
                        holder.mQtyPicker.setVisibility(View.GONE);
                        holder.mItemAddBtn.setVisibility(View.VISIBLE);
                    }

                });

            }
            @NotNull
            @Override
            public MenuItemHolder onCreateViewHolder(@NotNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.restaurant_menuitems_view, group, false);
                return new MenuItemHolder(view);
            }
            @Override
            public void onError(@NotNull FirebaseFirestoreException e) {
                Log.e("error", Objects.requireNonNull(e.getMessage()));
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        mMenuItemRecyclerView.setAdapter(adapter);

    }

    private void addItemToCart(String selectedItemName, RestaurantMenuItems model, String uid){

        Map<String ,String> cartItemMap = new HashMap<>();
        cartItemMap.put("select_name", selectedItemName);
        cartItemMap.put("select_price", model.getPrice());
        cartItemMap.put("select_specification", model.getSpecification());
        cartItemMap.put("item_count", "1");
        db.collection("UserList").document(uid).collection("CartItems")
                .document(selectedItemName).
                set(cartItemMap)
                .addOnSuccessListener(aVoid -> {

                    db.collection("UserList").document(uid).collection("CartItems").get().addOnCompleteListener(task -> {

                        if (task.isSuccessful()){
                            int count = 0;
                            for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                count++;
                            }
                            Snackbar snackbar = Snackbar
                                    .make(mRootView, "Added " + count + " items", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("UNDO", view -> {
                                        Snackbar snackbar1 = Snackbar.make(mRootView, "Message is restored!", Snackbar.LENGTH_SHORT);
                                        snackbar1.dismiss();
                                    });
                            snackbar.show();
                        }
                    });
                }).addOnFailureListener(e -> {
        });

        Map<String, Object> resNameMap = new HashMap<>();
        resNameMap.put("restaurant_cart_name", mResName);
        resNameMap.put("restaurant_cart_uid", mRestaurantUid);

        db.collection("UserList").document(uid).update(resNameMap).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Added Item Successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Adding Item Failed", Toast.LENGTH_SHORT).show();
        });

    }

    public static class MenuItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.menuItemName)
        TextView mItemName;
        @BindView(R.id.foodMark)
        ImageView foodSpecification;
        @BindView(R.id.menuItemPrice)
        TextView mItemPrice;
        @BindView(R.id.menuItemCategory)
        TextView mItemCategory;
        @BindView(R.id.addMenuItemBtn)
        Button mItemAddBtn;
        @BindView(R.id.quantityPicker)
        ElegantNumberButton mQtyPicker;

        public MenuItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}