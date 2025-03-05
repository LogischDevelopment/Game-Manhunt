package tv.logisch.manhunt;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tv.logisch.manhunt.commands.CompassCommand;
import tv.logisch.manhunt.commands.EventCommand;
import tv.logisch.manhunt.commands.completions.EventCommandCompletion;
import tv.logisch.manhunt.listener.*;

import java.util.logging.Logger;

public final class Manhunt extends JavaPlugin {

    @Getter @Accessors(fluent = true)
    private static Manhunt instance;

    @Getter @Accessors(fluent = true)
    private static Logger logger;

    @Override
    public void onLoad() {
        instance = this;
        logger = getLogger();
    }

    @Override
    public void onEnable() {

        logger().info("Manhunt plugin enabled!");

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new JoinListener(), this);
        pm.registerEvents(new PlayerQuitListener(), this);
        pm.registerEvents(new PlayerLoginListener(), this);
        pm.registerEvents(new PlayerMoveListener(), this);
        pm.registerEvents(new PlayerDeathListener(), this);
        pm.registerEvents(new EntityDeathEvent(), this);

        PluginCommand eventCmd = getCommand("event");
        eventCmd.setExecutor(new EventCommand());
        eventCmd.setTabCompleter(new EventCommandCompletion());
        getCommand("compass").setExecutor(new CompassCommand());

        Bukkit.setWhitelist(true);

        Bukkit.getWorlds().forEach(w -> {
            w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        });

        Bukkit.getServerTickManager().setFrozen(true);
        Bukkit.getOfflinePlayer("Logisch_XD").setWhitelisted(true);

    }

    @Override
    public void onDisable() {
        logger().info("Manhunt plugin disabled!");
    }
}
