package ui;

import model.User;
import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    public LoginPanel(MainFrame frame) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("SmartStay Hotel Management System", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JTextField userField = new JTextField("guest@smartstay.com", 20);
        JPasswordField passField = new JPasswordField("1234", 20);
        JButton loginBtn = new JButton("Login");

        JLabel hint = new JLabel("<html><center>Demo Accounts (Pass: 1234):<br>" +
                "<b>guest@smartstay.com</b> | <b>desk01</b> | <b>clean01</b> | <b>admin01</b></center></html>");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; add(title, gbc);
        gbc.gridwidth = 1;
        gbc.gridy = 1; add(new JLabel("Email / Staff ID:"), gbc);
        gbc.gridx = 1; add(userField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; add(passField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; add(loginBtn, gbc);
        gbc.gridy = 4; add(hint, gbc);

        loginBtn.addActionListener(e -> {
            User u = frame.system.authenticate(userField.getText().trim(), new String(passField.getPassword()));
            if (u == null) {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                frame.showPanel(u.role);
            }
        });
    }
}