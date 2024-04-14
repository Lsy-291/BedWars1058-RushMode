package cc.pixeleast.rushmode.utils;

import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.generator.GeneratorType;
import com.andrei1058.bedwars.api.arena.generator.IGenerator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.Collection;

import static cc.pixeleast.rushmode.RushMode.config;
import static cc.pixeleast.rushmode.configuration.ConfigPath.*;

public class Misc {

    public static boolean isRushArena(IArena arena) {
        return arena != null && config.getList(GENERAL_ARENA_GROUPS).contains(arena.getGroup()) && arena.getStatus() == GameState.playing;
    }

    public static boolean isWool(Material material) {
        return material.name().endsWith("WOOL");
    }

    public static boolean hasEntityCover(IArena arena, Location location) {
        Collection<Entity> entities = location.getWorld().getNearbyEntities(location.clone().add(0.5d, 0.5d, 0.5d), 0.5d, 0.5d, 0.5d);
        return entities.stream().anyMatch(entity -> !(entity instanceof Item) && !(entity instanceof Player && arena.isSpectator((Player) entity)));
    }

    public static void setGenerator(IGenerator generator, String type) {
        String typeString = generator.getType().name().toLowerCase();
        String finalPath = type + "." + typeString;
        generator.setAmount(config.getInt(GENERAL_GENERATOR_AMOUNT.replace("%type%", finalPath)));
        generator.setDelay(config.getInt(GENERAL_GENERATOR_DELAY.replace("%type%", finalPath)));
        generator.setSpawnLimit(config.getInt(GENERAL_GENERATOR_SPAWN_LIMIT.replace("%type%", finalPath)));
        if (generator.getType() != GeneratorType.EMERALD) generator.setNextSpawn(0);
    }
}
