package simpleArena;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;


public class ProtectionListeners implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public synchronized void teleport(PlayerTeleportEvent e){
		if(e.getCause().equals(TeleportCause.PLUGIN)){
			if(!e.getCause().equals(TeleportCause.PLUGIN)){
				if(SimpleArena.inArena(e.getPlayer())){
					e.setCancelled(true);
					e.getPlayer().sendMessage(Messages.get("simplearena.ingame.teleport"));
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public synchronized void onDrop(PlayerDropItemEvent e){
		if(SimpleArena.inArena(e.getPlayer()) && SimpleArena.getArena(e.getPlayer()).inProgress()){
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public synchronized void onPickup(PlayerPickupItemEvent e){
		if(SimpleArena.inArena(e.getPlayer()) && SimpleArena.getArena(e.getPlayer()).inProgress()){
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public synchronized void onCommand(PlayerCommandPreprocessEvent e){
		Player p = e.getPlayer();
		if(SimpleArena.inArena(p) && SimpleArena.getArena(p).inProgress()){
			if(!e.getMessage().equalsIgnoreCase("/arena leave") && !e.getMessage().equalsIgnoreCase("/arena stop")){
				if(!p.hasPermission("simplearena.command") || p.isOp()){
					e.setCancelled(true);
					p.sendMessage(Messages.get("simplearena.ingame.command"));
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public synchronized void onHelmetClick(InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
		if(SimpleArena.inArena(p)){
			if(e.getSlotType().equals(SlotType.ARMOR)){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public synchronized void onPlayerLeave(PlayerQuitEvent e){
		Player p = e.getPlayer();
		Arena a =SimpleArena.getArena(p);
		if(a != null){
			a.removePlayer(p, true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public synchronized void onPlayerKick(PlayerKickEvent e){
		Player p = e.getPlayer();
		Arena a =SimpleArena.getArena(p);
		if(a != null){
			a.removePlayer(p, true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public synchronized void onPlayerLogin(PlayerJoinEvent e){
		final Player p = e.getPlayer();
		
		if(!SimpleArena.uuidContains(p)){
			try{
				SimpleArena.setUUID(p, p.getUniqueId().toString());
			}catch (Exception e1){
				e1.printStackTrace();
			}
		}
		
		if(PlayerFiles.hasFile(p)){
			Bukkit.getScheduler().scheduleSyncDelayedTask(SimpleArena.getInstance(), new Runnable(){
			    public void run() {
			    	PlayerFiles.loadPlayerInven(p);
			    }
			}, 2L);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public synchronized void onPlayerDeath(PlayerDeathEvent e){
		Player p = e.getEntity();
		if(SimpleArena.inArena(p)){
			Arena a =SimpleArena.getArena(p);
			a.removePlayer(p, true);
		}
	}
}
