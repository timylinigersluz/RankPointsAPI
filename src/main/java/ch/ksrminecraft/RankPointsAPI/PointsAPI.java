package ch.ksrminecraft.RankPointsAPI;

import ch.ksrminecraft.RankPointsAPI.db.HikariDataSourceFactory;
import ch.ksrminecraft.RankPointsAPI.db.SchemaInitializer;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PointsAPI {
    private final Logger logger;
    private final boolean debug;

    private final DataSource ds;
    private final ch.ksrminecraft.RankPointsAPI.db.JdbcPointsService service;

    public PointsAPI(String jdbcUrl, String user, String pass, Logger logger, boolean debug) {
        this.logger = logger != null ? logger : Logger.getLogger("RankPointsAPI");
        this.debug = debug;

        this.ds = HikariDataSourceFactory.create(jdbcUrl, user, pass, debug);
        try {
            SchemaInitializer.ensure(ds, this.logger);
        } catch (Exception e) {
            this.logger.log(Level.SEVERE, "Failed to ensure schema", e);
            throw new RuntimeException(e);
        }
        this.service = new ch.ksrminecraft.RankPointsAPI.db.JdbcPointsService(ds);
    }

    public void addPoints(UUID uuid, int delta) {
        try {
            service.addPoints(uuid, delta);
            if (debug) logger.info(() -> "addPoints " + uuid + " +" + delta);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "addPoints failed for " + uuid, e);
        }
    }

    public void setPoints(UUID uuid, int points) {
        try {
            service.setPoints(uuid, points);
            if (debug) logger.info(() -> "setPoints " + uuid + " = " + points);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "setPoints failed for " + uuid, e);
        }
    }

    public int getPoints(UUID uuid) {
        try {
            int p = service.getPoints(uuid);
            if (debug) logger.info(() -> "getPoints " + uuid + " -> " + p);
            return p;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "getPoints failed for " + uuid, e);
            return 0;
        }
    }
}
