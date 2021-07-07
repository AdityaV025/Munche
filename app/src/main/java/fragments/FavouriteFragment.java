package fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.munche.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import models.FavoriteRestaurantDetails;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FavouriteFragment extends Fragment {

    private View view;
    private FirebaseFirestore db;
    LinearLayoutManager linearLayoutManager;
    private RecyclerView mFavoriteResRecyclerView;
    private String uid;

    public FavouriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favourite, container, false);

        init();
        getFavoriteRes();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        ImageView mGoBackBtn = view.findViewById(R.id.cartBackBtn);
        mGoBackBtn.setOnClickListener(view -> {
            Fragment fragment = new RestaurantFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        TextView mFavoriteResToolBarText = view.findViewById(R.id.confirmOrderText);
        mFavoriteResToolBarText.setText("Your Favorite Restaurants");
        db = FirebaseFirestore.getInstance();
        mFavoriteResRecyclerView = view.findViewById(R.id.favoriteResRecyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mFavoriteResRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void getFavoriteRes() {
        Query query = db.collection("UserList").document(uid).collection("FavoriteRestaurants");
        FirestoreRecyclerOptions<FavoriteRestaurantDetails> favResModel = new FirestoreRecyclerOptions.Builder<FavoriteRestaurantDetails>()
                .setQuery(query, FavoriteRestaurantDetails.class)
                .build();
        FirestoreRecyclerAdapter<FavoriteRestaurantDetails, FavoriteResMenuHolder> adapter = new FirestoreRecyclerAdapter<FavoriteRestaurantDetails, FavoriteResMenuHolder>(favResModel) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull FavoriteResMenuHolder holder, int i, @NonNull FavoriteRestaurantDetails model) {
                Glide.with(requireActivity())
                        .load(model.getRestaurant_image())
                        .placeholder(R.drawable.restaurant_image_placeholder)
                        .into(holder.mFavResImage);

                holder.mFavResName.setText(model.getRestaurant_name());
                holder.mFavResPrice.setText("\u20b9" + model.getRestaurant_price() + " per person");
            }

            @NonNull
            @Override
            public FavoriteResMenuHolder onCreateViewHolder(@NonNull ViewGroup group, int viewType) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.custom_favorite_res_layout, group, false);
                return new FavoriteResMenuHolder(view);
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        mFavoriteResRecyclerView.setAdapter(adapter);

    }

    public static class FavoriteResMenuHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.favResImage)
        ImageView mFavResImage;
        @BindView(R.id.favResName)
        TextView mFavResName;
        @BindView(R.id.favoriteResPrice)
        TextView mFavResPrice;

        public FavoriteResMenuHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}