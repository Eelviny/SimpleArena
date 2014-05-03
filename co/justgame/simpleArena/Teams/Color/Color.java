package co.justgame.simpleArena.Teams.Color;

import org.bukkit.ChatColor;


public class Color {
	
	public enum color{
		RED, LIGHT_BLUE, LIME_GREEN, YELLOW, BLACK, CYAN, ORANGE, LIGHT_GRAY, DARK_BLUE, DARK_GREEN,    
		 LIGHT_PURPLE, PURPLE, WHITE, DARK_GRAY
	}
	private int i = -1;

	public Color(){}
	
	public color next(){
		i++;
		return color.values()[i];
	}
	public static synchronized int getWoolColor(color Color){
		switch(Color){
		case BLACK:
			return 15;
		case CYAN:
			return 9;
		case DARK_BLUE:
			return 11;
		case DARK_GRAY:
			return 7;
		case DARK_GREEN:
			return 13;
		case LIGHT_BLUE:
			return 3;
		case LIGHT_GRAY:
			return 8;
		case LIME_GREEN:
			return 5;
		case ORANGE:
			return 1;
		case LIGHT_PURPLE:
			return 2;
		case PURPLE:
			return 10;
		case RED:
			return 14;
		case WHITE:
			return 0;
		case YELLOW:
			return 4;
		default:
			return 0;
		}
	}
	public static synchronized ChatColor getChatColor(color Color){
		switch(Color){
		case BLACK:
			return ChatColor.BLACK;
		case ORANGE:
			return ChatColor.GOLD;
		case CYAN:
			return ChatColor.DARK_AQUA;
		case DARK_BLUE:
			return ChatColor.DARK_BLUE;
		case DARK_GRAY:
			return ChatColor.DARK_GRAY;
		case DARK_GREEN:
			return ChatColor.DARK_GREEN;
		case LIGHT_BLUE:
			return ChatColor.AQUA;
		case LIGHT_GRAY:
			return ChatColor.GRAY;
		case LIME_GREEN:
			return ChatColor.GREEN;
		case LIGHT_PURPLE:
			return ChatColor.LIGHT_PURPLE;
		case PURPLE:
			return ChatColor.DARK_PURPLE;
		case RED:
			return ChatColor.RED;
		case WHITE:
			return ChatColor.WHITE;
		case YELLOW:
			return ChatColor.YELLOW;
		default:
			return ChatColor.WHITE;
		
		}
	}
}
