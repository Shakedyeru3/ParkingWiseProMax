package com.example.sizewiseparkingapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class WalletActivity extends AppCompatActivity {
    private static final int CREDIT_PRICE = 5;
    private int creditBalance = 0;
    private String userEmail = null;
    private TextView creditBalanceText, priceDisplay;
    private Spinner creditSpinner;
    private MaterialButton purchaseButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Wallet");
        }

        creditBalanceText = findViewById(R.id.creditBalanceText);
        priceDisplay = findViewById(R.id.priceDisplay);
        creditSpinner = findViewById(R.id.creditSpinner);
        purchaseButton = findViewById(R.id.purchaseButton);
        db = FirebaseFirestore.getInstance();

        // Spinner setup
        Integer[] creditOptions = {5, 10, 15, 20, 25};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, creditOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        creditSpinner.setAdapter(adapter);

        // GoogleSignIn
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null && account.getEmail() != null) {
            userEmail = account.getEmail();
            loadUserCredits();
        } else {
            Toast.makeText(this, "לא נמצא משתמש מחובר", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        creditSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                int credits = (int) parent.getItemAtPosition(position);
                priceDisplay.setText("Price: " + (credits * CREDIT_PRICE) + " ₪");
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                priceDisplay.setText("Price: 0 ₪");
            }
        });

        purchaseButton.setOnClickListener(v -> {
            int creditsToAdd = (int) creditSpinner.getSelectedItem();
            android.content.Intent intent = new android.content.Intent(this, CreditCardPaymentActivity.class);
            intent.putExtra("creditsToAdd", creditsToAdd);
            startActivityForResult(intent, 1001);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            int creditsToAdd = data.getIntExtra("creditsToAdd", 0);
            if (creditsToAdd > 0) {
                addCreditsToUser(creditsToAdd);
            }
        }
    }

    private void loadUserCredits() {
        DocumentReference docRef = db.collection("userCredits").document(userEmail);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Object creditsObj = documentSnapshot.get("credits");
                if (creditsObj instanceof Number) {
                    creditBalance = ((Number) creditsObj).intValue();
                } else {
                    creditBalance = 0;
                }
            } else {
                // Create new document
                Map<String, Object> data = new HashMap<>();
                data.put("credits", 0);
                data.put("email", userEmail);
                docRef.set(data);
                creditBalance = 0;
            }
            runOnUiThread(() -> creditBalanceText.setText("Current Credits: " + creditBalance));
        }).addOnFailureListener(e -> runOnUiThread(() -> {
            Toast.makeText(this, "שגיאה בטעינת קרדיטים", Toast.LENGTH_SHORT).show();
            creditBalanceText.setText("Current Credits: 0");
        }));
    }

    private void addCreditsToUser(int creditsToAdd) {
        DocumentReference docRef = db.collection("userCredits").document(userEmail);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            int currentCredits = 0;
            if (documentSnapshot.exists()) {
                Object creditsObj = documentSnapshot.get("credits");
                if (creditsObj instanceof Number) {
                    currentCredits = ((Number) creditsObj).intValue();
                }
            }
            int newCredits = currentCredits + creditsToAdd;
            docRef.update("credits", newCredits)
                .addOnSuccessListener(aVoid -> runOnUiThread(() -> {
                    creditBalance = newCredits;
                    creditBalanceText.setText("Current Credits: " + creditBalance);
                    Toast.makeText(this, "הקרדיטים נטענו בהצלחה", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> runOnUiThread(() ->
                    Toast.makeText(this, "שגיאה בעדכון קרדיטים", Toast.LENGTH_SHORT).show()
                ));
        }).addOnFailureListener(e -> runOnUiThread(() ->
            Toast.makeText(this, "שגיאה בטעינת קרדיטים", Toast.LENGTH_SHORT).show()
        ));
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