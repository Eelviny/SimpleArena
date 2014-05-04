package co.justgame.simpleArena.Listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import co.justgame.simpleArena.ArenaClasses.Arena;
import co.justgame.simpleArena.ClassClasses.Class;
import co.justgame.simpleArena.Main.SimpleArena;
import co.justgame.simpleArena.Resources.Messages;


public class SignListeners implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public synchronized void placeSign(SignChangeEvent e){
		if(!e.isCancelled()){
			String text = e.getLine(0);
			if(text.equalsIgnoreCase("Arena")){
				if(!e.getPlayer().hasPermission("simplearena.sign") && !e.getPlayer().isOp()){
					e.setCancelled(true);
					e.getPlayer().sendMessage(Messages.get("simplearena.sign.place"));
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public synchronized void clickSign(PlayerInteractEvent e){
		if(e.getClickedBlock() != null){
			Block b = e.getClickedBlock();
			Player p = e.getPlayer();
			if(b.getType().equals(Material.SIGN)|| b.getType().equals(Material.WALL_SIGN)){
				Sign s = (Sign) e.getClickedBlock().getState();
				if(removeColorCodes(s.getLine(0)).equalsIgnoreCase("Arena")){
					if(!p.isSneaking()){
						e.setCancelled(true);
						if(s.getLine(1)!= null){
							Arena arena = SimpleArena.getArena(removeColorCodes(s.getLine(1)));
							if(arena!= null){
								
								Arena pa = SimpleArena.getArena(p);
								if(pa != null && !pa.getName().equals(arena.getName())){
									pa.sendMessage(Messages.get("simplearena.leave.normal").replace("%player%", p.getName()));
									pa.removePlayer(p);
								}
								
								if(p.hasPermission("simplearena.join.sign") || e.getPlayer().isOp()){
									if(arena.contains(p)){
										if(arena.isMaxed()){
											e.getPlayer().sendMessage(Messages.get("simplearena.join.full"));
										}else{
											arena.sendMessage(Messages.get("simplearena.leave.normal").replace("%player%", p.getName()));
											arena.removePlayer(p);
										}
									}else{
										if(!arena.inProgress()){
											arena.addPlayer(p);
											arena.sendMessage(Messages.get("simplearena.join.normal").replace("%player%", p.getName()));
										}else{
											e.getPlayer().sendMessage(Messages.get("simplearena.join.inprogress"));
										}
									}
								}else{
									e.getPlayer().sendMessage(Messages.get("simplearena.join.noperm"));
								}
							}else{
								Class clazz = SimpleArena.getClass(s.getLine(1));
								if(clazz!= null){
									Arena playersArena = SimpleArena.getArena(p);
									if(playersArena!= null){
										if(playersArena.canChangeClass(p)){
											clazz.setPlayerClass(p);
											playersArena.setClass(p, clazz);
											p.sendMessage(Messages.get("simplearena.class.join").replace("%Clazz%", clazz.getName()));
										}else{
											if(playersArena.inProgress())
												p.sendMessage(Messages.get("simplearena.class.long").replace("%time%", String.valueOf(playersArena.getkitTime())));
											else
												p.sendMessage(Messages.get("simplearena.class.before"));
										}
									}else{
										p.sendMessage(Messages.get("simplearena.class.arena"));
									}
								}else{
									p.sendMessage(Messages.get("simplearena.sign.nothing").replace("%text%", s.getLine(1)));
								}
							}
						}
					}
				}
			}
		}
	}
	
	private String removeColorCodes(String s){
		StringBuilder sb = new StringBuilder(s);
		for(int i = 0; i < sb.length(); i++){
			char c = sb.charAt(i);
			if(c == '&' || c == '§'){
				sb.deleteCharAt(i);
				if(sb.length() > i)
					sb.deleteCharAt(i);
			}
		}
		return sb.toString();
	}
}
	
	
	
