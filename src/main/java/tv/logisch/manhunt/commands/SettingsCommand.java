package tv.logisch.manhunt.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import tv.logisch.manhunt.Manhunt;
import tv.logisch.manhunt.guis.SettingGUI;
import tv.logisch.manhunt.manager.GameManager;

import java.util.Collection;
import java.util.List;

public class SettingsCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack ctx, String[] strings) {
        CommandSender sender = ctx.getExecutor();
        if(!(sender instanceof Player p)) {
            if(sender == null) return;
            sender.sendMessage(Manhunt.prefix() + "§cDieser Befehl kann nur von einem Spieler ausgeführt werden!");
            return;
        }
        if(!GameManager.isHost(sender.getName()) && !sender.hasPermission("logisch.manhunt.admin")) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return;
        }

        SettingGUI.get(p).open();
        sender.sendMessage(Component.text(Manhunt.prefix() + "§7Die Einstellungen wurden geöffnet!"));
        return;
    }

    @Override
    public Collection<String> suggest(CommandSourceStack ctx, String[] args) {
        return List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public @Nullable String permission() {
        return null;
    }
}
