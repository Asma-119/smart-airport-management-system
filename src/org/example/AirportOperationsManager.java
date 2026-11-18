package org.example;

public class AirportOperationsManager {
    private final FlightManager flightManager = new FlightManager();
    private final SeatManager seatManager = new SeatManager();
    private final BookingManager bookingManager = new BookingManager(flightManager, seatManager);
    private final GateManager gateManager = new GateManager();
    private final RunwayManager runwayManager = new RunwayManager();
    private final CheckInManager checkInManager = new CheckInManager(bookingManager);
    private final FlightOperationManager operationManager = new FlightOperationManager();

    public AirportOperationsManager() {
        // Restore gate assignments for existing scheduled/delayed flights
        // after managers load their saved data.
        for (Flight flight : flightManager.getAllFlights()) {
            if (flight.getStatus().equalsIgnoreCase("Scheduled")
                    || flight.getStatus().equalsIgnoreCase("Delayed")) {
                if (gateManager.getGateAssignedToFlight(flight.getFlightId()) == null) {
                    gateManager.assignFreeGate(flight);
                }
            }
        }
    }

    public FlightManager flights() { return flightManager; }
    public SeatManager seats() { return seatManager; }
    public BookingManager bookings() { return bookingManager; }
    public GateManager gates() { return gateManager; }
    public RunwayManager runways() { return runwayManager; }
    public CheckInManager checkIns() { return checkInManager; }
    public FlightOperationManager operations() { return operationManager; }

    public void assignMissingGatesToExistingFlights() {
        for (Flight flight : flightManager.getAllFlights()) {
            if (flight.getStatus().equalsIgnoreCase("Scheduled")
                    || flight.getStatus().equalsIgnoreCase("Delayed")) {
                if (gateManager.getGateAssignedToFlight(flight.getFlightId()) == null) {
                    gateManager.assignFreeGate(flight);
                }
            }
        }
    }

    public boolean registerFlight(Flight flight) {
        if (flight == null) return false;
        if (!gateManager.hasAvailableGate()) {
            System.out.println("No available gate. Add or free a gate before registering this flight.");
            return false;
        }
        if (!flightManager.addFlight(flight)) return false;

        Gate assignedGate = gateManager.assignFreeGate(flight);
        if (assignedGate == null) {
            // Defensive check: do not leave a registered flight without a gate.
            flightManager.updateFlightStatus(flight.getFlightId(), "Gate Pending");
            return false;
        }

        seatManager.generateSeats(flight);
        System.out.println("Flight " + flight.getFlightId()
                + " registered with gate " + assignedGate.getGateId() + ".");
        return true;
    }

    /** Starts a departure and keeps the runway occupied until completion. */
    public boolean beginDeparture(String flightId, boolean emergency) {
        Flight requested = flightManager.searchFlight(flightId);
        if (requested == null) {
            System.out.println("Flight not found.");
            return false;
        }

        String status = requested.getStatus();
        if (!(status.equalsIgnoreCase("Scheduled") || status.equalsIgnoreCase("Delayed"))) {
            System.out.println("Only Scheduled or Delayed flights can begin departure.");
            return false;
        }

        Gate gate = gateManager.getGateAssignedToFlight(requested.getFlightId());
        if (gate == null) {
            gate = gateManager.assignFreeGate(requested);
            if (gate == null) {
                System.out.println("No available gate for this flight.");
                return false;
            }
        }

        Runway runway = runwayManager.assignFreeRunway(requested);
        if (runway == null) {
            System.out.println("No available runway. Flight remains scheduled and keeps its gate.");
            return false;
        }

        if (!flightManager.updateFlightStatus(requested.getFlightId(), "Departing")) {
            runwayManager.releaseRunwayForFlight(requested.getFlightId());
            return false;
        }

        System.out.println("Flight " + requested.getFlightId() + " is departing via runway "
                + runway.getRunwayId() + ". Runway remains occupied until completion.");
        return true;
    }

