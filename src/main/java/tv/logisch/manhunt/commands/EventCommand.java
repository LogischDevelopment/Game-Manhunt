package tv.logisch.manhunt.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.ban.BanListType;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import tv.logisch.manhunt.Manhunt;
import tv.logisch.manhunt.enums.GameState;
import tv.logisch.manhunt.guis.SettingGUI;
import tv.logisch.manhunt.manager.GameManager;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class EventCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack ctx, String[] strings) {
        
        CommandSender sender = ctx.getExecutor();

        if(!sender.hasPermission("logisch.manhunt.admin") && !GameManager.isHost(sender.getName())) {
            sender.sendMessage(Manhunt.prefix() + "§cDazu hast du keine Rechte!");
            return;
        }

        if(strings.length == 0) {
            sender.sendMessage("""
                    {prefix} §cUsage:
                    {prefix} §7/event start
                    {prefix} §7/event releaseTime <seconds>
                    {prefix} §7/event runner <add/remove> <player>
                    {prefix} §7/event pause
                    {prefix} §7/event resume
                    {prefix} §7/event gui
                    {prefix} §7/event kick <user>
                    {prefix} §7/event ban <user>
                    {prefix} §7/event unban <user>"""
                    .replaceAll("\\{prefix}", Manhunt.prefix()));
            return;
        }

        String subCommand = strings[0];

        if(subCommand.equalsIgnoreCase("start")) {
            if(!GameManager.state().equals(GameState.WAITING)) {
                sender.sendMessage(Component.text(Manhunt.prefix() + "§cDas Event läuft bereits! §8(§c/stop§8)"));
                return;
            }
            GameManager.startGame();
            sender.sendMessage(Component.text(Manhunt.prefix() + "§7Das Event wurde gestartet!"));

        } else if(subCommand.equalsIgnoreCase("releaseTime")) {

            if(GameManager.released()) {
                sender.sendMessage(Component.text(Manhunt.prefix() + "§cDie Hunter wurden bereits freigelassen!"));
                return;
            }

            if(strings.length != 2) {
                sender.sendMessage(Component.text(Manhunt.prefix() + "§7Die Release-Zeit ist aktuell auf §8(§7" + GameManager.totalReleaseTime() + "§8) §7Sekunden gesetzt!"));
                return;
            }
            long seconds = Long.parseLong(strings[1]);
            GameManager.totalReleaseTime(seconds);
            GameManager.releaseTime(seconds);
            sender.sendMessage(Component.text(Manhunt.prefix() + "§7Die Release-Zeit wurde auf §8(§7" + seconds + "§8) §7Sekunden gesetzt!"));

        } else if(subCommand.equalsIgnoreCase("runner")) {

            if (strings.length != 3) {
                sender.sendMessage(Manhunt.prefix() + "§cUsage: /event runner <add/remove> <player>");
                return;
            }
            String action = strings[1];
            String playerName = strings[2];
            if(action.equalsIgnoreCase("add")) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
                if(GameManager.isRunner(target.getUniqueId())) {
                    sender.sendMessage(Manhunt.prefix() + "§c" + target.getName() + " §8(§7Runner§8) §cist bereits ein Runner!");
                    return;
                }
                GameManager.addRunner(target.getUniqueId());
                sender.sendMessage(Manhunt.prefix() + "§7" + target.getName() + " §8(§7Runner§8) §7wurde hinzugefügt!");
            } else if(action.equalsIgnoreCase("remove")) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
                if(!GameManager.isRunner(target.getUniqueId())) {
                    sender.sendMessage(Manhunt.prefix() + "§c" + target.getName() + " §8(§7Hunter§8) §cist kein Runner!");
                    return;
                }
                GameManager.removeRunner(target.getUniqueId());
                sender.sendMessage(Manhunt.prefix() + "§7" + target.getName() + " §8(§7Hunter§8) §7wurde entfernt!");
            } else {
                sender.sendMessage(Manhunt.prefix() + "§cUsage: /event runner <add/remove> <player>");
            }

        } else if(subCommand.equalsIgnoreCase("pause")) {
            if(GameManager.state().equals(GameState.WAITING)) {
                sender.sendMessage(Manhunt.prefix() + "§cDas Event läuft nicht!");
                return;
            }
            GameManager.pauseGame();
            sender.sendMessage(Component.text(Manhunt.prefix() + "§7Das Event wurde pausiert!"));
        } else if(subCommand.equalsIgnoreCase("resume")) {
            if(!GameManager.state().equals(GameState.PAUSED)) {
                sender.sendMessage(Manhunt.prefix() + "§cDas Event ist nicht pausiert!");
                return;
            }
            GameManager.resumeGame();
            sender.sendMessage(Component.text(Manhunt.prefix() + "§7Das Event wurde fortgesetzt!"));
        } else if(subCommand.equalsIgnoreCase("gui")) {
            if(!(sender instanceof Player player)) {
                sender.sendMessage(Manhunt.prefix() + "§cDieser Befehl kann nur von einem Spieler ausgeführt werden!");
                return;
            }
            SettingGUI.get(player).open();
            sender.sendMessage(Component.text(Manhunt.prefix() + "§7Die Einstellungen wurden geöffnet!"));
        } else if(subCommand.equalsIgnoreCase("kick")) {
            if(strings.length != 2) {
                sender.sendMessage(Manhunt.prefix() + "§cUsage: /event kick <user>");
                return;
            }
            String playerName = strings[1];
            if(playerName.equalsIgnoreCase(sender.getName())) {
                sender.sendMessage(Manhunt.prefix() + "§cDu kannst dich nicht selbst kicken!");
                return;
            }
            Player targetPlayer = Bukkit.getPlayer(playerName);
            if(targetPlayer == null || !targetPlayer.isOnline()) {
                sender.sendMessage(Manhunt.prefix() + "§cSpieler §8(§7" + playerName + "§8) §cnicht gefunden!");
                return;
            }

            targetPlayer.kick(Component.text(Manhunt.prefix() + "§cDu wurdest aus dem Event gekickt!"));
            sender.sendMessage(Manhunt.prefix() + "§7" + targetPlayer.getName() + " §8("+(GameManager.isRunner(targetPlayer.getUniqueId()) ? "§aRunner" : "§cHunter")+"§8) §7wurde aus dem Event gekickt!");
        } else if(subCommand.equalsIgnoreCase("ban")) {
            if(strings.length != 2) {
                sender.sendMessage(Manhunt.prefix() + "§cUsage: /event ban <user>");
                return;
            }
            String playerName = strings[1];
            if(playerName.equalsIgnoreCase(sender.getName())) {
                sender.sendMessage(Manhunt.prefix() + "§cDu kannst dich nicht selbst bannen!");
                return;
            }
            Player targetPlayer = Bukkit.getPlayer(playerName);
            if(targetPlayer == null || !targetPlayer.isOnline()) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
                if(offlinePlayer.isBanned()) {
                    sender.sendMessage(Manhunt.prefix() + "§cSpieler §8(§7" + playerName + "§8) §cist bereits gebannt!");
                    return;
                }
                offlinePlayer.ban("Du wurdest aus dem Event gebannt!", Duration.ofDays(365), null);
                sender.sendMessage(Manhunt.prefix() + "§cSpieler §8(§7" + playerName + "§8) §cwurde aus dem Event gebannt!");
                return;
            }
            targetPlayer.ban(Manhunt.prefix() + "§cDu wurdest aus dem Event gekickt!", Duration.ofDays(365), null, true);
            sender.sendMessage(Manhunt.prefix() + "§7" + targetPlayer.getName() + " §8("+(GameManager.isRunner(targetPlayer.getUniqueId()) ? "§aRunner" : "§cHunter")+"§8) §7wurde aus dem Event gebannt!");
        } else if(subCommand.equalsIgnoreCase("unban")) {
            if (strings.length != 2) {
                sender.sendMessage(Manhunt.prefix() + "§cUsage: /event unban <user>");
                return;
            }
            String playerName = strings[1];
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            if (!offlinePlayer.isBanned()) {
                sender.sendMessage(Manhunt.prefix() + "§cSpieler §8(§7" + playerName + "§8) §cist nicht gebannt!");
                return;
            }
            Bukkit.getServer().getBanList(BanListType.PROFILE).pardon(offlinePlayer.getPlayerProfile());
            sender.sendMessage(Manhunt.prefix() + "§cSpieler §8(§7" + playerName + "§8) §cwurde für das Event entbannt!");
        } else {
            sender.sendMessage("""
                    {prefix} §cUsage:
                    {prefix} §7/event start
                    {prefix} §7/event releaseTime <seconds>
                    {prefix} §7/event runner <add/remove> <player>
                    {prefix} §7/event pause
                    {prefix} §7/event resume
                    {prefix} §7/event gui
                    {prefix} §7/event kick <user>
                    {prefix} §7/event ban <user>
                    {prefix} §7/event unban <user>"""
                    .replaceAll("\\{prefix}", Manhunt.prefix()));
        }
        return;
        
    }

    @Override
    public Collection<String> suggest(CommandSourceStack ctx, String[] args) {
        CommandSender commandSender = ctx.getExecutor();
        if(!commandSender.hasPermission("logisch.manhunt.admin") && !GameManager.isHost(commandSender.getName())) {
            return List.of();
        }
        if(args.length == 1) {
            return Stream.of("gui", "start", "pause", "resume", "releaseTime", "runner", "kick", "ban", "unban").filter(w -> w.toLowerCase().startsWith(args[0].toLowerCase())).toList();
        }
        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("runner")) {
                return Stream.of("add", "remove").filter(w -> w.toLowerCase().startsWith(args[1].toLowerCase())).toList();
            }
            if(args[0].equalsIgnoreCase("releaseTime")) {
                return Stream.of("120", "180", "240", "300", "360", "420", "480", "540", "600").filter(w -> w.toLowerCase().startsWith(args[1].toLowerCase())).toList();
            }
            if(args[0].equalsIgnoreCase("kick") || args[0].equalsIgnoreCase("ban")) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName)
                        .filter(w -> !w.equals(commandSender.getName()))
                        .filter(w -> w.toLowerCase().startsWith(args[1].toLowerCase()))
                        .toList();
            }
            if(args[0].equalsIgnoreCase("unban")) {
                return Bukkit.getServer().getBanList(BanListType.PROFILE).getEntries().stream()
                        .map(entry -> ((PlayerProfile) entry.getBanTarget()).getName())
                        .filter(Objects::nonNull)
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .toList();
            }
            return List.of();
        }
        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("runner")) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName)
                        .filter(w -> !w.equals(commandSender.getName()))
                        .filter(w -> w.toLowerCase().startsWith(args[2].toLowerCase()))
                        .toList();
            }
        }
        return List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("logisch.manhunt.admin") || GameManager.isHost(sender.getName());
    }

    @Override
    public @Nullable String permission() {
        return "logisch.manhunt.admin";
    }
}
