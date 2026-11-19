package ui;

import model.Reservation;
import service.MergeSortUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ReportsPanel extends JPanel {
    private MainFrame frame;
    private DefaultTableModel model = new DefaultTableModel(new String[]{"Ref", "Guest Name", "Check-In", "Amount (PHP)", "Status"}, 0);
    private JLabel revenueLbl = new JLabel("Total Revenue: PHP 0.0");

    public ReportsPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel();
        JButton sortDateBtn = new JButton("MergeSort: By Date");
        JButton sortNameBtn = new JButton("MergeSort: By Name");
        JButton sortAmtBtn = new JButton("MergeSort: By Amount");
        top.add(revenueLbl); top.add(sortDateBtn); top.add(sortNameBtn); top.add(sortAmtBtn);

        JButton logoutBtn = new JButton("Logout");
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        add(logoutBtn, BorderLayout.SOUTH);

        sortDateBtn.addActionListener(e -> loadSorted("DATE"));
        sortNameBtn.addActionListener(e -> loadSorted("NAME"));
        sortAmtBtn.addActionListener(e -> loadSorted("AMOUNT"));
        logoutBtn.addActionListener(e -> frame.showPanel("LOGIN"));
    }

    public void refresh() {
        loadSorted("DATE");
    }

    private void loadSorted(String criteria) {
        model.setRowCount(0);
        List<Reservation> sorted = MergeSortUtil.mergeSort(new ArrayList<>(frame.system.bookings.values()), criteria);
        double total = 0;
        for (Reservation r : sorted) {
            if (!r.status.equals("CANCELED")) total += r.totalAmount;
            model.addRow(new Object[]{r.bookingRef, r.guestName, r.checkIn, r.totalAmount, r.status});
        }
        revenueLbl.setText("Total Revenue: PHP " + total + "   |");
    }
}