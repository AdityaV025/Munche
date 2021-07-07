package ui.order;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.munche.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import ir.samanjafari.easycountdowntimer.CountDownInterface;
import ir.samanjafari.easycountdowntimer.EasyCountDownTextview;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class CurrentOrderActivity extends AppCompatActivity{

    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private MapView mapView;
    private DirectionsRoute currentRoute;
    private MapboxDirections client;
    private LottieAnimationView mDeliveryAnimation;
    private EasyCountDownTextview mCountDownTimer;
    private TextView mTimeFormatText;
    private String resUid,uid,resPhoneNum,resName;
    private double mUserLatitude,mUserLongitude,mResLatitude,mResLongitude;
    private FirebaseFirestore db;
    private String RES_LIST = "RestaurantList";
    private String USER_LIST = "UserList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_current_order);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        changestatusbarcolor();
        fetchInfo();

    }

    public void changestatusbarcolor() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void fetchInfo() {
        resUid = getIntent().getStringExtra("RES_UID");
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        db = FirebaseFirestore.getInstance();
        db.collection(RES_LIST).document(resUid).get().addOnCompleteListener(task -> {

            if (task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();
                assert documentSnapshot != null;
                mResLatitude = (double) documentSnapshot.get("latitude");
                mResLongitude = (double) documentSnapshot.get("longitude");
                resPhoneNum = (String) documentSnapshot.get("restaurant_phonenumber");
                resName = (String) documentSnapshot.get("restaurant_name");
                db.collection(USER_LIST).document(uid).get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        DocumentSnapshot documentSnapshot1 = task1.getResult();
                        assert  documentSnapshot1 != null;
                        mUserLatitude = (double) documentSnapshot1.get("latitude");
                        mUserLongitude = (double) documentSnapshot1.get("longitude");
                        String time = String.valueOf(documentSnapshot1.get("restaurant_delivery_time"));
                        int resDeliveryTime = Integer.parseInt(time);
                        mCountDownTimer = findViewById(R.id.easyCountDownTextview);
                        mTimeFormatText = findViewById(R.id.timeLeftText);
                        mCountDownTimer.setTime(0,0, resDeliveryTime,0);
                        mCountDownTimer.startTimer();
                        final Typeface typeface2 = ResourcesCompat.getFont(Objects.requireNonNull(getApplicationContext()), R.font.open_sans_semibold);
                        mCountDownTimer.setTypeFace(typeface2);
                        mCountDownTimer.setOnTick(new CountDownInterface() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTick(long time) {

                                if (time > 60000){
                                    mTimeFormatText.setText("minutes");
                                }else {
                                    mTimeFormatText.setText("seconds");
                                }

                            }

                            @Override
                            public void onFinish() {
                                Toast.makeText(getApplicationContext(), "time is finished!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        init(mResLongitude,mResLatitude,mUserLongitude,mUserLatitude,resName,resPhoneNum);
                    }

                });
            }

        });

    }

    private void init(double mResLongitude, double mResLatitude,double mUserLongitude, double mUserLatitude, String resName, String resPhoneNum) {
        TextView OrderedResName = findViewById(R.id.orderedRestaurantName);
        TextView mCurrentOrderText = findViewById(R.id.currentOrderResName);
        mCurrentOrderText.setText(resName);
        OrderedResName.setText(resName);
        Button mCallResBtn = findViewById(R.id.callResBtn);
        mCallResBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + resPhoneNum));
            startActivity(intent);
        });

        Point origin = Point.fromLngLat(mResLongitude, mResLatitude);
        Point destination = Point.fromLngLat(mUserLongitude,mUserLatitude);
        LatLng loc1 = new LatLng(mUserLatitude, mUserLongitude);
        LatLng loc2 = new LatLng(mResLatitude, mResLongitude);
        mapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            initSource(style, origin, destination);
            initLayers(style);
            getRoute(mapboxMap, origin, destination);
            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                    .include(loc1)
                    .include(loc2)
                    .build();
            mapboxMap.setLatLngBoundsForCameraTarget(latLngBounds);
            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 150));
            mapboxMap.setMinZoomPreference(5);

        }));

        mDeliveryAnimation = findViewById(R.id.deliveryAnimation);
        mDeliveryAnimation.playAnimation();
    }

    private void initSource(@NonNull Style loadedMapStyle, Point origin, Point destination) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[] {
                Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude())),
                Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude()))}));
        loadedMapStyle.addSource(iconGeoJsonSource);
    }

    /**
     * Add the route and marker icon layers to the map
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void initLayers(@NonNull Style loadedMapStyle) {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

// Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(3f),
                lineColor(getResources().getColor(R.color.colorAccent))
        );
        loadedMapStyle.addLayer(routeLayer);

// Add the red marker icon image to the map
        loadedMapStyle.addImage(RED_PIN_ICON_ID, Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.mapbox_marker_icon_default))));

// Add the red marker icon SymbolLayer to the map
        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                iconImage(RED_PIN_ICON_ID),
                iconIgnorePlacement(true),
                iconAllowOverlap(true)));
    }

    private void getRoute(MapboxMap mapboxMap, Point origin, Point destination) {
        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.mapbox_access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(retrofit2.Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
// You can get the generic HTTP info about the response

                if (response.body() == null) {

                    return;
                } else if (response.body().routes().size() < 1) {

                    return;
                }

// Get the directions route
                currentRoute = response.body().routes().get(0);

// Make a toast which displays the route's distance
                Toast.makeText(getApplicationContext(), "The distance is: " +  currentRoute.distance(), Toast.LENGTH_SHORT).show();

                if (mapboxMap != null) {
                    mapboxMap.getStyle(style -> {

// Retrieve and update the source designated for showing the directions route
                        GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

// Create a LineString with the directions route's geometry and
// reset the GeoJSON source for the route LineLayer source
                        if (source != null) {
                            source.setGeoJson(LineString.fromPolyline(Objects.requireNonNull(currentRoute.geometry()), PRECISION_6));
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull retrofit2.Call<DirectionsResponse> call, @NotNull Throwable t) {

                Toast.makeText(CurrentOrderActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
// Cancel the Directions API request
        if (client != null) {
            client.cancelCall();
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
