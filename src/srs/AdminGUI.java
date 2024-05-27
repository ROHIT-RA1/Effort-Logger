package srs;

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class AdminGUI {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/srs";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "mypass";

   

    public AdminGUI(String email) {

        // Create the main JFrame
        JFrame frame = new JFrame("Admin Page");

        // Create two JPanels to hold the components
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();

        // Create a JLabel to display the user's email
        JLabel emailLabel = new JLabel("Welcome to Admin Page " + email);

        // Add the label to panel1
        panel1.add(emailLabel);

        // Create a JButton to generate the report
        JButton generateReportButton = new JButton("Generate Projects Report");

        // Add an ActionListener to the button to generate the report when it is clicked
        generateReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateCsvReport();
            }
        });

        // Add the button to panel2
        panel2.add(generateReportButton);

        // Add the panels to the frame
        frame.add(panel1, BorderLayout.NORTH);
        frame.add(panel2, BorderLayout.CENTER);

        // Set the frame size and visibility
        frame.setSize(400, 400);
        frame.setVisible(true);
    }


    private static void generateCsvReport() {
        Connection conn = null;
        Statement stmt = null;
        FileWriter writer = null;
        try {
            // Connect to the database
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);

            // Create a statement to execute the SQL query
            stmt = conn.createStatement();
            String sql = "SELECT * FROM projects";
            ResultSet rs = stmt.executeQuery(sql);

            // Create a FileWriter to write the CSV file
            writer = new FileWriter("projects.csv");

            // Write the header row
            writer.append("name,description,deadline,assigned_employee,hours_worked,status\n");

            // Loop through the result set and write each row to the CSV file
            while (rs.next()) {
                String proj_name = rs.getString("name");
                String proj_desc = rs.getString("description");
                String deadlin = rs.getString("deadline");
                String assignee = rs.getString("assigned_employee");
                Double hours =  rs.getDouble("hours_worked");
                String ststs = rs.getString("status");

                writer.append(String.format("%s,%s,%s,%s,%.2f,%s\n", proj_name, proj_desc, deadlin, assignee, hours, ststs));
   }

            JOptionPane.showMessageDialog(null, "CSV report generated successfully.");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            // Close the resources
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
        
    }
    }
    
   
