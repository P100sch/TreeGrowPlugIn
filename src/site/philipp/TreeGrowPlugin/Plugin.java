package site.philipp.TreeGrowPlugin;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    private RegisteredListener _toggleSneakListener;
    private String _prefix = "[TreeGrowPlugIn]";
    private FileConfiguration _config = getConfig();
    public static final String TreeGrowSound = "TreeGrowSound";
    public static final String TreeGrowChance = "TreeGrowChance";

    public Plugin() {
        _toggleSneakListener = new RegisteredListener(new Listener() {
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
                                int hit = (int)Math.round(Math.random()*100);

                                //currentWorld.spawnParticle(Particle.DRAGON_BREATH, block.getLocation(), 5);
                                if(hit == _config.getInt(TreeGrowChance)){
                                    block.setType(Material.AIR);
                                    boolean worked = currentWorld.generateTree(block.getLocation(), treeType);

                                    if(_config.getBoolean(TreeGrowSound)) {
                                        if (worked) {
                                            player.playNote(player.getLocation(), Instrument.PLING, Note.natural(1, Note.Tone.E));
                                            //currentWorld.spawnParticle(Particle.VILLAGER_HAPPY, block.getLocation(), 10);
                                        } else {
                                            player.playNote(player.getLocation(), Instrument.PLING, Note.natural(1, Note.Tone.D));
                                            //currentWorld.spawnParticle(Particle.SMOKE_NORMAL, block.getLocation(), 10);
                                        }
                                    }
                                    else{
                                        if(!worked){
                                            block.setType(blockData.getMaterial());
                                        }
                                    }
                                }

                                //Bukkit.getPluginManager().callEvent(new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, new ItemStack(Material.BONE_MEAL, 1), block, BlockFace.UP));
                            }
                        }
                    }
                }
            }
        }, EventPriority.LOWEST, this, false);
    }

    @Override
    public void onEnable(){
        PlayerToggleSneakEvent.getHandlerList().register(_toggleSneakListener);
        System.out.println("\u001b[36m" + _prefix + " loading config..." + "\u001B[0m");

        System.out.println(_config.options());
        _config.addDefault(TreeGrowSound, true);
        _config.addDefault(TreeGrowChance, 5);
        _config.options().copyDefaults(true);
        saveConfig();

        getCommand(TreeGrowSound).setExecutor(new CommandTreeGrowSound(_config));
        getCommand(TreeGrowChance).setExecutor(new CommandTreeGrowChance(_config));

        System.out.println("\u001b[32m" + _prefix + " loaded config!" + "\u001B[0m");
        System.out.println("\u001B[32m" + _prefix + " enabled!" + "\u001B[0m");
    }
    @Override
    public void onDisable(){
        PlayerToggleSneakEvent.getHandlerList().unregister(_toggleSneakListener);
        saveConfig();
        System.out.println("TreeGrowPlugIn disabled!");
    }
}
