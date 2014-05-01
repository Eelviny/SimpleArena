package simpleArena;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import simpleArena.Color.color;


public class Team {
	
	private HashMap<Player, Integer> players = new HashMap<Player, Integer>();
	private int points;
	private color Color;
	private int Max;
	private Location spawnPoint;
	private Class defaultClass;

	public Team(color Color, int Max, Location spawnPoint, String defaultClass){
		this.Color = Color;
		this.Max = Max;
		this.spawnPoint = spawnPoint;
		this.defaultClass = SimpleArena.getClass(defaultClass);
	}
	public int size(){
		return players.size();
	}
	public int max(){
		return this.Max;
	}
	public color getColor(){
		return this.Color;
	}
	public String getName(){
		return simpleArena.Color.getChatColor(this.getColor())+WordUtils.capitalize(this.getColor()
				.name().toLowerCase().replace("_", " ")+" Team");
	}
	public Location getSpawn(){
		return this.spawnPoint;
	}
	public Class getDefualtClass(){
		return this.defaultClass;
	}
	public int getScore(){
		return this.points;
	}
	public void resetScore(){
		this.points = 0;
	}
	public String getMVP(){
		if(players.keySet().size() >= 1){
			int i = getMVPScore();
			StringBuilder names = new StringBuilder();
			for(Player p: players.keySet()){
				if(players.get(p) == i){
					names.append(p.getName()+" ");
				}
			}
			return names.toString();
		}
		return "";
	}
	public Integer getMVPScore(){
		int highest = 0;
		for(int i: players.values()){
			if(i > highest){
				highest = i;
			}
		}
		return highest;
	}
	public synchronized Set<Player> getPlayers(){
		return players.keySet();
	}
	public void addPlayer(Player p){
		players.put(p, 0);
	}
	public boolean isMaxed(){
		return size() >= Max;
	}
	public boolean contains(Player p){
		return players.containsKey(p);
	}
	public synchronized void removePlayer(Player p){
		players.remove(p);
	}
	public void clearTeam(){
		players = new HashMap<Player, Integer>();
	}
	public void incrementScore(Player p, int i){
		if(players.containsKey(p)){
			players.put(p, players.get(p)+i);
			this.points+=i;
		}
	}
	public void decrementScore(Player p, int i){
		if(players.containsKey(p)){
			players.put(p, players.get(p)-i);
			this.points-=i;
		}
	}
}
