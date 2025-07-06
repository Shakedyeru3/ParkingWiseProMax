package com.example.sizewiseparkingapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sizewiseparkingapp.R;
import com.example.sizewiseparkingapp.ui.components.ParkingSpotsAdapter;
import com.example.sizewiseparkingapp.ParkingViewModel;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import android.widget.TextView;
import android.content.Intent;

import java.util.Objects;

public class MapFragment extends Fragment {
    private RecyclerView parkingSpotsRecyclerView;
    private ParkingSpotsAdapter adapter;
    private ParkingViewModel viewModel;
    private EditText addressInput;
    private Button myLocationButton;
    private FirebaseFirestore db;
    private String userEmail;
    private TextView userLocationText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ParkingViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        initializeViews(view);
        db = FirebaseFirestore.getInstance();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
        if (account != null && account.getEmail() != null) {
            userEmail = account.getEmail();
            fetchAndDisplayUserLocation();
        } else {
            userEmail = null;
        }
        // Back button logic
        View backButton = view.findViewById(R.id.back_button);
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), com.example.sizewiseparkingapp.MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                requireActivity().finish();
            });
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewModel();
    }

    private void initializeViews(View view) {
        parkingSpotsRecyclerView = view.findViewById(R.id.parkingSpotsRecyclerView);
        parkingSpotsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addressInput = view.findViewById(R.id.addressInput);
        myLocationButton = view.findViewById(R.id.buttonloc);
        myLocationButton.setOnClickListener(v -> saveUserLocation());
        userLocationText = view.findViewById(R.id.userLocationText);
    }

    private void setupViewModel() {
        // הגנה על ערך null
        if (viewModel.getUiState().getValue() != null) {
            adapter = new ParkingSpotsAdapter(requireContext(), viewModel.getUiState().getValue().getParkingSpots());
        } else {
            adapter = new ParkingSpotsAdapter(requireContext(), new java.util.ArrayList<>());
        }
        parkingSpotsRecyclerView.setAdapter(adapter);

        viewModel.getUiState().observe(getViewLifecycleOwner(), uiState -> {
            if (uiState != null) {
                adapter.updateData(uiState.getParkingSpots());
            }
        });
    }

    private void fetchAndDisplayUserLocation() {
        if (userEmail == null) return;
        db.collection("Location").document(userEmail).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("location")) {
                String location = documentSnapshot.getString("location");
                if (location != null && !location.isEmpty()) {
                    addressInput.setText(location);
                    // השדה תמיד ניתן לעריכה
                    addressInput.setEnabled(true);
                    if (userLocationText != null) userLocationText.setText("Your location: " + location);
                } else {
                    addressInput.setText("");
                    addressInput.setEnabled(true);
                    if (userLocationText != null) userLocationText.setText("");
                }
            } else {
                addressInput.setText("");
                addressInput.setEnabled(true);
                if (userLocationText != null) userLocationText.setText("");
            }
        });
    }

    private void saveUserLocation() {
        if (userEmail == null) {
            Toast.makeText(getContext(), "User not signed in", Toast.LENGTH_SHORT).show();
            return;
        }
        String location = addressInput.getText() != null ? addressInput.getText().toString().trim() : "";
        if (location.isEmpty()) {
            Toast.makeText(getContext(), "Please enter your location", Toast.LENGTH_SHORT).show();
            return;
        }
        // Add ', Israel' automatically if not present
        String finalLocation;
        if (!location.toLowerCase().endsWith("israel")) {
            finalLocation = location + ", Israel";
        } else {
            finalLocation = location;
        }
        db.collection("Location").document(userEmail)
            .set(new java.util.HashMap<String, Object>() {{
                put("email", userEmail);
                put("location", finalLocation);
            }})
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(getContext(), "Location saved!", Toast.LENGTH_SHORT).show();
                // השדה תמיד ניתן לעריכה
                addressInput.setEnabled(true);
                if (userLocationText != null) userLocationText.setText("Your location: " + finalLocation);
            })
            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save location", Toast.LENGTH_SHORT).show());
    }
} 