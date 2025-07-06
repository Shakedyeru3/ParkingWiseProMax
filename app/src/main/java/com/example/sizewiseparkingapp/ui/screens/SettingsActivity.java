package com.example.sizewiseparkingapp.ui.screens;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.sizewiseparkingapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class SettingsActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Button logoutButton = findViewById(R.id.logoutButton);
        Button aboutButton = findViewById(R.id.aboutButton);
        Button reportProblemButton = findViewById(R.id.reportProblemButton);

        logoutButton.setOnClickListener(v -> {
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(SettingsActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);
            mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        });

        aboutButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("אודות האפליקציה")
                    .setMessage("אפליקציית SizeWise Parking - ניהול חניות חכם וקל.")
                    .setPositiveButton("סגור", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        reportProblemButton.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@yourapp.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "דיווח על בעיה באפליקציה");
            try {
                startActivity(Intent.createChooser(emailIntent, "בחר אפליקציית דואר"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "לא נמצאה אפליקציית דואר", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        navigateToDrawer();
        return true;
    }

    @Override
    public void onBackPressed() {
        navigateToDrawer();
    }

    private void navigateToDrawer() {
        android.content.Intent intent = new android.content.Intent(this, com.example.sizewiseparkingapp.MainActivity.class);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
} 