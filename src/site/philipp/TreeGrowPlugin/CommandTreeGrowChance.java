package site.philipp.TreeGrowPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class CommandTreeGrowChance implements CommandExecutor {
    private FileConfiguration _config;

    public CommandTreeGrowChance(FileConfiguration config){
        _config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        try {
            int num = Integer.parseInt(args[0].trim());
            _config.set(Plugin.TreeGrowChance, num);
        }
        catch (NumberFormatException e) {
            return false;
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
}
