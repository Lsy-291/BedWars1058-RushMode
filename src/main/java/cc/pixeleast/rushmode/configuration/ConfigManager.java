package cc.pixeleast.rushmode.configuration;

import org.bukkit.Bukkit;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import static cc.pixeleast.rushmode.RushMode.plugin;

public class ConfigManager {
    private YamlFile yml;
    private String name;
    private boolean firstTime = false;

    public ConfigManager(String name, String dir) {
        this.name = name;

        yml = new YamlFile(dir + name + ".yml");

        try {
            if (!yml.exists()) {
                yml.createNewFile();
                Bukkit.getLogger().log(Level.FINE, "Creating " + yml.getFilePath());
            }
            yml.createOrLoadWithComments();
        } catch (IOException exception) {
            Bukkit.getLogger().log(Level.SEVERE, exception.getMessage());
        }
    }

    public YamlFile getYml() {
        return yml;
    }

    public List<String> getList(String path) {
        return yml.getStringList(path);
    }

    public int getInt(String path) {
        return yml.getInt(path);
    }

    public double getDouble(String path) {
        return yml.getDouble(path);
    }

    public boolean getBoolean(String path) {
        return yml.getBoolean(path);
    }

    public String getString(String path) {
        return yml.getString(path);
    }
}
