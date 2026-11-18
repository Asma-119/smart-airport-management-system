package org.example;

public class Aircraft {
    private String aircraftId;
    private String model;
    private int capacity;

    public Aircraft(String aircraftId, String model, int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive.");
        this.aircraftId = aircraftId;
        this.model = model;
        this.capacity = capacity;
    }
    public String getAircraftId() { return aircraftId; }
    public String getModel() { return model; }
    public int getCapacity() { return capacity; }
}
