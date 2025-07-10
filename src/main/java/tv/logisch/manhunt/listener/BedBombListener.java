package tv.logisch.manhunt.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import tv.logisch.manhunt.manager.GameManager;

public class BedBombListener implements Listener {

    @EventHandler
    public void onBedBomb(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = e.getClickedBlock();
        if (block == null) return;
        if(!block.getType().name().endsWith("_BED")) return;
        if(GameManager.bedBomb()) return;

        World.Environment env = block.getWorld().getEnvironment();
        if (env == World.Environment.NETHER || env == World.Environment.THE_END) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Component.text("§8[§bManhunt§8] §cThe host has disabled bed bombs in this game!"));
        }

    }

}
