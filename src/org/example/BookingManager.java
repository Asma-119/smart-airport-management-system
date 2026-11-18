package org.example;

import java.io.*;
import java.util.*;

public class BookingManager {
    private final Map<String, Ticket> tickets = new LinkedHashMap<>();
    private final String fileName = "tickets.txt";
    private final FlightManager flights;
    private final SeatManager seats;

    public BookingManager(FlightManager flights, SeatManager seats) {
        this.flights = flights; this.seats = seats; loadTickets();
    }
    public boolean bookTicket(String ticketId, Passenger passenger, String flightId,
                              String seatNo, double price) {
        if (tickets.containsKey(FlightManager.key(ticketId))) {
            System.out.println("Ticket ID already exists."); return false;
        }
        Flight flight = flights.searchFlight(flightId);
        if (flight == null) { System.out.println("Flight not found."); return false; }
        if (!flight.getStatus().equalsIgnoreCase("Scheduled")) {
            System.out.println("Only scheduled flights can be booked."); return false;
        }
        if (passenger == null || price < 0) { System.out.println("Invalid passenger or price."); return false; }
        if (!seats.reserveSeat(flightId, seatNo)) return false;
        Seat seat = seats.searchSeat(flightId, seatNo);
        Ticket t = new Ticket(ticketId, passenger, flight, seat, price, "Booked");
        tickets.put(FlightManager.key(ticketId), t);
        saveTickets();
        System.out.println("Ticket booked successfully.");
        return true;
    }
    public Ticket searchTicket(String id) { return tickets.get(FlightManager.key(id)); }

    /** Generates the next passenger ID from the saved ticket records. */
    public int nextPassengerId() {
        int max = 1000;
        for (Ticket t : tickets.values()) max = Math.max(max, t.getPassenger().getId());
        return max + 1;
    }

    /** Generates a unique, readable ticket ID. */
    public String nextTicketId() {
        int max = 0;
        for (Ticket t : tickets.values()) {
            String id = t.getTicketId().toUpperCase(Locale.ROOT);
            if (id.startsWith("TKT")) {
                try { max = Math.max(max, Integer.parseInt(id.substring(3))); }
                catch (NumberFormatException ignored) { }
            }
        }
        return String.format(Locale.ROOT, "TKT%04d", max + 1);
    }

    /** Sample airport fare schedule in BDT; the fare depends on the selected seat class. */
    public double calculateFare(Seat seat) {
        if (seat == null) throw new IllegalArgumentException("Choose a seat first.");
        return seat.getClassType().equalsIgnoreCase("Business") ? 10000.0 : 5000.0;
    }
    public void viewTickets() {
        if (tickets.isEmpty()) { System.out.println("No tickets."); return; }
        for (Ticket t : tickets.values())
            System.out.println(t.getTicketId()+" | "+t.getPassenger().getName()+" | "+
                    t.getFlight().getFlightId()+" | "+t.getSeat().getSeatNumber()+" | "+
                    t.getPrice()+" | "+t.getStatus());
    }
    public boolean cancelTicket(String id) {
        Ticket t = searchTicket(id);
        if (t == null) { System.out.println("Ticket not found."); return false; }
        if (t.getStatus().equalsIgnoreCase("Cancelled")) { System.out.println("Already cancelled."); return false; }
        t.setStatus("Cancelled");
        seats.releaseSeat(t.getFlight().getFlightId(), t.getSeat().getSeatNumber());
        saveTickets();
        System.out.println("Ticket cancelled; seat released.");
        return true;
    }
    public Collection<Ticket> getTickets() { return Collections.unmodifiableCollection(tickets.values()); }
    private void saveTickets() {
        try (PrintWriter w = new PrintWriter(new FileWriter(fileName))) {
            for (Ticket t : tickets.values()) {
                Passenger p = t.getPassenger(); Flight f = t.getFlight(); Seat s = t.getSeat();
                w.println(FlightManager.enc(t.getTicketId())+","+p.getId()+","+FlightManager.enc(p.getName())+","+
                        FlightManager.enc(p.getPhone())+","+FlightManager.enc(p.getEmail())+","+
                        FlightManager.enc(p.getPassportNo())+","+FlightManager.enc(p.getNationality())+","+
                        FlightManager.enc(f.getFlightId())+","+FlightManager.enc(s.getSeatNumber())+","+
                        t.getPrice()+","+FlightManager.enc(t.getStatus()));
            }
        } catch (IOException e) { System.out.println("Could not save tickets."); }
    }
    private void loadTickets() {
        File file = new File(fileName); if (!file.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] d = line.split(",", -1); if (d.length != 11) continue;
                try {
                    Flight f = flights.searchFlight(FlightManager.dec(d[7]));
                    if (f == null) continue;
                    String seatNo = FlightManager.dec(d[8]);
                    Seat s = seats.searchSeat(f.getFlightId(), seatNo);
                    if (s == null) continue;
                    Passenger p = new Passenger(Integer.parseInt(d[1]), FlightManager.dec(d[2]),
                            FlightManager.dec(d[3]), FlightManager.dec(d[4]),
                            FlightManager.dec(d[5]), FlightManager.dec(d[6]));
                    Ticket t = new Ticket(FlightManager.dec(d[0]), p, f, s, Double.parseDouble(d[9]), FlightManager.dec(d[10]));
                    tickets.putIfAbsent(FlightManager.key(t.getTicketId()), t);
                    if (t.getStatus().equalsIgnoreCase("Booked")) s.setStatus("Reserved");
                } catch (RuntimeException ignored) { }
            }
        } catch (IOException e) { System.out.println("Could not load tickets."); }
    }
}
