package ch.ksrminecraft.RankPointsAPI;

import ch.ksrminecraft.RankPointsAPI.afk.EssentialsAfkBridge;
import ch.ksrminecraft.RankPointsAPI.placeholder.RankPointsPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RankPointsPlugin extends JavaPlugin {

    private PointsAPI internalPointsApi;
    private RankPointsPlaceholder rankPointsPlaceholder;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("RankPointsAPI enabled.");

        setupInternalPointsApi();
        registerPlaceholder();
        registerAfkBridge();
    }

    @Override
    public void onDisable() {
        if (rankPointsPlaceholder != null) {
            rankPointsPlaceholder.clearAllCache();
            rankPointsPlaceholder = null;
        }

        if (internalPointsApi != null) {
            internalPointsApi.close();
            internalPointsApi = null;
        }

        getLogger().info("RankPointsAPI disabled.");
    }

    private void setupInternalPointsApi() {
        boolean placeholderEnabled = getConfig().getBoolean("placeholder.enabled", true);
        if (!placeholderEnabled) {
            getLogger().info("Placeholder support disabled in config.");
            return;
        }

        String host = getConfig().getString("database.host", "");
        int port = getConfig().getInt("database.port", 3306);
        String database = getConfig().getString("database.name", "");
        String username = getConfig().getString("database.username", "");
        String password = getConfig().getString("database.password", "");
        boolean debug = getConfig().getBoolean("debug", false);
        boolean excludeStaff = getConfig().getBoolean("exclude-staff", true);

        if (host.isBlank() || database.isBlank() || username.isBlank()) {
            getLogger().warning("Internal PointsAPI for placeholders was not started because database config is incomplete.");
            return;
        }

        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;

        try {
            internalPointsApi = new PointsAPI(jdbcUrl, username, password, getLogger(), debug, excludeStaff);
            getLogger().info("Internal PointsAPI for placeholders initialized.");
        } catch (Exception e) {
            getLogger().severe("Could not initialize internal PointsAPI for placeholders: " + e.getMessage());
        }
    }

    private void registerPlaceholder() {
        boolean placeholderEnabled = getConfig().getBoolean("placeholder.enabled", true);
        if (!placeholderEnabled) {
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().info("PlaceholderAPI not found – placeholder disabled.");
            return;
        }

        if (internalPointsApi == null) {
            getLogger().warning("PlaceholderAPI found, but placeholder was not registered because internal PointsAPI is unavailable.");
            return;
        }

        String identifier = getConfig().getString("placeholder.identifier", "rankpoints");
        long cacheSeconds = getConfig().getLong("placeholder.cache-seconds", 10L);

        rankPointsPlaceholder = new RankPointsPlaceholder(this, identifier, cacheSeconds);
        rankPointsPlaceholder.register();

        getLogger().info("Placeholder registered: %" + identifier + "_points%");
    }

    private void registerAfkBridge() {
        if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, "rankproxy:afk");
            Bukkit.getPluginManager().registerEvents(new EssentialsAfkBridge(this), this);
            getLogger().info("AFK bridge with Essentials enabled.");
        } else {
            getLogger().info("Essentials not found – AFK bridge disabled.");
        }
    }

    public PointsAPI getInternalPointsApi() {
        return internalPointsApi;
    }
}