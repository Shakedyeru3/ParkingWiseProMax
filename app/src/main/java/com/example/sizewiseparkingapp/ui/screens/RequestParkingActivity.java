package com.example.sizewiseparkingapp.ui.screens;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;
import com.example.sizewiseparkingapp.R;
import com.example.sizewiseparkingapp.ui.base.BaseDrawerActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.text.DateFormat;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class RequestParkingActivity extends BaseDrawerActivity {
    private MaterialButton uploadImageButton, submitRequestButton;
    private TextInputEditText addressInput, floorInput;
    private MaterialSwitch facingStreetSwitch;
    private FirebaseFirestore db;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_request_parking;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        addressInput = findViewById(R.id.addressInput);
        floorInput = findViewById(R.id.floorInput);
        facingStreetSwitch = findViewById(R.id.facingStreetSwitch);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        submitRequestButton = findViewById(R.id.submitRequestButton);
        MaterialTextView requestParkingTitle = findViewById(R.id.requestParkingTitle);
    }

    private void setupListeners() {
        uploadImageButton.setOnClickListener(v ->
            Toast.makeText(this, "Image upload feature not implemented yet", Toast.LENGTH_SHORT).show());

        submitRequestButton.setOnClickListener(v -> submitParkingRequest());
    }

    private void submitParkingRequest() {
        String address = addressInput.getText() != null ? addressInput.getText().toString().trim() : "";
        String floor = floorInput.getText() != null ? floorInput.getText().toString().trim() : "";

        if (address.isEmpty()) {
            Toast.makeText(this, "Please enter the address", Toast.LENGTH_SHORT).show();
            return;
        }
        // שאר השדות אינם חובה
        boolean facingStreet = facingStreetSwitch.isChecked();
        String imageURL = "";
        String description = "";
        if (!floor.isEmpty()) {
            description += "Floor: " + floor + ", ";
        }
        description += "Facing Street: " + facingStreet;
        String time = DateFormat.getDateTimeInstance().format(new Date());

        Map<String, Object> parkingData = new HashMap<>();
        parkingData.put("Street", address);
        parkingData.put("street", address); // הוספת שדה street עם s קטנה
        parkingData.put("Description", description);
        parkingData.put("imageURL", imageURL);
        parkingData.put("time", time);

        db.collection("parkingSpots").add(parkingData)
            .addOnSuccessListener(documentReference -> rewardUserCredits())
            .addOnFailureListener(e -> Toast.makeText(this, "Failed to submit request", Toast.LENGTH_SHORT).show());
    }

    private void rewardUserCredits() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null || account.getEmail() == null) {
            showThankYouDialog(false);
            return;
        }
        String email = account.getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("userCredits").document(email);
        ref.get().addOnSuccessListener(doc -> {
            long now = System.currentTimeMillis();
            Long current = doc.getLong("credits");
            Long lastRewarded = doc.getLong("lastParkingReward");
            if (current != null) {
                if (lastRewarded != null && now - lastRewarded < 30000) {
                    showThankYouDialog(false); // לא נותן פעמיים תוך 30 שנ'
                    return;
                }
                ref.update("credits", current + 10, "lastParkingReward", now)
                    .addOnSuccessListener(aVoid -> showThankYouDialog(true))
                    .addOnFailureListener(e -> showThankYouDialog(false));
            } else {
                // משתמש חדש
                Map<String, Object> data = new HashMap<>();
                data.put("credits", 10);
                data.put("email", email);
                data.put("lastParkingReward", now);
                ref.set(data)
                    .addOnSuccessListener(aVoid -> showThankYouDialog(true))
                    .addOnFailureListener(e -> showThankYouDialog(false));
            }
        }).addOnFailureListener(e -> showThankYouDialog(false));
    }

    private void showThankYouDialog(boolean rewarded) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Success");
            if (rewarded) {
                builder.setMessage("Your parking spot was submitted.\nYou’ve earned 10 credits!");
            } else {
                builder.setMessage(getString(R.string.thank_you_message));
            }
            builder.setPositiveButton("OK", (dialog, which) -> {
                dialog.dismiss();
                finish();
            });
            builder.show();
        });
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