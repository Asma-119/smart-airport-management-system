package org.example;

public class Seat {
    private String flightId, seatNumber, classType, status;

    public Seat(String flightId, String seatNumber, String classType, String status) {
        this.flightId = flightId; this.seatNumber = seatNumber;
        this.classType = classType; this.status = status;
    }
    public String getFlightId() { return flightId; }
    public String getSeatNumber() { return seatNumber; }
    public String getClassType() { return classType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
