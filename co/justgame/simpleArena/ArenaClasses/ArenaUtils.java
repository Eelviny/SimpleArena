package co.justgame.simpleArena.ArenaClasses;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import co.justgame.simpleArena.ClassClasses.Class;
import co.justgame.simpleArena.Main.SimpleArena;
import co.justgame.simpleArena.Players.PlayerFiles;
import co.justgame.simpleArena.Teams.Team;
import co.justgame.simpleArena.Teams.Color.Color;

import com.spiny.pvpchoice.main.PVPChoiceAPI;

public class ArenaUtils {

    public static void resetPlayer(final Player p, boolean reloadInven){
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setSaturation(20);
        p.setGameMode(GameMode.SURVIVAL);
        if(SimpleArena.usePvP()) PVPChoiceAPI.setPVPEnabled(p, false);
        if(PlayerFiles.hasFile(p) && reloadInven) PlayerFiles.loadPlayerInven(p);

        for(PotionEffect effect: p.getActivePotionEffects()){
            p.removePotionEffect(effect.getType());
        }
        p.getInventory().clear();
        p.getInventory().setArmorContents(new ItemStack[4]);

        Bukkit.getScheduler().scheduleSyncDelayedTask(SimpleArena.getInstance(), new Runnable(){

            public void run(){
                p.setFireTicks(0);
            }
        }, 2L);
    }

    @SuppressWarnings("deprecation")
    public static void resetPlayer(Arena a, final Player p, Team t){
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setSaturation(20);
        p.setGameMode(a.getGamemode());
        if(SimpleArena.usePvP()) PVPChoiceAPI.setPVPEnabled(p, true);

        if(SimpleArena.useVanish)
            try{
                if(org.kitteh.vanish.staticaccess.VanishNoPacket.getManager().isVanished(p))
                    org.kitteh.vanish.staticaccess.VanishNoPacket.getManager().toggleVanish(p);
            }catch (org.kitteh.vanish.staticaccess.VanishNotLoadedException e){
            }

        for(PotionEffect effect: p.getActivePotionEffects()){
            p.removePotionEffect(effect.getType());
        }
        p.getInventory().clear();
        p.getInventory().setArmorContents(new ItemStack[4]);
        if(a.giveWoolHelmets())
            p.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, (short) Color.getWoolColor(t.getColor())));
        
        Class clazz = t.getDefualtClass();
        if(clazz != null){
            clazz.setPlayerClass(p);
            a.setClass(p, clazz);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(SimpleArena.getInstance(), new Runnable(){

            public void run(){
                p.setFireTicks(0);
            }
        }, 2L);
    }

    public static void playDeathAnimation(Player p){
        Location loc = p.getLocation();
        Entity e = loc.getWorld().spawnEntity(loc, EntityType.SKELETON);
        final Skeleton s = (Skeleton) e;
        s.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000, 100));
        s.setCustomName(p.getName());
        s.setFireTicks(p.getFireTicks());
        EntityEquipment ee = s.getEquipment();
        ee.setArmorContents(p.getInventory().getArmorContents());
        ee.setItemInHand(p.getItemInHand());

        loc.getWorld().playSound(loc, Sound.HURT_FLESH, 2F, 1F);
        loc.getWorld().playEffect(loc, Effect.SMOKE, 10, 20);
        Bukkit.getScheduler().scheduleSyncDelayedTask(SimpleArena.getInstance(), new Runnable(){

            public void run(){
                s.setHealth(0.0);
            }
        }, 3L);
    }

    public static void respawnPlayer(Arena a, final Player p){
        ArenaUtils.playDeathAnimation(p);
        p.teleport(a.getTeam(p).getSpawn().clone().add(.5, 0, .5));
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setSaturation(20);
        for(PotionEffect effect: p.getActivePotionEffects()){
            p.removePotionEffect(effect.getType());
        }
        a.getClass(p).setPlayerClass(p);
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 1));
        a.setRespawn(p);

        Bukkit.getScheduler().scheduleSyncDelayedTask(SimpleArena.getInstance(), new Runnable(){

            public void run(){
                p.setFireTicks(0);
            }
        }, 2L);
    }
}
