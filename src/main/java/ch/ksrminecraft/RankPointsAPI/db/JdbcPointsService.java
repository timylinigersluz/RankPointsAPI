package ch.ksrminecraft.RankPointsAPI.db;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Objects;
import java.util.UUID;

public final class JdbcPointsService {
    private final DataSource ds;

    private static final String SQL_ADD =
            "INSERT INTO points (UUID, points) " +
                    "SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM stafflist s WHERE s.UUID = ?) " +
                    "ON DUPLICATE KEY UPDATE points = points + VALUES(points)";

    private static final String SQL_SET =
            "INSERT INTO points (UUID, points) " +
                    "SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM stafflist s WHERE s.UUID = ?) " +
                    "ON DUPLICATE KEY UPDATE points = VALUES(points)";

    private static final String SQL_GET =
            "SELECT COALESCE(points, 0) FROM points WHERE UUID = ?";

    public JdbcPointsService(DataSource ds) {
        this.ds = Objects.requireNonNull(ds);
    }

    public void addPoints(UUID uuid, int delta) throws SQLException {
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_ADD)) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, delta);
            ps.setString(3, uuid.toString());
            ps.executeUpdate();
        }
    }

    public void setPoints(UUID uuid, int points) throws SQLException {
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_SET)) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, points);
            ps.setString(3, uuid.toString());
            ps.executeUpdate();
        }
    }

    public int getPoints(UUID uuid) throws SQLException {
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_GET)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}
