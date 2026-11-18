package org.example;

import java.awt.*;
import javax.swing.*;

public class WelcomeFrame extends JFrame {
    private final AirportOperationsManager airport;

    public WelcomeFrame(AirportOperationsManager airport) {
        this.airport = airport;

        setTitle("Smart Airport Operations & Flight Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(760, 470);
        setLocationRelativeTo(null);
        UITheme.styleFrame(this);

        JPanel root = UITheme.pagePanel();

        JPanel hero = new JPanel();
        hero.setBackground(UITheme.NAVY);
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBorder(BorderFactory.createEmptyBorder(28, 30, 28, 30));

        JLabel brand = new JLabel("SMART AIRPORT");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel heading = new JLabel(
                "Operations & Flight Management"
        );
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel sub = new JLabel(
                "A unified portal for passengers and airport staff"
        );
        sub.setForeground(new Color(215, 229, 240));

        hero.add(brand);
        hero.add(Box.createVerticalStrut(14));
        hero.add(heading);
        hero.add(Box.createVerticalStrut(8));
        hero.add(sub);

        root.add(hero, BorderLayout.NORTH);

        JPanel options = new JPanel(new GridLayout(1, 2, 18, 0));
        options.setOpaque(false);

        JPanel passenger = optionCard(
                "Passenger Portal",
                "Search flights, book tickets, cancel reservations and check in.",
                "Continue as Passenger"
        );

        JButton passengerBtn =
                (JButton) passenger.getClientProperty("action");

        passengerBtn.addActionListener(
                e -> new PassengerDashboard(airport).setVisible(true)
        );

        JPanel staff = optionCard(
                "Staff Portal",
                "Manage flights, flight status, bookings, gates and runways.",
                "Staff Login"
        );

        JButton staffBtn =
                (JButton) staff.getClientProperty("action");

        staffBtn.addActionListener(
                e -> new StaffLoginFrame(airport).setVisible(true)
        );

        options.add(passenger);
        options.add(staff);

        root.add(options, BorderLayout.CENTER);

        JLabel footer = new JLabel(
                "Academic OOP project • Java Swing",
                SwingConstants.CENTER
        );
        footer.setForeground(Color.GRAY);

        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel optionCard(
            String heading,
            String description,
            String buttonText
    ) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(222, 229, 237)
                ),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel h = new JLabel(heading);
        h.setFont(new Font("SansSerif", Font.BOLD, 20));
        h.setForeground(UITheme.NAVY);

        JTextArea d = new JTextArea(description);
        d.setWrapStyleWord(true);
        d.setLineWrap(true);
        d.setEditable(false);
        d.setOpaque(false);
        d.setFont(new Font("SansSerif", Font.PLAIN, 14));
        d.setForeground(UITheme.TEXT);
        d.setRows(3);

        JButton b = UITheme.button(buttonText);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(h);
        card.add(Box.createVerticalStrut(12));
        card.add(d);
        card.add(Box.createVerticalGlue());
        card.add(b);

        card.putClientProperty("action", b);
        return card;
    }
}