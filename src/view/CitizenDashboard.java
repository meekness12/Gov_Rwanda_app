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

public class CitizenDashboard extends Frame {

    private final User user;
    private final Panel pnlVehicles;
    private final VehicleDAO vehicleDAO;

    public CitizenDashboard(User user) {
        this.user = user;
        this.vehicleDAO = new VehicleDAO();

        setTitle("Citizen Dashboard - GVEI");
        setSize(750, 600);
        setLayout(new BorderLayout());
        setBackground(Color.CYAN);

        // Top panel with welcome label and logout
        Panel topPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        Label lblWelcome = new Label("Welcome, " + user.getName());
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(lblWelcome);

        Button btnLogout = new Button("Logout");
        btnLogout.addActionListener(e -> {
            new LoginForm();
            dispose();
        });
        topPanel.add(btnLogout);
        add(topPanel, BorderLayout.NORTH);

        // Center panel for vehicle list
        pnlVehicles = new Panel();
        pnlVehicles.setLayout(new GridLayout(0, 1, 5, 5));
        ScrollPane scroll = new ScrollPane();
        scroll.add(pnlVehicles);
        add(scroll, BorderLayout.CENTER);

        // Bottom panel for Add & Refresh
        Panel bottomPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        Button btnAddVehicle = new Button("Add Vehicle");
        btnAddVehicle.addActionListener(e -> showAddVehicleDialog());
        bottomPanel.add(btnAddVehicle);

        Button btnRefresh = new Button("Refresh Vehicles");
        btnRefresh.addActionListener(e -> loadVehicles());
        bottomPanel.add(btnRefresh);

        add(bottomPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); }
        });

        loadVehicles();
        setVisible(true);
    }

    private void loadVehicles() {
        pnlVehicles.removeAll();
        try {
            List<Vehicle> vehicles = vehicleDAO.getVehiclesByOwner(user.getUserId());
            ExchangeOfferDAO offerDAO = new ExchangeOfferDAO();

            if (vehicles.isEmpty()) {
                pnlVehicles.add(new Label("No vehicles registered."));
            } else {
                for (Vehicle v : vehicles) {
                    Panel panel = new Panel(new FlowLayout(FlowLayout.LEFT));

                    List<ExchangeOffer> offers = offerDAO.getOffersByVehicle(v.getVehicleId());
                    String status = offers.isEmpty() ? "No Offer" :
                            offers.get(offers.size() - 1).getStatus();

                    Label lbl = new Label("ID: " + v.getVehicleId() +
                            " | Plate: " + v.getPlateNo() +
                            " | Type: " + v.getVehicleType() +
                            " | Fuel: " + v.getFuelType() +
                            " | Year: " + v.getManufactureYear() +
                            " | Mileage: " + v.getMileage() +
                            " | Offer: " + status);
                    panel.add(lbl);

                    // Edit Button
                    Button btnEdit = new Button("Edit");
                    btnEdit.addActionListener(e -> showEditVehicleDialog(v));
                    panel.add(btnEdit);

                    // Delete Button
                    Button btnDelete = new Button("Delete");
                    btnDelete.addActionListener(e -> deleteVehicle(v));
                    panel.add(btnDelete);

                    pnlVehicles.add(panel);
                }
            }
            pnlVehicles.revalidate();
            pnlVehicles.repaint();
        } catch (SQLException ex) {
            pnlVehicles.add(new Label("Error loading vehicles: " + ex.getMessage()));
            ex.printStackTrace();
        }
    }

    private void showAddVehicleDialog() {
        showVehicleDialog(null); // null = adding new vehicle
    }

    private void showEditVehicleDialog(Vehicle vehicle) {
        showVehicleDialog(vehicle); // vehicle != null = editing
    }

    private void showVehicleDialog(Vehicle vehicle) {
        Dialog dlg = new Dialog(this, vehicle == null ? "Add Vehicle" : "Edit Vehicle", true);
        dlg.setSize(350, 350);
        dlg.setLayout(null);

        Label lblPlate = new Label("Plate:");
        lblPlate.setBounds(30, 40, 100, 30);
        dlg.add(lblPlate);

        TextField txtPlate = new TextField(vehicle != null ? vehicle.getPlateNo() : "");
        txtPlate.setBounds(140, 40, 150, 30);
        dlg.add(txtPlate);

        Label lblType = new Label("Type:");
        lblType.setBounds(30, 80, 100, 30);
        dlg.add(lblType);

        Choice chType = new Choice();
        chType.add("Car");
        chType.add("Bus");
        chType.add("Motorcycle");
        if (vehicle != null) chType.select(vehicle.getVehicleType());
        chType.setBounds(140, 80, 150, 30);
        dlg.add(chType);

        Label lblFuel = new Label("Fuel:");
        lblFuel.setBounds(30, 120, 100, 30);
        dlg.add(lblFuel);

        Choice chFuel = new Choice();
        chFuel.add("Petrol");
        chFuel.add("Diesel");
        chFuel.add("Electric");
        if (vehicle != null) chFuel.select(vehicle.getFuelType());
        chFuel.setBounds(140, 120, 150, 30);
        dlg.add(chFuel);

        Label lblYear = new Label("Year:");
        lblYear.setBounds(30, 160, 100, 30);
        dlg.add(lblYear);

        TextField txtYear = new TextField(vehicle != null ? String.valueOf(vehicle.getManufactureYear()) : "");
        txtYear.setBounds(140, 160, 150, 30);
        dlg.add(txtYear);

        Label lblMileage = new Label("Mileage:");
        lblMileage.setBounds(30, 200, 100, 30);
        dlg.add(lblMileage);

        TextField txtMileage = new TextField(vehicle != null ? String.valueOf(vehicle.getMileage()) : "");
        txtMileage.setBounds(140, 200, 150, 30);
        dlg.add(txtMileage);

        Label lblMessage = new Label("");
        lblMessage.setBounds(30, 240, 300, 30);
        lblMessage.setForeground(Color.RED);
        dlg.add(lblMessage);

        Button btnSave = new Button("Save");
        btnSave.setBounds(50, 280, 100, 30);
        btnSave.addActionListener(e -> {
            String plate = txtPlate.getText().trim();
            String type = chType.getSelectedItem();
            String fuel = chFuel.getSelectedItem();
            int year, mileage;

            try {
                year = Integer.parseInt(txtYear.getText().trim());
                mileage = Integer.parseInt(txtMileage.getText().trim());
            } catch (NumberFormatException ex) {
                lblMessage.setText("Year and Mileage must be numbers.");
                return;
            }

            if (plate.isEmpty()) {
                lblMessage.setText("Plate required.");
                return;
            }

            try {
                if (vehicle == null) {
                    // Add new vehicle
                    Vehicle v = new Vehicle();
                    v.setPlateNo(plate);
                    v.setVehicleType(type);
                    v.setFuelType(fuel);
                    v.setManufactureYear(year);
                    v.setMileage(mileage);
                    v.setOwnerId(user.getUserId());

                    vehicleDAO.addVehicle(v);
                } else {
                    // Update existing vehicle
                    vehicle.setPlateNo(plate);
                    vehicle.setVehicleType(type);
                    vehicle.setFuelType(fuel);
                    vehicle.setManufactureYear(year);
                    vehicle.setMileage(mileage);

                    vehicleDAO.updateVehicle(vehicle);
                }
                dlg.dispose();
                loadVehicles();
            } catch (SQLException ex) {
                ex.printStackTrace();
                lblMessage.setText("DB Error: " + ex.getMessage());
            }
        });
        dlg.add(btnSave);

        Button btnCancel = new Button("Cancel");
        btnCancel.setBounds(180, 280, 100, 30);
        btnCancel.addActionListener(e -> dlg.dispose());
        dlg.add(btnCancel);

        dlg.setVisible(true);
    }

    private void deleteVehicle(Vehicle vehicle) {
        Dialog confirm = new Dialog(this, "Confirm Delete", true);
        confirm.setSize(250, 150);
        confirm.setLayout(new FlowLayout());

        Label lbl = new Label("Delete vehicle " + vehicle.getPlateNo() + "?");
        confirm.add(lbl);

        Button btnYes = new Button("Yes");
        btnYes.addActionListener(e -> {
            try {
                vehicleDAO.deleteVehicle(vehicle.getVehicleId());
                confirm.dispose();
                loadVehicles();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        confirm.add(btnYes);

        Button btnNo = new Button("No");
        btnNo.addActionListener(e -> confirm.dispose());
        confirm.add(btnNo);

        confirm.setVisible(true);
    }

    // Test main
    public static void main(String[] args) {
        User dummy = new User();
        dummy.setUserId(1);
        dummy.setName("Test Citizen");
        new CitizenDashboard(dummy);
    }
}
