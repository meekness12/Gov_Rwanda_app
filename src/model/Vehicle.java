package model;

public class Vehicle {
    private int vehicleId, ownerId, manufactureYear, mileage;
    private String plateNo, vehicleType, fuelType;

    public Vehicle() {}

    public Vehicle(int ownerId, String plateNo, String vehicleType, String fuelType, int manufactureYear, int mileage) {
        this.ownerId = ownerId;
        this.plateNo = plateNo;
        this.vehicleType = vehicleType;
        this.fuelType = fuelType;
        this.manufactureYear = manufactureYear;
        this.mileage = mileage;
    }

    // Getters & Setters
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int id) { this.vehicleId = id; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getPlateNo() { return plateNo; }
    public void setPlateNo(String plateNo) { this.plateNo = plateNo; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String type) { this.vehicleType = type; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public int getManufactureYear() { return manufactureYear; }
    public void setManufactureYear(int year) { this.manufactureYear = year; }

    public int getMileage() { return mileage; }
    public void setMileage(int mileage) { this.mileage = mileage; }
}