    /** Completes a previously started departure and releases its resources. */
    public boolean completeDeparture(String flightId) {
        Flight flight = flightManager.searchFlight(flightId);
        if (flight == null) {
            System.out.println("Flight not found.");
            return false;
        }
        if (!flight.getStatus().equalsIgnoreCase("Departing")) {
            System.out.println("This flight is not currently departing.");
            return false;
        }

        boolean hasRunway = false;
        for (Runway runway : runwayManager.getAllRunways()) {
            if (runway.getAssignedFlightId().equalsIgnoreCase(flightId)
                    && runway.getStatus().equalsIgnoreCase("Occupied")) {
                hasRunway = true;
                break;
            }
        }
        if (!hasRunway) {
            System.out.println("No occupied runway is assigned to this flight.");
            return false;
        }

        if (!flightManager.updateFlightStatus(flightId, "Departed")) return false;
        runwayManager.releaseRunwayForFlight(flightId);
        gateManager.releaseGateForFlight(flightId);
        System.out.println("Departure completed for " + flightId + ". Runway and gate released.");
        return true;
    }


    /** Starts an arrival and occupies a runway during the landing phase. */
    public boolean beginArrival(String flightId) {
        Flight flight = flightManager.searchFlight(flightId);
        if (flight == null) {
            System.out.println("Flight not found.");
            return false;
        }
        if (!flight.getFlightType().equalsIgnoreCase("Arrival")) {
            System.out.println("This flight is registered as a departure.");
            return false;
        }
        if (!(flight.getStatus().equalsIgnoreCase("Scheduled") || flight.getStatus().equalsIgnoreCase("Delayed"))) {
            System.out.println("Only Scheduled or Delayed arrivals can begin landing.");
            return false;
        }
        Gate gate = gateManager.getGateAssignedToFlight(flight.getFlightId());
        if (gate == null) {
            gate = gateManager.assignFreeGate(flight);
            if (gate == null) {
                System.out.println("No available gate for this arriving flight.");
                return false;
            }
        }
        Runway runway = runwayManager.assignFreeRunway(flight);
        if (runway == null) {
            System.out.println("No available runway. Arrival remains scheduled.");
            return false;
        }
        if (!flightManager.updateFlightStatus(flightId, "Landing")) {
            runwayManager.releaseRunwayForFlight(flightId);
            return false;
        }
        System.out.println("Flight " + flightId + " is landing on runway " + runway.getRunwayId() + ".");
        return true;
    }

    /** Finishes landing, frees the runway, and keeps the gate assigned to the aircraft. */
    public boolean completeArrival(String flightId) {
        Flight flight = flightManager.searchFlight(flightId);
        if (flight == null || !flight.getFlightType().equalsIgnoreCase("Arrival")) {
            System.out.println("Arrival flight not found.");
            return false;
        }
        if (!flight.getStatus().equalsIgnoreCase("Landing")) {
            System.out.println("This flight is not currently landing.");
            return false;
        }
        boolean hasRunway = false;
        for (Runway runway : runwayManager.getAllRunways()) {
            if (runway.getAssignedFlightId().equalsIgnoreCase(flightId)
                    && runway.getStatus().equalsIgnoreCase("Occupied")) {
                hasRunway = true;
                break;
            }
        }
        if (!hasRunway) {
            System.out.println("No occupied runway is assigned to this arrival.");
            return false;
        }
        if (!flightManager.updateFlightStatus(flightId, "Landed")) return false;
        runwayManager.releaseRunwayForFlight(flightId);
        System.out.println("Arrival completed for " + flightId + ". Runway released; gate remains assigned.");
        return true;
    }

    /** Backward-compatible method: now starts, but does not instantly complete, departure. */
    public boolean processNextFlight(String flightId, boolean emergency) {
        return beginDeparture(flightId, emergency);
    }
}
