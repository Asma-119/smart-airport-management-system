package org.example;

public class Flight {
    private String flightId, source, destination, date, time, status, flightType;
    private Boolean priority;
    private Aircraft aircraft;

    public Flight(String flightId, String source, String destination, String date,
                  String time, String status, Boolean priority, Aircraft aircraft) {
        this(flightId, source, destination, date, time, status, priority, aircraft, "Departure");
    }

    public Flight(String flightId, String source, String destination, String date,
                  String time, String status, Boolean priority, Aircraft aircraft, String flightType) {
        this.flightId = flightId;
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.time = time;
        this.status = status;
        this.priority = priority;
        this.aircraft = aircraft;
        this.flightType = (flightType == null || flightType.trim().isEmpty()) ? "Departure" : flightType.trim();
    }
    public String getFlightId() { return flightId; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
    public Boolean getPriority() { return priority; }
    public String getFlightType() { return flightType; }
    public Aircraft getAircraft() { return aircraft; }
    public void setStatus(String status) { this.status = status; }
    public void setPriority(Boolean priority) { this.priority = priority; }
}
