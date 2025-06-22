package tv.logisch.manhunt;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tv.logisch.api.LogiAPI;
import tv.logisch.manhunt.commands.CompassCommand;
import tv.logisch.manhunt.commands.EventCommand;
import tv.logisch.manhunt.commands.completions.EventCommandCompletion;
import tv.logisch.manhunt.listener.*;
import tv.logisch.manhunt.manager.GameManager;
import tv.logisch.manhunt.objects.GameConfig;
import tv.logisch.manhunt.utils.Config;

import java.io.File;
import java.util.UUID;
import java.util.logging.Logger;

public final class Manhunt extends JavaPlugin {

    @Getter @Accessors(fluent = true)
    private static Manhunt instance;

    @Getter @Accessors(fluent = true)
    private static Logger logger;

    @Getter
    @Accessors(fluent = true)
    private static GameConfig gameConfig;

    @Getter
    @Accessors(fluent = true)
    private static LogiAPI logiAPI;

    @Override
    public void onLoad() {
        instance = this;
        logger = getLogger();
    }

    @Override
    public void onEnable() {
        logiAPI = new LogiAPI(new Config(new File(Bukkit.getPluginsFolder().getPath() + "/manhunt/config.json")).get("logisch.api.key").getAsString());
        gameConfig = new GameConfig().initialize();
        System.setProperty("LOGISCH_TYPE", "GAME");
        System.setProperty("LOGISCH_FLAGS", "host="+gameConfig.hostUUID().toString()+";hostName="+gameConfig.hostName());
        logger().info("Manhunt plugin enabled!");
        System.out.println("Hoster: " + gameConfig.hostName() + " (" + gameConfig.hostUUID() + ")");

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new JoinListener(), this);
        pm.registerEvents(new PlayerQuitListener(), this);
        pm.registerEvents(new PlayerLoginListener(), this);
        pm.registerEvents(new PlayerMoveListener(), this);
        pm.registerEvents(new PlayerDeathListener(), this);
        pm.registerEvents(new EntityDeathEvent(), this);
        pm.registerEvents(new PlayerFoodLevelChangeListener(), this);
        pm.registerEvents(new PlayerSwitchWorld(), this);
        pm.registerEvents(new PlayerDamageListener(), this);

        PluginCommand eventCmd = getCommand("event");
        eventCmd.setExecutor(new EventCommand());
        eventCmd.setTabCompleter(new EventCommandCompletion());
        getCommand("compass").setExecutor(new CompassCommand());

        Bukkit.setWhitelist(true);

        Bukkit.getWorlds().forEach(w -> {
            w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        });

        Bukkit.getServerTickManager().setFrozen(true);

        GameManager.addRunner(gameConfig.hostUUID());

    }

    @Override
    public void onDisable() {
        logger().info("Manhunt plugin disabled!");
    }
}
