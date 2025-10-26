package view;

import dao.UserDAO;
import model.User;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class LoginForm extends Frame implements ActionListener {
    Label lblEmail, lblPassword, lblMessage;
    TextField txtEmail, txtPassword;
    Button btnLogin, btnRegister;

    public LoginForm() {
        setTitle("GVEI - Login");
        setSize(400, 300);
        setLayout(null);
        setBackground(Color.LIGHT_GRAY);

        lblEmail = new Label("Email:");
        lblEmail.setBounds(50, 70, 80, 30);
        add(lblEmail);

        lblPassword = new Label("Password:");
        lblPassword.setBounds(50, 110, 80, 30);
        add(lblPassword);

        lblMessage = new Label("");
        lblMessage.setBounds(50, 160, 300, 30);
        lblMessage.setForeground(Color.RED);
        add(lblMessage);

        txtEmail = new TextField();
        txtEmail.setBounds(150, 70, 180, 30);
        add(txtEmail);

        txtPassword = new TextField();
        txtPassword.setBounds(150, 110, 180, 30);
        txtPassword.setEchoChar('*');
        add(txtPassword);

        btnLogin = new Button("Login");
        btnLogin.setBounds(50, 200, 130, 30);
        btnLogin.addActionListener(this);
        add(btnLogin);

        btnRegister = new Button("Register");
        btnRegister.setBounds(200, 200, 130, 30);
        btnRegister.addActionListener(this);
        add(btnRegister);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnLogin){
            String email = txtEmail.getText().trim();
            String password = txtPassword.getText().trim();
            if(email.isEmpty() || password.isEmpty()){
                lblMessage.setText("Please fill all fields.");
                return;
            }
            try{
                UserDAO dao = new UserDAO();
                User user = dao.login(email, password);
                if(user == null){
                    lblMessage.setText("Invalid email or password.");
                    return;
                }
                if(user.getRole().equals("citizen")){
                    new CitizenDashboard(user);
                } else {
                    new AdminDashboard(user);
                }
                dispose();
            } catch(SQLException ex){
                lblMessage.setText("DB Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        } else if(e.getSource() == btnRegister){
            new RegisterForm();
            dispose();
        }
    }

    public static void main(String[] args){
        new LoginForm();
    }
}
