package org.example;

import java.io.*;
import java.util.*;

public class RunwayManager {
    private final Map<String, Runway> runways = new LinkedHashMap<>();
    private final String fileName = "runways.txt";
    public RunwayManager() { load(); }

    public void addRunway(Runway r) {
        if (runways.containsKey(FlightManager.key(r.getRunwayId()))) { System.out.println("Runway already exists."); return; }
        runways.put(FlightManager.key(r.getRunwayId()), r); save();
    }
    public Runway assignFreeRunway(Flight f) {
        if (f == null) return null;
        for (Runway r : runways.values()) if (r.getStatus().equalsIgnoreCase("Available")) {
            r.setStatus("Occupied"); r.setAssignedFlightId(f.getFlightId()); save();
            System.out.println("Runway " + r.getRunwayId() + " assigned to " + f.getFlightId());
            return r;
        }
        System.out.println("No free runway available."); return null;
    }
    public void releaseRunwayForFlight(String flightId) {
        for (Runway r : runways.values()) if (r.getAssignedFlightId().equalsIgnoreCase(flightId)) {
            r.setStatus("Available"); r.setAssignedFlightId("None"); save();
        }
    }
    public void viewRunways() {
        if (runways.isEmpty()) System.out.println("No runways registered.");
        for (Runway r : runways.values()) System.out.println(r.getRunwayId()+" | "+r.getStatus()+" | "+r.getAssignedFlightId());
    }
    private void save() {
        try (PrintWriter w = new PrintWriter(new FileWriter(fileName))) {
            for (Runway r : runways.values()) w.println(FlightManager.enc(r.getRunwayId())+","+FlightManager.enc(r.getStatus())+","+FlightManager.enc(r.getAssignedFlightId()));
        } catch (IOException e) { System.out.println("Could not save runways."); }
    }
    private void load() {
        File f = new File(fileName); if (!f.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String line; while ((line = r.readLine()) != null) {
                String[] d = line.split(",", -1); if (d.length != 3) continue;
                try { Runway rw = new Runway(FlightManager.dec(d[0]), FlightManager.dec(d[1]), FlightManager.dec(d[2]));
                    runways.putIfAbsent(FlightManager.key(rw.getRunwayId()), rw);
                } catch (RuntimeException ignored) { }
            }
        } catch (IOException e) { System.out.println("Could not load runways."); }
    }
    public java.util.Collection<Runway> getAllRunways() {
        return java.util.Collections.unmodifiableCollection(
                runways.values()
        );
    }
}
