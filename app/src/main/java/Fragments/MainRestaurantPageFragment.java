package Fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.munche.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import Models.RestaurantMenuItems;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainRestaurantPageFragment extends Fragment {

    private View view;
    private AppBarLayout mToolBar;
    private String mRestaurantUid, mResName, mResDistance, mResPrice, mResDeliveryTime;
    private TextView mResNameToolBar, mResNameText, mResDistanceText,mResAvgPriceText, mResDeliveryTimeText;
    private ImageView mBackBtnView;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<RestaurantMenuItems, MenuItemHolder> adapter;
    LinearLayoutManager linearLayoutManager;
    private RecyclerView mMenuItemRecyclerView;
    private NestedScrollView mRootView;

    public MainRestaurantPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main_restaurant_page, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null){
            mRestaurantUid = bundle.getString("RUID");
            mResName = bundle.getString("NAME");
            mResDistance = bundle.getString("DISTANCE");
            mResPrice = bundle.getString("PRICE");
            mResDeliveryTime = bundle.getString("TIME");
        }
        init();
        getMenuItems();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void init() {

    mRootView = (NestedScrollView) view.findViewById(R.id.content1);
    db = FirebaseFirestore.getInstance();
    mToolBar = view.findViewById(R.id.mainResToolBar);
    mResNameToolBar = view.findViewById(R.id.restaurantTitleToolbar);
    mResNameText = view.findViewById(R.id.mainResName);
    mResDistanceText = view.findViewById(R.id.mainResDistance);
    mResAvgPriceText = view.findViewById(R.id.restaurantAvgPrice);
    mResDeliveryTimeText = view.findViewById(R.id.restaurantDeliveryTime);
    mResNameToolBar.setText(mResName);
    mResNameText.setText(mResName);
    mResDeliveryTimeText.setText(mResDeliveryTime + " mins");
    mResAvgPriceText.setText("\u20b9" + mResPrice + "/person");
    mResDistanceText.setText(mResDistance + " kms");
    mBackBtnView = view.findViewById(R.id.backBtn);
    mBackBtnView.setOnClickListener(view -> {
        Objects.requireNonNull(getActivity()).onBackPressed();
    });

    mMenuItemRecyclerView = view.findViewById(R.id.menuItemRecylerView);
    linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
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
                    Glide.with(Objects.requireNonNull(requireActivity()))
                            .load(R.drawable.veg_symbol).into(holder.foodSpecification);
                }else {
                    Glide.with(Objects.requireNonNull(requireActivity()))
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
            Toast.makeText(getContext(), "Added Item Successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Adding Item Failed", Toast.LENGTH_SHORT).show();
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