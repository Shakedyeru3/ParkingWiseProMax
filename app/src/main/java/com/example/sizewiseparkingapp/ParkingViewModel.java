package com.example.sizewiseparkingapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;

public class ParkingViewModel extends ViewModel {
    private final MutableLiveData<ParkingUiState> uiState = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ParkingViewModel() {
        loadParkingSpots();
    }

    public LiveData<ParkingUiState> getUiState() {
        return uiState;
    }

    private void loadParkingSpots() {
        db.collection("parkingSpots")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ParkingSpot> spots = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        ParkingSpot spot = doc.toObject(ParkingSpot.class);
                        if (spot != null) spots.add(spot);
                    }
                    uiState.setValue(new ParkingUiState(spots, false, null));
                })
                .addOnFailureListener(e -> {
                    uiState.setValue(new ParkingUiState(new ArrayList<>(), false, e.getMessage()));
                });
    }

    public void addParkingSpot(ParkingSpot spot) {
        db.collection("parkingSpots").add(spot);
    }
}



