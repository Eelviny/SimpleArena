package co.justgame.simpleArena.Players;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import co.justgame.simpleArena.Main.SimpleArena;

public class PlayerFiles {

    public synchronized static void savePlayerInven(final Player p){

        Location loc = p.getLocation();
        writeFile(SimpleArena.getUUID(p), p.getHealth(), p.getFoodLevel(), p.getLevel(), loc.getX(), loc.getY(), loc.getZ(), loc
                .getWorld().getName(), p.getActivePotionEffects(), p.getInventory().getContents(), p.getInventory()
                .getArmorContents());
    }

    private static void writeFile(final String UUID, final Double health, final int hunger, final int levels, final Double X,
            final Double Y, final Double Z, final String world, final Collection<PotionEffect> potionEffects,
            final ItemStack[] inven, final ItemStack[] armor){
        Bukkit.getScheduler().runTaskAsynchronously(SimpleArena.getInstance(), new Runnable(){

            public void run(){
                File playersFile = createPlayerFile(UUID, true);
                FileConfiguration config = YamlConfiguration.loadConfiguration(playersFile);

                config.set("health", health);
                config.set("hunger", hunger);
                config.set("levels", levels);
                config.set("location.x", X);
                config.set("location.y", Y);
                config.set("location.z", Z);
                config.set("location.world", world);
                int i = 1;
                for(PotionEffect pe: potionEffects){
                    config.set("potions.potion" + i + ".type", pe.getType().getName());
                    config.set("potions.potion" + i + ".level", pe.getAmplifier());
                    config.set("potions.potion" + i + ".duration", pe.getDuration());
                    i++;
                }
                config.set("inventory", inven);
                config.set("armor", armor);

                try{
                    config.save(playersFile);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    private synchronized static void setPlayer(final Player p, final Double health, final int hunger, final int levels,
            final Double X, final Double Y, final Double Z, final String world, final Collection<PotionEffect> potionEffects,
            final ItemStack[] inven, final ItemStack[] armor){

        Bukkit.getScheduler().scheduleSyncDelayedTask(SimpleArena.getInstance(), new Runnable(){

            public void run(){

                p.setHealth(health);
                p.setFoodLevel(hunger);
                p.setLevel(levels);
                Location loc = new Location(Bukkit.getWorld(world), X, Y, Z);
                p.teleport(loc);
                for(PotionEffect pe: potionEffects){
                    p.addPotionEffect(pe);
                }
                p.getInventory().setContents(inven);
                p.getInventory().setArmorContents(armor);

                p.updateInventory();
            }
        }, 1L);
    }

    @SuppressWarnings({ "unchecked", "unused" })
    public synchronized static void loadPlayerInvenWithoutThreading(final Player p){
        String UUID = SimpleArena.getUUID(p);
        File playersFile = createPlayerFile(UUID, true);
        FileConfiguration config = YamlConfiguration.loadConfiguration(playersFile);

        ArrayList<PotionEffect> potionEffects = new ArrayList<PotionEffect>();

        if(config.getConfigurationSection("potions") != null){
            int i = 1;
            for(String l: config.getConfigurationSection("potions").getKeys(false)){
                String s = config.getString("potions.potion" + i + ".type");
                int n = config.getInt("potions.potion" + i + ".level");
                int u = config.getInt("potions.potion" + i + ".duration");
                potionEffects.add(new PotionEffect(PotionEffectType.getByName(s), u, n));
                i++;
            }
        }

        ArrayList<ItemStack> contentArrayList = (ArrayList<ItemStack>) config.get("inventory");
        ItemStack[] inven = contentArrayList.toArray(new ItemStack[contentArrayList.size()]);

        ArrayList<ItemStack> armorArrayList = (ArrayList<ItemStack>) config.get("armor");
        ItemStack[] armor = armorArrayList.toArray(new ItemStack[armorArrayList.size()]);

        playersFile.delete();

        setPlayer(p, config.getDouble("health"), config.getInt("hunger"), config.getInt("levels"), config.getDouble("location.x"), config
                .getDouble("location.y"), config.getDouble("location.z"), config.getString("location.world"), potionEffects, inven, armor);
    }

    @SuppressWarnings({ "unchecked", "unused" })
    public synchronized static void loadPlayerInven(final Player p){
        final String UUID = SimpleArena.getUUID(p);
        Bukkit.getScheduler().runTaskAsynchronously(SimpleArena.getInstance(), new Runnable(){

            public void run(){

                File playersFile = createPlayerFile(UUID, true);
                try{
                    FileConfiguration config = YamlConfiguration.loadConfiguration(playersFile);

                    ArrayList<PotionEffect> potionEffects = new ArrayList<PotionEffect>();

                    if(config.getConfigurationSection("potions") != null){
                        int i = 1;
                        for(String l: config.getConfigurationSection("potions").getKeys(false)){
                            String s = config.getString("potions.potion" + i + ".type");
                            int n = config.getInt("potions.potion" + i + ".level");
                            int u = config.getInt("potions.potion" + i + ".duration");
                            potionEffects.add(new PotionEffect(PotionEffectType.getByName(s), u, n));
                            i++;
                        }
                    }

                    ArrayList<ItemStack> contentArrayList = (ArrayList<ItemStack>) config.get("inventory");
                    ItemStack[] inven = contentArrayList.toArray(new ItemStack[contentArrayList.size()]);

                    ArrayList<ItemStack> armorArrayList = (ArrayList<ItemStack>) config.get("armor");
                    ItemStack[] armor = armorArrayList.toArray(new ItemStack[armorArrayList.size()]);

                    playersFile.delete();

                    setPlayer(p, config.getDouble("health"), config.getInt("hunger"), config.getInt("levels"), config.getDouble("location.x"), config
                            .getDouble("location.y"), config.getDouble("location.z"), config.getString("location.world"), potionEffects, inven, armor);
                }catch (NullPointerException e){
                    Bukkit.getLogger().log(Level.WARNING, "Empty Player File Found in Player Folder!");
                }finally{
                    playersFile.delete();
                }
            }
        });
    }

    private static File createPlayerFile(String playerName, boolean bool){
        try{
            File dir = new File(SimpleArena.getInstance().getDataFolder() + File.separator + "Player" + File.separator);
            Files.createDirectories(dir.toPath());
            File playerFile = new File(dir, playerName + ".yml");

            if(!playerFile.exists() && bool){
                playerFile.createNewFile();
            }

            return playerFile;

        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized boolean hasFile(Player p){
        try{
            File dir = new File(SimpleArena.getInstance().getDataFolder() + File.separator + "Player" + File.separator);
            Files.createDirectories(dir.toPath());
            File playerFile = new File(dir, SimpleArena.getUUID(p) + ".yml");
            if(playerFile.exists())
                return true;
            else
                return false;
        }catch (Exception e){
            return false;
        }
    }

}
