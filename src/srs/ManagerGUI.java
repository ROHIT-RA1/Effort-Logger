package srs;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManagerGUI {
    // giving database credentials for connection
    private static final String DB_URL = "jdbc:mysql://localhost:3306/srs" // db url
            + "";
    private static final String DB_USER = "root"; // db user name
    private static final String DB_PASS = "mypass"; // db password

    // GUI components
    JFrame frame;
    private JTextField projectNameField;
    private JTextArea projectDescArea;
    private JTextField projectDeadlineField;

    // constructor
    public ManagerGUI(final String email) {
        // create and configure GUI components
        frame = new JFrame("Manager GUI");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        JLabel welcomeLabel = new JLabel("Welcome, " + email + "!");
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(welcomeLabel, constraints);

        JLabel projectNameLabel = new JLabel("Project Name:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(projectNameLabel, constraints);

        projectNameField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(projectNameField, constraints);

        JLabel projectDescLabel = new JLabel("Project Description:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(projectDescLabel, constraints);

        projectDescArea = new JTextArea(5, 20);
        projectDescArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(projectDescArea);
        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(scrollPane, constraints);

        JLabel projectDeadlineLabel = new JLabel("Project Deadline:");
        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(projectDeadlineLabel, constraints);

        projectDeadlineField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 3;
        panel.add(projectDeadlineField, constraints);
        
     // add employee combo box to GUI
        JLabel assignEmployeeLabel = new JLabel("Assign Employee:");
        constraints.gridx = 0;
        constraints.gridy = 4;
        panel.add(assignEmployeeLabel, constraints);
        
        final JComboBox<String> employeeComboBox = new JComboBox<String>();
        employeeComboBox.setPreferredSize(new Dimension(200, employeeComboBox.getPreferredSize().height));

        constraints.gridx = 1;
        constraints.gridy = 4;
        panel.add(employeeComboBox, constraints);
        
        // retrieve employees from database and add to combo box
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            PreparedStatement stmt = conn.prepareStatement("SELECT username FROM logindata")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String email2 = rs.getString("username");
                employeeComboBox.addItem(email2);
            }
            employeeComboBox.revalidate();
            employeeComboBox.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error retrieving employees: " + ex.getMessage());
        }

        

        JButton createProjectButton = new JButton("Create Project");
        createProjectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String projectName = projectNameField.getText();
                String projectDesc = projectDescArea.getText();
                String projectDeadlineStr = projectDeadlineField.getText();
                
            

                

                // get selected employee from combo box
                String selectedEmployee = (String) employeeComboBox.getSelectedItem();


                // validate user input
                if (projectName.isEmpty() || projectDesc.isEmpty() || projectDeadlineStr.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
                    return;
                }

                // convert deadline string to date
                Date projectDeadline;
                try {
                    projectDeadline = new SimpleDateFormat("yyyy-MM-dd").parse(projectDeadlineStr);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter deadline in format: yyyy-MM-dd");
                    return;
                }

                // insert project into database
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                		PreparedStatement stmt = conn.prepareStatement("INSERT INTO projects (name, description, deadline, assigned_employee) VALUES (?, ?, ?, ?)")) {
                    stmt.setString(1, projectName);
                    stmt.setString(2, projectDesc);
                    stmt.setDate(3, new java.sql.Date(projectDeadline.getTime()));
                    stmt.setString(4, selectedEmployee);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(frame, "Project created successfully!");
                    frame.dispose();
                    Manager manager = new Manager(email);
           	         
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error creating project: " + ex.getMessage());
                }
                


			}
		});
    constraints.gridx = 1;
    constraints.gridy = 5;
    panel.add(createProjectButton, constraints);

    frame.getContentPane().add(panel);
    frame.pack();
    frame.setVisible(true);
	}
}
