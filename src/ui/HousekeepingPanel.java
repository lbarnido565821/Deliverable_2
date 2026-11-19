package ui;

import model.CleaningTask;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.PriorityQueue;

public class HousekeepingPanel extends JPanel {
    private MainFrame frame;
    private DefaultTableModel model = new DefaultTableModel(new String[]{"Priority Level", "Room #", "Task Notes"}, 0);

    public HousekeepingPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JLabel("Housekeeping Min-Heap Priority Queue (1 = Highest Priority)", SwingConstants.CENTER), BorderLayout.NORTH);
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton cleanBtn = new JButton("Complete Highest-Priority Room (Poll)");
        JButton logoutBtn = new JButton("Logout");
        bottom.add(cleanBtn); bottom.add(logoutBtn);
        add(bottom, BorderLayout.SOUTH);

        cleanBtn.addActionListener(e -> {
            CleaningTask task = frame.system.cleaningQueue.poll();
            if (task != null) {
                frame.system.rooms.get(task.roomNumber).status = "AVAILABLE";
                JOptionPane.showMessageDialog(this, "Room " + task.roomNumber + " cleaned and marked AVAILABLE!");
                refresh();
            }
        });
        logoutBtn.addActionListener(e -> frame.showPanel("LOGIN"));
    }

    public void refresh() {
        model.setRowCount(0);
        PriorityQueue<CleaningTask> copy = new PriorityQueue<>(frame.system.cleaningQueue);
        while (!copy.isEmpty()) {
            CleaningTask t = copy.poll();
            model.addRow(new Object[]{"Priority " + t.priority, t.roomNumber, t.notes});
        }
    }
}