package srs;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Date;
import java.util.Properties;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginGUI {
    
	// giving database credentials for connection
	private static final String DB_URL = "jdbc:mysql://localhost:3306/srs" // db url
			+ "";
    private static final String DB_USER = "root"; // db user name
    private static final String DB_PASS = "mypass"; // db password
    
    public static void main(String[] args) {
        final JFrame frame = new JFrame("Login GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
     // creating all the necessary fields needed for a login page
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
      // Username
        JLabel usernameLabel = new JLabel("Email:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(usernameLabel, constraints);
        
        final JTextField usernameField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(usernameField, constraints);
      // Password
        JLabel passwordLabel = new JLabel("Password:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(passwordLabel, constraints);
        
        final JPasswordField passwordField = new JPasswordField(20);
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(passwordField, constraints);
       // Security Code  
        JLabel SecurityCodeLabel = new JLabel("SecurityCode:");
        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(SecurityCodeLabel, constraints);
        
        final JTextField SecurityFieldField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 3;
        panel.add(SecurityFieldField, constraints);
       // Button for sending code for verification 
        JButton sendCodeButton = new JButton("Send verification code");
        final String code = generateVerificationCode();
        System.out.println(code);
        sendCodeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = usernameField.getText();
                
                String subject = "Verification code for login";
                String message = "Your verification code for login is: " + code;
                try {
                    sendEmail(email, subject, message);
                    JOptionPane.showMessageDialog(frame, "Verification code sent successfully!");
                } catch (MessagingException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "An error occurred while trying to send verification code.");
                }
            }
        });
        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(sendCodeButton, constraints);
        // Adding login button 
        // validating user credentials with the ones that are registered in database
        // encrypting the user entered password before validating
        // sending email before login for the verification code
        // connecting database using jdbc
        
        String[] options = {"admin", "employee", "manager"};
        final JComboBox<String> dropdown = new JComboBox<>(options);
        
        JLabel roleLabel = new JLabel("Role:");
        constraints.gridx = 0;
        constraints.gridy = 4;
        panel.add(roleLabel, constraints);
        
        constraints.gridx = 1;
        constraints.gridy = 4;
        panel.add(dropdown, constraints);
        
        JButton loginButton = new JButton("Log In");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String verificationCode = SecurityFieldField.getText();
                String role = (String) dropdown.getSelectedItem();
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS); // Retrieving the data from database from login data table
                     PreparedStatement stmt = conn.prepareStatement("SELECT * FROM logindata WHERE username=? AND password=?")) {
                    stmt.setString(1, username);
                    String encryptedPassword = encryptPassword(password);
                    stmt.setString(2, encryptedPassword);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        String email = rs.getString("username");
                        String pass = rs.getString("password");
                        String roled = rs.getString("role");
                  
                        if (email.equals(username) && encryptedPassword.equals(pass)) {
                        	if(verificationCode.equals(code) ) {
                        	 if(roled.equals(role)) {
                        		 if(role.equals("admin")) {
                        			 JOptionPane.showMessageDialog(frame, " Admin Login successful!");
                        			 AdminGUI admin = new AdminGUI(email);
                        		 }
                        	  
                        		 if(role.equals("employee")) {
                        			 JOptionPane.showMessageDialog(frame, "Employee Login successful!");
                        			 EmployeeGUI employeegui = new EmployeeGUI(email);
                        			 
                        			
                        		 }
                        	  
                        		 if(role.equals("manager")) {
                        			 JOptionPane.showMessageDialog(frame, "Manager Login successful!");
                        			 Manager managerGUI = new Manager(email);
                        			 managerGUI.frame.setVisible(true);
                        	            frame.dispose();
                        		 }
                        	 } else {
                        		 JOptionPane.showMessageDialog(frame, "Wrong role.");
                        	 }
                        	} else {
                        		 JOptionPane.showMessageDialog(frame, "Invalid OTP.");
                        	}
                        } else {
                            JOptionPane.showMessageDialog(frame, "Invalid email or password.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid username or password.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "An error occurred while trying to log in.");
                }
            }

			
        });

        constraints.gridx = 1;
        constraints.gridy = 5;
        panel.add(loginButton, constraints);
        // Adding signup button
        // Adding all the required fields to the sign up page
        // connecting database using jdbc
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) { 
                JPanel signUpPanel = new JPanel(new GridLayout(5, 5));
                JLabel newUsernameLabel = new JLabel("Email:");
                JTextField newUsernameField = new JTextField(20);
                JLabel newPasswordLabel = new JLabel("New Password:");
                JPasswordField newPasswordField = new JPasswordField(20);
                JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
                JPasswordField confirmPasswordField = new JPasswordField(20);
                JLabel roleLabel = new JLabel("Role:");
                String[] options = {"admin", "employee", "manager"};
                JComboBox<String> dropdown = new JComboBox<>(options);
                signUpPanel.add(newUsernameLabel);
                signUpPanel.add(newUsernameField);
                signUpPanel.add(newPasswordLabel);
                signUpPanel.add(newPasswordField);
                signUpPanel.add(confirmPasswordLabel);
                signUpPanel.add(confirmPasswordField);
                signUpPanel.add(roleLabel);
                signUpPanel.add(dropdown);
                
                int result = JOptionPane.showConfirmDialog(frame, signUpPanel, "Sign Up", JOptionPane.OK_CANCEL_OPTION);
                if(result == JOptionPane.OK_OPTION) {
                    String newUsername = newUsernameField.getText();
                    String newPassword = new String(newPasswordField.getPassword());
                    String confirmPassword = new String(confirmPasswordField.getPassword());
                    String role = (String) dropdown.getSelectedItem();
                    //checking both new passwords and confirm passwords match
                    if(newPassword.equals(confirmPassword)) {
                        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS); // inserting new user data into database
                             PreparedStatement stmt = conn.prepareStatement("INSERT INTO logindata (username, password, role) VALUES (?, ?, ?)")) {
                            stmt.setString(1, newUsername);
                            stmt.setString(2, encryptPassword(newPassword));
                            stmt.setString(3,  role);
                            int rowsAffected = stmt.executeUpdate();
                            if (rowsAffected > 0) {
                                JOptionPane.showMessageDialog(frame, "Registration successful!");
                            } else {
                                JOptionPane.showMessageDialog(frame, "An error occurred while trying to register.");
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(frame, "An error occurred while trying to register.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Passwords do not match. Please try again.");
                    }
                }
            }
        });
        
        constraints.gridx = 1;
        constraints.gridy = 6;
        panel.add(signUpButton, constraints);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
    
    // generating the otp for two factor authentication
    private static String generateVerificationCode() {
        int code = (int) (Math.random() * 9000) + 1000;
        return Integer.toString(code);
    }
    // sending email with otp to the users from my personal email using javamail api
    private static void sendEmail(String to, String subject, String message) throws MessagingException {
        final String from = "rchiluku@asu.edu"; // email from which all the otp mails will be sent
        final String password = "hvehrdnxxeprxjco"; // app specific password thats generated by email

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject);
        msg.setText(message);

        Transport.send(msg);
    }
    
   
       // method to encrypt the plain password using SHA-256 hash encryption algorithm
    private static String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] hash = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error encrypting password: " + e.getMessage()); // if unable to encrypt the pain password
        }
    }
}
