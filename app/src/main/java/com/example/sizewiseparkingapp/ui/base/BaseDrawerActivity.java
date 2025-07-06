package com.example.sizewiseparkingapp.ui.base;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.example.sizewiseparkingapp.MainActivity;
import com.example.sizewiseparkingapp.R;
import com.example.sizewiseparkingapp.ui.screens.RequestParkingActivity;
import com.google.android.material.navigation.NavigationView;

public abstract class BaseDrawerActivity extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        initializeBaseViews();
        setupNavigationListener();
    }

    protected abstract int getLayoutResourceId();

    private void initializeBaseViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        
        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple_500)));
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    private void setupNavigationListener() {
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_map) {
            navigateToMainActivity();
            return true;
        } else if (itemId == R.id.nav_request_parking) {
            startActivity(new Intent(this, RequestParkingActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (itemId == R.id.nav_settings) {
            startActivity(new Intent(this, com.example.sizewiseparkingapp.ui.screens.SettingsActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (itemId == R.id.nav_wallet) {
            startActivity(new Intent(this, com.example.sizewiseparkingapp.WalletActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }
} 