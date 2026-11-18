package org.example;

import java.io.*;
import java.util.*;

public class FlightManager {
    private final Map<String, Flight> flights = new LinkedHashMap<>();
    private final String fileName = "flights.txt";

    public FlightManager() { loadFlights(); }

    public boolean addFlight(Flight f) {
        if (f == null || flights.containsKey(key(f.getFlightId()))) {
            System.out.println("Invalid flight or flight ID already exists.");
            return false;
        }
        flights.put(key(f.getFlightId()), f);
        saveFlights();
        System.out.println("Flight added.");
        return true;
    }
    public Flight searchFlight(String id) { return flights.get(key(id)); }

    public ArrayList<Flight> searchFlight(String destination, String date) {
        ArrayList<Flight> out = new ArrayList<>();
        for (Flight f : flights.values()) {
            if (f.getFlightType().equalsIgnoreCase("Departure")
                    && f.getDestination().equalsIgnoreCase(destination)
                    && f.getDate().equalsIgnoreCase(date)
                    && f.getStatus().equalsIgnoreCase("Scheduled")) out.add(f);
        }
        return out;
    }
    public Collection<Flight> getAllFlights() { return Collections.unmodifiableCollection(flights.values()); }

    public void viewFlights() {
        if (flights.isEmpty()) { System.out.println("No flights."); return; }
        for (Flight f : flights.values())
            System.out.printf("%s | %s -> %s | %s %s | %s | Priority: %s | Aircraft: %s%n",
                    f.getFlightId(), f.getSource(), f.getDestination(), f.getDate(),
                    f.getTime(), f.getStatus(), f.getPriority(), f.getAircraft().getAircraftId());
    }
    public boolean updateFlightStatus(String id, String status) {
        Flight f = searchFlight(id);
        if (f == null) { System.out.println("Flight not found."); return false; }
        f.setStatus(status); saveFlights();
        System.out.println(id + " status updated to " + status + ".");
        return true;
    }
    public void saveFlights() {
        try (PrintWriter w = new PrintWriter(new FileWriter(fileName))) {
            for (Flight f : flights.values()) {
                Aircraft a = f.getAircraft();
                w.println(enc(f.getFlightId())+","+enc(f.getSource())+","+enc(f.getDestination())+","+
                        enc(f.getDate())+","+enc(f.getTime())+","+enc(f.getStatus())+","+
                        f.getPriority()+","+enc(a.getAircraftId())+","+enc(a.getModel())+","+a.getCapacity()+","+enc(f.getFlightType()));
            }
        } catch (IOException e) { System.out.println("Could not save flights: " + e.getMessage()); }
    }
    private void loadFlights() {
        File file = new File(fileName); if (!file.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] d = line.split(",", -1);
                if (d.length != 10 && d.length != 11) continue;
                try {
                    Aircraft a = new Aircraft(dec(d[7]), dec(d[8]), Integer.parseInt(d[9]));
                    String flightType = d.length == 11 ? dec(d[10]) : "Departure";
                    Flight f = new Flight(dec(d[0]), dec(d[1]), dec(d[2]), dec(d[3]), dec(d[4]),
                            dec(d[5]), Boolean.parseBoolean(d[6]), a, flightType);
                    flights.putIfAbsent(key(f.getFlightId()), f);
                } catch (RuntimeException ignored) { }
            }
        } catch (IOException e) { System.out.println("Could not load flights."); }
    }
    static String key(String s) { return s == null ? "" : s.trim().toUpperCase(Locale.ROOT); }
    static String enc(String s) { return Base64.getEncoder().encodeToString(s.getBytes(java.nio.charset.StandardCharsets.UTF_8)); }
    static String dec(String s) { return new String(Base64.getDecoder().decode(s), java.nio.charset.StandardCharsets.UTF_8); }
}
