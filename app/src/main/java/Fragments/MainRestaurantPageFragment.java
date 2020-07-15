package Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.munche.R;

import java.util.Objects;

public class MainRestaurantPageFragment extends Fragment {

    private View view;
    private Toolbar mToolBar;
    private String mRestaurantUid;

    public MainRestaurantPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main_restaurant_page, container, false);

        init();
        Bundle bundle = this.getArguments();
        if (bundle != null){
            mRestaurantUid = bundle.getString("RUID");
        }

        Toast.makeText(getActivity(), mRestaurantUid, Toast.LENGTH_LONG).show();

        return view;
    }

    private void init() {

    mToolBar = view.findViewById(R.id.mainResToolBar);
    ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(mToolBar);
    Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayShowHomeEnabled(true);
    mToolBar.setNavigationOnClickListener(view -> {
        getActivity().onBackPressed();
    });
    mToolBar.setTitle("Biryani Blues");

    }

}