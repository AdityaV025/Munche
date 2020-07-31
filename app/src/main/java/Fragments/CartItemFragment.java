package Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.munche.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import Models.CartItemDetail;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CartItemFragment extends Fragment {

    private View view;
    private AppBarLayout mToolBar;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<CartItemDetail, CartItemHolder> itemAdapter;
    LinearLayoutManager linearLayoutManager;
    private RecyclerView mCartItemRecylerView;
    private TextView mRestaurantCartName;
    private String uid;
    private ImageView mCartBackBtn;
    private String USER_LIST = "UserList";
    private String CART_ITEMS = "CartItems";
    private String itemCount;
    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.fragment_cart_item, container, false);
         init();
         setRestaurantName();
         getCartItems();

         return view;
    }

    private void init() {
        db = FirebaseFirestore.getInstance();
        mToolBar = view.findViewById(R.id.cartItemToolBar);
        mRestaurantCartName = view.findViewById(R.id.restaurantCartName);
        mCartBackBtn = view.findViewById(R.id.cartBackBtn);
        mCartBackBtn.setOnClickListener(view -> {
            Objects.requireNonNull(getActivity()).onBackPressed();
        });
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mCartItemRecylerView = view.findViewById(R.id.cartItemRecyclerView);
        linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        mCartItemRecylerView.setLayoutManager(linearLayoutManager);
        itemCount = Objects.requireNonNull(getArguments()).getString("ITEM_COUNT");
        View bottomSheet = view.findViewById(R.id.bottomSheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
    }

    private void setRestaurantName() {
        db.collection(USER_LIST).document(uid).get().addOnCompleteListener(task -> {

            if (task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();
                assert documentSnapshot != null;
                String resName = (String) documentSnapshot.get("restaurant_cart_name");
                mRestaurantCartName.setText(resName);

            }else {
                Toast.makeText(getContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getCartItems() {

        Query query = db.collection(USER_LIST).document(uid).collection(CART_ITEMS);
        FirestoreRecyclerOptions<CartItemDetail> cartItemModel = new FirestoreRecyclerOptions.Builder<CartItemDetail>()
                .setQuery(query, CartItemDetail.class)
                .build();
        itemAdapter = new FirestoreRecyclerAdapter<CartItemDetail, CartItemHolder>(cartItemModel) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull CartItemHolder holder, int item, @NonNull CartItemDetail model) {

                String specImage = model.getSelect_specification();
                if (specImage.equals("Veg")){
                    Glide.with(Objects.requireNonNull(requireActivity()))
                            .load(R.drawable.veg_symbol).into(holder.mFoodMarkImg);
                }else {
                    Glide.with(Objects.requireNonNull(requireActivity()))
                            .load(R.drawable.non_veg_symbol).into(holder.mFoodMarkImg);
                }
                holder.mItemCartName.setText(model.getSelect_name());
                String itemCount = model.getItem_count();
                holder.mQtyPicker.setNumber(itemCount);
                int getItemPrice = Integer.parseInt(model.getSelect_price());
                int getItemCount = Integer.parseInt(model.getItem_count());
                int finalPrice = getItemPrice * getItemCount;
                holder.mItemCartPrice.setText("\u20b9 " + finalPrice);

                holder.mQtyPicker.setOnValueChangeListener((view, oldValue, newValue) -> {

                    String updatedPrice = String.valueOf(newValue * Integer.parseInt(model.getSelect_price()));
                    holder.mItemCartPrice.setText("\u20b9 " + updatedPrice);

                    Map<String, Object> updatedPriceMap = new HashMap<>();
                    updatedPriceMap.put("item_count", String.valueOf(newValue));

                    db.collection(USER_LIST)
                            .document(uid)
                            .collection(CART_ITEMS)
                            .document(model.getSelect_name())
                            .update(updatedPriceMap)
                            .addOnCompleteListener(task ->
                                    Log.d("SUCCESS", "SUCESSSSSSS"));

                });
                calculateTotalPrice();
            }

            @NonNull
            @Override
            public CartItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cart_items_layout, parent, false);

                return new CartItemHolder(view);
            }
            @Override
            public void onError(@NotNull FirebaseFirestoreException e) {
                Log.e("error", Objects.requireNonNull(e.getMessage()));
            }
        };
        itemAdapter.startListening();
        itemAdapter.notifyDataSetChanged();
        mCartItemRecylerView.setAdapter(itemAdapter);

    }

    public static class CartItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.foodMarkCart)
        ImageView mFoodMarkImg;
        @BindView(R.id.itemNameCart)
        TextView mItemCartName;
        @BindView(R.id.itemPriceCart)
        TextView mItemCartPrice;
        @BindView(R.id.quantityPicker)
        ElegantNumberButton mQtyPicker;

        public CartItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void calculateTotalPrice() {
        mCartItemRecylerView.postDelayed(() -> {
            if (Objects.requireNonNull(mCartItemRecylerView.findViewHolderForAdapterPosition(0)).itemView.findViewById(R.id.itemPriceCart) != null){
                int totPrice = 0;
                for (int i = 0; i < Objects.requireNonNull(mCartItemRecylerView.getAdapter()).getItemCount() ; i++){
                    TextView textView = Objects.requireNonNull(mCartItemRecylerView.findViewHolderForAdapterPosition(i)).itemView.findViewById(R.id.itemPriceCart);
                    String priceText = textView.getText().toString().replace("\u20b9 " , "");
                    int price = Integer.parseInt(priceText);
                    totPrice = price + totPrice;
                }
                Log.d("TOTPRICE", String.valueOf(totPrice));

            }

        },5);
    }

}