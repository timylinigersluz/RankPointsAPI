package ch.ksrminecraft.RankPointsAPI.db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Erstellt die benoetigten Tabellen, falls sie fehlen.
 *
 * SQLException wird bewusst weitergeworfen.
 * PointsAPI entscheidet danach, ob nur gedrosselt geloggt oder weitergearbeitet wird.
 */
public final class SchemaInitializer {

    private SchemaInitializer() {
    }

    public static void ensure(DataSource ds, Logger logger) throws SQLException {
        try (Connection con = ds.getConnection();
             Statement st = con.createStatement()) {

            st.execute("""
                    CREATE TABLE IF NOT EXISTS points (
                        UUID VARCHAR(36) PRIMARY KEY,
                        points INT NOT NULL DEFAULT 0
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """);

            st.execute("""
                    CREATE TABLE IF NOT EXISTS stafflist (
                        UUID VARCHAR(36) PRIMARY KEY,
                        name VARCHAR(50) NOT NULL
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """);

            if (logger != null) {
                logger.info("RankPointsAPI: Tabellen 'points' und 'stafflist' geprueft/erstellt.");
            }
        }
    }
}