package simpleArena;

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
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;


public class ClassFiles {
private static Plugin plugin = SimpleArena.getInstance();

	public static void createSample(){
		File classFolder = new File(plugin.getDataFolder()+File.separator+"Class"+File.separator);
		try{
			Files.createDirectories(classFolder.toPath());
			File classFile = new File(classFolder, "default.yml");
			if(!classFile.exists()){
				classFile.createNewFile();
			
				FileConfiguration config = YamlConfiguration.loadConfiguration(classFile);
			
				config.set("name", "default");
				
				config.set("chestplate.type", "IRON");
				config.set("chestplate.enchantment.type", "PROTECTION_PROJECTILE");
				config.set("chestplate.enchantment.level", 2);
				config.set("leggings.type", "LEATHER");
				config.set("boots.type", "GOLD");
				config.set("items", "IRON_SWORD, 1 (FIRE_ASPECT | 1); Iron_Axe, 2;cooked_beef, 24");
				config.set("potions.potion1.potiontype", "INSTANT_HEAL");
				config.set("potions.potion1.number", 2);
				config.set("potions.potion1.level", 2);
				config.set("potions.potion1.splash", true);
				config.set("potions.potion1.extended", false);
			
				config.set("effects.effect1.type", "HEAL");
				config.set("effects.effect1.strength", 1);
				config.set("effects.effect1.length", 5);
			
				config.save(classFile);
			}
		
		}catch (IOException e){
			Bukkit.getLogger().log(Level.SEVERE, ChatColor.RED + "Failed to load Class! Reason: Error creating Class folder!");
		}
	}
	
	@SuppressWarnings("deprecation")
	public static ArrayList<Class> loadClasses(){
		File classFolder = new File(plugin.getDataFolder()+File.separator+"Class"+File.separator);
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
				 	 
				 	 ArrayList<ItemStack> armor = new ArrayList<ItemStack>() ;
				 	 ItemStack Chestplate = getItem(config.getString("chestplate.type"), "chestplate");
				 	 if(config.getString("chestplate.enchantment.type") != null){
				 		 Chestplate.addEnchantment(Enchantment.getByName(config.getString("chestplate.enchantment.type").toUpperCase()), 
				 				config.getInt("chestplate.enchantment.level", 1));
				 	 }
				 	 if(Chestplate != null) armor.add(Chestplate);
				 	 
				 	 ItemStack Leggings = getItem(config.getString("leggings.type"), "leggings");
				 	if(config.getString("leggings.enchantment.type") != null){
				 		 Chestplate.addEnchantment(Enchantment.getByName(config.getString("leggings.enchantment.type").toUpperCase()), 
				 				config.getInt("leggings.enchantment.level", 1));
				 	 }
				 	 if(Leggings != null) armor.add(Leggings);
				 	 
				 	 ItemStack Boots = getItem(config.getString("boots.type"), "boots");
				 	if(config.getString("boots.enchantment.type") != null){
				 		 Chestplate.addEnchantment(Enchantment.getByName(config.getString("boots.enchantment.type").toUpperCase()), 
				 				config.getInt("boots.enchantment.level", 1));
				 	 }
				 	 if(Boots != null) armor.add(Boots);
				 	 
				 	ArrayList<ItemStack> items = new ArrayList<ItemStack>();
				 	
				 	List<String> itemGroups;
				 	itemGroups = Arrays.asList(config.getString("items").split(";"));
				 	
				 	for(String string: itemGroups){
				 		String[] components = string.split(",");
				 		if(components[1].contains("(")){
				 			String[] enchantment = components[1].replace(")", "").split("\\(");
				 			String[] enchantmentComponents = enchantment[1].split("\\|");
				 			ItemStack item = new ItemStack(Material.matchMaterial(components[0].trim()), Integer.parseInt(enchantment[0].trim()));
				 			item.addEnchantment(Enchantment.getByName(enchantmentComponents[0].trim()), Integer.parseInt(enchantmentComponents[1].trim()));
				 			items.add(item);
				 		}else{
				 			items.add(new ItemStack(Material.matchMaterial(components[0].trim()), Integer.parseInt(components[1].trim())));
				 		}
				 		
				 		
				 	}
				 	
				 	ArrayList<ItemStack> potions = new ArrayList<ItemStack>();
				 	
				 	PotionType potionType = PotionType.INSTANT_HEAL;
				 	int number = 1;
				 	int level = 1;
				 	boolean extended = false;
				 	boolean splash = false;
				 	
				 	for(String message: config.getConfigurationSection("").getKeys(true)){
				 		if(message.endsWith(".potiontype")){
				 			potionType = PotionType.valueOf((config.getString(message).toUpperCase()));
				 		}else if (message.endsWith(".number")){
				 			number = config.getInt(message);
				 		}else if (message.endsWith(".level")){
				 			level = config.getInt(message);
				 		}else if (message.endsWith(".extended")){
				 			extended = config.getBoolean(message);
				 		}else if (message.endsWith(".splash")){
				 			splash = config.getBoolean(message);
				 			potions.add(new Potion(potionType, level, splash, extended).toItemStack(number));
				 			potionType = PotionType.INSTANT_HEAL; level = 0; extended = false; splash = false;
				 		}
				 	}
				 	
				 	ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>();
				 	
				 	PotionEffectType type = PotionEffectType.HEAL;
				 	int length = 0;
				 	int strength = 0;
				 	for(String message: config.getConfigurationSection("").getKeys(true)){
				 		if(message.endsWith(".type")){
				 			type = PotionEffectType.getByName(config.getString(message).toUpperCase());
				 		}else if (message.endsWith(".strength")){
				 			strength = config.getInt(message);
				 		}else if (message.endsWith(".length")){
				 			length = config.getInt(message);
				 			effects.add(new PotionEffect(type, length, strength));
				 			type = PotionEffectType.HEAL; length = 0; strength = 0;
				 		}
				 	}
					
					clazz = new Class(config.getString("name"), armor, items, potions, effects);
					classes.add(clazz);
				}
			}catch (IOException e){
				Bukkit.getLogger().log(Level.SEVERE, ChatColor.RED + "Failed to load Class! Reason: "+ e.getMessage());
			}catch(Exception e){
				Bukkit.getLogger().log(Level.SEVERE, ChatColor.RED + "Failed to load Class! Reason: unparsable value in config!");
				e.printStackTrace();
			}
		}
		return classes;
	}
	
	private static ItemStack getItem(String string, String type)throws Exception{
		 String armor = string.toLowerCase().replace(type, "");
	 	 if(!armor.equalsIgnoreCase("Null") || !armor.equalsIgnoreCase("None")){
	 		 return new ItemStack(Material.matchMaterial(armor+"_"+type));
	 	 }
		return null;
	}
}
