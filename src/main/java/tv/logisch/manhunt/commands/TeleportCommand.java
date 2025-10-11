package tv.logisch.manhunt.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import tv.logisch.manhunt.Manhunt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TeleportCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack ctx, String[] strings) {
        CommandSender sender = ctx.getExecutor();
        if(!(sender instanceof Player p)) {
            if(sender == null) return;
            sender.sendMessage(Manhunt.prefix() + "§cDieser Befehl kann nur von einem Spieler ausgeführt werden!");
            return;
        }

        if(!p.hasPermission("logisch.manhunt.admin")) {
            p.sendMessage(Component.text(Manhunt.prefix() + "§cDazu hast du keine Rechte!"));
            return;
        }

        if(strings.length < 1) {
            p.sendMessage(Component.text(Manhunt.prefix() + "§cSyntax: /tp <player|@r|@s|@l>"));
            return;
        }

        String name = strings[0];
        Player target = this.findPlayer(name, p);
        if (target == null || !target.isOnline()) {
            p.sendMessage(Component.text(Manhunt.prefix() + "§cNo player found with input: " + name));
            return;
        }

        if(strings.length > 1) {
            Player receiver = this.findPlayer(strings[1], p);
            if (receiver == null || !receiver.isOnline()) {
                p.sendMessage(Component.text(Manhunt.prefix() + "§cNo player found with input: " + strings[1]));
                return;
            }
            target.teleportAsync(receiver.getLocation());
            p.sendMessage(Component.text(Manhunt.prefix() + "§7Du hast §b" + target.getName() + " §7zu §b" + receiver.getName() + " §7teleportiert!"));
            Bukkit.getConsoleSender().sendMessage("[TELEPORT] " + p.getName() + " teleported " + target.getName() + " to " + receiver.getName());
            return;
        }

        p.teleportAsync(target.getLocation());
        p.sendMessage(Component.text(Manhunt.prefix() + "§7Du wurdest zu §b" + target.getName() + " §7teleportiert!"));
        Bukkit.getConsoleSender().sendMessage("[TELEPORT] " + p.getName() + " to " + target.getName());
        return;
    }

    private Player findPlayer(String name, Player sender) {
        if(name.charAt(0) == '@') {
            Player player;
            switch (name.substring(1).toLowerCase()) {
                case "r", "random" -> player = Manhunt.instance().getServer().getOnlinePlayers().stream().skip((int) (Math.random() * Manhunt.instance().getServer().getOnlinePlayers().size())).findFirst().orElse(null);
                case "s", "self" -> player = sender;
                case "l", "last" -> player = Manhunt.instance().getServer().getOnlinePlayers().stream().reduce((first, second) -> second).orElse(null);
                default -> player = null;
            }
            return player;
        }
        return Bukkit.getPlayer(name);
    }

    @Override
    public Collection<String> suggest(CommandSourceStack ctx, String[] args) {
        if(args.length == 0) {
            List<String> list = new ArrayList<>(List.of("@r", "@s", "@l"));
            list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
            return list;
        }
        if(args.length == 1) {
            List<String> list = new ArrayList<>(List.of("@r", "@s", "@l"));
            list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
            String prefix = args[0].toLowerCase();
            return list.stream()
                    .filter(name -> name.toLowerCase().startsWith(prefix))
                    .sorted()
                    .toList();
        }
        if(args.length == 2) {
            List<String> list = new ArrayList<>(List.of("@r", "@s", "@l"));
            list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
            String prefix = args[1].toLowerCase();
            return list.stream()
                    .filter(name -> name.toLowerCase().startsWith(prefix))
                    .sorted()
                    .toList();
        }
        return List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("logisch.manhunt.admin");
    }

    @Override
    public @Nullable String permission() {
        return "logisch.manhunt.admin";
    }
}
