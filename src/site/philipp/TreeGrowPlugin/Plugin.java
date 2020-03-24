package site.philipp.TreeGrowPlugin;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.channels.InterruptedByTimeoutException;
import java.util.ArrayList;

public class Plugin extends JavaPlugin {
    private RegisteredListener _toggleSneakListener;
    private static String _prefix = "[TreeGrowPlugIn]";
    private FileConfiguration _config = getConfig();
    public static final String TreeGrowSound = "TreeGrowSound";
    public static final String TreeGrowChance = "TreeGrowChance";
    public static final String PlayerRing = "PlayerRing";
    public static final String TreeParticles = "TreeParticles";
    private final Double _radius = _config.getDouble("TreeParticleRadius");
    private final Integer _pointAmount = _config.getInt("TreeParticlePointAmount");
    private final Integer _particleAmount = _config.getInt("TreeParticleAmount");

    public Plugin() {
        _toggleSneakListener = new RegisteredListener(new Listener() {
        }, new EventExecutor() {
            @Override
            public void execute(Listener listener, Event event) throws EventException {
                PlayerToggleSneakEvent sneakEvent = (PlayerToggleSneakEvent) event;
                World currentWorld = sneakEvent.getPlayer().getWorld();
                Location playerLocation = sneakEvent.getPlayer().getLocation();
                Player player = sneakEvent.getPlayer();
                Boolean treeParticles = _config.getBoolean(TreeParticles);
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
                                int hit = (int)Math.round(Math.random()*_config.getInt(TreeGrowChance));

                                if(hit == _config.getInt(TreeGrowChance)){
                                    block.setType(Material.AIR);
                                    boolean worked = currentWorld.generateTree(block.getLocation(), treeType);
                                    ArrayList<Location> circleLocs = getCircle(block.getLocation().add(0.5,0.5,0.5), _radius,_pointAmount);
                                    Particle particleChoice;
                                    if(_config.getBoolean(TreeGrowSound)) {
                                        if (worked) {
                                            player.playNote(player.getLocation(), Instrument.PLING, Note.natural(1, Note.Tone.E));
                                            particleChoice = Particle.VILLAGER_HAPPY;
                                        } else {
                                            player.playNote(player.getLocation(), Instrument.PLING, Note.natural(1, Note.Tone.D));
                                            particleChoice = Particle.SMOKE_NORMAL;
                                        }

                                        if(treeParticles){
                                            for(Location circleLoc : circleLocs) {
                                                player.getWorld().spawnParticle(particleChoice, circleLoc, _particleAmount);
                                            }
                                        }
                                    }
                                    else{
                                        if(!worked){
                                            block.setType(blockData.getMaterial());
                                        }
                                    }
                                }
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
        _config.addDefault(TreeParticles, true);
        _config.addDefault("TreeParticleRadius", 1.0);
        _config.addDefault("TreeParticlePointAmount", 100);
        _config.addDefault("TreeParticleAmount", 3);
        _config.options().copyDefaults(true);
        saveConfig();

        getCommand(TreeGrowSound).setExecutor(new CommandTreeGrowSound(_config));
        getCommand(TreeGrowChance).setExecutor(new CommandTreeGrowChance(_config));
        getCommand(PlayerRing).setExecutor(new CommandPlayerRing());
        getCommand(TreeParticles).setExecutor(new CommandTreeParticles(_config));


        System.out.println("\u001b[32m" + _prefix + " loaded config!" + "\u001B[0m");
        System.out.println("\u001B[32m" + _prefix + " enabled!" + "\u001B[0m");
    }
    @Override
    public void onDisable(){
        PlayerToggleSneakEvent.getHandlerList().unregister(_toggleSneakListener);
        saveConfig();
        System.out.println("TreeGrowPlugIn disabled!");
    }

    public ArrayList<Location> getCircle(Location center, double radius, int amount)
    {
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        ArrayList<Location> locations = new ArrayList<Location>();
        for(int i = 0;i < amount; i++)
        {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, x, center.getY(), z));
        }
        return locations;
    }
}
