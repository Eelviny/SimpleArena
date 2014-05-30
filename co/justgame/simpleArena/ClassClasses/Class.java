package co.justgame.simpleArena.ClassClasses;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import co.justgame.simpleArena.Main.SimpleArena;
import co.justgame.simpleArena.Teams.Color.Color;

import com.google.common.collect.Iterables;

public class Class {

    private String name;
    private ArrayList<ItemStack> armor;
    private ItemStack[] items;
    private ArrayList<PotionEffect> effects;

    public Class(String name, ArrayList<ItemStack> armor, ArrayList<ItemStack> items, ArrayList<PotionEffect> effects){
        this.name = name;
        this.armor = armor;
        Collections.reverse(this.armor);
        this.items = Iterables.toArray(items, ItemStack.class);
        this.effects = effects;
    }

    public String getName(){
        return name;
    }

    @SuppressWarnings("deprecation")
    public void setPlayerClass(Player p){
        PlayerInventory inven = p.getInventory();
        inven.clear();
        inven.setArmorContents(new ItemStack[4]);
        inven.setArmorContents(armor.toArray(new ItemStack[armor.size() + 1]));
        inven.setContents(items);
        if(SimpleArena.getArena(p).giveWoolHelmets())
            p.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, (short) Color.getWoolColor(SimpleArena.getArena(p).getTeam(p)
                .getColor())));
        p.updateInventory();
        p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 2);

        for(PotionEffect effect: p.getActivePotionEffects()){
            p.removePotionEffect(effect.getType());
        }
        for(PotionEffect pe: effects){
            p.addPotionEffect(pe);
        }

    }
}
