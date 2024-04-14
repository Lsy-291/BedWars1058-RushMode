package cc.pixeleast.rushmode.listeners;

import cc.pixeleast.rushmode.utils.BedProtectionPlacer;
import cc.pixeleast.rushmode.utils.Misc;
import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.NextEvent;
import com.andrei1058.bedwars.api.arena.generator.GeneratorType;
import com.andrei1058.bedwars.api.arena.generator.IGenerator;
import com.andrei1058.bedwars.api.events.gameplay.GameStateChangeEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cc.pixeleast.rushmode.RushMode.config;
import static cc.pixeleast.rushmode.RushMode.plugin;
import static cc.pixeleast.rushmode.configuration.ConfigPath.*;
import static cc.pixeleast.rushmode.language.MessagePath.MESSAGES_ARENA_START_TUTORIAL;

public class GameStartListener implements Listener {

    @EventHandler
    public void onGameStart(GameStateChangeEvent event) {
        IArena arena = event.getArena();
        if (!Misc.isRushArena(arena)) return;
        if (event.getNewState() == GameState.playing) {
            // todo: 1058 is required to add event management related APIs for Addon
            // Replace the original events in the arena
            arena.getEventQueue().clear();
            arena.getNextEventList().clear();
            arena.getUpgradeTasks().forEach(BukkitTask::cancel);
            config.getList(GENERAL_EVENTS).forEach(eventData -> {
                String[] eventSplit = eventData.split(",");
                String eventName = eventSplit[0];
                NextEvent nextEvent = NextEvent.valueOf(eventName);
                long eventTime = Integer.parseInt(eventSplit[1].trim());
                arena.getEventQueue().put(nextEvent, eventTime);
                arena.setEventUpgrade(nextEvent, eventTime);
            });
            List<Map.Entry<NextEvent, Long>> eventQueueOrdered = new ArrayList<>(arena.getEventQueue().entrySet());
            eventQueueOrdered.sort(Map.Entry.comparingByValue());
            eventQueueOrdered.forEach(addEvent -> arena.getNextEventList().add(addEvent.getKey()));
            arena.setNextEvent(eventQueueOrdered.get(0).getKey());

            arena.getTeams().forEach(team -> {
                config.getList(GENERAL_TEAM_UPGRADES).forEach(upgrade -> {
                    String[] upgradeSplit = upgrade.split(",");
                    String upgradeName = upgradeSplit[0];
                    try {
                        // todo: Implement shop/upgrade improvements to achieve the highest tier
                        int upgradeTier = Integer.parseInt(upgradeSplit[1]);
                        team.getTeamUpgradeTiers().put(upgradeName, upgradeTier);
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
                        plugin.getLogger().severe("Invalid team upgrade tier for " + upgradeName);
                    }
                });

                config.getList(GENERAL_TEAM_EFFECTS).forEach(effect -> {
                    String[] effectSplit = effect.split(",");
                    String effectName = effectSplit[0];
                    PotionEffectType potionEffect = PotionEffectType.getByName(effectName);

                    if (potionEffect == null) {
                        plugin.getLogger().severe("Invalid team effect: " + effectName);
                    } else {
                        try {
                            int amplifier = Integer.parseInt(effectSplit[1]);
                            int duration = Integer.parseInt(effectSplit[2]);
                            team.addTeamEffect(potionEffect, amplifier - 1, duration  <= 0 ? Integer.MAX_VALUE : duration * 20);
                        } catch (NumberFormatException exception) {
                            plugin.getLogger().severe("Invalid team effect amplifier/duration for " + effectName);
                        }
                    }
                });

                // Set iron/gold generator rates
                for (IGenerator generator : team.getGenerators()) {
                    Misc.setGenerator(generator, "base");
                }

                // Create emerald generator
                arena.getConfig().getArenaLocations("Team." + team.getName() + ".Emerald").forEach(location -> {
                    IGenerator generator = arena.addGenerator(location, GeneratorType.CUSTOM, team);
                    generator.setOre(new ItemStack(Material.EMERALD));
                    generator.setType(GeneratorType.EMERALD);
                    Misc.setGenerator(generator, "base");
                });

                if (!team.isBedDestroyed()) new BedProtectionPlacer(arena, team).run();
            });

            // Set diamond/emerald generator rates and tiers
            arena.getOreGenerators().forEach(generator -> {
                Misc.setGenerator(generator, "mid");
                generator.setTier(3, false);
            });
        } else if (event.getNewState() == GameState.starting) {
            arena.getStartingTask().setTutorialMessage(MESSAGES_ARENA_START_TUTORIAL);
        }
    }
}
