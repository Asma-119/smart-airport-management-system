package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AirportOperationsManager airport = new AirportOperationsManager();

        // addGate/addRunway check IDs, so starter resources are not duplicated on each run.
        airport.gates().addGate(new Gate("G01", "Terminal A", "Available", "None"));
        airport.gates().addGate(new Gate("G02", "Terminal A", "Available", "None"));
        airport.gates().addGate(new Gate("G03", "Terminal B", "Available", "None"));
        airport.runways().addRunway(new Runway("R01", "Available", "None"));
        airport.runways().addRunway(new Runway("R02", "Available", "None"));

        while (true) {
            System.out.println("\\n===== SMART AIRPORT OPERATIONS =====");
            System.out.println("1. Register flight (also generates seats)");
            System.out.println("2. View all flights");
            System.out.println("3. Search flight by ID");
            System.out.println("4. View seats for a flight");
            System.out.println("5. Book ticket");
            System.out.println("6. View tickets");
            System.out.println("7. Cancel ticket");
            System.out.println("8. Check in passenger");
            System.out.println("9. Mark flight delayed");
            System.out.println("10. Process normal/priority flight");
            System.out.println("11. Process emergency flight");
            System.out.println("12. View gates and runways");
            System.out.println("13. View check-in records");
            System.out.println("0. Exit");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1": {
                        System.out.print("Flight ID: "); String id = sc.nextLine().trim();
                        System.out.print("Source: "); String source = sc.nextLine().trim();
                        System.out.print("Destination: "); String destination = sc.nextLine().trim();
                        System.out.print("Date (YYYY-MM-DD): "); String date = sc.nextLine().trim();
                        System.out.print("Time (HH:MM): "); String time = sc.nextLine().trim();
                        System.out.print("Aircraft ID: "); String aircraftId = sc.nextLine().trim();
                        System.out.print("Aircraft model: "); String model = sc.nextLine().trim();
                        System.out.print("Aircraft capacity: "); int capacity = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Priority flight? (y/n): ");
                        boolean priority = sc.nextLine().trim().equalsIgnoreCase("y");

                        Aircraft aircraft = new Aircraft(aircraftId, model, capacity);
                        Flight flight = new Flight(id, source, destination, date, time,
                                "Scheduled", priority, aircraft);
                        airport.registerFlight(flight);
                        break;
                    }
                    case "2":
                        airport.flights().viewFlights();
                        break;
                    case "3": {
                        System.out.print("Flight ID: ");
                        Flight f = airport.flights().searchFlight(sc.nextLine().trim());
                        if (f == null) System.out.println("Flight not found.");
                        else System.out.println(f.getFlightId() + " | " + f.getSource() + " -> " +
                                f.getDestination() + " | " + f.getDate() + " " + f.getTime() +
                                " | " + f.getStatus() + " | Priority: " + f.getPriority());
                        break;
                    }
                    case "4":
                        System.out.print("Flight ID: ");
                        airport.seats().viewSeats(sc.nextLine().trim());
                        break;
                    case "5": {
                        System.out.print("Ticket ID: "); String ticketId = sc.nextLine().trim();
                        System.out.print("Passenger ID: "); int passengerId = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Passenger name: "); String name = sc.nextLine().trim();
                        System.out.print("Phone: "); String phone = sc.nextLine().trim();
                        System.out.print("Email: "); String email = sc.nextLine().trim();
                        System.out.print("Passport number: "); String passport = sc.nextLine().trim();
                        System.out.print("Nationality: "); String nationality = sc.nextLine().trim();
                        System.out.print("Flight ID: "); String flightId = sc.nextLine().trim();
                        System.out.print("Seat number (e.g. 1A): "); String seatNo = sc.nextLine().trim();
                        System.out.print("Ticket price: "); double price = Double.parseDouble(sc.nextLine().trim());

                        Passenger passenger = new Passenger(passengerId, name, phone, email, passport, nationality);
                        airport.bookings().bookTicket(ticketId, passenger, flightId, seatNo, price);
                        break;
                    }
                    case "6":
                        airport.bookings().viewTickets();
                        break;
                    case "7":
                        System.out.print("Ticket ID: ");
                        airport.bookings().cancelTicket(sc.nextLine().trim());
                        break;
                    case "8":
                        System.out.print("Ticket ID: ");
                        airport.checkIns().checkInPassenger(sc.nextLine().trim());
                        break;
                    case "9":
                        System.out.print("Flight ID: ");
                        airport.flights().updateFlightStatus(sc.nextLine().trim(), "Delayed");
                        break;
                    case "10":
                        System.out.print("Flight ID to process: ");
                        airport.processNextFlight(sc.nextLine().trim(), false);
                        break;
                    case "11":
                        System.out.print("Emergency flight ID: ");
                        airport.processNextFlight(sc.nextLine().trim(), true);
                        break;
                    case "12":
                        System.out.println("\\nGATES:");
                        airport.gates().viewGates();
                        System.out.println("\\nRUNWAYS:");
                        airport.runways().viewRunways();
                        break;
                    case "13":
                        airport.checkIns().viewCheckIns();
                        break;
                    case "0":
                        System.out.println("Exiting Airport Operations System.");
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid numeric input.");
            } catch (RuntimeException e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }
}
