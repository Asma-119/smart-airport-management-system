package org.example;

public class Ticket {
    private String ticketId, status;
    private Passenger passenger;
    private Flight flight;
    private Seat seat;
    private double price;

    public Ticket(String ticketId, Passenger passenger, Flight flight,
                  Seat seat, double price, String status) {
        this.ticketId = ticketId; this.passenger = passenger; this.flight = flight;
        this.seat = seat; this.price = price; this.status = status;
    }
    public String getTicketId() { return ticketId; }
    public Passenger getPassenger() { return passenger; }
    public Flight getFlight() { return flight; }
    public Seat getSeat() { return seat; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
