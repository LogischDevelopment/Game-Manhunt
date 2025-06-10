package tv.logisch.manhunt.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import tv.logisch.manhunt.manager.GameManager;

import java.util.UUID;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {

        e.deathMessage(Component.empty());
        if(GameManager.isRunner(e.getPlayer().getUniqueId())) {

            e.getPlayer().setGameMode(GameMode.SPECTATOR);
            e.getPlayer().sendMessage(Component.text("§8[§bManhunt§8] §cDu bist ausgeschieden!"));
            GameManager.latestPositions.removeIf(latestPosition -> latestPosition.getPlayer().getUniqueId().equals(e.getPlayer().getUniqueId()));
            Bukkit.getOnlinePlayers().forEach(p -> {
                p.sendMessage(Component.text("§8[§c†§8] §7" + e.getPlayer().getName()));
            });

            for(UUID playerUuid : GameManager.getRunners()) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(playerUuid);
                if(player.getPlayer() != null && Bukkit.getOnlinePlayers().contains(player.getPlayer()) && player.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
                    return;
                }
            }
            GameManager.endGame(false);

        } else {

            Location respawn = e.getPlayer().getRespawnLocation();
            if(respawn == null) respawn = e.getPlayer().getWorld().getSpawnLocation();
            e.getPlayer().teleport(respawn);
            Bukkit.getOnlinePlayers().forEach(p -> {
                p.sendMessage(Component.text("§8[§c†§8] §7" + e.getPlayer().getName()));
            });

        }

    }

}
