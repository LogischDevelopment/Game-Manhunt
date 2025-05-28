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
        if(GameManager.bossBar != null) GameManager.bossBar.addPlayer(e.getPlayer());

        String role = GameManager.isRunner(e.getPlayer().getUniqueId()) ? "§aRunner" : "§cHunter";
        Bukkit.getOnlinePlayers().forEach(target -> {
            target.sendMessage(Component.text("§8[§a+§8] §7" + p.getName() + " §8(§7" + role + "§8)"));
        });

        if(!GameManager.isRunner(e.getPlayer().getUniqueId())) {
            ItemStack compass = new ItemStack(Material.COMPASS);
            ItemMeta meta = compass.getItemMeta();
            meta.displayName(Component.text("§8» §bTracker"));
            compass.setItemMeta(meta);
            p.getInventory().addItem(compass);
        }

    }

}
