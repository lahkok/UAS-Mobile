package com.example.uas_mobile.ui.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.uas_mobile.R;
import com.example.uas_mobile.data.model.MyApiEndpoint;
import com.example.uas_mobile.ui.view.fragment.HomeFragment;
import com.example.uas_mobile.ui.view.fragment.NotificationsFragment;
import com.example.uas_mobile.ui.view.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        setSupportActionBar(toolbar);

        // Set up the listener for BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment selectedFragment = null;
            String title = "";

            if (id == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
                title = "Home";
            } else if (id == R.id.navigation_notifications) {
                selectedFragment = new NotificationsFragment();
                title = "Notifications";
            } else if (id == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
                title = "Profile";
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
                getSupportActionBar().setTitle(title);
            }
            return true;
        });

        // Display the HomeFragment when the activity starts
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
        getSupportActionBar().setTitle("Home");
    }
}