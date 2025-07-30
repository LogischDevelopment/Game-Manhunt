package tv.logisch.manhunt.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.Nullable;
import tv.logisch.manhunt.Manhunt;

import java.util.Collection;
import java.util.List;

public class CompassCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack ctx, String[] strings) {
        CommandSender commandSender = ctx.getExecutor();
        if (!(commandSender instanceof Player player)) {
            if(commandSender == null) return;
            commandSender.sendMessage(Manhunt.prefix() + "§cThis command can only be executed by a player!");
            return;
        }
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        meta.displayName(Component.text("§8» §bTracker"));
        compass.setItemMeta(meta);
        player.getInventory().addItem(compass);

        player.sendMessage(Manhunt.prefix() + "§7You have received a tracker!");
        player.sendMessage(Manhunt.prefix() + "§7In less than 30 seconds, the tracker will point to the nearest runner!");
        return;
    }

    @Override
    public Collection<String> suggest(CommandSourceStack ctx, String[] args) {
        return List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender instanceof Player p;
    }

    @Override
    public @Nullable String permission() {
        return null;
    }
}
