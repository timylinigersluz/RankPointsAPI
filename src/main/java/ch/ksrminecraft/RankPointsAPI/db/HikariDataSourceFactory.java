package ch.ksrminecraft.RankPointsAPI.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Properties;

public final class HikariDataSourceFactory {
    private HikariDataSourceFactory() {}

    public static DataSource create(String jdbcUrl, String user, String pass, boolean debug) {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(jdbcUrl);
        cfg.setUsername(user);
        cfg.setPassword(pass);

        // Treiber
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // Poolgrössen (bei Bedarf anpassen)
        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(2);

        // Timeouts
        cfg.setConnectionTimeout(5000);
        cfg.setIdleTimeout(600000);
        cfg.setMaxLifetime(1800000);

        // MySQL-spezifische Optimierungen
        Properties dsProps = new Properties();
        dsProps.setProperty("cachePrepStmts", "true");
        dsProps.setProperty("prepStmtCacheSize", "250");
        dsProps.setProperty("prepStmtCacheSqlLimit", "2048");
        dsProps.setProperty("useServerPrepStmts", "true");
        dsProps.setProperty("useSSL", "false");
        dsProps.setProperty("serverTimezone", "UTC");
        cfg.setDataSourceProperties(dsProps);

        if (debug) {
            cfg.setInitializationFailTimeout(-1);
        }

        return new HikariDataSource(cfg);
    }
}
