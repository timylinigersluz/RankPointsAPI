package ch.ksrminecraft.RankPointsAPI.db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Logger;

public final class SchemaInitializer {
    private SchemaInitializer() {}

    public static void ensure(DataSource ds, Logger logger) throws Exception {
        try (Connection con = ds.getConnection(); Statement st = con.createStatement()) {
            st.execute("""
        CREATE TABLE IF NOT EXISTS points (
          UUID VARCHAR(36) PRIMARY KEY,
          points INT NOT NULL DEFAULT 0
        )
      """);
            st.execute("""
        CREATE TABLE IF NOT EXISTS stafflist (
          UUID VARCHAR(36) PRIMARY KEY,
          name VARCHAR(50) NOT NULL
        )
      """);
            if (logger != null) logger.info("Tables ensured.");
        }
    }
}
