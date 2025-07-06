package com.example.sizewiseparkingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.example.sizewiseparkingapp.ui.screens.RequestParkingActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.sizewiseparkingapp.ui.fragments.*;
import com.example.sizewiseparkingapp.ui.base.BaseDrawerActivity;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends BaseDrawerActivity {
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        setupBottomNavigation();
        handleInitialNavigation(savedInstanceState);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_map) {
                return loadFragment(new MapFragment());
            } else if (item.getItemId() == R.id.bottom_settings) {
                startActivity(new Intent(this, com.example.sizewiseparkingapp.ui.screens.SettingsActivity.class));
                return true;
            } else if (item.getItemId() == R.id.bottom_wallet) {
                startActivity(new Intent(this, com.example.sizewiseparkingapp.WalletActivity.class));
                return true;
            }
            return true;
        });
    }

    private void handleInitialNavigation(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            loadFragment(new MapFragment());
        }
    }

    private Fragment getFragmentForNavigationItem(int itemId) {
        if (itemId == R.id.nav_map || itemId == R.id.bottom_map) {
            return new MapFragment();
        }
        return null;
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}