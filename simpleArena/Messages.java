package simpleArena;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;


public class Messages {
	
	private static HashMap<String, String> messageData = new HashMap<String, String>();
	static Plugin plugin = SimpleArena.getInstance();
	
	public static synchronized String get(String key){
		return messageData.get(key);
	}
	
	public static synchronized void loadMessages(){
		
		File Messages = new File(plugin.getDataFolder() + File.separator + "messages.yml");
		if(!Messages.exists()) try{
			Messages.createNewFile();
		}catch (IOException e){
			e.printStackTrace();
		}
		
		setMessage("simplearena.sign.place", "&cYou may not place a sign that begins with the line \"Arena\"!");
		setMessage("simplearena.sign.nothing", "&c%text% does not correspond to any known Arena or Class!");
		
		setMessage("simplearena.ingame.teleport", "&8[Arena] &cYou may not teleport while in an Arena!");
		setMessage("simplearena.ingame.command", "&8[Arena] &cYou may not use commands while in an Arena!");
		
		setMessage("simplearena.command.noperms", "&cYou do not have permission to use this command!");
		
		setMessage("simplearena.command.notplayer", "&cYou must be a Player to use this command!");
		setMessage("simplearena.command.unknown", "&cArena %arena% does not exist!");
		setMessage("simplearena.command.players", "&8[Arena]&c Not enough players to begin match!");
		setMessage("simplearena.command.notin", "&cYou must be in an Arena to use this command!");
		setMessage("simplearena.command.in", "&8[Arena]&c You are already in this Arena!");
		setMessage("simplearena.command.inprogress", "&8[Arena]&c Match already in progress!");
		setMessage("simplearena.command.notinprogress", "&8[Arena]&c A match is not in progress!");
		setMessage("simplearena.command.start", "&8[Arena]&9 Match force started by %player%!");
		setMessage("simplearena.command.stop", "&8[Arena]&9 Match force stopped by %player%!");
		setMessage("simplearena.command.stopqueue", "&8[Arena]&9 Queue force closed by %player%!");
		
		setMessage("simplearena.command.usage.arena", "&cUsage: /arena join || leave || start || stop");
		setMessage("simplearena.command.usage.join", "&cUsage: /arena join || join <arena>");
		setMessage("simplearena.command.usage.leave", "&cUsage: /arena leave");
		setMessage("simplearena.command.usage.start", "&cUsage: /arena start || start <arena>");
		setMessage("simplearena.command.usage.stop", "&cUsage: /arena stop || stop <arena>");
		
		setMessage("simplearena.class.join", "&8[Arena] &9You joined the %Clazz% Class!");
		setMessage("simplearena.class.long", "&8[Arena] &cYou cannot change Class more than %time% sec. after respawn!");
		setMessage("simplearena.class.before", "&8[Arena] &cYou cannot change Class before the Match has begun!");
		setMessage("simplearena.class.arena", "&cYou must be in an Arena to change Class!");
		
		
		setMessage("simplearena.join.normal", "&8[Arena] &9%player% joined queue!");
		setMessage("simplearena.join.full", "&eThis Arena is full. Please wait, a new match will begin shortly.");
		setMessage("simplearena.join.noperm", "&cYou do not have permission to join Arenas!");
		setMessage("simplearena.join.inprogress","&ePlease wait. A match is currently in progress!");
		
		setMessage("simplearena.leave.normal","&8[Arena] &9%player% left game!");
		setMessage("simplearena.leave.lose","&8[Arena] &9%player% is out of the game!");
		
		setMessage("simplearena.alert.lead", "&8[Arena] %team%&9 in the lead with &3%points%&9! %teamList%");
		setMessage("simplearena.alert.tie", "&8[Arena]&9 Tie at &3%points%&9! %teamList%");
		setMessage("simplearena.win", "%team%&9 wins in &3%arena%&9 Arena with &3%points%&9! MVP: &3%mvp%(%score%)");
		setMessage("simplearena.tie", "&9Tie in &3%arena%&9 Arena at &3%points%&9! MVP: &3%mvp%(%score%)");
		
		setMessage("simplearena.death.default", "&8[Arena] &9%killer% killed %killed%!");
		setMessage("simplearena.death.sword", "&8[Arena] &9%killer% cut down %killed% using a Sword!");
		setMessage("simplearena.death.axe", "&8[Arena] &9%killer% smashed %killed% with an Axe!");
		setMessage("simplearena.death.potion", "&8[Arena] &9%killer% killed %killed% with Magic!");
		setMessage("simplearena.death.bow", "&8[Arena] &9%killer% shot %killed% with a deadly Arrow!");
		setMessage("simplearena.death.sniper", "&8[Arena] &9%killer% snipered %killed%!");
		setMessage("simplearena.death.drown", "&8[Arena] &9%killed% drowned while fighting %killer%!");
		setMessage("simplearena.death.fall", "&8[Arena] &9%killed% fell to his death while fighting %killer%!");
		setMessage("simplearena.death.starve", "&8[Arena] &9%killed% starved while fighting %killer%!");
		setMessage("simplearena.death.lava", "&8[Arena] &9%killed% burned in lava while fighting %killer%!");
		setMessage("simplearena.death.fire", "&8[Arena] &9%killed% burned to a crisp while fighting %killer%!");
		
		setMessage("simplearena.death.default2", "&8[Arena] &9%killed% died!");
		setMessage("simplearena.death.drown2", "&8[Arena] &9%killed% drowned!");
		setMessage("simplearena.death.fall2", "&8[Arena] &9%killed% fell to his death!");
		setMessage("simplearena.death.starve2", "&8[Arena] &9%killed% starved!");
		setMessage("simplearena.death.lava2", "&8[Arena] &9%killed% burned in lava!");
		setMessage("simplearena.death.fire2", "&8[Arena] &9%killed% burned to a crisp!");
		
		
		
		try{
			FileConfiguration config = YamlConfiguration.loadConfiguration(Messages);
			for(String message: config.getConfigurationSection("").getKeys(true)){
				messageData.put(message, formatString(config.getString(message)));
			}
		}catch (Exception e){
			Bukkit.getServer().getLogger().log(Level.WARNING, "§cError loading messages.yml!");
		}
		
	}
	
	private static void setMessage(String name, String message){
		File f = new File(plugin.getDataFolder() + File.separator + "messages.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);
		if(!config.isSet(name)){
			config.set(name, message);
			try{
				config.save(f);
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public synchronized static String formatString(String string){
		return string.replaceAll("&", "§");
	}
}
