package com.pigeoncraft.opencaptcha;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class PlayerListener implements Listener {

	private Main plugin;
	private String url;
	private FileConfiguration conf;
	
	public PlayerListener(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		//give captcha
		Player player = e.getPlayer();
		String secret = generateSecret();
		url = "http://image.captchas.net?client=" + plugin.getConfig().getString("user") + "&random=" + secret + "&width=150&height=150";
		String answer = calcAnswer(plugin.getConfig().getString("key") + "" + secret);
		ItemStack map = new ItemStack(Material.FILLED_MAP, 1);
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + "&aType what you see on the map"));
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + "&aCan't see it too well? Use the link to view the image:"));
		player.sendMessage(ChatColor.GREEN + "" + url);
		player.getInventory().clear();
		player.getInventory().setHeldItemSlot(4);
		player.getInventory().setItemInMainHand(map);
		conf = YamlConfiguration.loadConfiguration(plugin.getUserConf());
		conf.set("user." + player.getUniqueId() + ".answer", answer);
		conf.set("user." + player.getUniqueId() + ".tries", 5);
		try {
			conf.save(plugin.getUserConf());
		} catch (IOException e1) {
				e1.printStackTrace();
		}
	}
	
	@EventHandler
	public void onMapInitialize(MapInitializeEvent e) {
		MapView view = e.getMap();
		for(MapRenderer render : view.getRenderers()) {
			view.removeRenderer(render);
		}
		view.addRenderer(new CaptchaRenderer(url));
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		String answ = conf.getString("user." + player.getUniqueId() + ".answer");
		if(e.getMessage().equalsIgnoreCase(answ)) {
			e.setCancelled(true);
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + "&aSuccess you will now be redirected!"));
			conf.set("user." + player.getUniqueId(), null);
			try {
				conf.save(plugin.getUserConf());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			plugin.sendPlayerToServer(player);
		} else {
			e.setCancelled(true);
			int tries = conf.getInt("user." + player.getUniqueId() + ".tries");
			tries--;
			final Player p = player;
			if(tries == 0) {
				conf.set("user." + player.getUniqueId(), null);
				try {
					conf.save(plugin.getUserConf());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Bukkit.getScheduler().runTask(plugin, new Runnable() {
					public void run() {
						p.kickPlayer(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + "You failed to complete the captcha!"));
					}
				});
			}
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + "&cIncorrect answer please try again. " + tries + " tries remaining"));
			conf.set("user." + player.getUniqueId() + ".tries", tries);
		}
		
	}
	
	private String generateSecret() {
		;
	    String generatedString = UUID.randomUUID().toString().replaceAll("[^a-z]", "");
	    return generatedString;
	}
	
	public static int getDecimal(String hex){  
	    String digits = "0123456789ABCDEF";  
	             hex = hex.toUpperCase();  
	             int val = 0;  
	             for (int i = 0; i < hex.length(); i++)  
	             {  
	                 char c = hex.charAt(i);  
	                 int d = digits.indexOf(c);  
	                 val = 16*val + d;  
	             }  
	             return val;  
	}
	
	private String calcAnswer(String secret) {
		String answer = "";
		String alphabet = "abcdefghijklmnopqrstuvwxyz";
		try {
		    
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(secret.getBytes());

			for(int i = 0; i < 6; i++) {
				String hexVal = Integer.toHexString(((digest[i] % 26) & 0xFF) | 0x100).substring(1, 3);
				int hex = getDecimal(hexVal);
				answer += alphabet.charAt(hex % 26);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return answer;
	}
	
}
