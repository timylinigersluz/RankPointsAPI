package ch.ksrminecraft.RankPointsAPI;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RankPointsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("RankPointsAPI enabled.");

        // Nur auf Bukkit-/Paper-Servern sinnvoll
        if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, "rankproxy:afk");
            Bukkit.getPluginManager().registerEvents(
                    new ch.ksrminecraft.RankPointsAPI.afk.EssentialsAfkBridge(this), this
            );
            getLogger().info("AFK bridge with Essentials enabled.");
        } else {
            getLogger().info("Essentials not found – AFK bridge disabled.");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("RankPointsAPI disabled.");
    }
}
