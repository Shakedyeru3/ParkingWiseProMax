package com.example.sizewiseparkingapp.ui.theme;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowInsetsController;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.sizewiseparkingapp.R;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set status bar color dynamically
        setStatusBarColor(this, R.color.primary_purple);
    }

    public static void setStatusBarColor(Activity activity, int colorResId) {
        Window window = activity.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(activity, colorResId));

        // Ensure status bar icons remain visible if the background is light
        WindowInsetsController insetsController = window.getInsetsController();
        if (insetsController != null) {
            insetsController.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
        }
    }
}
