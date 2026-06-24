package ch.ksrminecraft.RankPointsAPI.db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

/**
 * Fuehrt die eigentlichen SQL-Operationen aus.
 *
 * Wichtig:
 * - Es wird keine Connection als Feld gehalten.
 * - Jede Methode holt sich eine Connection aus dem Pool.
 * - Connection, PreparedStatement und ResultSet werden mit try-with-resources geschlossen.
 */
public final class JdbcPointsService {

    private final DataSource ds;
    private final boolean excludeStaff;

    private static final String SQL_ADD_EXCLUDE =
            "INSERT INTO points (UUID, points) " +
                    "SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM stafflist s WHERE s.UUID = ?) " +
                    "ON DUPLICATE KEY UPDATE points = points + VALUES(points)";

    private static final String SQL_ADD_NORMAL =
            "INSERT INTO points (UUID, points) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE points = points + VALUES(points)";

    private static final String SQL_SET_EXCLUDE =
            "INSERT INTO points (UUID, points) " +
                    "SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM stafflist s WHERE s.UUID = ?) " +
                    "ON DUPLICATE KEY UPDATE points = VALUES(points)";

    private static final String SQL_SET_NORMAL =
            "INSERT INTO points (UUID, points) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE points = VALUES(points)";

    private static final String SQL_GET =
            "SELECT COALESCE(points, 0) FROM points WHERE UUID = ?";

    public JdbcPointsService(DataSource ds, boolean excludeStaff) {
        this.ds = Objects.requireNonNull(ds, "DataSource darf nicht null sein");
        this.excludeStaff = excludeStaff;
    }

    public void addPoints(UUID uuid, int delta) throws SQLException {
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(excludeStaff ? SQL_ADD_EXCLUDE : SQL_ADD_NORMAL)) {

            ps.setString(1, uuid.toString());
            ps.setInt(2, delta);

            if (excludeStaff) {
                ps.setString(3, uuid.toString());
            }

            ps.executeUpdate();
        }
    }

    public void setPoints(UUID uuid, int points) throws SQLException {
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(excludeStaff ? SQL_SET_EXCLUDE : SQL_SET_NORMAL)) {

            ps.setString(1, uuid.toString());
            ps.setInt(2, points);

            if (excludeStaff) {
                ps.setString(3, uuid.toString());
            }

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