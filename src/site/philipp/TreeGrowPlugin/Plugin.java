package site.philipp.TreeGrowPlugin;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
                for (int x = playerLocation.getBlockX() - 2; x <= playerLocation.getBlockX() + 2; x++) {
                    if (x == playerLocation.getBlockX()) {
                        continue;
                    }
                    for (int y = playerLocation.getBlockY() - 2; y <= playerLocation.getBlockY() + 2; y++) {
                        if (y == playerLocation.getBlockY()) {
                            continue;
                        }
                        for (int z = playerLocation.getBlockZ() - 2; z <= playerLocation.getBlockZ() + 2; z++) {
                            if (z == playerLocation.getBlockZ()) {
                                continue;
                            }
                            BlockData blockData = currentWorld.getBlockAt(x, y, z).getBlockData();
                            if (blockData instanceof Sapling) {
                                Sapling sapling = ((Sapling) blockData);
                                sapling.setStage(sapling.getMaximumStage());
                                System.out.println("TreeGrowPlugIn executed!");
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
        System.out.println("TreeGrowPlugIn aktiviert!");
    }
    @Override
    public void onDisable(){
        PlayerToggleSneakEvent.getHandlerList().unregister(toggleSneakListener);
        System.out.println("TreeGrowPlugIn deaktiviert!");
    }
}
