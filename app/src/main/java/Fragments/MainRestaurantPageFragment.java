package Fragments;

import android.annotation.SuppressLint;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.munche.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.travijuu.numberpicker.library.NumberPicker;

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
                holder.mItemAddBtn.setOnClickListener(view -> {
                    Toast.makeText(getContext(), model.getPrice(), Toast.LENGTH_SHORT).show();
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

        public MenuItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}