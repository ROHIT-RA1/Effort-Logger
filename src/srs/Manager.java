package srs;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Manager {
    // Create the main JFrame
    JFrame frame = new JFrame("Manager Page");
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/srs" // db url
            + "";
    private static final String DB_USER = "root"; // db user name
    private static final String DB_PASS = "mypass"; // db password
    
    // Create two JPanels to hold the components
    JPanel panel1 = new JPanel();
    JPanel panel2 = new JPanel();

    // Create a JLabel to display the user's email
    JLabel emailLabel = new JLabel("Welcome to Manager Page ");

    // Create the buttons for creating and viewing tasks
    JButton createTaskButton = new JButton("Create Task");
    JButton viewTasksButton = new JButton("View Tasks");

    public Manager(final String email) {
        // Add the label to panel1
        panel1.add(emailLabel);

        // Add the buttons to panel2
        panel2.add(createTaskButton);
        panel2.add(viewTasksButton);

        // Add action listeners to the buttons
        createTaskButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Open a new window to create a task
            	ManagerGUI managerGUI = new ManagerGUI(email);
   			 managerGUI.frame.setVisible(true);
   	            frame.dispose();
   	         
            }
        });

        viewTasksButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	TaskViewer taskViewer = new TaskViewer();
                 taskViewer.setVisible(true);
            }
        });

        // Add the panels to the frame
        frame.add(panel1, BorderLayout.NORTH);
        frame.add(panel2, BorderLayout.CENTER);

        // Set the frame size and visibility
        frame.setSize(400, 400);
        frame.setVisible(true);
    }
}

