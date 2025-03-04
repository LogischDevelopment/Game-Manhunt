package tv.logisch.manhunt.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tv.logisch.manhunt.manager.GameManager;

import java.util.Set;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        e.joinMessage(Component.empty());

        Server s = Bukkit.getServer();
        Set<OfflinePlayer> runner = s.getWhitelistedPlayers();
        boolean isRunning = !s.hasWhitelist();

        if(!isRunning) {
            if(!runner.contains(e.getPlayer())) {
                p.kick(Component.text("§8[§bManhunt§8] §cDie Hunter sind noch nicht berechtigt loszulaufen!"));
                return;
            }
        }
        if(GameManager.bossBar != null) GameManager.bossBar.addPlayer(e.getPlayer());

        String role = runner.contains(e.getPlayer()) ? "§aRunner" : "§cHunter";
        Bukkit.getOnlinePlayers().forEach(target -> {
            target.sendMessage(Component.text("§8[§a+§8] §7" + p.getName() + " §8(§7" + role + "§8)"));
        });

        if(!runner.contains(e.getPlayer())) {
            ItemStack compass = new ItemStack(Material.COMPASS);
            ItemMeta meta = compass.getItemMeta();
            meta.displayName(Component.text("§8» §bSpawn"));
            compass.setItemMeta(meta);
            GameManager.updatePlayerCompass(e.getPlayer());
            p.getInventory().addItem(compass);
        }

    }

}
