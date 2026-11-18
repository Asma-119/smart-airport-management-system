package org.example;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public final class UITheme {
    public static final Color NAVY = new Color(18, 35, 61);
    public static final Color TEAL = new Color(0, 150, 136);
    public static final Color BG = new Color(244, 247, 251);
    public static final Color TEXT = new Color(35, 48, 65);

    private UITheme() {}

    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(BG);
        frame.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 25));
        label.setForeground(NAVY);
        return label;
    }

    public static JButton button(String text) {
        JButton b = new JButton(text);

        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(10, 25, 100)); // Dark teal
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setBorder(new EmptyBorder(10, 16, 10, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return b;
    }
    public static JPanel pagePanel() {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(22, 22, 22, 22));
        return p;
    }

    public static JTextField field() {
        JTextField f = new JTextField(18);
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return f;
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(
                parent, message, "Smart Airport",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(
                parent, message, "Please check",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
