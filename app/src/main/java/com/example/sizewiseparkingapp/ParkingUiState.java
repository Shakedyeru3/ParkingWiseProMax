package com.example.sizewiseparkingapp;

import java.util.List;

public class ParkingUiState {
    private final List<ParkingSpot> parkingSpots;
    private final boolean isLoading;
    private final String error;

    public ParkingUiState(List<ParkingSpot> parkingSpots, boolean isLoading, String error) {
        this.parkingSpots = parkingSpots;
        this.isLoading = isLoading;
        this.error = error;
    }

    public List<ParkingSpot> getParkingSpots() {
        return parkingSpots;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public String getError() {
        return error;
    }
}
