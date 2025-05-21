package ch.ksrminecraft.RankPointsAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseAPI {
    private final Connection connection;
    private final Logger logger;

    public DatabaseAPI(Connection conn, Logger logger) {
        if (conn == null) {
            throw new IllegalArgumentException("Database connection cannot be null.");
        }
        this.connection = conn;
        this.logger = logger;
    }

    public int SQLgetInt(String query) {
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("points");
            }
        } catch (SQLException e) {
            logger.warning("[RankPointsAPI] SQLgetInt failed: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public void SQLUpdate(String query) {
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.warning("[RankPointsAPI] SQLUpdate failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
