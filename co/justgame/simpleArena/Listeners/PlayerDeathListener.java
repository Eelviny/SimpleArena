package co.justgame.simpleArena.Listeners;

import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import co.justgame.simpleArena.ArenaClasses.Arena;
import co.justgame.simpleArena.ArenaClasses.ArenaUtils;
import co.justgame.simpleArena.Listeners.DeathMessages.DeathMessages;
import co.justgame.simpleArena.Main.SimpleArena;
import co.justgame.simpleArena.Resources.Messages;

public class PlayerDeathListener implements Listener {

    private static HashMap<Player, Player> lastDamager = new HashMap<Player, Player>();
    private static HashMap<Player, Location> tntDetonators = new HashMap<Player, Location>();
    private static HashMap<Player, Integer> timeSinceDamage = new HashMap<Player, Integer>();

    public PlayerDeathListener(int time){
        startTimer(time);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public synchronized void playerDamage(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Player && !e.getCause().equals(DamageCause.BLOCK_EXPLOSION)){
            Player damagee = (Player) e.getEntity();
            if(e.getDamager() instanceof Player || e.getDamager() instanceof Projectile){
                Player damager = null;
                if(e.getDamager() instanceof Player)
                    damager = (Player) e.getDamager();
                else if(((Projectile) e.getDamager()).getShooter() instanceof Player)
                    damager = (Player) ((Projectile) e.getDamager()).getShooter();
                else{ 
                    checkForDeathAndSubtractPoints(damagee, e.getDamager(), e);
                    return;
                }
                if(SimpleArena.inArena(damagee) && SimpleArena.inArena(damager)){
                    lastDamager.put(damagee, damager);
                    timeSinceDamage.put(damagee, 0);
        
                   checkForDeathAndSubtractPoints(damagee, damager, e);
                }
            }else{
                checkForDeathAndSubtractPoints(damagee, e.getDamager(), e);
            }
        }
    }
    
    private void checkForDeathAndSubtractPoints(Player damagee, Entity damager, EntityDamageByEntityEvent e){
        if(damagee.getHealth() - e.getDamage() <= 0){
            e.setCancelled(true);
            Arena a = SimpleArena.getArena(damagee);
            a.addPoints(damagee, 1, false);
            if(lastDamager.containsKey(damagee) && damager instanceof Player){
                a.addPoints(lastDamager.get(damagee), 1, true);
                lastDamager.remove(damagee);
                timeSinceDamage.remove(damagee);
            }
            
            if(damager instanceof Player){
                  a.sendMessage(DeathMessages.getDeathMessage(damagee, (Player) damager, e));
            }else{
                a.sendMessage(DeathMessages.getDeathMessage(damagee, damager, e));
            }
            
            if(a.respawn()){
                ArenaUtils.respawnPlayer(a, damagee);
            }else{
                a.sendMessage(Messages.get("simplearena.leave.lose").replace("%player%", damagee.getName()));
                a.removePlayer(damagee, true);
            }
        } 
    }

    @EventHandler(priority = EventPriority.HIGH)
    public synchronized void playerDamage(EntityDamageEvent e){
        if(!e.getCause().equals(DamageCause.ENTITY_ATTACK) && !e.getCause().equals(DamageCause.MAGIC)
                && !e.getCause().equals(DamageCause.PROJECTILE)){
            if(e.getEntity() instanceof Player){
                Player p = (Player) e.getEntity();
                if(SimpleArena.inArena(p)){
                    
                    if(e.getCause().equals(DamageCause.BLOCK_EXPLOSION)){
                        Player closest = getClosestDetonator(p.getLocation());
                        lastDamager.put(p, closest);
                    }
                    
                    if(p.getHealth() - e.getDamage() <= 0){
                        e.setCancelled(true);
                        Arena a = SimpleArena.getArena(p);
                        a.addPoints(p, 1, false);

                        if(lastDamager.containsKey(p)){
                            a.sendMessage(DeathMessages.getDeathMessage(p, lastDamager.get(p), e)); 
                            a.addPoints(lastDamager.get(p), 1, true);
                            lastDamager.remove(p);
                            timeSinceDamage.remove(p);
                        }else{
                            a.sendMessage(DeathMessages.getDeathMessage(p, e));
                        }

                        if(a.respawn()){
                            ArenaUtils.respawnPlayer(a, p);
                        }else{
                            a.sendMessage(Messages.get("simplearena.leave.lose").replace("%player%", p.getName()));
                            a.removePlayer(p, true);
                        }
                    }
                }
            }
        }
    }
    
    private synchronized Player getClosestDetonator(Location loc){
        Player closest = (Player) tntDetonators.keySet().toArray()[0];
        for(Player p: tntDetonators.keySet()){
            if(tntDetonators.get(p).distance(loc) <= tntDetonators.get(closest).distance(loc)){
                closest = p;
            }
        }
        return closest;
    }
    
    public static synchronized void addTNTDetonator(Player p, Location loc){
        tntDetonators.put(p, loc);
    }
    
    public static synchronized void resetTNTDetonators(){
        tntDetonators = new HashMap<Player, Location>();
    }

    public synchronized void startTimer(final int time){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(SimpleArena.getInstance(), new Runnable(){

            public void run(){
                synchronized (timeSinceDamage){
                    Iterator<Player> iterator = timeSinceDamage.keySet().iterator();
                    while(iterator.hasNext()){
                        Player player = iterator.next();
                        timeSinceDamage.put(player, timeSinceDamage.get(player) + 1);
                        if(timeSinceDamage.get(player) > time){
                            lastDamager.remove(player);
                            iterator.remove();
                        }
                    }
                }
            }
        }, 1, 20L);
    }

}
