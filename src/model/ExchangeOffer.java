package model;

public class ExchangeOffer {
    private int offerId, vehicleId;
    private double exchangeValue, subsidyPercent;
    private String status;

    public ExchangeOffer() {}

    public ExchangeOffer(int vehicleId, double exchangeValue, double subsidyPercent, String status) {
        this.vehicleId = vehicleId;
        this.exchangeValue = exchangeValue;
        this.subsidyPercent = subsidyPercent;
        this.status = status;
    }

    // Getters & Setters
    public int getOfferId() { return offerId; }
    public void setOfferId(int offerId) { this.offerId = offerId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public double getExchangeValue() { return exchangeValue; }
    public void setExchangeValue(double exchangeValue) { this.exchangeValue = exchangeValue; }

    public double getSubsidyPercent() { return subsidyPercent; }
    public void setSubsidyPercent(double subsidyPercent) { this.subsidyPercent = subsidyPercent; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
