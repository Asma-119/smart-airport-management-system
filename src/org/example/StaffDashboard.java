package org.example;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StaffDashboard extends JFrame {
    private final AirportOperationsManager airport;

    private final DefaultTableModel flightsModel = model(
            "Flight ID", "Source", "Destination", "Date",
            "Time", "Type", "Status", "Priority", "Aircraft", "Capacity"
    );

    private final DefaultTableModel ticketsModel = model(
            "Ticket ID", "Passenger", "Flight",
            "Seat", "Price", "Status"
    );

    private final DefaultTableModel gatesModel = model(
            "Gate", "Terminal", "Status", "Assigned Flight"
    );

    private final DefaultTableModel runwaysModel = model(
            "Runway", "Status", "Assigned Flight"
    );

    public StaffDashboard(AirportOperationsManager airport) {
        this.airport = airport;

        setTitle("Staff Dashboard");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        UITheme.styleFrame(this);

        JPanel root = UITheme.pagePanel();

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(
                UITheme.title("Airport Staff Dashboard"),
                BorderLayout.WEST
        );

        JButton logout = UITheme.button("Logout");
        logout.addActionListener(e -> {
            dispose();
            new WelcomeFrame(airport).setVisible(true);
        });

        header.add(logout, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Flights", flightPanel());
        tabs.addTab("Bookings", bookingPanel());
        tabs.addTab("Gates & Runways", resourcesPanel());

        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);

        refreshAll();
    }

    private JPanel flightPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(UITheme.BG);

        JTable table = new JTable(flightsModel);
        table.setRowHeight(24);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        // Two-row layout keeps every flight action visible, including Complete Arrival.
        JPanel actions = new JPanel(new GridLayout(2, 4, 8, 8));
        actions.setOpaque(false);

        JButton add = UITheme.button("Register Flight");
        add.addActionListener(e -> registerFlight());

        JButton delay = UITheme.button("Mark Delayed");
        delay.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(
                    this, "Flight ID to mark delayed:"
            );

            if (id != null && !id.trim().isEmpty()) {
                boolean ok = airport.flights().updateFlightStatus(
                        id.trim(), "Delayed"
                );

                if (ok) {
                    UITheme.info(this, "Flight marked delayed.");
                } else {
                    UITheme.error(this, "Flight not found.");
                }

                refreshFlights();
            }
        });

        JButton process = UITheme.button("Process Departure");
        process.addActionListener(e -> processFlight(false));

        JButton emergency = UITheme.button("Process Emergency");
        emergency.addActionListener(e -> processFlight(true));

        JButton complete = UITheme.button("Complete Departure");
        complete.addActionListener(e -> completeFlightDeparture());

        JButton arrival = UITheme.button("Process Arrival");
        arrival.addActionListener(e -> processArrival());

        JButton completeArrival = UITheme.button("Complete Arrival");
        completeArrival.addActionListener(e -> completeFlightArrival());

        JButton refresh = UITheme.button("Refresh");
        refresh.addActionListener(e -> refreshAll());

        actions.add(add);
        actions.add(delay);
        actions.add(process);
        actions.add(emergency);
        actions.add(complete);
        actions.add(arrival);
        actions.add(completeArrival);
        actions.add(refresh);

        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    private JPanel bookingPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(UITheme.BG);

        JTable table = new JTable(ticketsModel);
        table.setRowHeight(24);

        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );
        actions.setOpaque(false);

        JButton refresh = UITheme.button("Refresh Bookings");
        refresh.addActionListener(e -> refreshTickets());

        JButton cancel = UITheme.button("Cancel Selected Ticket");
        cancel.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row < 0) {
                UITheme.info(this, "Select a ticket row first.");
                return;
            }

            String id = String.valueOf(
                    ticketsModel.getValueAt(
                            table.convertRowIndexToModel(row), 0
                    )
            );

            if (JOptionPane.showConfirmDialog(
                    this,
                    "Cancel ticket " + id + "?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            ) == JOptionPane.YES_OPTION) {

                boolean ok = airport.bookings().cancelTicket(id);

                if (ok) {
                    UITheme.info(this, "Ticket cancelled.");
                } else {
                    UITheme.error(this, "Could not cancel ticket.");
                }

                refreshTickets();
            }
        });

        JButton checkin = UITheme.button("Check In by Ticket ID");
        checkin.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(
                    this, "Ticket ID:"
            );

            if (id != null) {
                boolean ok = airport.checkIns().checkInPassenger(
                        id.trim()
                );

                if (ok) {
                    UITheme.info(this, "Check-in complete.");
                } else {
                    UITheme.error(
                            this,
                            "Check-in failed; verify ticket status."
                    );
                }
            }
        });

        actions.add(refresh);
        actions.add(cancel);
        actions.add(checkin);

        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    private JPanel resourcesPanel() {
        JPanel p = new JPanel(new GridLayout(1, 2, 12, 12));
        p.setBackground(UITheme.BG);

        JPanel left = new JPanel(new BorderLayout(5, 5));
        left.setBackground(Color.WHITE);
        left.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        left.add(new JLabel("Gates"), BorderLayout.NORTH);

        JTable gt = new JTable(gatesModel);
        left.add(new JScrollPane(gt), BorderLayout.CENTER);

        JPanel gb = new JPanel(new FlowLayout());
        gb.setOpaque(false);

        JButton addGate = UITheme.button("Add Gate");
        addGate.addActionListener(e -> {
            JTextField id = UITheme.field();
            JTextField term = UITheme.field();

            JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
            form.add(new JLabel("Gate ID"));
            form.add(id);
            form.add(new JLabel("Terminal"));
            form.add(term);

            if (JOptionPane.showConfirmDialog(
                    this, form, "Add Gate",
                    JOptionPane.OK_CANCEL_OPTION
            ) == JOptionPane.OK_OPTION) {

                if (id.getText().trim().isEmpty()
                        || term.getText().trim().isEmpty()) {
                    UITheme.error(this, "Enter gate ID and terminal.");
                } else {
                    airport.gates().addGate(
                            new Gate(
                                    id.getText().trim(),
                                    term.getText().trim(),
                                    "Available",
                                    "None"
                            )
                    );
                    refreshResources();
                }
            }
        });

        gb.add(addGate);
        left.add(gb, BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout(5, 5));
        right.setBackground(Color.WHITE);
        right.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );
        right.add(new JLabel("Runways"), BorderLayout.NORTH);

        JTable rt = new JTable(runwaysModel);
        right.add(new JScrollPane(rt), BorderLayout.CENTER);

        JPanel rb = new JPanel(new FlowLayout());
        rb.setOpaque(false);

        JButton addRunway = UITheme.button("Add Runway");
        addRunway.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(
                    this, "New runway ID:"
            );

            if (id != null && !id.trim().isEmpty()) {
                airport.runways().addRunway(
                        new Runway(
                                id.trim(), "Available", "None"
                        )
                );
                refreshResources();
            }
        });

        rb.add(addRunway);
        right.add(rb, BorderLayout.SOUTH);

        p.add(left);
        p.add(right);

        return p;
    }

    private void registerFlight() {
        JTextField id = UITheme.field();
        JTextField source = UITheme.field();
        JTextField dest = UITheme.field();
        JTextField date = UITheme.field();
        JTextField time = UITheme.field();
        JTextField aircraftId = UITheme.field();
        JTextField model = UITheme.field();
        JTextField capacity = UITheme.field();

        JCheckBox priority = new JCheckBox("Priority flight");
        priority.setOpaque(false);
        JComboBox<String> flightType = new JComboBox<>(new String[]{"Departure", "Arrival"});

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));

        form.add(new JLabel("Flight ID"));
        form.add(id);
        form.add(new JLabel("Source"));
        form.add(source);
        form.add(new JLabel("Destination"));
        form.add(dest);
        form.add(new JLabel("Date (YYYY-MM-DD)"));
        form.add(date);
        form.add(new JLabel("Time (HH:MM)"));
        form.add(time);
        form.add(new JLabel("Aircraft ID"));
        form.add(aircraftId);
        form.add(new JLabel("Aircraft model"));
        form.add(model);
        form.add(new JLabel("Capacity"));
        form.add(capacity);
        form.add(new JLabel("Flight Type"));
        form.add(flightType);
        form.add(new JLabel("Priority"));
        form.add(priority);

        if (JOptionPane.showConfirmDialog(
                this, form, "Register Flight",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        ) != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int cap = Integer.parseInt(capacity.getText().trim());

            if (id.getText().trim().isEmpty()
                    || source.getText().trim().isEmpty()
                    || dest.getText().trim().isEmpty()
                    || date.getText().trim().isEmpty()
                    || time.getText().trim().isEmpty()
                    || aircraftId.getText().trim().isEmpty()
                    || model.getText().trim().isEmpty()
                    || cap < 1) {
                throw new IllegalArgumentException();
            }

            Flight f = new Flight(
                    id.getText().trim(),
                    source.getText().trim(),
                    dest.getText().trim(),
                    date.getText().trim(),
                    time.getText().trim(),
                    "Scheduled",
                    priority.isSelected(),
                    new Aircraft(
                            aircraftId.getText().trim(),
                            model.getText().trim(),
                            cap
                    ),
                    String.valueOf(flightType.getSelectedItem())
            );

            if (airport.registerFlight(f)) {
                UITheme.info(
                        this,
                        "Flight registered and seats generated."
                );
            } else {
                UITheme.error(
                        this,
                        "Could not register flight. Flight ID may already exist."
                );
            }

            refreshAll();

        } catch (Exception ex) {
            UITheme.error(
                    this,
                    "Please complete all fields and enter a positive "
                            + "whole-number capacity."
            );
        }
    }

    private void processFlight(boolean emergency) {
        String id = JOptionPane.showInputDialog(
                this,
                emergency
                        ? "Emergency flight ID:"
                        : "Flight ID to depart:"
        );

        if (id == null || id.trim().isEmpty()) {
            return;
        }

        boolean ok = airport.beginDeparture(id.trim(), emergency);

        if (ok) {
            String runwayId = "";
            for (Runway runway : airport.runways().getAllRunways()) {
                if (runway.getAssignedFlightId().equalsIgnoreCase(id.trim())) {
                    runwayId = runway.getRunwayId();
                    break;
                }
            }
            UITheme.info(this, "Flight " + id.trim() + " is now DEPARTING.\n"
                    + "Assigned runway: " + (runwayId.isEmpty() ? "See Runways tab" : runwayId)
                    + "\nRunway status: OCCUPIED\n\n"
                    + "Click Complete Departure when the simulation should finish.");
        } else {
            UITheme.error(this, "Could not start departure. Check flight status and runway availability.");
        }

        refreshAll();
    }

    private void processArrival() {
        String id = JOptionPane.showInputDialog(this, "Arrival flight ID to land:");
        if (id == null || id.trim().isEmpty()) return;
        boolean ok = airport.beginArrival(id.trim());
        if (ok) {
            String runwayId = "";
            for (Runway runway : airport.runways().getAllRunways()) {
                if (runway.getAssignedFlightId().equalsIgnoreCase(id.trim())) {
                    runwayId = runway.getRunwayId();
                    break;
                }
            }
            UITheme.info(this, "Flight " + id.trim() + " is now LANDING.\nAssigned runway: "
                    + (runwayId.isEmpty() ? "See Runways tab" : runwayId)
                    + "\nRunway status: OCCUPIED\n\nClick Complete Arrival after landing finishes.");
        } else {
            UITheme.error(this, "Could not start arrival. Check that it is an Arrival flight and a runway is available.");
        }
        refreshAll();
    }

    private void completeFlightArrival() {
        String id = JOptionPane.showInputDialog(this, "Arrival flight ID to complete:");
        if (id == null || id.trim().isEmpty()) return;
        boolean ok = airport.completeArrival(id.trim());
        if (ok) {
            UITheme.info(this, "Flight " + id.trim() + " has LANDED.\nRunway released; gate remains assigned to the aircraft.");
        } else {
            UITheme.error(this, "Could not complete arrival. Make sure the flight is currently Landing.");
        }
        refreshAll();
    }

    private void completeFlightDeparture() {
        String id = JOptionPane.showInputDialog(this, "Flight ID to complete departure:");
        if (id == null || id.trim().isEmpty()) return;

        boolean ok = airport.completeDeparture(id.trim());
        if (ok) {
            UITheme.info(this, "Flight " + id.trim() + " has departed.\n"
                    + "Its gate and runway are now available again.");
        } else {
            UITheme.error(this, "Could not complete departure. Make sure the flight is currently Departing.");
        }
        refreshAll();
    }

    private void refreshAll() {
        refreshFlights();
        refreshTickets();
        refreshResources();
    }

    private void refreshFlights() {
        flightsModel.setRowCount(0);

        for (Flight f : airport.flights().getAllFlights()) {
            flightsModel.addRow(new Object[]{
                    f.getFlightId(),
                    f.getSource(),
                    f.getDestination(),
                    f.getDate(),
                    f.getTime(),
                    f.getFlightType(),
                    f.getStatus(),
                    Boolean.TRUE.equals(f.getPriority()) ? "Yes" : "No",
                    f.getAircraft().getModel(),
                    f.getAircraft().getCapacity()
            });
        }
    }

    private void refreshTickets() {
        ticketsModel.setRowCount(0);

        for (Ticket t : airport.bookings().getTickets()) {
            ticketsModel.addRow(new Object[]{
                    t.getTicketId(),
                    t.getPassenger().getName(),
                    t.getFlight().getFlightId(),
                    t.getSeat().getSeatNumber(),
                    t.getPrice(),
                    t.getStatus()
            });
        }
    }

    private void refreshResources() {
        gatesModel.setRowCount(0);
        runwaysModel.setRowCount(0);

        for (Gate g : airport.gates().getAllGates()) {
            gatesModel.addRow(new Object[]{
                    g.getGateId(),
                    g.getTerminal(),
                    g.getStatus(),
                    g.getAssignedFlightId()
            });
        }

        for (Runway r : airport.runways().getAllRunways()) {
            runwaysModel.addRow(new Object[]{
                    r.getRunwayId(),
                    r.getStatus(),
                    r.getAssignedFlightId()
            });
        }
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