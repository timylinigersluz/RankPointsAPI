package ch.ksrminecraft.RankPointsAPI.db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Einfache Drosselung fuer wiederholte DB-Fehler.
 *
 * Identische Fehler werden pro Kontext, Exception-Klasse und Fehlermeldung
 * hoechstens einmal pro Intervall geloggt.
 */
public final class DbErrorThrottle {

    private static final long DEFAULT_INTERVAL_MILLIS = 5 * 60 * 1000L;

    private final long intervalMillis;
    private final Map<String, Long> lastLogByKey = new ConcurrentHashMap<>();

    public DbErrorThrottle() {
        this(DEFAULT_INTERVAL_MILLIS);
    }

    public DbErrorThrottle(long intervalMillis) {
        this.intervalMillis = Math.max(1_000L, intervalMillis);
    }

    public void logWarning(Logger logger, String throttleKey, String message, Throwable throwable) {
        if (logger == null) {
            return;
        }

        String key = buildKey(throttleKey, throwable);
        long now = System.currentTimeMillis();

        Long lastLog = lastLogByKey.get(key);
        if (lastLog != null && (now - lastLog) < intervalMillis) {
            return;
        }

        lastLogByKey.put(key, now);

        String errorMessage = throwable == null
                ? "unbekannter Fehler"
                : normalizeMessage(throwable.getMessage());

        logger.log(Level.WARNING, message + ": " + errorMessage);

        /*
         * Stacktrace nur auf FINE-Level.
         * So bleibt die normale Konsole ruhig.
         */
        if (throwable != null && logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, message, throwable);
        }
    }

    private String buildKey(String throttleKey, Throwable throwable) {
        String exceptionClass = throwable == null
                ? "null"
                : throwable.getClass().getName();

        String exceptionMessage = throwable == null
                ? ""
                : normalizeMessage(throwable.getMessage());

        return String.valueOf(throttleKey) + "|" + exceptionClass + "|" + exceptionMessage;
    }

    private String normalizeMessage(String message) {
        if (message == null || message.isBlank()) {
            return "(keine Fehlermeldung)";
        }

        return message.trim().replaceAll("\\s+", " ");
    }
}