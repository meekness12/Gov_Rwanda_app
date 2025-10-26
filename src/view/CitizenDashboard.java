package view;

import dao.VehicleDAO;
import dao.ExchangeOfferDAO;
import model.User;
import model.Vehicle;
import model.ExchangeOffer;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class CitizenDashboard extends Frame implements ActionListener {
    private final User user;
    private final TextArea txtVehicles;
    private final Button btnAddVehicle, btnRefresh, btnLogout;

    public CitizenDashboard(User user) {
        this.user = user;
        setTitle("Citizen Dashboard - GVEI");
        setSize(600, 450);
        setLayout(null);
        setBackground(Color.CYAN);
        setResizable(false);

        // Welcome label
        Label lblWelcome = new Label("Welcome, " + user.getName());
        lblWelcome.setBounds(20, 20, 400, 30);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblWelcome);

        // TextArea to display vehicles
        txtVehicles = new TextArea();
        txtVehicles.setBounds(20, 60, 550, 250);
        txtVehicles.setEditable(false);
        add(txtVehicles);

        // Buttons
        btnAddVehicle = new Button("Add Vehicle");
        btnAddVehicle.setBounds(20, 330, 150, 40);
        btnAddVehicle.addActionListener(this);
        add(btnAddVehicle);

        btnRefresh = new Button("Refresh Vehicles");
        btnRefresh.setBounds(200, 330, 150, 40);
        btnRefresh.addActionListener(this);
        add(btnRefresh);

        btnLogout = new Button("Logout");
        btnLogout.setBounds(400, 330, 150, 40);
        btnLogout.addActionListener(this);
        add(btnLogout);

        // Window closing
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); }
        });

        // Load vehicles initially
        loadVehicles();

        setVisible(true);
    }

    private void loadVehicles() {
        try {
            VehicleDAO vehicleDAO = new VehicleDAO();
            ExchangeOfferDAO offerDAO = new ExchangeOfferDAO();
            List<Vehicle> vehicles = vehicleDAO.findByOwner(user.getUserId());

            txtVehicles.setText(""); // clear
            if (vehicles.isEmpty()) {
                txtVehicles.setText("No vehicles registered yet.");
            } else {
                txtVehicles.append(String.format("%-10s %-10s %-10s %-5s %-7s %-10s%n",
                        "Plate", "Type", "Fuel", "Year", "Mileage", "Offer Status"));
                txtVehicles.append("-------------------------------------------------------------\n");

                for (Vehicle v : vehicles) {
                    List<ExchangeOffer> offers = offerDAO.getOffersByVehicle(v.getVehicleId());
                    String status = "No Offer";
                    if (!offers.isEmpty()) {
                        // Take the latest offer's status
                        status = offers.get(offers.size() - 1).getStatus();
                    }
                    txtVehicles.append(String.format("%-10s %-10s %-10s %-5d %-7d %-10s%n",
                            v.getPlateNo(), v.getVehicleType(), v.getFuelType(),
                            v.getManufactureYear(), v.getMileage(), status));
                }
            }
        } catch (SQLException ex) {
            txtVehicles.setText("Error loading vehicles: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAddVehicle) {
            new VehicleForm(user);
        } else if (e.getSource() == btnRefresh) {
            loadVehicles();
        } else if (e.getSource() == btnLogout) {
            new LoginForm();
            dispose();
        }
    }

    // Optional main for testing
    public static void main(String[] args) {
        User dummy = new User();
        dummy.setUserId(1);
        dummy.setName("Test User");
        new CitizenDashboard(dummy);
    }
}
