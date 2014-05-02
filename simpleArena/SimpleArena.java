package simpleArena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;


public class SimpleArena extends JavaPlugin{
	
	public static Plugin simpleArena;
	public static ArrayList<Arena> arenas;
	public static ArrayList<Class> classes;
	
	public static HashMap<Player, String> UUIDs = new HashMap<Player, String>();

	public static boolean usePvPChoice = false;
	public static boolean useVanish = false;
	
	@Override
	public void onEnable(){
		simpleArena = this;
		
		getLogger().info("Began Loading Classes!");
		ClassFiles.createSample();
		classes = ClassFiles.loadClasses();
		getLogger().info("Finished Loading Classes!");
		
		getLogger().info("Began Loading Arenas!");
		ArenaFiles.createSample();
		arenas = ArenaFiles.loadArenas();
		getLogger().info("Finished Loading Arenas!");
		getLogger().info("Comparing Arena and Class names...");
		
		for(Arena a: arenas){
			for(Class c: classes){
				if(a.getName().equalsIgnoreCase(c.getName())){
					Bukkit.getLogger().log(Level.SEVERE, "DuplicateNameException: Duplicate name for Arena and Class. Name: "+a.getName());
					Bukkit.getPluginManager().disablePlugin(this);
				}
			}
		}
		getLogger().info("Finished. All Clear!");
		
		for(Player p: Bukkit.getOnlinePlayers()){
			try{
				SimpleArena.setUUID(p, p.getUniqueId().toString());
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		
		if(Bukkit.getPluginManager().getPlugin("PVPChoice") != null && Bukkit.getPluginManager().getPlugin("PVPChoice").isEnabled())
			usePvPChoice = true;
		if(Bukkit.getPluginManager().getPlugin("VanishNoPacket") != null && Bukkit.getPluginManager().getPlugin("VanishNoPacket").isEnabled())
			useVanish = true;
		
		
		Messages.loadMessages();
		getServer().getPluginManager().registerEvents(new SignListeners(), this);
		getServer().getPluginManager().registerEvents(new ProtectionListeners(), this);
		
		try{
			SpecialChars.ModifyAllowedCharacters();
		}catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e){
			getLogger().info("Could not modify Chars!");
		}
		
		getLogger().info("SimpleArena has been enabled!");
		
	}
	@Override
	public void onDisable(){
		getLogger().info("SimpleArena has been disabled!");
		
		for(Arena a: arenas){
			if(a.inProgress()){
				a.endGame(false);
			}
		}
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		
		for(Player p: Bukkit.getOnlinePlayers()){
			p.setScoreboard(manager.getNewScoreboard());
		}
	}
	
	@Override
	public synchronized boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getLabel().equalsIgnoreCase("arena")){
			if(args.length > 0){
				String command = args[0];
				
				if(command.equalsIgnoreCase("join")){
					if(sender.hasPermission("simplearena.join")){
					
						Player p = null;
						if(sender instanceof Player){
							p = (Player) sender;
						}else{
							sender.sendMessage(Messages.get("simplearena.command.notplayer"));
							return true;
						}
						
						if(args.length >= 2){
							String a = StringUtils.join(args, " ", 1, args.length);
							Arena arena = SimpleArena.getArena(a);
							if(arena!= null){
								Arena pa = SimpleArena.getArena(p);
								
								if(pa != null && !pa.getName().equals(arena.getName())){
									pa.sendMessage(Messages.get("simplearena.leave.normal").replace("%player%", p.getName()));
									pa.removePlayer(p);
								}
								
								if(arena.isMaxed()){
									p.sendMessage(Messages.get("simplearena.join.full"));
								}else{
									if(arena.contains(p)){
										p.sendMessage(Messages.get("simplearena.command.in"));
									}else{
										if(!arena.inProgress()){
											arena.addPlayer(p);
											arena.sendMessage(Messages.get("simplearena.join.normal").replace("%player%", p.getName()));
										}else{
											p.sendMessage(Messages.get("simplearena.join.inprogress"));
										}
									}
								}
							}else{
								p.sendMessage(Messages.get("simplearena.command.unknown").replace("%arena%", StringUtils.join(args, " ", 1, args.length)));
							}
						}else{
							p.sendMessage(Messages.get("simplearena.command.usage.join"));
						}
					}else{
						sender.sendMessage(Messages.get("simplearena.command.noperms"));
					}
				}else if(command.equalsIgnoreCase("leave")){
					if(sender.hasPermission("simplearena.leave")){
						Player p = null;
						if(sender instanceof Player){
							p = (Player) sender;
						}else{
							sender.sendMessage(Messages.get("simplearena.command.notplayer"));
							return true;
						}
						
						if(args.length == 1){
							Arena a = SimpleArena.getArena(p);
							if(a != null){
								a.sendMessage(Messages.get("simplearena.leave.normal").replace("%player%", p.getName()));
								a.removePlayer(p);
							}else{
								p.sendMessage(Messages.get("simplearena.command.notin"));
							}
						}else{
							p.sendMessage(Messages.get("simplearena.command.usage.leave"));
						}
					}else{
						sender.sendMessage(Messages.get("simplearena.command.noperms"));
					}
				}else if(command.equalsIgnoreCase("start")){
					Arena a = null;
					
					if(args.length == 1){
						if(sender.hasPermission("simplearena.start")){
							Player p = null;
							if(sender instanceof Player){
								p = (Player) sender;
							}else{
								sender.sendMessage(Messages.get("simplearena.command.notplayer"));
								return true;
							}
							 a = SimpleArena.getArena(p);
						}else{
							sender.sendMessage(Messages.get("simplearena.command.noperms"));
							return true;
						}
					}else if(args.length >= 2){
						if(sender.hasPermission("simplearena.startarena")){
							a = SimpleArena.getArena(StringUtils.join(args, " ", 1, args.length));
						}else{
							sender.sendMessage(Messages.get("simplearena.command.noperms"));
							return true;
						}
					}else{
						sender.sendMessage(Messages.get("simplearena.command.usage.start"));
						return true;
					}
					
					if(a != null){
						if(a.inProgress() == false){
							if(a.getSize() >= 2){
								a.sendMessage(Messages.get("simplearena.command.start").replace("%player%", sender.getName()));
								
								if(sender instanceof Player && SimpleArena.getArena((Player) sender) != null 
										&& !SimpleArena.getArena((Player) sender).getName().equals(a.getName()))
									sender.sendMessage(Messages.get("simplearena.command.start").replace("%player%", sender.getName()));
								
								a.startGame();
							}else{
								sender.sendMessage(Messages.get("simplearena.command.players"));
							}
						}else{
							sender.sendMessage(Messages.get("simplearena.command.inprogress"));
						}
					}else{
						if(args.length == 1){
							sender.sendMessage(Messages.get("simplearena.command.notin"));
						}else{
							sender.sendMessage(Messages.get("simplearena.command.unknown").replace("%arena%", StringUtils.join(args, " ", 1, args.length)));
						}
					}
				}else if(command.equalsIgnoreCase("stop")){
					if(args.length == 1){
						if(sender.hasPermission("simplearena.stop")){
						
							Player p = null;
							if(sender instanceof Player){
								p = (Player) sender;
							}else{
								sender.sendMessage(Messages.get("simplearena.command.notplayer"));
								return true;
							}
							
							Arena a = SimpleArena.getArena(p);
							if(a != null){
								if(a.inProgress()){
									a.sendMessage(Messages.get("simplearena.command.stop").replace("%player%", p.getName()));
									a.endGame(true);
								}else{
									a.sendMessage(Messages.get("simplearena.command.stopqueue").replace("%player%", p.getName()));
									a.stopCountDown();
								}
							}else{
								p.sendMessage(Messages.get("simplearena.command.notin"));
							}
						}else{
							sender.sendMessage(Messages.get("simplearena.command.noperms"));
						}
					}else if(args.length >= 2){
						if(sender.hasPermission("simplearena.stoparena")){
							Arena a = SimpleArena.getArena(StringUtils.join(args, " ", 1, args.length));
							if(a != null){
								if(a.inProgress()){
									a.sendMessage(Messages.get("simplearena.command.stop").replace("%player%", sender.getName()));
									
									if(sender instanceof Player && SimpleArena.getArena((Player) sender) != null 
											&& !SimpleArena.getArena((Player) sender).getName().equals(a.getName()))
										sender.sendMessage(Messages.get("simplearena.command.stop").replace("%player%", sender.getName()));
									a.endGame(true);	
								}else{
									a.sendMessage(Messages.get("simplearena.command.stopqueue").replace("%player%", sender.getName()));
									
									if(sender instanceof Player && SimpleArena.getArena((Player) sender) != null 
											&& !SimpleArena.getArena((Player) sender).getName().equals(a.getName()))
										sender.sendMessage(Messages.get("simplearena.command.stopqueue").replace("%player%", sender.getName()));
									a.stopCountDown();
								}
							}else{
								sender.sendMessage(Messages.get("simplearena.command.unknown").replace("%arena%", StringUtils.join(args, " ", 1, args.length)));
							}
						}else{
							sender.sendMessage(Messages.get("simplearena.command.noperms"));
						}
					}else{
						sender.sendMessage(Messages.get("simplearena.command.usage.stop"));
					}
				}else{
					sender.sendMessage(Messages.get("simplearena.command.usage.arena"));
				}
			}else{
				sender.sendMessage(Messages.get("simplearena.command.usage.arena"));
			}
		}
		return true;
	}
	
	public static synchronized Plugin getInstance(){
		return simpleArena;
	}
	
	public static boolean usePvP(){
		return usePvPChoice;
	}
	
	public static boolean useVanish(){
		return useVanish;
	}
	
	public static void setUUID(Player p, String uuid){
		UUIDs.put(p, uuid);
	}
	
	public static String getUUID(Player p){
		return UUIDs.get(p);
	}
	
	public static boolean uuidContains(Player p){
		return UUIDs.containsKey(p);
	}
	
	public static synchronized Arena getArena(Player p){
		for(Arena arena: arenas){
			if(arena.contains(p)){
				return arena;
			}
		}
		return null;
	}
	public static synchronized boolean inArena(Player p){
		for(Arena arena: arenas){
			if(arena.contains(p)){
				return true;
			}
		}
		return false;
	}
	
	public static synchronized Arena getArena(String name){
		for(Arena arena: arenas){
			if(arena.getName().equalsIgnoreCase(name)){
				return arena;
			}
		}
		return null;
	}
	
	public static synchronized Class getClass(String name){
		for(Class clazz: classes){
			if(clazz.getName().equalsIgnoreCase(name)){
				return clazz;
			}
		}
		return null;
	}

}
