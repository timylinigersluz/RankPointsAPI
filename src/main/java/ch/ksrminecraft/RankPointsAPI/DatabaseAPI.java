package ch.ksrminecraft.RankPointsAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseAPI {
    private final Connection connection;

    public DatabaseAPI(Connection conn) {
        if (conn == null) {
            throw new IllegalArgumentException("Database connection cannot be null.");
        }
        this.connection = conn;
    }

    /**
     * Executes a SQL SELECT query that returns a single integer value.
     * @param query SQL query string (must return a column named "points")
     * @return int value from result or 0 if failed
     */
    public int SQLgetInt(String query) {
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("points");  // column name must match!
            }
        } catch (SQLException e) {
            System.err.println("[RankPointsAPI] SQLgetInt failed: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Executes an SQL update, such as INSERT, UPDATE, or DELETE.
     * @param query SQL update query string
     */
    public void SQLUpdate(String query) {
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[RankPointsAPI] SQLUpdate failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
