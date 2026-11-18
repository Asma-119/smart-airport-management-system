package org.example;

import java.util.*;

public class FlightOperationManager {
    private final Queue<Flight> priorityQueue = new LinkedList<>();
    private final Queue<Flight> regularQueue = new LinkedList<>();
    private final Set<String> queuedIds = new HashSet<>();

    public void addFlightToQueue(Flight f) {
        if (f == null) { System.out.println("Flight not found."); return; }
        if (!f.getStatus().equalsIgnoreCase("Scheduled") && !f.getStatus().equalsIgnoreCase("Delayed")) {
            System.out.println("Only scheduled or delayed flights can be queued."); return;
        }
        if (!queuedIds.add(FlightManager.key(f.getFlightId()))) { System.out.println("Flight already queued."); return; }
        if (Boolean.TRUE.equals(f.getPriority())) priorityQueue.offer(f);
        else regularQueue.offer(f);
    }
    public void addEmergencyFlight(Flight f) {
        if (f == null) { System.out.println("Flight not found."); return; }
        if (!queuedIds.add(FlightManager.key(f.getFlightId()))) { System.out.println("Flight already queued."); return; }
        priorityQueue.offer(f);
        System.out.println("Emergency priority assigned to " + f.getFlightId());
    }
    public Flight pollNextFlight() {
        Flight f = !priorityQueue.isEmpty() ? priorityQueue.poll() : regularQueue.poll();
        if (f != null) queuedIds.remove(FlightManager.key(f.getFlightId()));
        return f;
    }
    public void viewQueues() {
        System.out.println("Priority queue:"); printQueue(priorityQueue);
        System.out.println("Normal queue:"); printQueue(regularQueue);
    }
    private void printQueue(Queue<Flight> q) {
        if (q.isEmpty()) System.out.println("  (empty)");
        for (Flight f : q) System.out.println("  " + f.getFlightId() + " | " + f.getStatus());
    }
}
