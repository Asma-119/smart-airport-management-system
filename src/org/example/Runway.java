package org.example;

public class Runway {
    private String runwayId, status, assignedFlightId;

    public Runway(String runwayId, String status, String assignedFlightId) {
        this.runwayId = runwayId; this.status = status; this.assignedFlightId = assignedFlightId;
    }
    public String getRunwayId() { return runwayId; }
    public String getStatus() { return status; }
    public String getAssignedFlightId() { return assignedFlightId; }
    public void setStatus(String status) { this.status = status; }
    public void setAssignedFlightId(String id) { this.assignedFlightId = id; }
}
