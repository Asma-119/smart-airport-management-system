package org.example;

import javax.swing.*;

public class MainGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ignored) {
            }

            AirportOperationsManager airport =
                    new AirportOperationsManager();

            // Add starter resources only if their IDs do not already exist.
            airport.gates().addGate(
                    new Gate("G01", "Terminal A", "Available", "None")
            );
            airport.gates().addGate(
                    new Gate("G02", "Terminal A", "Available", "None")
            );
            airport.gates().addGate(
                    new Gate("G03", "Terminal B", "Available", "None")
            );

            airport.runways().addRunway(
                    new Runway("R01", "Available", "None")
            );
            airport.runways().addRunway(
                    new Runway("R02", "Available", "None")
            );

            // Assign gates to previously saved scheduled flights if needed.
            airport.assignMissingGatesToExistingFlights();

            new WelcomeFrame(airport).setVisible(true);
        });
    }
}