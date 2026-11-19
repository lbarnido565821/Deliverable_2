package ui;

import model.Reservation;
import model.Room;
import service.HotelSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FrontDeskPanel extends JPanel {
    private HotelSystem hotelSystem;
    private MainFrame mainFrame;
    private JTable bookingTable;
    private DefaultTableModel bookingTableModel;
    
    private JTable roomTable;
    private DefaultTableModel roomTableModel;

    public FrontDeskPanel(MainFrame mainFrame, HotelSystem hotelSystem) {
        this.mainFrame = mainFrame;
        this.hotelSystem = hotelSystem;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top Banner
        JLabel titleLabel = new JLabel("Front Desk Management Dashboard", JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Center Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(220);

        // --- TOP SECTION: Edit User Reservation (CRUD: Confirm, Cancel, Change Room) ---
        JPanel bookingPanel = new JPanel(new BorderLayout(5, 5));
        bookingPanel.setBorder(BorderFactory.createTitledBorder("Edit User Reservation (Confirm, Cancel, Change Room)"));
        
        String[] bookingCols = {"Booking Ref", "Guest Email", "Room #", "Check-In", "Check-Out", "Total", "Status"};
        bookingTableModel = new DefaultTableModel(bookingCols, 0);
        bookingTable = new JTable(bookingTableModel);
        bookingPanel.add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        JPanel crudButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton confirmBtn = new JButton("Confirm");
        JButton cancelBtn = new JButton("Cancel");
        JButton changeRoomBtn = new JButton("Change Room");
        JButton refreshBtn = new JButton("Refresh Data");

        crudButtonPanel.add(confirmBtn);
        crudButtonPanel.add(cancelBtn);
        crudButtonPanel.add(changeRoomBtn);
        crudButtonPanel.add(refreshBtn);
        bookingPanel.add(crudButtonPanel, BorderLayout.SOUTH);

        splitPane.setTopComponent(bookingPanel);

        // --- BOTTOM SECTION: Available Rooms & Amenities Dashboard ---
        JPanel roomPanel = new JPanel(new BorderLayout(5, 5));
        roomPanel.setBorder(BorderFactory.createTitledBorder("Available Rooms & Amenities"));

        String[] roomCols = {"Room #", "Category", "Rate/Night", "Status"};
        roomTableModel = new DefaultTableModel(roomCols, 0);
        roomTable = new JTable(roomTableModel);
        roomPanel.add(new JScrollPane(roomTable), BorderLayout.CENTER);

        splitPane.setBottomComponent(roomPanel);
        add(splitPane, BorderLayout.CENTER);

        // Logout Button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        bottomPanel.add(logoutBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        refresh();

        // Action Listeners
        refreshBtn.addActionListener(e -> refresh());
        logoutBtn.addActionListener(e -> mainFrame.showPanel("LOGIN"));

        confirmBtn.addActionListener(e -> {
            int row = bookingTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a reservation first.");
                return;
            }
            String ref = (String) bookingTableModel.getValueAt(row, 0);
            String msg = hotelSystem.confirmReservation(ref);
            JOptionPane.showMessageDialog(this, msg);
            refresh();
        });

        cancelBtn.addActionListener(e -> {
            int row = bookingTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a reservation first.");
                return;
            }
            String ref = (String) bookingTableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this booking?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String msg = hotelSystem.cancelReservation(ref);
                JOptionPane.showMessageDialog(this, msg);
                refresh();
            }
        });

        changeRoomBtn.addActionListener(e -> {
            int row = bookingTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a reservation first.");
                return;
            }
            String ref = (String) bookingTableModel.getValueAt(row, 0);
            String input = JOptionPane.showInputDialog(this, "Enter new Room Number (e.g., 101, 102, 201, 301):");
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int newRoomNum = Integer.parseInt(input.trim());
                    String msg = hotelSystem.changeRoom(ref, newRoomNum);
                    JOptionPane.showMessageDialog(this, msg);
                    refresh();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid numeric room number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public void refresh() {
        // Refresh Bookings
        bookingTableModel.setRowCount(0);
        for (Reservation res : hotelSystem.bookings.values()) {
            bookingTableModel.addRow(new Object[]{
                res.bookingRef,
                res.guestEmail,
                res.roomNumber,
                res.checkIn,
                res.checkOut,
                "PHP " + res.totalAmount,
                res.status
            });
        }

        // Refresh Available Rooms & Amenities
        roomTableModel.setRowCount(0);
        for (Room r : hotelSystem.rooms.values()) {
            roomTableModel.addRow(new Object[]{
                r.roomNumber,
                r.category,
                "PHP " + r.ratePerNight,
                r.status != null ? r.status : "AVAILABLE"
            });
        }
    }
}