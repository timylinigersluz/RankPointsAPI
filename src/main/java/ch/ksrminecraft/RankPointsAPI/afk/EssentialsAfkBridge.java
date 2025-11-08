package ch.ksrminecraft.RankPointsAPI.afk;

import com.earth2me.essentials.Essentials;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.nio.charset.StandardCharsets;

public class EssentialsAfkBridge implements Listener {

    private final Essentials essentials;
    private final Plugin plugin;

    public EssentialsAfkBridge(Plugin plugin) {
        this.plugin = plugin;
        this.essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
    }

    @EventHandler
    public void onAfkChange(AfkStatusChangeEvent event) {
        Player player = event.getAffected().getBase();
        boolean isAfk = event.getValue();

        // Nachricht an Velocity-Proxy
        String msg = player.getUniqueId() + ";" + isAfk;
        player.sendPluginMessage(plugin, "rankproxy:afk", msg.getBytes(StandardCharsets.UTF_8));
    }
}
