package com.example.sizewiseparkingapp.ui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sizewiseparkingapp.ParkingSpot;
import com.example.sizewiseparkingapp.R;
import com.google.android.material.button.MaterialButton;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class ParkingSpotsAdapter extends RecyclerView.Adapter<ParkingSpotsAdapter.ViewHolder> {
    private List<ParkingSpot> parkingSpots;
    private final Context context;

    public ParkingSpotsAdapter(Context context, List<ParkingSpot> parkingSpots) {
        this.context = context;
        this.parkingSpots = parkingSpots != null ? parkingSpots : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_parking_spot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParkingSpot spot = parkingSpots.get(position);
        holder.bind(spot);

        String imageUrl = spot.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.parkingImage.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_parking)
                .into(holder.parkingImage);
        } else {
            Glide.with(holder.parkingImage.getContext())
                .load(R.drawable.ic_parking)
                .into(holder.parkingImage);
        }

        holder.streetText.setText(spot.getStreet() != null && !spot.getStreet().isEmpty() ? spot.getStreet() : "No Street Name");

        holder.mainContent.setOnClickListener(v -> {
            spot.setExpanded(!spot.isExpanded());
            notifyItemChanged(position);
        });

        holder.navigateButton.setOnClickListener(v -> {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
            if (account != null && account.getEmail() != null) {
                String userEmail = account.getEmail();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("userCredits").document(userEmail).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long currentCredits = documentSnapshot.getLong("credits");
                        if (currentCredits != null && currentCredits >= 10) {
                            db.collection("userCredits").document(userEmail).update("credits", currentCredits - 10)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "10 credits deducted for navigation", Toast.LENGTH_SHORT).show();
                                    fetchUserLocationAndNavigate(context, userEmail, spot.getStreet());
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to update credits", Toast.LENGTH_SHORT).show();
                                });
                        } else {
                            Toast.makeText(context, "Not enough credits", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(context, "User not signed in", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return parkingSpots.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<ParkingSpot> newSpots) {
        this.parkingSpots = newSpots;
        notifyDataSetChanged();
    }

    private void startNavigationToLocation(Context context, String address) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        } else {
            Toast.makeText(context, "Google Maps is not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUserLocationAndNavigate(Context context, String userEmail, String destination) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Location").document(userEmail).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("location")) {
                String origin = documentSnapshot.getString("location");
                if (origin != null && !origin.isEmpty()) {
                    startNavigationFromTo(context, origin, destination);
                } else {
                    Toast.makeText(context, "No saved location found. Please set your location first.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "No saved location found. Please set your location first.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startNavigationFromTo(Context context, String origin, String destination) {
        // ודא שכל כתובת מסתיימת ב-, Israel
        String finalOrigin = origin.trim();
        String finalDestination = destination.trim();
        if (!finalOrigin.toLowerCase().endsWith("israel")) {
            finalOrigin = finalOrigin + ", Israel";
        }
        if (!finalDestination.toLowerCase().endsWith("israel")) {
            finalDestination = finalDestination + ", Israel";
        }
        Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" + Uri.encode(finalOrigin) + "&destination=" + Uri.encode(finalDestination) + "&travelmode=driving");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        } else {
            Toast.makeText(context, "Google Maps is not installed", Toast.LENGTH_SHORT).show();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View mainContent;
        View expandableContent;
        ImageView parkingImage;
        TextView streetText;
        TextView timeText;
        TextView descriptionText;
        MaterialButton navigateButton;

        ViewHolder(View itemView) {
            super(itemView);
            mainContent = itemView.findViewById(R.id.main_content);
            expandableContent = itemView.findViewById(R.id.expandable_content);
            parkingImage = itemView.findViewById(R.id.parking_image);
            streetText = itemView.findViewById(R.id.parking_street);
            timeText = itemView.findViewById(R.id.parking_time);
            descriptionText = itemView.findViewById(R.id.parking_description);
            navigateButton = itemView.findViewById(R.id.navigate_button);
        }

        void bind(ParkingSpot spot) {
            streetText.setText(spot.getStreet());
            timeText.setText(spot.getTime());
            descriptionText.setText(spot.getDescription());
            expandableContent.setVisibility(spot.isExpanded() ? View.VISIBLE : View.GONE);
        }
    }
}
