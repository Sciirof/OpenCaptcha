package com.pigeoncraft.opencaptcha;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor {

	private Main plugin;
	
	public CommandManager(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("oc") || command.getName().equalsIgnoreCase("captcha")) {
			if(args.length > 0) {
				if(args[0].equalsIgnoreCase("bypass")) {
					if(args.length == 1) {
						if(sender instanceof Player) {
							Player player = (Player) sender;
							if(player.hasPermission("opencaptcha.bypass")) {
								plugin.sendPlayerToServer(player);
								return true;
							} else {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to do this!"));
								return true;
							}
						} else {
							sender.sendMessage("This is an in-game command");
							return true;
						}
					}
					if(args.length > 1) {
						Player player = Bukkit.getPlayerExact(args[1]);
						if(player != null) {
							if(sender instanceof Player) {
								if(((Player) sender).hasPermission("opencaptcha.admin")) {
									plugin.sendPlayerToServer(player);
									return true;
								} else {
									sender.sendMessage("This is an in-game command");
									return true;
								}
							} else {
								plugin.sendPlayerToServer(player);
								return true;
							}
						} else {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer '" + args[1] + "' was not found"));
							return true;
						}
					}
				}
			}
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /oc|captcha <bypass> <playername>(Optional)"));
			return true;
		}
		return false;
	}

}
