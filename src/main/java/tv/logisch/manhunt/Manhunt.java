package tv.logisch.manhunt;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tv.logisch.api.LogiAPI;
import tv.logisch.manhunt.commands.CompassCommand;
import tv.logisch.manhunt.commands.EventCommand;
import tv.logisch.manhunt.commands.SettingsCommand;
import tv.logisch.manhunt.guis.SettingGUIListener;
import tv.logisch.manhunt.listener.*;
import tv.logisch.manhunt.manager.GameManager;
import tv.logisch.manhunt.objects.GameConfig;
import tv.logisch.manhunt.utils.Config;
import tv.logisch.manhunt.utils.VoiceChat;

import java.io.File;
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

    @Getter
    @Accessors(fluent = true)
    private static int voiceChatPort;

    @Getter
    @Accessors(fluent = true)
    private static String prefix = "§b§lMANHUNT §8» §7";

    @Override
    public void onLoad() {
        instance = this;
        logger = getLogger();

        // SET VOICECHAT PORT
        int port = System.getenv("service-name") != null
                ? Integer.parseInt(System.getenv("service-name").split("-")[1]) + 24454
                : -1;
        if (port == -1) {
            logger.warning("No free port found for VoiceChat. Try using default port 24454.");
            port = 24454;
        }
        VoiceChat.setVoiceChatPort(port);
        VoiceChat.allowUfwPort(port);
        Manhunt.voiceChatPort = port;
        System.setProperty("LOGISCH_VOICECHAT_PORT", String.valueOf(port));
    }

    @Override
    public void onEnable() {
        logiAPI = new LogiAPI(new Config(new File(Bukkit.getPluginsFolder().getPath() + "/manhunt/config.json")).get("logisch.api.key").getAsString());
        gameConfig = new GameConfig().initialize();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "logisch:actions");

        /* Set Serverhost Properties for LogiAPI in Core Plugin */
        System.setProperty("LOGISCH_TYPE", "GAME");
        String hostUUID = gameConfig.hostUUID() != null ? gameConfig.hostUUID().toString() : "null";
        String hostName = gameConfig.hostName() != null ? gameConfig.hostName() : "null";
        System.setProperty("LOGISCH_FLAGS", "host="+hostUUID+";hostName="+hostName+";sendJoinMe=true;retrieveJoinMe=false");

        logger().info("Manhunt plugin enabled!");
        System.out.println("Hoster: " + gameConfig.hostName() + " (" + gameConfig.hostUUID() + ")");

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new JoinListener(), this);
        pm.registerEvents(new PlayerQuitListener(), this);
        // pm.registerEvents(new PlayerLoginListener(), this);
        pm.registerEvents(new PlayerMoveListener(), this);
        pm.registerEvents(new PlayerDeathListener(), this);
        pm.registerEvents(new EntityDeathEvent(), this);
        pm.registerEvents(new PlayerFoodLevelChangeListener(), this);
        pm.registerEvents(new PlayerSwitchWorld(), this);
        pm.registerEvents(new PlayerDamageListener(), this);
        pm.registerEvents(new BreakBlockListener(), this);
        pm.registerEvents(new PlaceBlockListener(), this);
        pm.registerEvents(new SettingGUIListener(), this);
        pm.registerEvents(new BedBombListener(), this);
        pm.registerEvents(new VehicleMoveListener(), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, (event) -> {
            Commands registrar = event.registrar();

            registrar.register("compass", new CompassCommand());
            registrar.register("event", new EventCommand());
            registrar.register("settings", new SettingsCommand());
        });

        Bukkit.getServerTickManager().setFrozen(true);

        GameManager.addRunner(gameConfig.hostUUID());

        GameManager.gameWorld(Bukkit.createWorld(new WorldCreator("world")));
        GameManager.waitingWorld(Bukkit.createWorld(new WorldCreator("waiting")));

        Bukkit.getWorlds().forEach(w -> {
            w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            w.setGameRule(GameRule.LOCATOR_BAR, false);
            w.setGameRule(GameRule.SPAWN_RADIUS, 0);
            w.setGameRule(GameRule.SPAWN_CHUNK_RADIUS, 0);
        });

    }

    @Override
    public void onDisable() {
        logger().info("Manhunt plugin disabled!");
        VoiceChat.denyUfwPort(Manhunt.voiceChatPort());
    }
}
