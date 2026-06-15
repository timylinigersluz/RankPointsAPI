package ch.ksrminecraft.RankPointsAPI;

import ch.ksrminecraft.RankPointsAPI.db.HikariDataSourceFactory;
import ch.ksrminecraft.RankPointsAPI.db.JdbcPointsService;
import ch.ksrminecraft.RankPointsAPI.db.SchemaInitializer;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PointsAPI implements RankPointsService {
    private final Logger logger;
    private final boolean debug;
    private final boolean excludeStaff;

    private final DataSource ds;
    private final JdbcPointsService service;

    public PointsAPI(String jdbcUrl, String user, String pass, Logger logger, boolean debug, boolean excludeStaff) {
        this.logger = logger != null ? logger : Logger.getLogger("RankPointsAPI");
        this.debug = debug;
        this.excludeStaff = excludeStaff;

        DataSource createdDataSource = HikariDataSourceFactory.create(jdbcUrl, user, pass, debug);
        try {
            SchemaInitializer.ensure(createdDataSource, this.logger);
            this.service = new JdbcPointsService(createdDataSource, excludeStaff);
            this.ds = createdDataSource;
        } catch (SQLException e) {
            closeDataSource(createdDataSource);
            this.logger.log(Level.SEVERE, "Failed to ensure schema", e);
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            closeDataSource(createdDataSource);
            throw e;
        }
    }

    /**
     * Adds points to the given player UUID.
     * Staff exclusion and database handling are managed internally by RankPointsAPI.
     * Negative values are allowed and subtract points.
     *
     * @param uuid player UUID
     * @param delta amount of points to add
     */
    @Override
    public void addPoints(UUID uuid, int delta) {
        if (uuid == null) {
            logger.warning("addPoints ignored because UUID is null.");
            return;
        }

        try {
            service.addPoints(uuid, delta);
            if (debug) logger.info(() -> "addPoints " + uuid + " +" + delta);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "addPoints failed for " + uuid, e);
        }
    }

    /**
     * Sets the point total for the given player UUID.
     * Staff exclusion and database handling are managed internally by RankPointsAPI.
     *
     * @param uuid player UUID
     * @param points new point total
     */
    @Override
    public void setPoints(UUID uuid, int points) {
        if (uuid == null) {
            logger.warning("setPoints ignored because UUID is null.");
            return;
        }

        try {
            service.setPoints(uuid, points);
            if (debug) logger.info(() -> "setPoints " + uuid + " = " + points);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "setPoints failed for " + uuid, e);
        }
    }

    /**
     * Returns the current point total for the given player UUID.
     * Returns 0 if the player has no entry or if the point lookup fails.
     *
     * @param uuid player UUID
     * @return current point total
     */
    @Override
    public int getPoints(UUID uuid) {
        if (uuid == null) {
            logger.warning("getPoints returned 0 because UUID is null.");
            return 0;
        }

        try {
            int p = service.getPoints(uuid);
            if (debug) logger.info(() -> "getPoints " + uuid + " -> " + p);
            return p;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "getPoints failed for " + uuid, e);
            return 0;
        }
    }

    /**
     * Removes points from the given player UUID.
     * Staff exclusion and database handling are managed internally by RankPointsAPI.
     *
     * @param uuid player UUID
     * @param delta positive amount of points to remove
     */
    @Override
    public void removePoints(UUID uuid, int delta) {
        if (delta < 0) {
            logger.warning("removePoints ignored because delta is negative: " + delta);
            return;
        }

        addPoints(uuid, -delta);
    }

    public boolean isExcludeStaffEnabled() {
        return excludeStaff;
    }

    public void close() {
        closeDataSource(ds);
    }

    private void closeDataSource(DataSource dataSource) {
        if (dataSource == null) {
            return;
        }

        if (dataSource instanceof HikariDataSource hikari && !hikari.isClosed()) {
            hikari.close();
            if (debug) {
                logger.info("RankPointsAPI datasource closed.");
            }
        }
    }
}
