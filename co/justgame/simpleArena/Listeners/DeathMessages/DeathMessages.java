package co.justgame.simpleArena.Listeners.DeathMessages;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import co.justgame.simpleArena.Resources.Messages;

public class DeathMessages {

    public static synchronized String getDeathMessage(Player killed, Player killer, EntityDamageByEntityEvent e){
        DamageCause dc = e.getCause();
        if(dc.equals(DamageCause.ENTITY_ATTACK)){
            Material item = killer.getItemInHand().getType();
            if(item.equals(Material.IRON_SWORD) || item.equals(Material.STONE_SWORD) || item.equals(Material.WOOD_SWORD)
                    || item.equals(Material.GOLD_SWORD) || item.equals(Material.DIAMOND_SWORD)){
                return Messages.get("simplearena.death.sword").replace("%killer%", killer.getName())
                        .replace("%killed%", killed.getName());
            }else if(item.equals(Material.IRON_AXE) || item.equals(Material.STONE_AXE) || item.equals(Material.WOOD_AXE)
                    || item.equals(Material.GOLD_AXE) || item.equals(Material.DIAMOND_AXE)){
                return Messages.get("simplearena.death.axe").replace("%killer%", killer.getName())
                        .replace("%killed%", killed.getName());
            }else{
                return Messages.get("simplearena.death.default").replace("%killer%", killer.getName())
                        .replace("%killed%", killed.getName());
            }
        }else if(dc.equals(DamageCause.PROJECTILE)){
            if(killer.getLocation().distance(killed.getLocation()) > 30){
                return Messages.get("simplearena.death.sniper").replace("%killer%", killer.getName())
                        .replace("%killed%", killed.getName());
            }else{
                return Messages.get("simplearena.death.bow").replace("%killer%", killer.getName())
                        .replace("%killed%", killed.getName());
            }
        }else if(dc.equals(DamageCause.MAGIC)){
            return Messages.get("simplearena.death.potion").replace("%killer%", killer.getName())
                    .replace("%killed%", killed.getName());
        }
        return Messages.get("simplearena.death.default").replace("%killer%", killer.getName())
                .replace("%killed%", killed.getName());
    }

    public static synchronized String getDeathMessage(Player killed, Player killer, EntityDamageEvent e){
        DamageCause dc = e.getCause();
        if(dc.equals(DamageCause.DROWNING)){
            return Messages.get("simplearena.death.drown").replace("%killer%", killer.getName())
                    .replace("%killed%", killed.getName());
        }else if(dc.equals(DamageCause.FALL)){
            return Messages.get("simplearena.death.fall").replace("%killer%", killer.getName())
                    .replace("%killed%", killed.getName());
        }else if(dc.equals(DamageCause.FIRE)){
            return Messages.get("simplearena.death.fire").replace("%killer%", killer.getName())
                    .replace("%killed%", killed.getName());
        }else if(dc.equals(DamageCause.STARVATION)){
            return Messages.get("simplearena.death.starve").replace("%killer%", killer.getName())
                    .replace("%killed%", killed.getName());
        }else if(dc.equals(DamageCause.LAVA)){
            return Messages.get("simplearena.death.lava").replace("%killer%", killer.getName())
                    .replace("%killed%", killed.getName());
        }else{
            return Messages.get("simplearena.death.default").replace("%killer%", killer.getName())
                    .replace("%killed%", killed.getName());
        }
    }

    public static synchronized String getDeathMessage(Player killed, EntityDamageEvent e){
        DamageCause dc = e.getCause();
        if(dc.equals(DamageCause.DROWNING)){
            return Messages.get("simplearena.death.drown2").replace("%killed%", killed.getName());
        }else if(dc.equals(DamageCause.FALL)){
            return Messages.get("simplearena.death.fall2").replace("%killed%", killed.getName());
        }else if(dc.equals(DamageCause.FIRE)){
            return Messages.get("simplearena.death.fire2").replace("%killed%", killed.getName());
        }else if(dc.equals(DamageCause.STARVATION)){
            return Messages.get("simplearena.death.starve2").replace("%killed%", killed.getName());
        }else if(dc.equals(DamageCause.LAVA)){
            return Messages.get("simplearena.death.lava2").replace("%killed%", killed.getName());
        }else{
            return Messages.get("simplearena.death.default2").replace("%killed%", killed.getName());
        }
    }

}
