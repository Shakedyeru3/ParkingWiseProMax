package com.example.sizewiseparkingapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.appbar.MaterialToolbar;

public class CreditCardPaymentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_payment);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("תשלום בכרטיס אשראי");
        }

        TextInputEditText fullNameInput = findViewById(R.id.fullNameInput);
        TextInputEditText idInput = findViewById(R.id.idInput);
        TextInputEditText cardNumberInput = findViewById(R.id.cardNumberInput);
        TextInputEditText expDateInput = findViewById(R.id.expDateInput);
        TextInputEditText cvvInput = findViewById(R.id.cvvInput);
        MaterialButton confirmButton = findViewById(R.id.confirmPaymentButton);

        // הגבלת אורך מספר כרטיס
        cardNumberInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        // הגבלת אורך CVV
        cvvInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        // מסיכת תאריך
        expDateInput.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;
                String input = s.toString().replace("/", "");
                if (input.length() > 2) {
                    input = input.substring(0,2) + "/" + input.substring(2);
                }
                if (!input.equals(s.toString())) {
                    s.replace(0, s.length(), input);
                }
                isFormatting = false;
            }
        });

        confirmButton.setOnClickListener(v -> {
            int creditsToAdd = getIntent().getIntExtra("creditsToAdd", 0);
            Toast.makeText(this, "Credits successfully added", Toast.LENGTH_LONG).show();
            android.content.Intent resultIntent = new android.content.Intent();
            resultIntent.putExtra("creditsToAdd", creditsToAdd);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
} 