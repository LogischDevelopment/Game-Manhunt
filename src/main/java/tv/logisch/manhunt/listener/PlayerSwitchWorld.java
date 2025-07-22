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
        if(e.getFrom().equals(GameManager.waitingWorld())) return;
        if(!p.getGameMode().equals(GameMode.SURVIVAL)) return;
        if(p.getWorld().getName().equalsIgnoreCase("waiting")) {
            p.teleport(GameManager.gameWorld().getSpawnLocation());
            return;
        }
        String worldDimension = p.getWorld().getEnvironment() == org.bukkit.World.Environment.NORMAL ? "§aOverworld" : p.getWorld().getEnvironment() == org.bukkit.World.Environment.NETHER ? "§cNether" : "§dEnd";
        Bukkit.getOnlinePlayers().forEach(pl -> {
            pl.sendMessage(Component.text("§f§lWORLD §8» §f" + p.getName() + " §7entered the dimension " + worldDimension + "§7."));
        });

    }

}
