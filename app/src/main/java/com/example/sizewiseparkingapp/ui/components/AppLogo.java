package com.example.sizewiseparkingapp.ui.components;

import android.content.Context;
import android.view.ViewGroup;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;
import com.example.sizewiseparkingapp.R;

public class AppLogo extends AppCompatImageView {
    private static final int DEFAULT_SIZE = 120; // Default size in dp

    public AppLogo(Context context) {
        super(context);
        init(context);
    }

    public AppLogo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AppLogo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setImageResource(R.drawable.logo_sizewise);
        setContentDescription(context.getString(R.string.app_logo));

        float scale = context.getResources().getDisplayMetrics().density;
        int pixelSize = (int) (AppLogo.DEFAULT_SIZE * scale + 0.5f); // Convert dp to pixels
        setLayoutParams(new ViewGroup.LayoutParams(pixelSize, pixelSize));
    }
}
