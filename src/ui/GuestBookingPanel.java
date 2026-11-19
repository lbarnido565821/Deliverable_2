package ui;

import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class GuestBookingPanel extends JPanel {
    private MainFrame frame;
    private DefaultTableModel model = new DefaultTableModel(new String[]{"Room #", "Category", "Rate/Night", "Status"}, 0);
    private JTable table = new JTable(model);
    private JTextField inField = new JTextField(LocalDate.now().toString(), 10);
    private JTextField outField = new JTextField(LocalDate.now().plusDays(2).toString(), 10);
    private JComboBox<String> catBox = new JComboBox<>(new String[]{"All", "Standard", "Deluxe", "Suite"});
    private JLabel pointsLbl = new JLabel("Reward Points: 0");

    public GuestBookingPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel();
        top.add(new JLabel("Check-In:")); top.add(inField);
        top.add(new JLabel("Check-Out:")); top.add(outField);
        top.add(new JLabel("Type:")); top.add(catBox);
        JButton searchBtn = new JButton("Search Rooms");
        top.add(searchBtn);
        top.add(pointsLbl);

        JPanel bottom = new JPanel();
        JButton bookBtn = new JButton("Book Selected Room");
        JButton waitBtn = new JButton("Join Waitlist (FIFO Queue)");
        JButton logoutBtn = new JButton("Logout");
        bottom.add(bookBtn); bottom.add(waitBtn); bottom.add(logoutBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> refresh());
        bookBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;
            int roomNum = (int) model.getValueAt(row, 0);
            Reservation res = frame.system.createBooking(
                frame.system.currentUser.idOrEmail, roomNum,
                LocalDate.parse(inField.getText()), LocalDate.parse(outField.getText())
            );
            if (res != null) {
                JOptionPane.showMessageDialog(this, "Booked! Ref: " + res.bookingRef + " | Total: PHP " + res.totalAmount);
                refresh();
            }
        });
        waitBtn.addActionListener(e -> {
            String entry = frame.system.currentUser.idOrEmail + " (" + catBox.getSelectedItem() + " | " + inField.getText() + ")";
            frame.system.waitlist.offer(entry);
            JOptionPane.showMessageDialog(this, "Added to Waitlist Queue! Position: #" + frame.system.waitlist.size());
        });
        logoutBtn.addActionListener(e -> frame.showPanel("LOGIN"));
    }

    public void refresh() {
        if (frame.system.currentUser != null) {
            pointsLbl.setText("Points: " + frame.system.currentUser.rewardPoints);
        }
        model.setRowCount(0);
        try {
            List<Room> avail = frame.system.searchAvailableRooms(
                (String) catBox.getSelectedItem(),
                LocalDate.parse(inField.getText()), LocalDate.parse(outField.getText())
            );
            for (Room r : avail) {
                model.addRow(new Object[]{r.roomNumber, r.category, "PHP " + r.ratePerNight, r.status});
            }
        } catch (Exception ignored) {}
    }
}