package ch.ksrminecraft.RankPointsAPI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private Connection connection;

    public void connect(String url, String user, String pass) {
        try {
            // optional, aber helpful for db and debug
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("[RankPointsAPI] MySQL connection established successfully.");

        } catch (ClassNotFoundException e) {
            System.err.println("[RankPointsAPI] MySQL Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("[RankPointsAPI] Failed to connect to the database.");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
