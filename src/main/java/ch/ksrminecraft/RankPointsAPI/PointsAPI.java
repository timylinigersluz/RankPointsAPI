package ch.ksrminecraft.RankPointsAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

public class PointsAPI {
    private final Database db;
    private final Connection con;
    private final DatabaseAPI api;
    private final Logger logger;
    private final boolean debug;

    public PointsAPI(String url, String user, String pass, Logger logger, boolean debug) {
        this.logger = logger;
        this.debug = debug;

        this.db = new Database(logger, debug);
        this.db.connect(url, user, pass);
        this.con = db.getConnection();

        if (this.con == null) {
            throw new IllegalStateException("[RankPointsAPI] Connection is null – check DB credentials or URL.");
        }

        this.api = new DatabaseAPI(con, logger);
    }

    public void setPoints(UUID uuid, int points) {
        if (!isConnected()) return;
        if (isStaff(uuid)) {
            if (debug) logger.info("[RankPointsAPI] Skipped setPoints for staff: " + uuid);
            return;
        }

        api.SQLUpdate("INSERT INTO points (UUID, points) VALUES ('" + uuid + "', " + points + ") " +
                "ON DUPLICATE KEY UPDATE points = " + points);
    }

    public void addPoints(UUID uuid, int delta) {
        if (!isConnected()) return;
        if (isStaff(uuid)) {
            if (debug) logger.info("[RankPointsAPI] Skipped addPoints for staff: " + uuid);
            return;
        }

        api.SQLUpdate("INSERT IGNORE INTO points (UUID, points) VALUES ('" + uuid + "', 0)");
        api.SQLUpdate("UPDATE points SET points = points + " + delta + " WHERE UUID = '" + uuid + "'");
    }

    public int getPoints(UUID uuid) {
        if (!isConnected()) return 0;

        api.SQLUpdate("INSERT IGNORE INTO points (UUID, points) VALUES ('" + uuid + "', 0)");
        return api.SQLgetInt("SELECT points FROM points WHERE UUID = '" + uuid + "'");
    }

    private boolean isConnected() {
        if (this.con == null) {
            logger.warning("[RankPointsAPI] No active database connection.");
            return false;
        }
        return true;
    }

    private boolean isStaff(UUID uuid) {
        String sql = "SELECT UUID FROM stafflist WHERE UUID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            logger.warning("[RankPointsAPI] Failed to check stafflist: " + e.getMessage());
            return false;
        }
    }

    public Connection getConnection() {
        return con;
    }
}
