package org.example;

import java.io.*;
import java.util.*;

public class GateManager {
    private final Map<String, Gate> gates = new LinkedHashMap<>();
    private final String fileName = "gates.txt";
    public GateManager() { load(); }

    public void addGate(Gate g) {
        if (gates.containsKey(FlightManager.key(g.getGateId()))) { System.out.println("Gate already exists."); return; }
        gates.put(FlightManager.key(g.getGateId()), g); save();
    }

    public boolean hasAvailableGate() {
        for (Gate g : gates.values()) {
            if (g.getStatus().equalsIgnoreCase("Available")) return true;
        }
        return false;
    }

    public Gate getGateAssignedToFlight(String flightId) {
        for (Gate g : gates.values()) {
            if (g.getAssignedFlightId() != null
                    && g.getAssignedFlightId().equalsIgnoreCase(flightId)
                    && g.getStatus().equalsIgnoreCase("Assigned")) {
                return g;
            }
        }
        return null;
    }

    public Gate assignFreeGate(Flight f) {
        if (f == null) return null;
        for (Gate g : gates.values()) {
            if (g.getStatus().equalsIgnoreCase("Available")) {
                g.setStatus("Assigned"); g.setAssignedFlightId(f.getFlightId()); save();
                System.out.println("Gate " + g.getGateId() + " assigned to " + f.getFlightId());
                return g;
            }
        }
        System.out.println("No free gate available."); return null;
    }
    public void releaseGateForFlight(String flightId) {
        for (Gate g : gates.values()) if (g.getAssignedFlightId().equalsIgnoreCase(flightId)) {
            g.setStatus("Available"); g.setAssignedFlightId("None"); save();
        }
    }
    public void viewGates() {
        if (gates.isEmpty()) System.out.println("No gates registered.");
        for (Gate g : gates.values()) System.out.println(g.getGateId()+" | "+g.getTerminal()+" | "+g.getStatus()+" | "+g.getAssignedFlightId());
    }
    private void save() {
        try (PrintWriter w = new PrintWriter(new FileWriter(fileName))) {
            for (Gate g : gates.values()) w.println(FlightManager.enc(g.getGateId())+","+FlightManager.enc(g.getTerminal())+","+
                    FlightManager.enc(g.getStatus())+","+FlightManager.enc(g.getAssignedFlightId()));
        } catch (IOException e) { System.out.println("Could not save gates."); }
    }
    private void load() {
        File f = new File(fileName); if (!f.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String line; while ((line = r.readLine()) != null) {
                String[] d = line.split(",", -1); if (d.length != 4) continue;
                try { Gate g = new Gate(FlightManager.dec(d[0]), FlightManager.dec(d[1]), FlightManager.dec(d[2]), FlightManager.dec(d[3]));
                    gates.putIfAbsent(FlightManager.key(g.getGateId()), g);
                } catch (RuntimeException ignored) { }
            }
        } catch (IOException e) { System.out.println("Could not load gates."); }
    }

    public java.util.Collection<Gate> getAllGates() {
        return java.util.Collections.unmodifiableCollection(
                gates.values()
        );
    }
}
