package view;

import dao.UserDAO;
import model.User;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class RegisterForm extends Frame implements ActionListener {

    // Components
    Label lblName, lblEmail, lblPassword, lblRole, lblMessage;
    TextField txtName, txtEmail, txtPassword;
    Choice roleChoice;
    Button btnRegister, btnBack;

    public RegisterForm() {
        // Frame settings
        setTitle("GVEI - Register");
        setSize(450, 350);
        setLayout(null);
        setBackground(Color.LIGHT_GRAY);

        // Labels
        lblName = new Label("Name:");
        lblName.setBounds(50, 70, 80, 30);
        add(lblName);

        lblEmail = new Label("Email:");
        lblEmail.setBounds(50, 110, 80, 30);
        add(lblEmail);

        lblPassword = new Label("Password:");
        lblPassword.setBounds(50, 150, 80, 30);
        add(lblPassword);

        lblRole = new Label("Role:");
        lblRole.setBounds(50, 190, 80, 30);
        add(lblRole);

        lblMessage = new Label("");
        lblMessage.setBounds(50, 240, 350, 30);
        lblMessage.setForeground(Color.RED);
        add(lblMessage);

        // TextFields
        txtName = new TextField();
        txtName.setBounds(150, 70, 200, 30);
        add(txtName);

        txtEmail = new TextField();
        txtEmail.setBounds(150, 110, 200, 30);
        add(txtEmail);

        txtPassword = new TextField();
        txtPassword.setBounds(150, 150, 200, 30);
        txtPassword.setEchoChar('*');
        add(txtPassword);

        // Role choice
        roleChoice = new Choice();
        roleChoice.add("citizen");
        roleChoice.add("admin");
        roleChoice.setBounds(150, 190, 200, 30);
        add(roleChoice);

        // Buttons
        btnRegister = new Button("Register");
        btnRegister.setBounds(50, 280, 150, 30);
        btnRegister.addActionListener(this);
        add(btnRegister);

        btnBack = new Button("Back to Login");
        btnBack.setBounds(220, 280, 150, 30);
        btnBack.addActionListener(this);
        add(btnBack);

        // Window closing
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRegister) {
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            String password = txtPassword.getText().trim();
            String role = roleChoice.getSelectedItem();

            // Validation
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                lblMessage.setText("Please fill all fields.");
                return;
            }

            try {
                UserDAO userDAO = new UserDAO();

                // Check if email already exists
                if (userDAO.findByEmail(email) != null) {
                    lblMessage.setText("Email already exists.");
                    return;
                }

                // Create user object
                User user = new User(name, email, password, role);

                // Register user in DB
                boolean success = userDAO.register(user);
                if (success) {
                    lblMessage.setForeground(Color.GREEN);
                    lblMessage.setText("Registration successful! Redirecting...");
                    
                    // Delay and go to login
                    new Thread(() -> {
                        try { Thread.sleep(1500); } catch (InterruptedException ex) {}
                        new LoginForm();
                        dispose();
                    }).start();
                } else {
                    lblMessage.setForeground(Color.RED);
                    lblMessage.setText("Registration failed. Try again.");
                }

            } catch (SQLException ex) {
                lblMessage.setForeground(Color.RED);
                lblMessage.setText("Database error: " + ex.getMessage());
                ex.printStackTrace();
            }

        } else if (e.getSource() == btnBack) {
            new LoginForm();
            dispose();
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        new RegisterForm();
    }
}
