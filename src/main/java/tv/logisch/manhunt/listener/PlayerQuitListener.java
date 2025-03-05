package tv.logisch.manhunt.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import tv.logisch.manhunt.enums.GameState;
import tv.logisch.manhunt.manager.GameManager;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        e.quitMessage(Component.empty());

        String role = Bukkit.getWhitelistedPlayers().contains(e.getPlayer()) ? "§aRunner" : "§cHunter";
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.sendMessage(Component.text("§8[§c-§8] §7" + e.getPlayer().getName() + " §8(§7" + role + "§8)"));
        });

        if(!GameManager.state().equals(GameState.WAITING)) return;

        for(OfflinePlayer player : Bukkit.getWhitelistedPlayers()) {
            if(player.getPlayer() != null && Bukkit.getOnlinePlayers().contains(player.getPlayer()) && player.getPlayer().getGameMode().equals(GameMode.SURVIVAL) && !player.getPlayer().getUniqueId().equals(e.getPlayer().getUniqueId())) {
                return;
            }
        }
        GameManager.pauseGame();

    }

}
