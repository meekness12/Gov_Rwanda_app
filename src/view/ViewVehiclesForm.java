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

public class ViewVehiclesForm extends Frame implements ActionListener {

    private final User currentUser;
    private final TextArea txtVehicles;
    private final Button btnBack, btnRefresh;

    public ViewVehiclesForm(User user) {
        this.currentUser = user;

        // Frame settings
        setTitle("GVEI - My Vehicles & Offers");
        setSize(600, 450);
        setLayout(null);
        setBackground(Color.LIGHT_GRAY);
        setResizable(false);

        // TextArea to display vehicles
        txtVehicles = new TextArea();
        txtVehicles.setBounds(30, 50, 540, 300);
        txtVehicles.setEditable(false);
        add(txtVehicles);

        // Buttons
        btnRefresh = new Button("Refresh");
        btnRefresh.setBounds(30, 370, 120, 40);
        btnRefresh.addActionListener(this);
        add(btnRefresh);

        btnBack = new Button("Back to Dashboard");
        btnBack.setBounds(150, 370, 150, 40);
        btnBack.addActionListener(this);
        add(btnBack);

        // Window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                new CitizenDashboard(currentUser);
            }
        });

        loadVehiclesWithStatus();
        setVisible(true);
    }

    private void loadVehiclesWithStatus() {
        txtVehicles.setText(""); // Clear previous
        try {
            VehicleDAO vehicleDAO = new VehicleDAO();
            ExchangeOfferDAO offerDAO = new ExchangeOfferDAO();

            List<Vehicle> vehicles = vehicleDAO.findByOwner(currentUser.getUserId());

            if (vehicles.isEmpty()) {
                txtVehicles.setText("No vehicles registered yet.");
            } else {
                for (Vehicle v : vehicles) {
                    txtVehicles.append(String.format(
                            "Plate: %s | Type: %s | Fuel: %s | Year: %d | Mileage: %d%n",
                            v.getPlateNo(),
                            v.getVehicleType(),
                            v.getFuelType(),
                            v.getManufactureYear(),
                            v.getMileage()
                    ));

                    // Get latest offer for this vehicle
                    List<ExchangeOffer> offers = offerDAO.getAllOffers();
                    ExchangeOffer latest = offers.stream()
                            .filter(o -> o.getVehicleId() == v.getVehicleId())
                            .reduce((first, second) -> second) // last offer
                            .orElse(null);

                    String statusDisplay = "No offers";
                    if (latest != null) {
                        switch (latest.getStatus().toLowerCase()) {
                            case "approved":
                                statusDisplay = "[APPROVED]";
                                break;
                            case "rejected":
                                statusDisplay = "[REJECTED]";
                                break;
                            case "pending":
                                statusDisplay = "[PENDING]";
                                break;
                        }
                    }
                    txtVehicles.append("Latest Offer Status: " + statusDisplay + "\n\n");
                }
            }

        } catch (SQLException ex) {
            txtVehicles.setText("Error loading vehicles: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRefresh) {
            loadVehiclesWithStatus();
        } else if (e.getSource() == btnBack) {
            new CitizenDashboard(currentUser);
            dispose();
        }
    }

    // Optional main for standalone testing
    public static void main(String[] args) {
        User dummy = new User();
        dummy.setUserId(1);
        dummy.setName("Test User");
        new ViewVehiclesForm(dummy);
    }
}
