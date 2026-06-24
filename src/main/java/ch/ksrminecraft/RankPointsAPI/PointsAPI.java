package ch.ksrminecraft.RankPointsAPI;

import ch.ksrminecraft.RankPointsAPI.db.DbErrorThrottle;
import ch.ksrminecraft.RankPointsAPI.db.HikariDataSourceFactory;
import ch.ksrminecraft.RankPointsAPI.db.JdbcPointsService;
import ch.ksrminecraft.RankPointsAPI.db.SchemaInitializer;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Zentrale API fuer Punktezugriffe.
 *
 * Wichtige Robustheitsregeln:
 * - Es wird keine dauerhafte Connection gehalten.
 * - Jede DB-Operation holt bei Bedarf kurz eine Connection aus dem Pool.
 * - Wenn die DB beim Start nicht erreichbar ist, wird das Plugin/API-Objekt trotzdem erstellt.
 * - Die Schema-Pruefung wird beim naechsten Zugriff erneut versucht.
 * - Wiederholte identische DB-Fehler werden gedrosselt geloggt.
 */
public class PointsAPI {

    private final Logger logger;
    private final boolean debug;
    private final boolean excludeStaff;

    private final DataSource ds;
    private final JdbcPointsService service;
    private final DbErrorThrottle dbErrorThrottle;

    private final Object schemaLock = new Object();

    /**
     * Wird erst true, wenn die Tabellen erfolgreich geprueft oder erstellt wurden.
     * Falls MariaDB beim Start nicht erreichbar ist, bleibt dies false.
     * Beim naechsten API-Zugriff wird dann erneut versucht, das Schema sicherzustellen.
     */
    private volatile boolean schemaReady = false;

    public PointsAPI(String jdbcUrl, String user, String pass, Logger logger, boolean debug, boolean excludeStaff) {
        this.logger = logger != null ? logger : Logger.getLogger("RankPointsAPI");
        this.debug = debug;
        this.excludeStaff = excludeStaff;
        this.dbErrorThrottle = new DbErrorThrottle();

        this.ds = HikariDataSourceFactory.create(jdbcUrl, user, pass, debug);
        this.service = new JdbcPointsService(ds, excludeStaff);

        /*
         * Wichtig:
         * Schema beim Start versuchen, aber bei DB-Problemen nicht abbrechen.
         * Hikari ist mit initializationFailTimeout=-1 so konfiguriert,
         * dass der Pool auch ohne sofort erreichbare DB erstellt werden kann.
         */
        ensureSchemaReady("startup");
    }

    /**
     * Rueckwaertskompatibler Konstruktor fuer bestehenden Code.
     * Standard: Staff wird nicht ausgeschlossen.
     */
    public PointsAPI(String jdbcUrl, String user, String pass, Logger logger, boolean debug) {
        this(jdbcUrl, user, pass, logger, debug, false);
    }

    public void addPoints(UUID uuid, int delta) {
        if (uuid == null) {
            logger.warning("RankPointsAPI: addPoints wurde mit uuid=null aufgerufen.");
            return;
        }

        if (!ensureSchemaReady("addPoints")) {
            return;
        }

        try {
            service.addPoints(uuid, delta);

            if (debug) {
                logger.info(() -> "addPoints " + uuid + " +" + delta);
            }

        } catch (SQLException e) {
            dbErrorThrottle.logWarning(
                    logger,
                    "RankPointsAPI.addPoints",
                    "RankPointsAPI: addPoints fehlgeschlagen fuer " + uuid,
                    e
            );
        }
    }

    public void setPoints(UUID uuid, int points) {
        if (uuid == null) {
            logger.warning("RankPointsAPI: setPoints wurde mit uuid=null aufgerufen.");
            return;
        }

        if (!ensureSchemaReady("setPoints")) {
            return;
        }

        try {
            service.setPoints(uuid, points);

            if (debug) {
                logger.info(() -> "setPoints " + uuid + " = " + points);
            }

        } catch (SQLException e) {
            dbErrorThrottle.logWarning(
                    logger,
                    "RankPointsAPI.setPoints",
                    "RankPointsAPI: setPoints fehlgeschlagen fuer " + uuid,
                    e
            );
        }
    }

    public int getPoints(UUID uuid) {
        if (uuid == null) {
            logger.warning("RankPointsAPI: getPoints wurde mit uuid=null aufgerufen.");
            return 0;
        }

        if (!ensureSchemaReady("getPoints")) {
            return 0;
        }

        try {
            int points = service.getPoints(uuid);

            if (debug) {
                logger.info(() -> "getPoints " + uuid + " -> " + points);
            }

            return points;

        } catch (SQLException e) {
            dbErrorThrottle.logWarning(
                    logger,
                    "RankPointsAPI.getPoints",
                    "RankPointsAPI: getPoints fehlgeschlagen fuer " + uuid,
                    e
            );
            return 0;
        }
    }

    public boolean isExcludeStaffEnabled() {
        return excludeStaff;
    }

    /**
     * Prueft oder erstellt die benoetigten Tabellen.
     *
     * Falls die Datenbank nicht erreichbar ist, wird kein RuntimeException mehr geworfen.
     * Stattdessen wird der Fehler gedrosselt geloggt und beim naechsten API-Zugriff erneut versucht.
     */
    private boolean ensureSchemaReady(String operation) {
        if (schemaReady) {
            return true;
        }

        synchronized (schemaLock) {
            if (schemaReady) {
                return true;
            }

            try {
                SchemaInitializer.ensure(ds, logger);
                schemaReady = true;

                if (debug) {
                    logger.info("RankPointsAPI: Datenbankschema ist bereit.");
                }

                return true;

            } catch (SQLException e) {
                dbErrorThrottle.logWarning(
                        logger,
                        "RankPointsAPI.schema",
                        "RankPointsAPI: Datenbankschema konnte noch nicht geprueft/erstellt werden"
                                + " (Operation: " + operation + ")."
                                + " Die API laeuft weiter und versucht es beim naechsten Zugriff erneut",
                        e
                );
                return false;
            }
        }
    }

    public void close() {
        if (ds instanceof HikariDataSource hikari && !hikari.isClosed()) {
            hikari.close();

            if (debug) {
                logger.info("RankPointsAPI datasource closed.");
            }
        }
    }
}