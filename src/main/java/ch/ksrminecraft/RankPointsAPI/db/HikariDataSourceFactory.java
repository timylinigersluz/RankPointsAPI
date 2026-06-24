package ch.ksrminecraft.RankPointsAPI.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Erstellt den HikariCP-Pool fuer RankPointsAPI.
 *
 * Die Konfiguration ist bewusst defensiv:
 * - kleiner Pool, damit mehrere Server/Plugins MariaDB nicht ueberlasten
 * - minimumIdle = 0, damit keine unnoetigen Idle-Verbindungen gehalten werden
 * - kurze maxLifetime, damit alte/geschlossene DB-Verbindungen schnell ersetzt werden
 * - initializationFailTimeout = -1, damit das Plugin auch bei kurz nicht erreichbarer DB startet
 */
public final class HikariDataSourceFactory {

    private HikariDataSourceFactory() {
    }

    public static DataSource create(String jdbcUrl, String user, String pass, boolean debug) {
        HikariConfig cfg = new HikariConfig();

        cfg.setPoolName("RankPointsAPI-MainPool");
        cfg.setJdbcUrl(jdbcUrl);
        cfg.setUsername(user);
        cfg.setPassword(pass);

        applyRobustDefaults(cfg);

        Properties dsProps = new Properties();

        dsProps.setProperty("cachePrepStmts", "true");
        dsProps.setProperty("prepStmtCacheSize", "250");
        dsProps.setProperty("prepStmtCacheSqlLimit", "2048");
        dsProps.setProperty("useServerPrepStmts", "true");
        dsProps.setProperty("useSSL", "false");
        dsProps.setProperty("serverTimezone", "UTC");
        dsProps.setProperty("tcpKeepAlive", "true");

        cfg.setDataSourceProperties(dsProps);

        return new HikariDataSource(cfg);
    }

    private static void applyRobustDefaults(HikariConfig cfg) {
        cfg.setMaximumPoolSize(3);
        cfg.setMinimumIdle(0);

        cfg.setMaxLifetime(120_000);
        cfg.setConnectionTimeout(5_000);
        cfg.setValidationTimeout(3_000);
        cfg.setInitializationFailTimeout(-1);

        /*
         * HikariCP erlaubt idleTimeout erst sinnvoll ab 10 Sekunden.
         * 60 Sekunden ist kurz genug, damit bei minimumIdle=0 ungenutzte Connections
         * wieder verschwinden, aber nicht so aggressiv, dass staendig neu verbunden wird.
         */
        cfg.setIdleTimeout(60_000);

        /*
         * Kein Keepalive.
         * Bei kurzer maxLifetime und minimumIdle=0 ist es robuster, unbenutzte
         * Verbindungen zu schliessen und bei Bedarf neu zu oeffnen.
         */
        cfg.setKeepaliveTime(0);
    }
}