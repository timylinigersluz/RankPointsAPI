package ch.ksrminecraft.RankPointsAPI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {
    private Connection connection;

    public void connect(String url, String user, String pass) {
        try {
            // Register shaded driver explicitly
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("[RankPointsAPI] MySQL connection established.");

            ensurePointsTable();
            ensureStafflistTable();

        } catch (ClassNotFoundException e) {
            System.err.println("[RankPointsAPI] MySQL Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("[RankPointsAPI] Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void ensurePointsTable() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS points (" +
                        "UUID VARCHAR(36) PRIMARY KEY," +
                        "points INT NOT NULL DEFAULT 0)"
        )) {
            ps.executeUpdate();
            System.out.println("[RankPointsAPI] Table 'points' checked/created.");
        }
    }

    private void ensureStafflistTable() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS stafflist (" +
                        "UUID VARCHAR(36) PRIMARY KEY," +
                        "name VARCHAR(50) NOT NULL)"
        )) {
            ps.executeUpdate();
            System.out.println("[RankPointsAPI] Table 'stafflist' checked/created.");
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
