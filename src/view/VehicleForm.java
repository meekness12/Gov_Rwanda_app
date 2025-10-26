package view;

import dao.VehicleDAO;
import dao.ExchangeOfferDAO;
import model.User;
import model.Vehicle;
import model.ExchangeOffer;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class VehicleForm extends Frame implements ActionListener {

    private final User user;
    private final Label lblPlate, lblType, lblFuel, lblYear, lblMileage, lblMessage;
    private final TextField txtPlate, txtYear, txtMileage;
    private final Choice chType, chFuel;
    private final Button btnAdd;

    public VehicleForm(User user){
        this.user = user;
        setTitle("Register Vehicle");
        setSize(400,400);
        setLayout(null);
        setBackground(Color.PINK);
        setResizable(false);

        // Labels
        lblPlate = new Label("Plate Number:"); lblPlate.setBounds(50,50,100,30); add(lblPlate);
        lblType = new Label("Vehicle Type:"); lblType.setBounds(50,90,100,30); add(lblType);
        lblFuel = new Label("Fuel Type:"); lblFuel.setBounds(50,130,100,30); add(lblFuel);
        lblYear = new Label("Manufacture Year:"); lblYear.setBounds(50,170,120,30); add(lblYear);
        lblMileage = new Label("Mileage:"); lblMileage.setBounds(50,210,100,30); add(lblMileage);
        lblMessage = new Label(""); lblMessage.setBounds(50,250,300,30); lblMessage.setForeground(Color.RED); add(lblMessage);

        // Inputs
        txtPlate = new TextField(); txtPlate.setBounds(160,50,150,30); add(txtPlate);
        chType = new Choice(); chType.add("Car"); chType.add("Bus"); chType.add("Motorcycle"); chType.setBounds(160,90,150,30); add(chType);
        chFuel = new Choice(); chFuel.add("Petrol"); chFuel.add("Diesel"); chFuel.add("Electric"); chFuel.setBounds(160,130,150,30); add(chFuel);
        txtYear = new TextField(); txtYear.setBounds(160,170,150,30); add(txtYear);
        txtMileage = new TextField(); txtMileage.setBounds(160,210,150,30); add(txtMileage);

        // Button
        btnAdd = new Button("Add Vehicle"); btnAdd.setBounds(100,290,150,30); btnAdd.addActionListener(this); add(btnAdd);

        addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e){ dispose(); } });
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        String plate = txtPlate.getText().trim();
        String type = chType.getSelectedItem();
        String fuel = chFuel.getSelectedItem();
        int year, mileage;

        try{
            year = Integer.parseInt(txtYear.getText().trim());
            mileage = Integer.parseInt(txtMileage.getText().trim());
        } catch(NumberFormatException ex){
            lblMessage.setText("Year and Mileage must be numbers");
            return;
        }

        if(plate.isEmpty()){ lblMessage.setText("Plate number required"); return; }

        Vehicle v = new Vehicle(user.getUserId(), plate, type, fuel, year, mileage);

        try{
            VehicleDAO vdao = new VehicleDAO();
            if(vdao.addVehicle(v)){
                lblMessage.setForeground(Color.GREEN);
                lblMessage.setText("Vehicle added successfully!");

                // Automatically create offer for Petrol/Diesel
                if(fuel.equalsIgnoreCase("Petrol") || fuel.equalsIgnoreCase("Diesel")){
                    double exchangeValue = (2025 - year) * 500.0;
                    double subsidy = exchangeValue * 0.2;

                    ExchangeOffer offer = new ExchangeOffer(v.getVehicleId(), exchangeValue, subsidy, "pending");
                    ExchangeOfferDAO edao = new ExchangeOfferDAO();
                    if(edao.createOffer(offer)){
                        lblMessage.setText("Vehicle & Exchange Offer created!");
                    } else {
                        lblMessage.setForeground(Color.RED);
                        lblMessage.setText("Vehicle added but offer creation failed.");
                    }
                }
            } else {
                lblMessage.setForeground(Color.RED);
                lblMessage.setText("Failed to add vehicle. Plate may already exist.");
            }
        } catch(SQLException ex){
            ex.printStackTrace();
            lblMessage.setForeground(Color.RED);
            lblMessage.setText("DB Error: " + ex.getMessage());
        }
    }
}
