package site.philipp.TreeGrowPlugin;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import site.philipp.TreeGrowPlugin.Plugin.*;

import java.awt.*;
import java.util.ArrayList;

public class CommandPlayerRing implements CommandExecutor {

    public CommandPlayerRing(){

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(sender instanceof Player){
            try {
                double radius = Integer.parseInt(args[0].trim());
                int PointAmount = Integer.parseInt(args[1].trim());
                int ParticleAmount = Integer.parseInt(args[2].trim());
                Player player = (Player) sender;
                player.sendMessage("Radius: " + radius + " PointAmount: " + PointAmount + " ParticleAmount: " + ParticleAmount);
                Location playerLoc = player.getLocation();
                ArrayList<Location> circleLocs = getCircle(playerLoc.add(0,2,0), radius,PointAmount);
                for(Location circleLoc : circleLocs) {
                    player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, circleLoc, ParticleAmount);
                }
            }
            catch(NumberFormatException nfe){
                sender.sendMessage("Please provide the correct format!");
            }
            catch(CommandException ce){
                ((Player) sender).performCommand("help /PlayerRing");
            }
            catch(Exception e){
                ((Player) sender).performCommand("help /PlayerRing");
            }
        }
        else{
            return false;
        }
        return true;
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
