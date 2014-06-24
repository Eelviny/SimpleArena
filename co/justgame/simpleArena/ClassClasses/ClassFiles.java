package co.justgame.simpleArena.ClassClasses;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import co.justgame.simpleArena.Main.SimpleArena;

public class ClassFiles {

    private static Plugin plugin = SimpleArena.getInstance();

    public static void createSample(){
        File classFolder = new File(plugin.getDataFolder() + File.separator + "Class" + File.separator);
        try{
            Files.createDirectories(classFolder.toPath());
            File classFile = new File(classFolder, "default.yml");
            if(!classFile.exists()){
                classFile.createNewFile();

                FileConfiguration config = YamlConfiguration.loadConfiguration(classFile);

                config.set("name", "default");
                config.set("items", Arrays
                        .asList("DIAMOND_CHESTPLATE: 1, 50 (DURABILITY: 1; PROTECTION_EXPLOSIONS: 2)", "DIAMOND_LEGGINGS: 1, 50", "DIAMOND_BOOTS: 1, 50", "DIAMOND_SWORD: 1, 50"));
                config.set("effects.effect1.type", "HEAL");
                config.set("effects.effect1.strength", 1);
                config.set("effects.effect1.length", 5);

                config.save(classFile);
            }

        }catch (IOException e){
            Bukkit.getLogger().log(Level.SEVERE, ChatColor.RED + "Failed to load Class! Reason: Error creating Class folder!");
        }
    }

    public static ArrayList<Class> loadClasses(){
        File classFolder = new File(plugin.getDataFolder() + File.separator + "Class" + File.separator);
        try{
            Files.createDirectories(classFolder.toPath());
        }catch (IOException e){
            Bukkit.getLogger().log(Level.SEVERE, ChatColor.RED + "Failed to load Class! Reason: Error creating Class folder!");
        }

        ArrayList<Class> classes = new ArrayList<Class>();
        for(File file: classFolder.listFiles()){
            try{
                if(file.getCanonicalPath().endsWith("yml")){
                    FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                    Class clazz = null;

                    ArrayList<ItemStack> items = new ArrayList<ItemStack>();
                    ArrayList<ItemStack> armor = new ArrayList<ItemStack>();

                    List<String> itemGroups = config.getStringList("items");
                    for(String string: itemGroups){
                        ItemStack BuiltItem;
                        String[] components = string.split("\\(");
                        String item = components[0];
                        String[] itemComponents = item.split(":");
                        Material itemtype = Material.getMaterial(itemComponents[0].trim().toUpperCase());
                        String numberAndDamage = itemComponents[1];
                        String[] numberAndDamageSplit = numberAndDamage.split(",");
                        int number = Integer.valueOf(numberAndDamageSplit[0].trim());
                        int damage = Integer.valueOf(numberAndDamageSplit[1].trim());
                        BuiltItem = new ItemStack(itemtype, number, (short) damage);

                        if(components.length == 2){
                            String enchantsList = components[1].replace(")", "");
                            String[] enchants = enchantsList.split(";");
                            for(String enchant: enchants){
                                String[] typeAndStrength = enchant.split(":");
                                BuiltItem
                                        .addUnsafeEnchantment(Enchantment.getByName(typeAndStrength[0].trim().toUpperCase()), Integer
                                                .valueOf(typeAndStrength[1].trim()));
                            }
                        }
                        items.add(BuiltItem);
                    }
                    int lookup = 0;
                    for(int i = 0; i < 3; i++){
                        if(items.size() >= i + 1 && isArmor(items.get(lookup))){
                            armor.add(items.get(lookup));
                            items.remove(lookup);
                        }else{
                            lookup++;
                        }
                    }

                    ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>();

                    PotionEffectType type = PotionEffectType.HEAL;
                    int length = 0;
                    int strength = 0;
                    for(String message: config.getConfigurationSection("").getKeys(true)){
                        if(message.endsWith(".type")){
                            type = PotionEffectType.getByName(config.getString(message).toUpperCase());
                        }else if(message.endsWith(".strength")){
                            strength = config.getInt(message);
                        }else if(message.endsWith(".length")){
                            length = config.getInt(message);
                            effects.add(new PotionEffect(type, length * 20, strength));
                            type = PotionEffectType.HEAL;
                            length = 0;
                            strength = 0;
                        }
                    }

                    clazz = new Class(config.getString("name"), armor, items, effects);
                    classes.add(clazz);
                }
            }catch (IOException e){
                Bukkit.getLogger().log(Level.SEVERE, ChatColor.RED + "Failed to load Class! Reason: " + e.getMessage());
            }catch (Exception e){
                Bukkit.getLogger().log(Level.SEVERE, ChatColor.RED + "Failed to load Class! Reason: unparsable value in config!");
                e.printStackTrace();
            }
        }
        return classes;
    }

    private static boolean isArmor(ItemStack item){
        Material m = item.getType();
        return m.equals(Material.LEATHER_CHESTPLATE) || m.equals(Material.GOLD_CHESTPLATE) || m.equals(Material.IRON_CHESTPLATE)
                || m.equals(Material.DIAMOND_CHESTPLATE) || m.equals(Material.LEATHER_LEGGINGS)
                || m.equals(Material.GOLD_LEGGINGS) || m.equals(Material.IRON_LEGGINGS) || m.equals(Material.DIAMOND_LEGGINGS)
                || m.equals(Material.LEATHER_BOOTS) || m.equals(Material.GOLD_BOOTS) || m.equals(Material.IRON_BOOTS)
                || m.equals(Material.DIAMOND_BOOTS) || m.equals(Material.CHAINMAIL_BOOTS)
                || m.equals(Material.CHAINMAIL_LEGGINGS) || m.equals(Material.CHAINMAIL_CHESTPLATE);
    }
}
