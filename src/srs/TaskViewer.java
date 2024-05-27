package srs;

import java.awt.BorderLayout;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class TaskViewer extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/srs"; // db url
    private static final String DB_USER = "root"; // db user name
    private static final String DB_PASS = "mypass"; // db password

    private JPanel contentPanel;
    public TaskViewer() {
        super("Task Viewer");

        // Create the content panel and set its layout
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(0, 1));

        // Add a scrollbar to the content panel
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        // Add the scrollbar to the frame
        add(scrollPane, BorderLayout.CENTER);

        // Set the frame size and visibility
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Load all tasks into the content panel
        loadAllTasks();
    }

    private void loadAllTasks() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM projects")) {
            // Execute the query and get the result set
            ResultSet rs = stmt.executeQuery();

            // Clear the content panel before adding new components
            contentPanel.removeAll();

            // Build a list of TaskPanel objects from the result set
            List<TaskPanel> taskPanels = new ArrayList<>();
            while (rs.next()) {
                String name = rs.getString("name");
                String desc = rs.getString("description");
                Date deadline = rs.getDate("deadline");
                String assignedEmployee = rs.getString("assigned_employee");
                int hours = rs.getInt("hours_worked");
                String status = rs.getString("status");

                TaskPanel taskPanel = new TaskPanel(name, desc, deadline, assignedEmployee, hours, status);
                taskPanels.add(taskPanel);
            }

            // Add the TaskPanel objects to the content panel
            for (TaskPanel taskPanel : taskPanels) {
                contentPanel.add(taskPanel);
            }

            // Update the UI
            contentPanel.revalidate();
            contentPanel.repaint();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error retrieving tasks: " + ex.getMessage());
        }
    }

  

    private class TaskPanel extends JPanel {

    public TaskPanel(String name, String desc, Date deadline, String assignedEmployee, int hours, String status) {
        Container panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Name: "));
        panel.add(new JLabel(name));
        panel.add(new JLabel("Description: "));
        panel.add(new JLabel(desc));
        panel.add(new JLabel("Deadline: "));
        panel.add(new JLabel(deadline.toString()));
        panel.add(new JLabel("Assigned Employee: "));
        panel.add(new JLabel(assignedEmployee));
        panel.add(new JLabel("Hours Worked: "));
        panel.add(new JLabel(String.valueOf(hours)));
        panel.add(new JLabel("Status: "));
        panel.add(new JLabel(status));

        add(panel);
    }
}


}

