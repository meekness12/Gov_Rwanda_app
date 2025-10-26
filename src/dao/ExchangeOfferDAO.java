package dao;

import db.DBConnection;
import model.ExchangeOffer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeOfferDAO {

    public boolean createOffer(ExchangeOffer offer) throws SQLException {
        double exchangeValue = Math.abs(offer.getExchangeValue());
        double subsidyPercent = Math.abs(offer.getSubsidyPercent());
        String status = offer.getStatus() != null ? offer.getStatus() : "pending";

        String sql = "INSERT INTO exchange_offers (vehicle_id, exchange_value, subsidy_percent, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, offer.getVehicleId());
            ps.setDouble(2, exchangeValue);
            ps.setDouble(3, subsidyPercent);
            ps.setString(4, status);

            int affected = ps.executeUpdate();
            if(affected == 0) return false;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if(rs.next()) {
                    offer.setOfferId(rs.getInt(1));
                }
            }
            return true;
        }
    }

    public List<ExchangeOffer> getAllOffers() throws SQLException {
        List<ExchangeOffer> list = new ArrayList<>();
        String sql = "SELECT * FROM exchange_offers ORDER BY offer_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while(rs.next()) list.add(mapResultSetToOffer(rs));
        }
        return list;
    }

    public List<ExchangeOffer> getOffersByVehicle(int vehicleId) throws SQLException {
        List<ExchangeOffer> list = new ArrayList<>();
        String sql = "SELECT * FROM exchange_offers WHERE vehicle_id = ? ORDER BY offer_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) list.add(mapResultSetToOffer(rs));
            }
        }
        return list;
    }

    public boolean offerExists(int vehicleId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM exchange_offers WHERE vehicle_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public boolean updateStatus(int offerId, String status) throws SQLException {
        if(status == null || status.isBlank()) status = "pending";
        String sql = "UPDATE exchange_offers SET status = ? WHERE offer_id = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, offerId);
            return ps.executeUpdate() > 0;
        }
    }

    private ExchangeOffer mapResultSetToOffer(ResultSet rs) throws SQLException {
        ExchangeOffer o = new ExchangeOffer();
        o.setOfferId(rs.getInt("offer_id"));
        o.setVehicleId(rs.getInt("vehicle_id"));
        o.setExchangeValue(rs.getDouble("exchange_value"));
        o.setSubsidyPercent(rs.getDouble("subsidy_percent"));
        o.setStatus(rs.getString("status"));
        return o;
    }
}
