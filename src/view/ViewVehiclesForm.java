package view;

import dao.VehicleDAO;
import model.User;
import model.Vehicle;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class ViewVehiclesForm extends Frame {

    private final User user;
    private final Panel pnlVehicles;
    private List<Vehicle> vehicles;
    private final VehicleDAO vehicleDAO;
    private final Runnable onUpdateCallback; // callback to notify AdminDashboard

    // Updated constructor to include callback
    public ViewVehiclesForm(User user, Runnable onUpdateCallback) {
        this.user = user;
        this.vehicleDAO = new VehicleDAO();
        this.onUpdateCallback = onUpdateCallback;

        setTitle("My Vehicles");
        setSize(700, 500);
        setLayout(new BorderLayout());

        pnlVehicles = new Panel();
        pnlVehicles.setLayout(new GridLayout(0, 1, 5, 5));
        ScrollPane scroll = new ScrollPane();
        scroll.add(pnlVehicles);
        add(scroll, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        loadVehicles();
        setVisible(true);
    }

    private void loadVehicles() {
        pnlVehicles.removeAll();
        try {
            vehicles = vehicleDAO.getVehiclesByOwner(user.getUserId());
            if (vehicles.isEmpty()) {
                pnlVehicles.add(new Label("No vehicles registered."));
            } else {
                for (Vehicle v : vehicles) {
                    Panel panel = new Panel(new FlowLayout(FlowLayout.LEFT));

                    Label lbl = new Label("ID: " + v.getVehicleId() +
                            " | Plate: " + v.getPlateNo() +
                            " | Type: " + v.getVehicleType() +
                            " | Fuel: " + v.getFuelType() +
                            " | Year: " + v.getManufactureYear() +
                            " | Mileage: " + v.getMileage());
                    panel.add(lbl);

                    // Edit Button
                    Button btnEdit = new Button("Edit");
                    btnEdit.addActionListener(e -> showEditDialog(v));
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
            ex.printStackTrace();
            pnlVehicles.add(new Label("Error loading vehicles: " + ex.getMessage()));
        }
    }

    private void showEditDialog(Vehicle v) {
        Dialog dlg = new Dialog(this, "Edit Vehicle", true);
        dlg.setSize(350, 350);
        dlg.setLayout(null);

        Label lblPlate = new Label("Plate:");
        lblPlate.setBounds(30, 40, 100, 30);
        dlg.add(lblPlate);

        TextField txtPlate = new TextField(v.getPlateNo());
        txtPlate.setBounds(140, 40, 150, 30);
        dlg.add(txtPlate);

        Label lblType = new Label("Type:");
        lblType.setBounds(30, 80, 100, 30);
        dlg.add(lblType);

        Choice chType = new Choice();
        chType.add("Car");
        chType.add("Bus");
        chType.add("Motorcycle");
        chType.select(v.getVehicleType());
        chType.setBounds(140, 80, 150, 30);
        dlg.add(chType);

        Label lblFuel = new Label("Fuel:");
        lblFuel.setBounds(30, 120, 100, 30);
        dlg.add(lblFuel);

        Choice chFuel = new Choice();
        chFuel.add("Petrol");
        chFuel.add("Diesel");
        chFuel.add("Electric");
        chFuel.select(v.getFuelType());
        chFuel.setBounds(140, 120, 150, 30);
        dlg.add(chFuel);

        Label lblYear = new Label("Year:");
        lblYear.setBounds(30, 160, 100, 30);
        dlg.add(lblYear);

        TextField txtYear = new TextField(String.valueOf(v.getManufactureYear()));
        txtYear.setBounds(140, 160, 150, 30);
        dlg.add(txtYear);

        Label lblMileage = new Label("Mileage:");
        lblMileage.setBounds(30, 200, 100, 30);
        dlg.add(lblMileage);

        TextField txtMileage = new TextField(String.valueOf(v.getMileage()));
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

            v.setPlateNo(plate);
            v.setVehicleType(type);
            v.setFuelType(fuel);
            v.setManufactureYear(year);
            v.setMileage(mileage);

            try {
                if (vehicleDAO.updateVehicle(v)) {
                    dlg.dispose();
                    loadVehicles();
                    if (onUpdateCallback != null) onUpdateCallback.run(); // refresh AdminDashboard
                } else {
                    lblMessage.setText("Update failed.");
                }
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

    private void deleteVehicle(Vehicle v) {
        Dialog confirm = new Dialog(this, "Confirm Delete", true);
        confirm.setSize(250, 150);
        confirm.setLayout(new FlowLayout());

        Label lbl = new Label("Delete vehicle " + v.getPlateNo() + "?");
        confirm.add(lbl);

        Button btnYes = new Button("Yes");
        btnYes.addActionListener(e -> {
            try {
                if (vehicleDAO.deleteVehicle(v.getVehicleId())) {
                    confirm.dispose();
                    loadVehicles();
                    if (onUpdateCallback != null) onUpdateCallback.run(); // refresh AdminDashboard
                }
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

    // Optional main for testing
    public static void main(String[] args) {
        User testUser = new User();
        testUser.setUserId(1);
        testUser.setName("Test User");
        new ViewVehiclesForm(testUser, null); // null callback for standalone test
    }
}
