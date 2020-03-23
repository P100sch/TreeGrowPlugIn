package site.philipp.TreeGrowPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class CommandTreeGrowSound implements CommandExecutor {
    private FileConfiguration _config;

    public CommandTreeGrowSound(FileConfiguration config){
        _config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (args[0].equalsIgnoreCase("on")){
            _config.set(Plugin.TreeGrowSound, true);
        }
        else if (args[0].equalsIgnoreCase("off")){
            _config.set(Plugin.TreeGrowSound, false);
        }
        else{
            return false;
        }
        return true;
    }
}
