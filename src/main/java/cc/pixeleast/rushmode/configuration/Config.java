package cc.pixeleast.rushmode.configuration;

import org.simpleyaml.configuration.file.YamlFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static cc.pixeleast.rushmode.RushMode.api;
import static cc.pixeleast.rushmode.configuration.ConfigPath.*;

public class Config extends ConfigManager {

    public Config(String name) {
        super(name, api.getAddonsPath().getPath() + "/RushMode/");

        YamlFile yml = getYml();

        yml.setComment(GENERAL_ARENA_GROUPS, "List of arena groups that will be used for Rush Mode");
        yml.addDefault(GENERAL_ARENA_GROUPS, List.of("Solo"));

        LinkedHashMap<String, List<Integer>> generators = new LinkedHashMap<>();
        generators.put("base.iron", Arrays.asList(5, 1, 64));
        generators.put("base.gold", Arrays.asList(2, 3, 32));
        generators.put("base.emerald", Arrays.asList(1, 45, 10));
        generators.put("mid.diamond", Arrays.asList(1, 15, 10));
        generators.put("mid.emerald", Arrays.asList(1, 30, 10));

        generators.forEach((type, config) -> {
            yml.addDefault(GENERAL_GENERATOR_AMOUNT.replace("%type%", type), config.get(0));
            yml.addDefault(GENERAL_GENERATOR_DELAY.replace("%type%", type), config.get(1));
            yml.addDefault(GENERAL_GENERATOR_SPAWN_LIMIT.replace("%type%", type), config.get(2));
        });

        // Due to the poor design of 1058, even if the default upgrade forge/miner has tier 4/2, it must be reduced by one
        // This is counterintuitive, implementing shop/upgrade improvements is urgent
        yml.setComment(GENERAL_TEAM_UPGRADES, "List of team upgrades and their tiers\n" +
                "Format: Upgrade Name, Tier\n" +
                "Note: Upgrades in the following list will display as unlocked, but they will not actually have any effect");
        yml.addDefault(GENERAL_TEAM_UPGRADES, Arrays.asList("upgrade-forge,3", "upgrade-miner,1"));
        yml.setComment(GENERAL_TEAM_EFFECTS, "List of team effects and their amplifiers/durations\n" +
                "Format: Effect Name, Level, Duration (0 is infinite)\n" +
                "For example, after disabling upgrade-miner in the options above, corresponding effects can be added here");
        yml.addDefault(GENERAL_TEAM_EFFECTS, Arrays.asList("SPEED,1,0", "FAST_DIGGING,1,0"));

        yml.setComment(GENERAL_BRIDGE_DELAY, "Delay for placing each block");
        yml.addDefault(GENERAL_BRIDGE_DELAY, 2);
        yml.setComment(GENERAL_BRIDGE_LENGTH, "The length of the blocks constructed (excluding those placed by the player)");
        yml.addDefault(GENERAL_BRIDGE_LENGTH, 5);
        yml.setComment(GENERAL_BRIDGE_CONSUMING_BLOCKS, "Whether to consume the number of blocks built");
        yml.addDefault(GENERAL_BRIDGE_CONSUMING_BLOCKS, false);

        yml.setComment(GENERAL_BRIDGE_KEEP_COLOR, "The blocks placed are the original color or team color");
        yml.addDefault(GENERAL_BRIDGE_KEEP_COLOR, true);

        yml.setComment(GENERAL_BED_PROTECTION, "The defense layer of the bed\n" +
                "Corresponding versions of materials, check https://helpch.at/docs/{version}/org/bukkit/Material");
        yml.addDefault(GENERAL_BED_PROTECTION, Arrays.asList("WOOD", "WOOL", "STAINED_GLASS"));

        yml.setComment(GENERAL_BANNED_ITEMS, "List of items that cannot be purchased in Rush Mode");
        yml.addDefault(GENERAL_BANNED_ITEMS, List.of("OBSIDIAN"));

        yml.addDefault(GENERAL_EVENTS, Arrays.asList("BEDS_DESTROY, 900", "ENDER_DRAGON, 2100", "GAME_END, 2340"));

        // 1058 has implemented Sounds internally, but the API is not open to the public
        yml.setComment(SOUNDS, "Manage sound in various situations\n" +
        "Corresponding versions of sounds, check https://helpch.at/docs/{version}/org/bukkit/Sound");
        yml.setComment(SOUNDS_BRIDGE, "The sound played when placing each block");
        yml.addDefault(SOUNDS_BRIDGE, "STEP_WOOL");
        yml.setComment(SOUNDS_CANT_BUY, "The sound of players purchasing banned items");
        yml.addDefault(SOUNDS_CANT_BUY, "VILLAGER_NO");

        yml.options().copyDefaults(true);

        try {
            yml.save();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
