# Citizen Dashboard - GVEI

A Java AWT-based desktop application that allows citizens to manage their vehicles and view the status of exchange offers. The dashboard combines features of adding, editing, deleting, and viewing vehicles, as well as displaying the latest offer status for each vehicle.

## Features

- **View Vehicles**: See all your registered vehicles in a scrollable list.
- **Add Vehicle**: Add a new vehicle with details like plate number, type, fuel type, manufacture year, and mileage.
- **Edit Vehicle**: Modify any details of an existing vehicle.
- **Delete Vehicle**: Remove a vehicle from your account.
- **Refresh Vehicle List**: Reload the list to see updates instantly.
- **View Exchange Offer Status**: Displays the latest offer status for each vehicle (e.g., pending, approved, rejected, or no offer).

## Project Structure

view/
├── CitizenDashboard.java # Main combined dashboard with Add/Edit/Delete functionality
├── VehicleForm.java # Optional individual form for adding a vehicle
├── LoginForm.java # Login window
dao/
├── VehicleDAO.java # Database operations for vehicles
├── ExchangeOfferDAO.java # Database operations for exchange offers
model/
├── Vehicle.java # Vehicle model class
├── User.java # User model class
├── ExchangeOffer.java # Exchange offer model class


## Requirements

- Java Development Kit (JDK) 8 or higher
- JDBC-compatible database with `vehicles` and `exchange_offers` tables
- AWT and Java Swing (part of standard JDK)

## Installation

1. Clone or download the repository:
   ```bash
   git clone <repository-url>

Make sure your database is set up and update DAO classes with the correct connection settings.

Compile the Java files:

javac view/*.java dao/*.java model/*.java


Run the dashboard:

java view.CitizenDashboard

Usage

Login with a valid user account.

Use the Add Vehicle button to register a new vehicle.

Edit or delete any vehicle using the corresponding buttons in the vehicle list.

Refresh the vehicle list with the Refresh Vehicles button.

See the latest exchange offer status for each vehicle directly in the list.
