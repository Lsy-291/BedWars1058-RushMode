package cc.pixeleast.rushmode;

import cc.pixeleast.rushmode.configuration.Config;
import cc.pixeleast.rushmode.language.Message;
import cc.pixeleast.rushmode.listeners.BridgeListener;
import cc.pixeleast.rushmode.listeners.GameStartListener;
import cc.pixeleast.rushmode.listeners.PlayerBuyListener;
import cc.pixeleast.rushmode.versioncontrol.VersionController;
import com.andrei1058.bedwars.api.BedWars;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class RushMode extends JavaPlugin {
    public static RushMode plugin;
    public static VersionController versionController;
    public static BedWars api;
    public static Config config;

    @Override
    public void onEnable() {
        plugin = this;

        if (Bukkit.getPluginManager().getPlugin("BedWars1058") == null) {
            getLogger().severe("BedWars1058 was not found. Disabling...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        api = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();

        config = new Config("config");

        registerEvents(new GameStartListener(), new BridgeListener(), new PlayerBuyListener());
        new Message();

        versionController = new VersionController();
        getServer().getConsoleSender().sendMessage("§bVersion: " + getDescription().getVersion() + " §2(" + versionController.getCommitAbbrev() + ")" +
                (versionController.isDirty() ? "§e - DEV" : ""));
    }

    @Override
    public void onDisable() {

    }

    public static void registerEvents(Listener... listeners) {
        Arrays.stream(listeners).forEach(listener -> plugin.getServer().getPluginManager().registerEvents(listener, plugin));
    }
}
