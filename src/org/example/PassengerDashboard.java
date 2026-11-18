package org.example;

import java.awt.*;
import java.util.Collection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PassengerDashboard extends JFrame {
    private final AirportOperationsManager airport;

    private final DefaultTableModel flightModel = model(
            "Flight ID", "From", "To", "Date",
            "Time", "Status", "Priority"
    );

    private final JTable flightTable = new JTable(flightModel);

    private final JTextField destination = UITheme.field();
    private final JTextField date = UITheme.field();

    public PassengerDashboard(AirportOperationsManager airport) {
        this.airport = airport;

        setTitle("Passenger Portal");
        setSize(1050, 680);
        setLocationRelativeTo(null);
        UITheme.styleFrame(this);

        JPanel root = UITheme.pagePanel();

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UITheme.title("Passenger Portal"), BorderLayout.WEST);

        JButton refresh = UITheme.button("Refresh Flights");
        refresh.addActionListener(e -> refreshFlights());

        top.add(refresh, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Search & Book", buildBookingTab());
        tabs.addTab("My Booking Actions", buildActionsTab());

        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);

        refreshFlights();
    }

    private JPanel buildBookingTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG);

        JPanel search = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );
        search.setOpaque(false);

        search.add(new JLabel("Destination"));
        search.add(destination);

        search.add(new JLabel("Date (YYYY-MM-DD)"));
        search.add(date);

        JButton find = UITheme.button("Search");
        find.addActionListener(e -> searchFlights());

        JButton all = UITheme.button("Show All");
        all.addActionListener(e -> refreshFlights());

        search.add(find);
        search.add(all);

        panel.add(search, BorderLayout.NORTH);

        flightTable.setRowHeight(25);
        flightTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        panel.add(
                new JScrollPane(flightTable),
                BorderLayout.CENTER
        );

        JPanel book = new JPanel(
                new GridLayout(0, 4, 8, 8)
        );
        book.setBackground(Color.WHITE);

        JTextField name = UITheme.field();
        JTextField phone = UITheme.field();
        JTextField email = UITheme.field();
        JTextField passport = UITheme.field();
        JTextField nationality = UITheme.field();
        JTextField flightId = UITheme.field();
        JTextField seat = UITheme.field();
        JTextField price = UITheme.field();
        price.setEditable(false);
        price.setText("Select a seat to see fare");

        addField(book, "Full name", name);
        addField(book, "Phone", phone);
        addField(book, "Email", email);
        addField(book, "Passport number", passport);
        addField(book, "Nationality", nationality);
        addField(book, "Flight ID", flightId);
        addField(book, "Seat (e.g. 1A)", seat);
        addField(book, "Ticket price (BDT)", price);

        JButton selected = UITheme.button("Use Selected Flight");

        selected.addActionListener(e -> {
            int row = flightTable.getSelectedRow();
            if (row >= 0) {
                int modelRow = flightTable.convertRowIndexToModel(row);
                flightId.setText(String.valueOf(flightModel.getValueAt(modelRow, 0)));
                seat.setText("");
                price.setText("Select a seat to see fare");
            } else {
                UITheme.info(this, "Select a flight row first.");
            }
        });

        JButton chooseSeat = UITheme.button("View / Choose Seat");
        chooseSeat.addActionListener(e -> {
            String selectedFlightId = flightId.getText().trim();
            if (selectedFlightId.isEmpty()) {
                UITheme.error(this, "Select a flight and click 'Use Selected Flight' first.");
                return;
            }
            showSeatPicker(selectedFlightId, seat, price);
        });

        JButton bookButton = UITheme.button("Book Ticket");
        bookButton.addActionListener(e -> {
            try {
                if (name.getText().trim().isEmpty() || passport.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("Enter your full name and passport number.");
                }
                if (flightId.getText().trim().isEmpty() || seat.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("Select a flight and an available seat first.");
                }

                Seat chosenSeat = airport.seats().searchSeat(flightId.getText().trim(), seat.getText().trim());
                if (chosenSeat == null || !chosenSeat.getStatus().equalsIgnoreCase("Available")) {
                    throw new IllegalArgumentException("That seat is no longer available. Please choose another seat.");
                }

                double amount = airport.bookings().calculateFare(chosenSeat);
                int generatedPassengerId = airport.bookings().nextPassengerId();
                String generatedTicketId = airport.bookings().nextTicketId();

                Passenger passenger = new Passenger(
                        generatedPassengerId,
                        name.getText().trim(),
                        phone.getText().trim(),
                        email.getText().trim(),
                        passport.getText().trim(),
                        nationality.getText().trim()
                );

                boolean ok = airport.bookings().bookTicket(
                        generatedTicketId, passenger, flightId.getText().trim(),
                        seat.getText().trim(), amount
                );

                if (ok) {
                    price.setText(String.format(java.util.Locale.ROOT, "%.2f", amount));
                    UITheme.info(this, "Booking successful!\nPassenger ID: " + generatedPassengerId
                            + "\nTicket ID: " + generatedTicketId
                            + "\nFlight: " + flightId.getText().trim()
                            + "\nSeat: " + seat.getText().trim()
                            + "\nTicket price: BDT " + String.format(java.util.Locale.ROOT, "%.2f", amount)
                            + "\n\nKeep your Ticket ID for check-in, ticket details and cancellation.");
                    refreshFlights();
                } else {
                    UITheme.error(this, "Booking failed. Check the flight status and seat availability.");
                }
            } catch (IllegalArgumentException ex) {
                UITheme.error(this, ex.getMessage());
            }
        });

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );
        buttons.setOpaque(false);
        buttons.add(selected);
        buttons.add(chooseSeat);
        buttons.add(bookButton);

        JPanel south = new JPanel(new BorderLayout(5, 5));
        south.setOpaque(false);
        south.add(book, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.SOUTH);

        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    private void showSeatPicker(String flightId, JTextField seatField, JTextField priceField) {
        java.util.List<Seat> flightSeats = airport.seats().getSeatsForFlight(flightId);
        if (flightSeats.isEmpty()) {
            UITheme.error(this, "No seats have been generated for flight " + flightId
                    + ". Make sure the flight was registered through the airport system.");
            return;
        }

        JDialog dialog = new JDialog(this, "Choose a Seat - " + flightId, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout(12, 12));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        heading.add(new JLabel("Select an available seat. Reserved seats cannot be selected.  Fare: Economy BDT 5,000 | Business BDT 10,000."), BorderLayout.NORTH);
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        legend.add(new JLabel("GREEN = Available"));
        legend.add(new JLabel("RED = Reserved / Booked"));
        heading.add(legend, BorderLayout.SOUTH);
        dialog.add(heading, BorderLayout.NORTH);

        JPanel seatGrid = new JPanel(new GridLayout(0, 7, 8, 8));
        seatGrid.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        for (int i = 0; i < flightSeats.size(); i++) {
            Seat currentSeat = flightSeats.get(i);
            JButton seatButton = new JButton("<html><center>" + currentSeat.getSeatNumber()
                    + "<br>" + currentSeat.getClassType() + "</center></html>");
            seatButton.setFont(new Font("SansSerif", Font.BOLD, 12));
            seatButton.setFocusPainted(false);
            seatButton.setOpaque(true);
            seatButton.setBorderPainted(true);
            boolean available = "Available".equalsIgnoreCase(currentSeat.getStatus());

            if (available) {
                seatButton.setBackground(new Color(213, 241, 222));
                seatButton.setForeground(new Color(20, 100,  50));
                seatButton.setToolTipText("Available - click to select");
                seatButton.addActionListener(ev -> {
                    seatField.setText(currentSeat.getSeatNumber());
                    double fare = airport.bookings().calculateFare(currentSeat);
                    priceField.setText(String.format(java.util.Locale.ROOT, "%.2f", fare));
                    dialog.dispose();
                });
            } else {
                seatButton.setBackground(new Color(250, 218, 218));
                seatButton.setForeground(new Color(150, 35, 35));
                seatButton.setEnabled(false);
                seatButton.setToolTipText("Not available: " + currentSeat.getStatus());
            }
            seatGrid.add(seatButton);

            // Keep a visible aisle between columns C and D.
            if (currentSeat.getSeatNumber().endsWith("C")) {
                JLabel aisle = new JLabel("AISLE", SwingConstants.CENTER);
                aisle.setForeground(Color.GRAY);
                aisle.setFont(new Font("SansSerif", Font.PLAIN, 9));
                seatGrid.add(aisle);
            }
        }

        dialog.add(new JScrollPane(seatGrid), BorderLayout.CENTER);
        JButton close = UITheme.button("Close");
        close.addActionListener(e -> dialog.dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(close);
        dialog.add(bottom, BorderLayout.SOUTH);

        dialog.setSize(650, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel buildActionsTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField ticketId = UITheme.field();

        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Ticket ID"), c);

        c.gridx = 1;
        panel.add(ticketId, c);

        JButton cancel = UITheme.button("Cancel Ticket");

        cancel.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Cancel ticket " + ticketId.getText().trim() + "?",
                    "Confirm cancellation",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = airport.bookings().cancelTicket(
                        ticketId.getText().trim()
                );

                if (ok) {
                    UITheme.info(
                            this,
                            "Ticket cancelled and seat released."
                    );
                } else {
                    UITheme.error(
                            this,
                            "Cancellation failed. Check the ticket ID and status."
                    );
                }
            }
        });

        JButton checkin = UITheme.button("Check In");

        checkin.addActionListener(e -> {
            boolean ok = airport.checkIns().checkInPassenger(
                    ticketId.getText().trim()
            );

            if (ok) {
                UITheme.info(this, "Check-in successful.");
            } else {
                UITheme.error(
                        this,
                        "Check-in failed. A valid booked ticket is required; "
                                + "duplicate check-ins are not allowed."
                );
            }
        });

        JButton details = UITheme.button("Show Ticket Details");

        details.addActionListener(e -> {
            Ticket t = airport.bookings().searchTicket(
                    ticketId.getText().trim()
            );

            if (t == null) {
                UITheme.error(this, "Ticket not found.");
            } else {
                UITheme.info(
                        this,
                        "Ticket: " + t.getTicketId()
                                + "\nPassenger: " + t.getPassenger().getName()
                                + "\nFlight: " + t.getFlight().getFlightId()
                                + "\nRoute: " + t.getFlight().getSource()
                                + " → " + t.getFlight().getDestination()
                                + "\nSeat: " + t.getSeat().getSeatNumber()
                                + "\nPrice: " + t.getPrice()
                                + "\nStatus: " + t.getStatus()
                );
            }
        });

        c.gridx = 0;
        c.gridy = 1;
        panel.add(cancel, c);

        c.gridx = 1;
        panel.add(checkin, c);

        c.gridx = 2;
        panel.add(details, c);

        JTextArea note = new JTextArea(
                "Use the ticket ID from your booking. "
                        + "Cancelled tickets cannot be checked in."
        );

        note.setEditable(false);
        note.setOpaque(false);
        note.setFont(new Font("SansSerif", Font.PLAIN, 14));

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;

        panel.add(note, c);

        return panel;
    }

    private void refreshFlights() {
        flightModel.setRowCount(0);

        for (Flight f : airport.flights().getAllFlights()) {
            if (!"Scheduled".equalsIgnoreCase(f.getStatus())
                    || !"Departure".equalsIgnoreCase(f.getFlightType())) {
                continue;
            }
            flightModel.addRow(new Object[]{
                    f.getFlightId(),
                    f.getSource(),
                    f.getDestination(),
                    f.getDate(),
                    f.getTime(),
                    f.getStatus(),
                    Boolean.TRUE.equals(f.getPriority()) ? "Yes" : "No"
            });
        }
    }

    private void searchFlights() {
        flightModel.setRowCount(0);

        String dest = destination.getText().trim();
        String day = date.getText().trim();

        if (dest.isEmpty() || day.isEmpty()) {
            UITheme.error(
                    this,
                    "Enter both destination and date to search."
            );
            return;
        }

        Collection<Flight> found =
                airport.flights().searchFlight(dest, day);

        for (Flight f : found) {
            flightModel.addRow(new Object[]{
                    f.getFlightId(),
                    f.getSource(),
                    f.getDestination(),
                    f.getDate(),
                    f.getTime(),
                    f.getStatus(),
                    Boolean.TRUE.equals(f.getPriority()) ? "Yes" : "No"
            });
        }

        if (found.isEmpty()) {
            UITheme.info(
                    this,
                    "No scheduled flights matched that destination and date."
            );
        }
    }

    private void addField(
            JPanel panel,
            String label,
            JTextField field
    ) {
        panel.add(new JLabel(label));
        panel.add(field);
    }

    private static DefaultTableModel model(String... cols) {
        return new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }
}