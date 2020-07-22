package me.reagan.jukelooper.commands;

import me.reagan.jukelooper.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class JukeLooper implements CommandExecutor {
    private Main plugin;

    public JukeLooper(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.DARK_GRAY + "Plugin Name: " + ChatColor.GRAY + "JukeLooper");
            sender.sendMessage(ChatColor.DARK_GRAY + "Description : " + ChatColor.GRAY + "Allows you to put jukeboxes on a continuous loop!");
            sender.sendMessage(ChatColor.DARK_GRAY + "Version: " + ChatColor.GRAY + "1.0.1");
            sender.sendMessage(ChatColor.DARK_GRAY + "Coded by " + ChatColor.GRAY + "MinecraftReagan");
        } else {
        	if (args.length >= 1) {
        		sender.sendMessage(ChatColor.DARK_GRAY + "Error: " + ChatColor.GRAY + "Invalid command, please try again!");
        	}
        }
        return true;
    }
}
