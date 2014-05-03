package co.justgame.simpleArena.ArenaClasses;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import co.justgame.simpleArena.Main.SimpleArena;
import co.justgame.simpleArena.Resources.Messages;


public class ArenaFiles {
	
	private static Plugin plugin = SimpleArena.getInstance();
	
	public static void createSample(){
		File arenaFolder = new File(plugin.getDataFolder()+File.separator+"Arena"+File.separator);
		try{
			Files.createDirectories(arenaFolder.toPath());
			File arenaFile = new File(arenaFolder, "sample.yml");
			if(!arenaFile.exists()){
				arenaFile.createNewFile();
			
				FileConfiguration config = YamlConfiguration.loadConfiguration(arenaFile);
				
				config.set("name", "sample");
				config.set("message-on-start", "&8[Arena] &9First team to 25 points or the team with the most points at the end of 300 seconds wins!");
				config.set("arena-teams", 2);
				config.set("delay-time", 20);
				config.set("point-on-death-time", 20);
				config.set("players", 2);
				config.set("kit-time", 10);
				config.set("respawn-on-death", true);
				config.set("time-limit", 300);
				config.set("points-limit", 25);
				
				config.set("spawn.team1.x", 0);
				config.set("spawn.team1.y", 64);
				config.set("spawn.team1.z", 0);
				config.set("spawn.team1.world", "world");
				
				config.set("spawn.team2.x", 0);
				config.set("spawn.team2.y", 64);
				config.set("spawn.team2.z", 0);
				config.set("spawn.team2.world", "world");
				
				config.set("default-class.team1.class", "default");
				config.set("default-class.team2.class", "default");
				
				config.save(arenaFile);
			}
		}catch (IOException e){
			Bukkit.getLogger().log(Level.SEVERE, ChatColor.RED + "Failed to load Arenas! Reason: Error creating Arena folder!");
		}
	}
	
	public static ArrayList<Arena> loadArenas(){
		File arenaFolder = new File(plugin.getDataFolder()+File.separator+"Arena"+File.separator);
		try{
			Files.createDirectories(arenaFolder.toPath());
		}catch (IOException e){
			Bukkit.getLogger().log(Level.SEVERE, ChatColor.RED + "Failed to load Arenas! Reason: Error creating Arena folder!");
		}
		
		ArrayList<Arena> arenas = new ArrayList<Arena>();
		for(File file: arenaFolder.listFiles()){
			try{
				if(file.getCanonicalPath().endsWith("yml")){
				 FileConfiguration config = YamlConfiguration.loadConfiguration(file);
				 
				 	 Arena arena = null;
					 ArrayList<Location> spawnPoints = new ArrayList<Location>();
					 Location loc = new Location(null, 0, 0, 0 );
					 for(String message: config.getConfigurationSection("").getKeys(true)){
						 if(message.contains(".x")){
							 loc.setX(config.getInt(message));
						 }else if(message.contains(".y")){
							 loc.setY(config.getInt(message));
						 }else if(message.contains(".z")){
							 loc.setZ(config.getInt(message));
						 }else if(message.contains(".world")){
							 loc.setWorld(Bukkit.getWorld(config.getString(message)));
							 spawnPoints.add(loc);
							 loc = new Location(null, 0, 0, 0 );
						 }
					 }
					 ArrayList<String> defaultClasses = new ArrayList<String>();
					 for(String message: config.getConfigurationSection("").getKeys(true)){
						 if(message.contains(".class")){
							 String clazz = config.getString(message);
							 if(SimpleArena.getClass(clazz) != null)
								 defaultClasses.add(clazz);
							 else
								 throw new NullPointerException("Unknown Class!");
						 }
					 }
					 
					 arena = new Arena( 
							 config.getString("name"),
							 Messages.formatString(config.getString("message-on-start", "Error Loading Message!")),
							 config.getInt("arena-teams", 2),
							 config.getInt("players", 2),
							 config.getInt("kit-time", 20),
							 config.getInt("delay-time", 20),
							 config.getInt("point-on-death-time", 30),
							 config.getBoolean("respawn-on-death", true),
							 config.getInt("time-limit", 300),
							 config.getInt("points-limit", 25),
							 spawnPoints, defaultClasses);
					 arenas.add(arena);
				}
			}catch (IOException e){
				Bukkit.getLogger().log(Level.SEVERE, ChatColor.RED + "Failed to load Arenas! Reason: "+ e.getMessage());
			}catch(IndexOutOfBoundsException e){
				Bukkit.getLogger().log(Level.SEVERE, ChatColor.RED + "Failed to load Arenas! Reason: Missing team spawn point or default class!");
			}catch(Exception e){
				Bukkit.getLogger().log(Level.SEVERE, ChatColor.RED + "Failed to load Arenas! Reason: unparsable value in config!");
				e.printStackTrace();
			}
		}
		return arenas;
	}
}




