package site.philipp.TreeGrowPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class CommandTreeParticles implements CommandExecutor {
    private FileConfiguration _config;

    public CommandTreeParticles(FileConfiguration config){
        _config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (args[0].equalsIgnoreCase("on")){
            _config.set(Plugin.TreeParticles, true);
        }
        else if (args[0].equalsIgnoreCase("off")){
            _config.set(Plugin.TreeParticles, false);
        }
        else{
            return false;
        }
        return true;
    }
}
