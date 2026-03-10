package ch.ksrminecraft.RankPointsAPI.placeholder;

import ch.ksrminecraft.RankPointsAPI.PointsAPI;
import ch.ksrminecraft.RankPointsAPI.RankPointsPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RankPointsPlaceholder extends PlaceholderExpansion {

    private final RankPointsPlugin plugin;
    private final Map<UUID, CacheEntry> cache = new HashMap<>();
    private final long cacheDurationMillis;
    private final String identifier;

    public RankPointsPlaceholder(RankPointsPlugin plugin, String identifier, long cacheDurationSeconds) {
        this.plugin = plugin;
        this.identifier = identifier;
        this.cacheDurationMillis = Math.max(1L, cacheDurationSeconds) * 1000L;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getAuthor() {
        return "KSRMinecraft";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null || params == null) {
            return "";
        }

        if (params.equalsIgnoreCase("points") || params.equalsIgnoreCase("rankpoints")) {
            PointsAPI api = plugin.getInternalPointsApi();
            if (api == null) {
                return "0";
            }

            UUID uuid = player.getUniqueId();
            long now = System.currentTimeMillis();

            CacheEntry entry = cache.get(uuid);
            if (entry == null || now - entry.timestamp > cacheDurationMillis) {
                int points = api.getPoints(uuid);
                entry = new CacheEntry(points, now);
                cache.put(uuid, entry);
            }

            return String.valueOf(entry.points);
        }

        return null;
    }

    public void clearCache(UUID uuid) {
        if (uuid != null) {
            cache.remove(uuid);
        }
    }

    public void clearAllCache() {
        cache.clear();
    }

    private static class CacheEntry {
        private final int points;
        private final long timestamp;

        private CacheEntry(int points, long timestamp) {
            this.points = points;
            this.timestamp = timestamp;
        }
    }
}