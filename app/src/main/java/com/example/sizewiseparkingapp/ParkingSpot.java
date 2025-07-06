package com.example.sizewiseparkingapp;

import com.google.firebase.firestore.PropertyName;

public class ParkingSpot {
    private int id;
    private String street;
    private String time;
    private String imageUrl;
    private String description;
    private boolean isExpanded;

    public ParkingSpot(int id, String street, String time, String imageUrl, String description) {
        this.id = id;
        this.street = street;
        this.time = time;
        this.imageUrl = imageUrl;
        this.description = description;
        this.isExpanded = false;
    }

    // קונסטרקטור ריק נדרש ל-Firestore
    public ParkingSpot() {}

    public int getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public String getTime() {
        return time;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
