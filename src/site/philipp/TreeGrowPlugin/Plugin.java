package site.philipp.TreeGrowPlugin;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    private RegisteredListener toggleSneakListener;

    public Plugin() {
        toggleSneakListener = new RegisteredListener(new Listener() {
        }, new EventExecutor() {
            @Override
            public void execute(Listener listener, Event event) throws EventException {
                PlayerToggleSneakEvent sneakEvent = (PlayerToggleSneakEvent) event;
                World currentWorld = sneakEvent.getPlayer().getWorld();
                Location playerLocation = sneakEvent.getPlayer().getLocation();
                Player player = sneakEvent.getPlayer();
                for (int x = playerLocation.getBlockX() - 2; x <= playerLocation.getBlockX() + 2; x++) {
                    for (int y = playerLocation.getBlockY() - 2; y <= playerLocation.getBlockY() + 2; y++) {
                        for (int z = playerLocation.getBlockZ() - 2; z <= playerLocation.getBlockZ() + 2; z++) {
                            if (z == playerLocation.getBlockZ() && y == playerLocation.getBlockY() && x == playerLocation.getBlockX()) {
                                continue;
                            }
                            Block block = currentWorld.getBlockAt(x, y, z);
                            BlockData blockData = block.getBlockData();
                            if (blockData instanceof Sapling) {
                                Sapling sapling = ((Sapling) blockData);
                                sapling.setStage(sapling.getMaximumStage());
                                block.setBlockData(blockData,true);

                                String blockName = block.getType().name();
                                int indexOfUnderscore = blockName.indexOf('_');
                                int indexOfDarkOak = blockName.indexOf("DARK_OAK");

                                TreeType treeType = TreeType.DARK_OAK;
                                if (indexOfDarkOak < 0){
                                    String materialName = blockName.substring(0, indexOfUnderscore);
                                    player.sendMessage(materialName);
                                    //treeType = blockName.substring(0, indexOfUnderscore);
                                    if(materialName.equalsIgnoreCase("OAK")){
                                        if(block.getBiome() == Biome.SWAMP){
                                            treeType = TreeType.SWAMP;
                                        }
                                        else{
                                            treeType = TreeType.TREE;
                                        }
                                    }
                                    else{
                                        treeType = TreeType.valueOf(materialName);
                                    }
                                }
                                block.setType(Material.AIR);

                                currentWorld.generateTree(block.getLocation(), treeType);
                            }
                        }
                    }
                }
            }
        }, EventPriority.LOWEST, this, false);
    }

    @Override
    public void onEnable(){
        PlayerToggleSneakEvent.getHandlerList().register(toggleSneakListener);
        System.out.println("\u001B[32m" + "TreeGrowPlugIn enabled!" + "\u001B[0m");
    }
    @Override
    public void onDisable(){
        PlayerToggleSneakEvent.getHandlerList().unregister(toggleSneakListener);
        System.out.println("TreeGrowPlugIn disabled!");
    }
}
