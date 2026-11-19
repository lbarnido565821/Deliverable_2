package ui;

import service.HotelSystem;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public HotelSystem system;
    private CardLayout cardLayout = new CardLayout();
    private JPanel container = new JPanel(cardLayout);

    private GuestBookingPanel guestPanel;
    private FrontDeskPanel deskPanel;
    private HousekeepingPanel housePanel;
    private ReportsPanel reportsPanel;

    public MainFrame() {
        system = new HotelSystem();
        setTitle("SmartStay: Hotel Reservation & Room Allocation System");
        setSize(900, 560);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Instantiate panels using the 1-argument constructor that matches your project files
        guestPanel = new GuestBookingPanel(this);
        deskPanel = new FrontDeskPanel(this, system); // FrontDeskPanel uses (MainFrame, HotelSystem)
        housePanel = new HousekeepingPanel(this);
        reportsPanel = new ReportsPanel(this);

        container.add(new LoginPanel(this), "LOGIN");
        container.add(guestPanel, "GUEST");
        container.add(deskPanel, "FRONT_DESK");
        container.add(housePanel, "HOUSEKEEPING");
        container.add(reportsPanel, "ADMIN");

        add(container);
        showPanel("LOGIN");
    }

    public void showPanel(String name) {
        try { deskPanel.refresh(); } catch (Exception e) {}
        try { guestPanel.refresh(); } catch (Exception e) {}
        try { housePanel.refresh(); } catch (Exception e) {}
        try { reportsPanel.refresh(); } catch (Exception e) {}
        cardLayout.show(container, name);
    }
}