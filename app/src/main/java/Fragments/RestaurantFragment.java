package Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.munche.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import Models.RestaurantDetail;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantFragment extends Fragment {

    private View view;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private FirebaseUser mCurrentUser;
    private FirebaseFirestore db;
    private String address;
    private FirestoreRecyclerAdapter<RestaurantDetail, RestaurantItemViewHolder> restaurantAdapter;
    LinearLayoutManager linearLayoutManager;
    private RecyclerView mRestaurantRecyclerView;

    public RestaurantFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_restaurant, container, false);
        init(view);
        fetchLocation(view);
        getRestaurantDetails();

        return view;
    }

    private void init(View view) {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        mToolbar = view.findViewById(R.id.customToolBar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(mToolbar);
        mRestaurantRecyclerView = view.findViewById(R.id.restaurant_recyclerView);
        linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRestaurantRecyclerView.setLayoutManager(linearLayoutManager);
        mRestaurantRecyclerView.setHasFixedSize(true);
    }

    private void fetchLocation(View view) {
        if (mCurrentUser != null) {
            DocumentReference docRef = db.collection("UserList").document(mCurrentUser.getUid());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    assert documentSnapshot != null;
                    address = String.valueOf(documentSnapshot.get("address"));
                    TextView textView = view.findViewById(R.id.userLocation);
                    textView.setText(address);
                }
            });
        }
    }

    private void getRestaurantDetails() {
        Query query = db.collection("RestaurantList");
        FirestoreRecyclerOptions<RestaurantDetail> menuItemModel = new FirestoreRecyclerOptions.Builder<RestaurantDetail>()
                .setQuery(query, RestaurantDetail.class)
                .build();
        restaurantAdapter = new FirestoreRecyclerAdapter<RestaurantDetail, RestaurantItemViewHolder>(menuItemModel) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(@NonNull RestaurantItemViewHolder holder, int position, @NonNull RestaurantDetail model) {

                holder.mRestaurantName.setText(model.getRestaurant_name());
                String RUID = model.getRestaurant_uid();
                Glide.with(requireActivity())
                        .load(model.getRestaurant_spotimage())
                        .into(holder.mRestaurantSpotImage);
                holder.mAveragePrice.setText("\u20B9" +  model.getAverage_price() + " Per Person | ");

                holder.itemView.setOnClickListener(view -> {

                    Bundle bundle = new Bundle();
                    bundle.putString("RUID",RUID);
                    Fragment fragment = new MainRestaurantPageFragment();
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                });

            }
            @NonNull
            @Override
            public RestaurantItemViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.restaurant_main_detail, group, false);
                return new RestaurantItemViewHolder(view);
            }
            @Override
            public void onError(@NonNull @NotNull FirebaseFirestoreException e) {
                Log.e("error", Objects.requireNonNull(e.getMessage()));
            }
        };
        restaurantAdapter.startListening();
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(mRestaurantRecyclerView);
        restaurantAdapter.notifyDataSetChanged();
        mRestaurantRecyclerView.setAdapter(restaurantAdapter);

    }

    public static class RestaurantItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.resName)
        TextView mRestaurantName;
        @BindView(R.id.resImage)
        ImageView mRestaurantSpotImage;
        @BindView(R.id.average_price)
        TextView mAveragePrice;

        public RestaurantItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}