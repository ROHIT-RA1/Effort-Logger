package srs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EmployeeGUI {
	 // method to show up a new page for logging work hours
	
	// Create a JLabel to display the user's email
    JLabel emailLabel = new JLabel("Welcome to employee Page ");
    
	public EmployeeGUI(final String email) {
	    final JFrame newFrame = new JFrame("Welcome to effortLogger");
	    newFrame.setTitle("Log Hours");
	    newFrame.setSize(300, 300);
	    newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    newFrame.setLocationRelativeTo(null);
	    
	    
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    GridBagConstraints constraints = new GridBagConstraints();
	    constraints.insets = new Insets(10, 10, 10, 10);

	    // Retrieve the list of projects assigned to the employee
	    ArrayList<String> projectNames = getAssignedProjects(email);

	    panel.add(emailLabel);
	    JLabel projectLabel = new JLabel("Project:");
	    constraints.gridx = 0;
	    constraints.gridy = 1;
	    panel.add(projectLabel, constraints);

	    // Create a drop-down list to display the projects
	    final JComboBox<String> projectList = new JComboBox<String>(projectNames.toArray(new String[0]));
	    constraints.gridx = 1;
	    constraints.gridy = 1;
	    panel.add(projectList, constraints);


	    JLabel hoursLabel = new JLabel("Hours Worked:");
	    constraints.gridx = 0;
	    constraints.gridy = 3;
	    panel.add(hoursLabel, constraints);

	    final JTextField hoursField = new JTextField(20);
	   // hoursField.setPreferredSize(new Dimension(100, 25));
	    constraints.gridx = 1;
	    constraints.gridy = 3;
	    panel.add(hoursField, constraints);
	    
	    String[] options = {"In Progress", "Delivered"};
        final JComboBox<String> dropdown = new JComboBox<>(options);
        
        JLabel stat = new JLabel("Status:");
        constraints.gridx = 0;
        constraints.gridy = 4;
        panel.add(stat, constraints);
        
        constraints.gridx = 1;
        constraints.gridy = 4;
        panel.add(dropdown, constraints);

	    JButton submitButton = new JButton("Submit");
	    submitButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            // Retrieve the selected project from the drop-down list
	            String selectedProject = (String) projectList.getSelectedItem();

	            Date date = new Date();
	            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	            String currentDateTime = dateFormat.format(date);
	            
	            String hoursText = hoursField.getText().trim();
	            if (hoursText.isEmpty()) {
	                // Display an error message if the hours field is empty
	                JOptionPane.showMessageDialog(newFrame, "Please enter hours worked.");
	                return;
	            }

	            double hours = Double.parseDouble(hoursField.getText());

	            // Log the work hours for the selected project
	            boolean result = logWorkHours(email, selectedProject, currentDateTime, hours, (String)dropdown.getSelectedItem());
	            if (result) {
	                JOptionPane.showMessageDialog(newFrame, "Hours logged successfully.");
	                hoursField.setText("");
	            } 
	            
	           
	        }
	    });
	    constraints.gridx = 1;
	    constraints.gridy = 5;
	    panel.add(submitButton, constraints);
	    
	   

	    newFrame.add(panel);
	    newFrame.setVisible(true);
	}

	// Retrieve the list of projects assigned to the employee
	private static ArrayList<String> getAssignedProjects(String username) {
	    Connection conn = null;
	    PreparedStatement stmt = null;
	    ResultSet rs = null;

	    ArrayList<String> projectNames = new ArrayList<String>();

	    try {
	        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/srs", "root", "mypass");
	        String sql = "SELECT name FROM projects WHERE assigned_employee = ?";
	        stmt = conn.prepareStatement(sql);
	        stmt.setString(1, username);
	        rs = stmt.executeQuery();

	        while (rs.next()) {
	            String projectName = rs.getString("name");
	            projectNames.add(projectName);
	        }
	    } catch (SQLException e) {
	        System.out.println("Error retrieving assigned projects: " + e.getMessage());
	    } finally {
	        try {
	            if (rs != null) {
	                rs.close();
	            }
	            if (stmt != null) {
	                stmt.close();
	            }
	            if (conn != null) {
	                conn.close();
	            }
	        } catch (SQLException ex) {
	            System.out.println("Error closing database resources: " + ex.getMessage());
	        }
	    }
		return projectNames;
	}
	
	
	private static boolean logWorkHours(String email, String projectName, String dateTime, double hours, String status) {
	    Connection conn = null;
	    PreparedStatement stmt = null;
	    PreparedStatement stmt1 = null;
	 
	    try {
	        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/srs", "root", "mypass");
	        String sql = "INSERT INTO work_hours (employee_email, project_name, date_time, hours, status) " +
	                "VALUES (?, ?, ?, ?, ?)";
	        stmt = conn.prepareStatement(sql);
	        stmt.setString(1, email);
	        stmt.setString(2, projectName);
	        stmt.setString(3, dateTime);
	        stmt.setDouble(4, hours);
	        stmt.setNString(5, status);
	        stmt.executeUpdate();
	 
	        
	        String sql1 = "UPDATE projects SET hours_worked = (SELECT SUM(hours) FROM work_hours WHERE project_name = ? AND employee_email = ?),status = ? WHERE name = ?";
	        stmt1 = conn.prepareStatement(sql1);
	        stmt1.setString(1, projectName);
	        stmt1.setString(2, email);
	        stmt1.setString(3, status);
	        stmt1.setString(4, projectName);
	        stmt1.executeUpdate();

	        return true;
	    } catch (SQLException e) {
	        System.out.println("Error logging work hours: " + e.getMessage());
	        return false;
	    } finally {
	        try {
	            if (stmt != null) {
	                stmt.close();
	            }
	            if (stmt1 != null) {
	                stmt1.close();
	            }
	            if (conn != null) {
	                conn.close();
	            }
	        } catch (SQLException ex) {
	            System.out.println("Error closing database resources: " + ex.getMessage());
	        }
	    }
	}

}
