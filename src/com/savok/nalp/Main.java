package com.savok.nalp;

import java.util.ArrayList;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	public FileConfiguration config = getConfig();
	public static boolean isShutdown = false;
	
	@Override
	public void onDisable() { 
	}
	
	@Override
	public void onEnable() { 
		File configFile = new File(getDataFolder(), "config.yml");
		if(!configFile.exists()) {
			createConfig();
		}
		
		checkForGroups();
		
	}
	
	public void createConfig() {
		config.options().header("You can check config documentation on GitHub: https://github.com/SavokBS/nalp/tree/main");
	    config.addDefault("groups", Arrays.asList("creator", "admin", "helper"));
	    config.addDefault("time", 180);
	    config.addDefault("shutdown-message", "&cAll admins have left. The server will shutdown in %e minutes.");
	    config.options().copyDefaults(true);
	    saveConfig();
	}
	
	public void checkForGroups() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("NALP"), new Runnable() {
			@Override
			public void run() {
				for (Player p: Bukkit.getOnlinePlayers()) {{
					int playersInGroup = 0;
					int time = getConfig().getInt("time");
			        List<String> groups = getConfig().getStringList("groups");
			        String shutdownMessage = getConfig().getString("shutdown-message");
			        
			        for(String group : groups) {
			        	if(p.hasPermission("group." + group)) {
			        		playersInGroup++;
			        	}
			        }
			        if(playersInGroup == 0 && !isShutdown) {
			        	isShutdown = true;
			        	getServer().broadcastMessage(shutdownMessage.replace("&", "§").replace("%e", "" + time));
				        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("NALP"), new Runnable() {
				        	
				        	@Override
				        	public void run() {
				        		Bukkit.shutdown();
				        	}
				        }, time*60*20L);
			        }
			        else if (playersInGroup > 0 && isShutdown) {
			        	isShutdown = false;
			        }
			        
				}
			}
		}
	}, 0L, 20L);
	}

}
