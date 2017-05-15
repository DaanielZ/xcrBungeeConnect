package pl.xcrafters.xcrbungeeconnect;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

public class ConfigManager {

    ConnectPlugin plugin;

    public ConfigManager(ConnectPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    Configuration config;

    public String redisHost;
    public String redisPass;
    public int redisBase;

    private void load() {
        saveDefaultConfig();
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(this.plugin.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        redisHost = config.getString("config.redis.host");
        redisPass = config.getString("config.redis.pass");
        redisBase = config.getInt("config.redis.base");
    }

    private void saveDefaultConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                InputStream is = plugin.getResourceAsStream("config.yml");
                OutputStream os = new FileOutputStream(configFile);
                ByteStreams.copy(is, os);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
