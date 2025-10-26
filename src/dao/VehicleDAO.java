package dao;

import db.DBConnection;
import model.Vehicle;

import java.sql.*;
import java.util.ArrayList;

public class VehicleDAO {

    // Add a new vehicle
    public boolean addVehicle(Vehicle v) throws SQLException {
        String sql = "INSERT INTO vehicles (owner_id, plate_no, vehicle_type, fuel_type, manufacture_year, mileage) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, v.getOwnerId());
            ps.setString(2, v.getPlateNo());
            ps.setString(3, v.getVehicleType());
            ps.setString(4, v.getFuelType());
            ps.setInt(5, v.getManufactureYear());
            ps.setInt(6, v.getMileage());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) v.setVehicleId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLIntegrityConstraintViolationException ex) {
            System.err.println("Error: Vehicle with this plate number already exists.");
            return false;
        }
    }

    // Get vehicles by owner
    public ArrayList<Vehicle> getVehiclesByOwner(int ownerId) throws SQLException {
        ArrayList<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE owner_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vehicle v = new Vehicle();
                    v.setVehicleId(rs.getInt("vehicle_id"));
                    v.setOwnerId(rs.getInt("owner_id"));
                    v.setPlateNo(rs.getString("plate_no"));
                    v.setVehicleType(rs.getString("vehicle_type"));
                    v.setFuelType(rs.getString("fuel_type"));
                    v.setManufactureYear(rs.getInt("manufacture_year"));
                    v.setMileage(rs.getInt("mileage"));
                    list.add(v);
                }
            }
        }
        return list;
    }

    // Alias for backward compatibility
    public ArrayList<Vehicle> findByOwner(int ownerId) throws SQLException {
        return getVehiclesByOwner(ownerId);
    }

    // Get all vehicles
    public ArrayList<Vehicle> getAllVehicles() throws SQLException {
        ArrayList<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicles";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Vehicle v = new Vehicle();
                v.setVehicleId(rs.getInt("vehicle_id"));
                v.setOwnerId(rs.getInt("owner_id"));
                v.setPlateNo(rs.getString("plate_no"));
                v.setVehicleType(rs.getString("vehicle_type"));
                v.setFuelType(rs.getString("fuel_type"));
                v.setManufactureYear(rs.getInt("manufacture_year"));
                v.setMileage(rs.getInt("mileage"));
                list.add(v);
            }
        }
        return list;
    }

    // ✅ Update vehicle
    public boolean updateVehicle(Vehicle v) throws SQLException {
        String sql = "UPDATE vehicles SET plate_no = ?, vehicle_type = ?, fuel_type = ?, manufacture_year = ?, mileage = ? WHERE vehicle_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, v.getPlateNo());
            ps.setString(2, v.getVehicleType());
            ps.setString(3, v.getFuelType());
            ps.setInt(4, v.getManufactureYear());
            ps.setInt(5, v.getMileage());
            ps.setInt(6, v.getVehicleId());

            return ps.executeUpdate() > 0;
        }
    }

    // ✅ Delete vehicle
    public boolean deleteVehicle(int vehicleId) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE vehicle_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, vehicleId);
            return ps.executeUpdate() > 0;
        }
    }
}
