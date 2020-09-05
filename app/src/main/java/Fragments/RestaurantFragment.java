package Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.munche.CartItemActivity;
import com.example.munche.EmptyCartActivity;
import com.example.munche.MainRestaurantPageActivity;
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
import UI.ChangeLocationActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import ru.nikartm.support.ImageBadgeView;
import timber.log.Timber;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

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
    private ImageBadgeView mImageBadgeView;
    private TextView mTrendingTextView;
    private LinearLayout mAddressContainer;
    private GridView mCuisineFoodView;
    private String[] fruitNames = {"Biryani","North Indian","South Indian","Cakes","Desserts","Burgers","Chinese","Rolls","Pizza"};
    private String biryaniImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fbiryani.jpg?alt=media&token=6eceb101-07c1-49ff-95a3-e540b1e0fb35";
    private String southIndianImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fsouth_indian.jpg?alt=media&token=e925ee1d-5855-484a-9928-9716025ecc43";
    private String northIndianImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fnorth_indian.jpg?alt=media&token=d24f26e0-071e-4eec-93f5-f8f063b0ad5a";
    private String rollsImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Frolls.jpg?alt=media&token=d52c1f03-0558-44a8-a7c3-90b8fda6d77f";
    private String burgersImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fburgers.jpg?alt=media&token=f6f2ca46-fc84-4ed6-84f2-2d199686f45e";
    private String pizzaImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fpizza.jpg?alt=media&token=3239cc9d-c2e6-4a8c-8a76-e8ee307ec72e";
    private String cakesImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fcake.jpeg?alt=media&token=1ded9af6-25a6-4deb-8b1a-0681d7a154d5";
    private String chineseImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fchinese.jpg?alt=media&token=b407830e-0e8d-47d2-8fd0-de43b34bb4d2";
    private String dessertsImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fdesserts.jpg?alt=media&token=209f4592-7b36-4768-9b71-f35d5c05ef74";
    private String[] fruitImages = {biryaniImg,northIndianImg,southIndianImg,cakesImg,dessertsImg,burgersImg,chineseImg,rollsImg,pizzaImg};

    public RestaurantFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_restaurant, container, false);
        init(view);
        fetchLocation(view);
        getItemsInCartNo();
        getRestaurantDetails();

        return view;
    }

    private void init(View view) {
        mAddressContainer = view.findViewById(R.id.addressContainer);
        mCuisineFoodView = view.findViewById(R.id.cuisineGridView);
        CuisineImageAdapter adapter = new CuisineImageAdapter();
        mCuisineFoodView.setAdapter(adapter);
        mAddressContainer.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), ChangeLocationActivity.class);
            intent.putExtra("INT", "ONE");
            startActivity(intent);
        });
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mTrendingTextView = view.findViewById(R.id.trendingTextView);
        db = FirebaseFirestore.getInstance();
        mToolbar = view.findViewById(R.id.customToolBar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(mToolbar);
        mRestaurantRecyclerView = view.findViewById(R.id.restaurant_recyclerView);
        linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRestaurantRecyclerView.setLayoutManager(linearLayoutManager);
        mRestaurantRecyclerView.setHasFixedSize(true);
        mImageBadgeView = view.findViewById(R.id.imageBadgeView);
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

    private void getItemsInCartNo() {
        db.collection("UserList").document(mCurrentUser.getUid()).collection("CartItems").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                int count = 0;
                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    count++;
                }
                mImageBadgeView.setBadgeValue(count);
                mImageBadgeView.setOnClickListener(view -> {
                    if (mImageBadgeView.getBadgeValue() != 0){
                        sendUserToCheckOut();
                    }else {
                        sendUserToEmptyCart();
                    }
                });

            }
        });
    }

    public  class CuisineImageAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return fruitImages.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View view1 = getLayoutInflater().inflate(R.layout.custom_cuisine_layout,null);
            //getting view in row_data
            TextView name = view1.findViewById(R.id.cuisineName);
            ImageView image = view1.findViewById(R.id.cuisineImage);

            name.setText(fruitNames[i]);
            Glide.with(Objects.requireNonNull(getContext()))
                    .load(fruitImages[i])
                    .apply(new RequestOptions()
                            .override(200,200))
                    .centerCrop()
                    .into(image);
            return view1;
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
                if (mCurrentUser != null) {
                    DocumentReference docRef = db.collection("UserList").document(mCurrentUser.getUid());
                    docRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            assert documentSnapshot != null;
                            Double mUserLat = (Double) documentSnapshot.get("latitude");
                            Double mUserLong = (Double) documentSnapshot.get("longitude");
                            Double mResLat = model.getLatitude();
                            Double mResLong = model.getLongitude();
                            int prepTime = Integer.parseInt(model.getRestaurant_prep_time().replace(" Mins" , ""));
                            Location userLocation = new Location("");
                            userLocation.setLatitude(mUserLat);
                            userLocation.setLongitude(mUserLong);
                            Location restaurantLocation = new Location("");
                            restaurantLocation.setLatitude(mResLat);
                            restaurantLocation.setLongitude(mResLong);

                            int distanceInMeters = (int) (userLocation.distanceTo(restaurantLocation));
                            int AvgDrivingSpeedPerKm = 666;
                            int estimatedDriveTimeInMinutes = (int) (distanceInMeters / AvgDrivingSpeedPerKm);
                            String deliveryTime = String.valueOf(estimatedDriveTimeInMinutes + prepTime);
                            holder.mRestaurantName.setText(model.getRestaurant_name());
                            String RUID = model.getRestaurant_uid();
                            holder.mAverageDeliveryTime.setText(deliveryTime + " mins");

                            Glide.with(requireActivity())
                                    .load(model.getRestaurant_spotimage())
                                    .into(holder.mRestaurantSpotImage);
                            holder.mAveragePrice.setText("\u20B9" +  model.getAverage_price() + " Per Person | ");
                            holder.itemView.setOnClickListener(view -> {

                                Intent intent = new Intent(getActivity(), MainRestaurantPageActivity.class);
                                intent.putExtra("RUID", RUID);
                                intent.putExtra("NAME", model.getRestaurant_name());
                                intent.putExtra("DISTANCE", String.valueOf(distanceInMeters/1000));
                                intent.putExtra("TIME", deliveryTime);
                                intent.putExtra("PRICE", model.getAverage_price());
                                intent.putExtra("RES_IMAGE", model.getRestaurant_spotimage());
                                intent.putExtra("RES_NUM", model.getRestaurant_phonenumber());
                                startActivity(intent);
                                Objects.requireNonNull(getActivity()).overridePendingTransition(0,0);

                            });
                        }
                    });
                }

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
                Timber.e(Objects.requireNonNull(e.getMessage()));
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
        @BindView(R.id.average_time)
        TextView mAverageDeliveryTime;

        public RestaurantItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void sendUserToCheckOut() {
        Intent intent = new Intent(getActivity(), CartItemActivity.class);
        startActivity(intent);
        Objects.requireNonNull(getActivity()).overridePendingTransition(0,0);
    }

    private void sendUserToEmptyCart() {
        Intent intent = new Intent(getActivity(), EmptyCartActivity.class);
        startActivity(intent);
        Objects.requireNonNull(getActivity()).overridePendingTransition(0,0);
    }

}