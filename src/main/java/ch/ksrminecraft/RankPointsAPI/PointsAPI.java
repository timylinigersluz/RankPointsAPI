package ch.ksrminecraft.RankPointsAPI;

import java.sql.Connection;
import java.util.UUID;

public class PointsAPI {
    private final Database db;
    private final Connection con;
    private final DatabaseAPI api;

    public PointsAPI(String url, String user, String pass) {
        this.db = new Database();
        this.db.connect(url, user, pass);
        this.con = db.getConnection();

        if (this.con == null) {
            throw new IllegalStateException("[RankPointsAPI] Connection is null – check DB credentials or URL.");
        }

        this.api = new DatabaseAPI(con);
    }

    /**
     * Sets the points of a specific player to the given amount.
     * Use this only for hard overwrites.
     */
    public void setPoints(UUID uuid, int points) {
        if (!isConnected()) return;
        api.SQLUpdate("UPDATE points SET points = " + points + " WHERE UUID = '" + uuid + "'");
    }

    /**
     * Adds a number of points to the player’s total score.
     * Negative values will subtract points.
     */
    public void addPoints(UUID uuid, int delta) {
        if (!isConnected()) return;
        int current = api.SQLgetInt("SELECT points FROM points WHERE UUID = '" + uuid + "'");
        int updated = current + delta;
        api.SQLUpdate("UPDATE points SET points = " + updated + " WHERE UUID = '" + uuid + "'");
    }

    /**
     * Returns the current point total for the given player.
     */
    public int getPoints(UUID uuid) {
        if (!isConnected()) return 0;
        return api.SQLgetInt("SELECT points FROM points WHERE UUID = '" + uuid + "'");
    }

    /**
     * Checks if the database connection is active.
     */
    private boolean isConnected() {
        if (this.con == null) {
            System.err.println("[RankPointsAPI] No active database connection.");
            return false;
        }
        return true;
    }
}
