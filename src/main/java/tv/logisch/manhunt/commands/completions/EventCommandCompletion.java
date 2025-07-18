package tv.logisch.manhunt.commands.completions;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.ban.BanListType;
import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tv.logisch.manhunt.manager.GameManager;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class EventCommandCompletion implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if(!commandSender.hasPermission("logisch.manhunt.admin") && !GameManager.isHost(commandSender.getName())) {
            return List.of();
        }
        if(strings.length == 1) {
            return Stream.of("gui", "start", "pause", "resume", "releaseTime", "runner", "kick", "ban", "unban").filter(w -> w.toLowerCase().startsWith(strings[0].toLowerCase())).toList();
        }
        if(strings.length == 2) {
            if(strings[0].equalsIgnoreCase("runner")) {
                return Stream.of("add", "remove").filter(w -> w.toLowerCase().startsWith(strings[1].toLowerCase())).toList();
            }
            if(strings[0].equalsIgnoreCase("releaseTime")) {
                return Stream.of("120", "180", "240", "300", "360", "420", "480", "540", "600").filter(w -> w.toLowerCase().startsWith(strings[1].toLowerCase())).toList();
            }
            if(strings[0].equalsIgnoreCase("kick") || strings[0].equalsIgnoreCase("ban")) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName)
                        .filter(w -> !w.equals(commandSender.getName()))
                        .filter(w -> w.toLowerCase().startsWith(strings[1].toLowerCase()))
                        .toList();
            }
            if(strings[0].equalsIgnoreCase("unban")) {
                return Bukkit.getServer().getBanList(BanListType.PROFILE).getEntries().stream()
                        .map(entry -> ((PlayerProfile) entry.getBanTarget()).getName())
                        .filter(Objects::nonNull)
                        .filter(name -> name.toLowerCase().startsWith(strings[1].toLowerCase()))
                        .toList();
            }
            return List.of();
        }
        if(strings.length == 3) {
            if(strings[0].equalsIgnoreCase("runner")) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName)
                        .filter(w -> !w.equals(commandSender.getName()))
                        .filter(w -> w.toLowerCase().startsWith(strings[2].toLowerCase()))
                        .toList();
            }
        }
        return List.of();

    }
}
