package tv.logisch.manhunt.utils;

import org.bukkit.Bukkit;

import java.io.*;
import java.util.Properties;

public class VoiceChat {

    public static void setVoiceChatPort(int port) {
        File configFile = new File(Bukkit.getPluginsFolder().getPath() + "/voicechat/voicechat-server.properties");

        try {
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            }

            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream(configFile)) {
                props.load(in);
            }

            props.setProperty("port", String.valueOf(port));

            try (FileOutputStream out = new FileOutputStream(configFile)) {
                props.store(out, "Voice Chat Server Config (updated)");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void allowUfwPort(int port) {
        executeCommand("sudo ufw allow " + port);
        executeCommand("sudo ufw allow " + port + "/udp");
    }

    public static void denyUfwPort(int port) {
        executeCommand("sudo ufw delete allow " + port);
        executeCommand("sudo ufw delete allow " + port + "/udp");
    }

    private static void executeCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            if (process.exitValue() != 0) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.err.println("[UFW ERROR] " + line);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
