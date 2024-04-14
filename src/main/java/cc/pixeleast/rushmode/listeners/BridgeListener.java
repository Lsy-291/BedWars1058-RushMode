package cc.pixeleast.rushmode.listeners;

import cc.pixeleast.rushmode.utils.Misc;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.events.gameplay.GameEndEvent;
import com.andrei1058.bedwars.api.language.Language;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static cc.pixeleast.rushmode.RushMode.*;
import static cc.pixeleast.rushmode.configuration.ConfigPath.*;
import static cc.pixeleast.rushmode.language.MessagePath.MESSAGES_BRIDGE_MODE_ACTIVATED;
import static cc.pixeleast.rushmode.language.MessagePath.MESSAGES_BRIDGE_MODE_DEACTIVATED;

public class BridgeListener implements Listener {
    private Set<UUID> bridgePlayers = new HashSet<>();

    @EventHandler
    public void onBridgeModeToggle(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        IArena arena = api.getArenaUtil().getArenaByPlayer(player);
        if (!Misc.isRushArena(arena)) return;

        if ((event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) && event.getMaterial().name().endsWith("WOOL")) {
            if (bridgePlayers.add(playerUUID)) {
                api.getVersionSupport().playAction(player, Language.getMsg(player, MESSAGES_BRIDGE_MODE_ACTIVATED));
                return;
            }
            bridgePlayers.remove(playerUUID);
            api.getVersionSupport().playAction(player, Language.getMsg(player, MESSAGES_BRIDGE_MODE_DEACTIVATED));
        }
    }

    @EventHandler
    public void onWoolPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        IArena arena = api.getArenaUtil().getArenaByPlayer(player);
        if (!Misc.isRushArena(arena)) return;

        if (!Misc.isWool(event.getBlockPlaced().getType()) || !bridgePlayers.contains(playerUUID)) return;

        Block placedBlock = event.getBlockPlaced();
        BlockFace face = placedBlock.getFace(event.getBlockAgainst());
        Vector vector = new Vector(-face.getModX(), -face.getModY(), -face.getModZ());
        Location nextBlock = placedBlock.getLocation().clone().add(vector);

        new BukkitRunnable() {
            int count = 0;
            final int length = config.getInt(GENERAL_BRIDGE_LENGTH);

            @Override
            public void run() {
                if (count < length && nextBlock.getBlock().getType() == Material.AIR && !arena.isProtected(nextBlock) && !Misc.hasEntityCover(arena, nextBlock)) {
                    if (config.getBoolean(GENERAL_BRIDGE_CONSUMING_BLOCKS)) {
                        boolean blockEnough = false;
                        for (ItemStack item : player.getInventory()) {
                            if (item != null && item.getType() != Material.AIR &&Misc.isWool(item.getType())) {
                                api.getVersionSupport().minusAmount(player, item, 1);
                                blockEnough = true;
                                break;
                            }
                        }

                        if (!blockEnough) {
                            cancel();
                            return;
                        }
                    }

                    Block block = nextBlock.getBlock();
                    block.setType(Material.WOOL);
                    api.getVersionSupport().setBlockTeamColor(block, arena.getTeam(player).getColor());
                    nextBlock.add(vector);
                    block.getWorld().playSound(nextBlock, Sound.valueOf(config.getString(SOUNDS_BRIDGE)), 1, 1);
                    arena.addPlacedBlock(block);
                    count++;

                    return;
                }
                cancel();
            }
        }.runTaskTimer(plugin, 0L, config.getInt(GENERAL_BRIDGE_DELAY));
    }

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        IArena arena = event.getArena();
        if (!Misc.isRushArena(arena)) return;

        bridgePlayers.removeIf(playerUUID -> arena.getAllPlayers().stream().anyMatch(player -> player.getUniqueId().equals(playerUUID)));
    }

}
