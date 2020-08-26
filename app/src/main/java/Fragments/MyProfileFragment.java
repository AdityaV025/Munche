package Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.munche.OrdersActivity;
import com.example.munche.R;
import com.example.munche.CurrentOrderActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import UI.LoginActivity;

public class MyProfileFragment extends Fragment implements View.OnClickListener {

    private TextView mLogOutText, mMyOrdersText;
    private View view;
    private FirebaseAuth mAuth;
    private String uid;
    private FirebaseFirestore db;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        init();
        mLogOutText.setOnClickListener(this);
        mMyOrdersText.setOnClickListener(this);
        return view;
    }

    private void init() {
        db = FirebaseFirestore.getInstance();
        mLogOutText = view.findViewById(R.id.logOutText);
        mAuth = FirebaseAuth.getInstance();
        uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mMyOrdersText = view.findViewById(R.id.myOrdersText);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.logOutText:
                new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                        .setMessage("Are you sure you want to log out ?")
                        .setPositiveButton("Log Out", (dialog, which) -> {

                            Map<String, Object> updateDeviceToken = new HashMap<>();
                            updateDeviceToken.put("devicetoken", FieldValue.delete());

                            db.collection("UserList").document(uid).update(updateDeviceToken).addOnSuccessListener(aVoid -> {
                                mAuth.signOut();
                                sendUserToLogin();
                            });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                break;

            case R.id.myOrdersText:
                Intent intent = new Intent(getActivity(), OrdersActivity.class);
                startActivity(intent);
                break;

        }
    }

    private void sendUserToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}