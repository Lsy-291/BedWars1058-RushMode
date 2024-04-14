package cc.pixeleast.rushmode.utils;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.*;

import static cc.pixeleast.rushmode.RushMode.api;
import static cc.pixeleast.rushmode.RushMode.config;
import static cc.pixeleast.rushmode.configuration.ConfigPath.GENERAL_BED_PROTECTION;

public class BedProtectionPlacer {
    private IArena arena;
    private ITeam team;
    private List<Material> layersType = new ArrayList<>();
    private List<Block> bedBlocks;
    private Queue<Block> placeQueue = new ArrayDeque<>();
    private List<Block> placedBlocks = new ArrayList<>();

    public BedProtectionPlacer(IArena arena, ITeam team) {
        this.arena = arena;
        this.team = team;
        this.bedBlocks = team.getBedPart();
        config.getList(GENERAL_BED_PROTECTION).forEach(type -> layersType.add(Material.getMaterial(type)));
    }

    public void run() {
        if (bedBlocks.isEmpty()) return;
        placeQueue.addAll(bedBlocks);
        processAndUpdateQueue(0);
    }

    private void processAndUpdateQueue(int layer) {
        int count = placeQueue.size();
        for (int i = 0; i < count; i++) {
            processBlock(placeQueue.poll(), layer);
        }

        if (layer >= layersType.size()) return;
        processAndUpdateQueue(++layer);
    }

    private void processBlock(Block block, int layer) {
        if (layer >= 1 && block.getType() == Material.AIR) {
            block.setType(layersType.get(layer - 1));
            api.getVersionSupport().setBlockTeamColor(block, team.getColor());
            placedBlocks.add(block);
            arena.addPlacedBlock(block);
        }

        if (++layer > layersType.size()) return;
        List<BlockFace> facing = Arrays.asList(BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.UP);
        facing.forEach(face -> {
           Block newBlock = block.getRelative(face);
           if (placedBlocks.contains(newBlock)) return;
           placeQueue.add(newBlock);
        });
    }

}
