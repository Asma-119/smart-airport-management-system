package org.example;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class CheckInManager {
    private final Map<String, CheckIn> records = new LinkedHashMap<>();
    private final String fileName = "checkins.txt";
    private final BookingManager bookings;

    public CheckInManager(BookingManager bookings) { this.bookings = bookings; load(); }

    public boolean checkInPassenger(String ticketId) {
        Ticket t = bookings.searchTicket(ticketId);
        if (t == null || !t.getStatus().equalsIgnoreCase("Booked")) {
            System.out.println("A valid booked ticket is required for check-in."); return false;
        }
        String id = "CI-" + t.getTicketId();
        if (records.containsKey(FlightManager.key(id))) { System.out.println("Passenger already checked in."); return false; }
        CheckIn c = new CheckIn(id, t.getPassenger(), t.getFlight(), LocalDateTime.now().toString(), "Checked In");
        records.put(FlightManager.key(id), c); save();
        System.out.println("Check-in successful for " + t.getPassenger().getName());
        return true;
    }
    public void viewCheckIns() {
        if (records.isEmpty()) System.out.println("No check-in records.");
        for (CheckIn c : records.values()) System.out.println(c.getCheckInId()+" | "+c.getPassenger().getName()+" | "+c.getFlight().getFlightId()+" | "+c.getCheckInTime()+" | "+c.getStatus());
    }
    private void save() {
        try (PrintWriter w = new PrintWriter(new FileWriter(fileName))) {
            for (CheckIn c : records.values()) w.println(FlightManager.enc(c.getCheckInId())+","+c.getPassenger().getId()+","+
                    FlightManager.enc(c.getPassenger().getName())+","+FlightManager.enc(c.getFlight().getFlightId())+","+
                    FlightManager.enc(c.getCheckInTime())+","+FlightManager.enc(c.getStatus()));
        } catch (IOException e) { System.out.println("Could not save check-ins."); }
    }
    private void load() {
        File f = new File(fileName); if (!f.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String line; while ((line = r.readLine()) != null) {
                String[] d = line.split(",", -1); if (d.length != 6) continue;
                try {
                    Ticket t = null;
                    for (Ticket candidate : bookings.getTickets())
                        if (candidate.getPassenger().getId() == Integer.parseInt(d[1]) &&
                                candidate.getFlight().getFlightId().equalsIgnoreCase(FlightManager.dec(d[3]))) { t = candidate; break; }
                    if (t != null) {
                        CheckIn c = new CheckIn(FlightManager.dec(d[0]), t.getPassenger(), t.getFlight(),
                                FlightManager.dec(d[4]), FlightManager.dec(d[5]));
                        records.putIfAbsent(FlightManager.key(c.getCheckInId()), c);
                    }
                } catch (RuntimeException ignored) { }
            }
        } catch (IOException e) { System.out.println("Could not load check-ins."); }
    }
}
