package com.pigeoncraft.opencaptcha;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class Main extends JavaPlugin {
	private static String path;
	private File userConf;
	@Override 
	public void onEnable(){
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getDataFolder().mkdir();
		setupConfig();
		path = getDataFolder().getPath();
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		getLogger().info("[OpenCaptcha] Enabled!");
		this.getCommand("oc").setExecutor(new CommandManager(this));
		this.getCommand("captcha").setExecutor(new CommandManager(this));
	}
	
	private void setupConfig() {
		File configFile = new File(getDataFolder()+File.separator+"config.yml");
		File userData = new File(getDataFolder()+File.separator+"userdata"+File.separator+"users.yml");
		File userDataFolder = new File(getDataFolder()+File.separator+"userdata"+File.separator);
		userDataFolder.mkdir();
		if(!configFile.exists()) {
			this.saveDefaultConfig();
		}
		if(!userData.exists()) {
			try {
				userData.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		userConf = userData;
	}
	
	public static String getDataPath() {
		return path;
	}
	
	public File getUserConf() {
		return userConf;
	}
	
	public void sendPlayerToServer(Player player) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(this.getConfig().getString("server"));    
		player = Bukkit.getPlayerExact(player.getName());
		player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
	}
}
