package ch.ksrminecraft.RankPointsAPI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Database {
    private Connection connection;
    private final Logger logger;
    private final boolean debug;

    public Database(Logger logger, boolean debug) {
        this.logger = logger;
        this.debug = debug;
    }

    public void connect(String url, String user, String pass) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, pass);
            logger.info("[RankPointsAPI] MySQL connection established.");

            ensurePointsTable();
            ensureStafflistTable();
        } catch (ClassNotFoundException e) {
            logger.severe("[RankPointsAPI] MySQL Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            logger.severe("[RankPointsAPI] Connection failed: " + e.getMessage());
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
            if (debug) logger.info("[RankPointsAPI] Table 'points' checked/created.");
        }
    }

    private void ensureStafflistTable() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS stafflist (" +
                        "UUID VARCHAR(36) PRIMARY KEY," +
                        "name VARCHAR(50) NOT NULL)"
        )) {
            ps.executeUpdate();
            if (debug) logger.info("[RankPointsAPI] Table 'stafflist' checked/created.");
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
