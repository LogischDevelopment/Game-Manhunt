package tv.logisch.manhunt.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import tv.logisch.manhunt.manager.GameManager;

public class PlayerSwitchWorld implements Listener {

    @EventHandler
    public void onPlayerWorldChange(PlayerChangedWorldEvent e) {

        Player p = e.getPlayer();
        if(!p.getGameMode().equals(GameMode.SURVIVAL)) return;
        Bukkit.getOnlinePlayers().forEach(pl -> {
            pl.sendMessage(Component.text("§8[§b❉§8] §7" + p.getName() + " §fentered the world §b" + p.getWorld().getName()));
        });

    }

}
