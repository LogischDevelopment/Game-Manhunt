package tv.logisch.manhunt.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import tv.logisch.manhunt.manager.GameManager;

import java.util.Set;

public class PlayerLoginListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {

        if(e.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
            e.disallow(PlayerLoginEvent.Result.KICK_FULL, Component.text("§cDer Server ist voll!"));
            return;
        } else if(e.getResult() == PlayerLoginEvent.Result.KICK_WHITELIST) {
            if(GameManager.isRunner(e.getPlayer())) {
                e.allow();
                return;
            }
            e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, Component.text("§8[§bManhunt§8] §cDie Hunter sind noch nicht berechtigt loszulaufen!"));
            return;
        } else if(e.getResult() == PlayerLoginEvent.Result.KICK_BANNED) {
            e.disallow(PlayerLoginEvent.Result.KICK_BANNED, Component.text("§8[§bManhunt§8] §cDu bist vom Event ausgeschlossen!"));
            return;
        }

        Server s = Bukkit.getServer();
        boolean isRunning = !s.hasWhitelist();

        if(!isRunning && !GameManager.isRunner(e.getPlayer())) {
            e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, Component.text("§8[§bManhunt§8] §cDie Hunter sind noch nicht berechtigt loszulaufen!"));
            return;
        }

    }

}
