package db;

import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        try(Connection conn = DBConnection.getConnection()) {
            System.out.println("? Database connected successfully!");
        } catch(Exception e) {
            System.out.println("? Connection failed: " + e.getMessage());
        }
    }
}
