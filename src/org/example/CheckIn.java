package org.example;

public class CheckIn {
    private String checkInId, checkInTime, status;
    private Passenger passenger;
    private Flight flight;

    public CheckIn(String checkInId, Passenger passenger, Flight flight,
                   String checkInTime, String status) {
        this.checkInId = checkInId; this.passenger = passenger; this.flight = flight;
        this.checkInTime = checkInTime; this.status = status;
    }
    public String getCheckInId() { return checkInId; }
    public Passenger getPassenger() { return passenger; }
    public Flight getFlight() { return flight; }
    public String getCheckInTime() { return checkInTime; }
    public String getStatus() { return status; }
}
