package org.example;

import java.io.*;
import java.util.*;

public class SeatManager {
    private final Map<String, Seat> seats = new LinkedHashMap<>();
    private final String fileName = "seats.txt";

    public SeatManager() { load(); }

    private String id(String flightId, String seatNo) {
        return FlightManager.key(flightId) + "|" + FlightManager.key(seatNo);
    }
    public void generateSeats(Flight flight) {
        if (flight == null) { System.out.println("Flight not found."); return; }
        String fid = flight.getFlightId();
        int capacity = flight.getAircraft().getCapacity();
        int created = 0;
        for (int i = 1; i <= capacity; i++) {
            String row = String.valueOf((i - 1) / 6 + 1);
            char col = "ABCDEF".charAt((i - 1) % 6);
            String seatNo = row + col;
            String classType = i <= Math.max(1, capacity / 10) ? "Business" : "Economy";
            String k = id(fid, seatNo);
            if (!seats.containsKey(k)) {
                seats.put(k, new Seat(fid, seatNo, classType, "Available"));
                created++;
            }
        }
        save();
        System.out.println(created == 0 ? "Seats already generated for " + fid + "." :
                created + " seats generated for " + fid + ".");
    }
    public Seat searchSeat(String flightId, String seatNo) { return seats.get(id(flightId, seatNo)); }
    public List<Seat> getSeatsForFlight(String flightId) {
        List<Seat> out = new ArrayList<>();
        for (Seat s : seats.values()) if (s.getFlightId().equalsIgnoreCase(flightId)) out.add(s);
        return out;
    }
    public void viewSeats(String flightId) {
        List<Seat> list = getSeatsForFlight(flightId);
        if (list.isEmpty()) { System.out.println("No seats generated for " + flightId + "."); return; }
        for (Seat s : list) System.out.println(s.getFlightId()+" | "+s.getSeatNumber()+" | "+s.getClassType()+" | "+s.getStatus());
    }
    public boolean reserveSeat(String flightId, String seatNo) {
        Seat s = searchSeat(flightId, seatNo);
        if (s == null) { System.out.println("Seat not found for this flight."); return false; }
        if (!s.getStatus().equalsIgnoreCase("Available")) { System.out.println("Seat is not available."); return false; }
        s.setStatus("Reserved"); save(); return true;
    }
    public void releaseSeat(String flightId, String seatNo) {
        Seat s = searchSeat(flightId, seatNo);
        if (s != null) { s.setStatus("Available"); save(); }
    }
    private void save() {
        try (PrintWriter w = new PrintWriter(new FileWriter(fileName))) {
            for (Seat s : seats.values())
                w.println(FlightManager.enc(s.getFlightId())+","+FlightManager.enc(s.getSeatNumber())+","+
                        FlightManager.enc(s.getClassType())+","+FlightManager.enc(s.getStatus()));
        } catch (IOException e) { System.out.println("Could not save seats."); }
    }
    private void load() {
        File f = new File(fileName); if (!f.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] d = line.split(",", -1); if (d.length != 4) continue;
                try {
                    Seat s = new Seat(FlightManager.dec(d[0]), FlightManager.dec(d[1]),
                            FlightManager.dec(d[2]), FlightManager.dec(d[3]));
                    seats.putIfAbsent(id(s.getFlightId(), s.getSeatNumber()), s);
                } catch (RuntimeException ignored) { }
            }
        } catch (IOException e) { System.out.println("Could not load seats."); }
    }
}
