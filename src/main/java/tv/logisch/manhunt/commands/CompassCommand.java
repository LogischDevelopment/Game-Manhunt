package tv.logisch.manhunt.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import tv.logisch.manhunt.manager.GameManager;

public class CompassCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        Player player = (Player) commandSender;
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        meta.displayName(Component.text("§8» §bTracker"));
        compass.setItemMeta(meta);
        player.getInventory().addItem(compass);

        player.sendMessage("§8[§bManhunt§8] §7You have received a tracker!");
        player.sendMessage("§8[§bManhunt§8] §7In less than 30 seconds, the tracker will point to the nearest runner!");
        return true;

    }
}
