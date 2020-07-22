package me.reagan.jukelooper.commands;

import me.reagan.jukelooper.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class JukeLooperReload implements CommandExecutor {
    private Main plugin;

    public JukeLooperReload(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            if (sender.hasPermission("jukelooper.reload")) {
            	plugin.reloadConfig();
            	sender.sendMessage(ChatColor.DARK_GRAY + "JukeLooper: " + ChatColor.GRAY + "Successfully reloaded the config!");
            } else {
            	if (!sender.hasPermission("jukelooper.reload")) {
            		sender.sendMessage(ChatColor.DARK_GRAY + "Error: " + ChatColor.GRAY + "You do not have permission to this command!");
            	}
            }
    }
        if (args.length >= 1) {
        	sender.sendMessage(ChatColor.DARK_GRAY + "Error: " + ChatColor.GRAY + "Invalid command, please try again!");
        }
        return true;
    }
}
