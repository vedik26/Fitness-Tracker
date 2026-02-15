import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private Connection connection;

    public DatabaseHelper() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bmi_db", "root", "Vinod@2005");
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable() {
        try {
            String createUserTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "username VARCHAR(255) PRIMARY KEY," +
                    "password VARCHAR(255))";
            String createBmiTable = "CREATE TABLE IF NOT EXISTS bmi_records (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(255)," +
                    "name VARCHAR(255)," +
                    "height DOUBLE," +
                    "weight DOUBLE," +
                    "bmi DOUBLE," +
                    "status VARCHAR(255)," +
                    "FOREIGN KEY (username) REFERENCES users(username))";
            Statement stmt = connection.createStatement();
            stmt.execute(createUserTable);
            stmt.execute(createBmiTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void registerUser(String username, String password) {
        try {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean authenticateUser(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void insertRecord(String username, String name, double height, double weight, double bmi, String status) {
        try {
            String query = "INSERT INTO bmi_records (username, name, height, weight, bmi, status) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, name);
            pstmt.setDouble(3, height);
            pstmt.setDouble(4, weight);
            pstmt.setDouble(5, bmi);
            pstmt.setString(6, status);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getPreviousData(String username) {
        List<String> records = new ArrayList<>();
        try {
            String query = "SELECT * FROM bmi_records WHERE username = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String record = String.format("Name: %s, Height: %.2f, Weight: %.2f, BMI: %.2f, Status: %s",
                        rs.getString("name"), rs.getDouble("height"), rs.getDouble("weight"),
                        rs.getDouble("bmi"), rs.getString("status"));
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public void bookAppointment(String patientName, String doctorName, String appointmentDate) {
        try {
            String query = "INSERT INTO appointments (patientName, doctorName, appointmentDate) VALUES (?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, patientName);
            pstmt.setString(2, doctorName);
            pstmt.setString(3, appointmentDate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
