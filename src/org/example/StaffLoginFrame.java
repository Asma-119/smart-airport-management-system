package org.example;

import java.awt.*;
import javax.swing.*;

public class StaffLoginFrame extends JFrame {

    public StaffLoginFrame(AirportOperationsManager airport) {
        setTitle("Staff Login");
        setSize(420, 300);
        setLocationRelativeTo(null);
        UITheme.styleFrame(this);

        JPanel root = UITheme.pagePanel();
        root.add(UITheme.title("Staff sign in"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(7, 5, 7, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField username = UITheme.field();
        JPasswordField password = new JPasswordField(18);

        c.gridx = 0;
        c.gridy = 0;
        form.add(new JLabel("Username"), c);

        c.gridx = 1;
        form.add(username, c);

        c.gridx = 0;
        c.gridy = 1;
        form.add(new JLabel("Password"), c);

        c.gridx = 1;
        form.add(password, c);

        root.add(form, BorderLayout.CENTER);

        JButton login = UITheme.button("Sign in");

        login.addActionListener(e -> {
            boolean valid =
                    "admin".equals(username.getText().trim())
                            && "SkyPort@123".equals(
                            new String(password.getPassword())
                    );

            if (valid) {
                dispose();
                new StaffDashboard(airport).setVisible(true);
            } else {
                UITheme.error(
                        this,
                        "Incorrect username or password."
                );
            }
        });

        JPanel bottom = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );
        bottom.setOpaque(false);
        bottom.add(login);

        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);
    }
}