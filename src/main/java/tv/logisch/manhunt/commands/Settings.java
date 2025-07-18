package tv.logisch.manhunt.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tv.logisch.manhunt.Manhunt;
import tv.logisch.manhunt.guis.SettingGUI;

public class Settings implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if(!sender.hasPermission("logisch.manhunt.cmd.settings")) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }

        if(!(sender instanceof Player player)) {
            sender.sendMessage(Manhunt.prefix() + "§cDieser Befehl kann nur von einem Spieler ausgeführt werden!");
            return true;
        }
        SettingGUI.get(player).open();
        sender.sendMessage(Component.text(Manhunt.prefix() + "§7Die Einstellungen wurden geöffnet!"));
        return true;

    }
}
