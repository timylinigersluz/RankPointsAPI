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
    private DatabaseAPI api;
    private final Logger logger;
    private final boolean debug;
    private final String dbUrl, dbUser, dbPass;

    public PointsAPI(String url, String user, String pass, Logger logger, boolean debug) {
        this.logger = logger;
        this.debug = debug;
        this.dbUrl = url;
        this.dbUser = user;
        this.dbPass = pass;

        this.db = new Database(logger, debug);
        this.db.connect(url, user, pass);
        this.con = db.getConnection();

        if (this.con == null) {
            throw new IllegalStateException("[RankPointsAPI] Connection is null – check DB credentials or URL.");
        }

        this.api = new DatabaseAPI(con, logger);
    }

    public void setPoints(UUID uuid, int points) {
        ensureConnection();
        if (!isConnected()) return;
        if (isStaff(uuid)) {
            if (debug) logger.info("[RankPointsAPI] Skipped setPoints for staff: " + uuid);
            return;
        }

        api.SQLUpdate("INSERT INTO points (UUID, points) VALUES ('" + uuid + "', " + points + ") " +
                "ON DUPLICATE KEY UPDATE points = " + points);
    }

    public boolean addPoints(UUID uuid, int delta) {
        ensureConnection();
        if (!isConnected()) return false;
        if (isStaff(uuid)) {
            if (debug) logger.info("[RankPointsAPI] Skipped addPoints for staff: " + uuid);
            return false;
        }

        api.SQLUpdate("INSERT IGNORE INTO points (UUID, points) VALUES ('" + uuid + "', 0)");
        api.SQLUpdate("UPDATE points SET points = points + " + delta + " WHERE UUID = '" + uuid + "'");
        return true;
    }

    public int getPoints(UUID uuid) {
        ensureConnection();
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


    private void ensureConnection() {
        try {
            if (con == null || con.isClosed() || !con.isValid(2)) {
                logger.warning("[RankPointsAPI] Lost DB connection – reconnecting...");
                db.connect(dbUrl, dbUser, dbPass); // du brauchst Felder für diese Strings
                this.api = new DatabaseAPI(db.getConnection(), logger);
            }
        } catch (SQLException e) {
            logger.severe("[RankPointsAPI] DB reconnect failed: " + e.getMessage());
        }
    }
}
