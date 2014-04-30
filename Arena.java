package simpleArena;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.spiny.pvpchoice.main.PVPChoiceAPI;

import simpleArena.Color.color;

public class Arena {
	
	private boolean inProgress;
	private LinkedHashMap<color, Team> teams = new LinkedHashMap<color, Team>();
	private LinkedHashMap<Player, Class> classes = new LinkedHashMap<Player, Class>();
	private LinkedHashMap<Player, Integer> timeSinceRespawn = new LinkedHashMap<Player, Integer>();
	
	private String name;
	private String startMessage = "";
	private int numberOfTeams = 2;
	private int players = 8;
	private int kitTime = 15;
	private int pointTime = 30;
	private int delayTime = 30;
	private boolean respawn = true;
	private int limit = 300;
	private int pointsLimit = 20;
	Listener listener = null;
	
	private SideBarDisplay sideBar;
	
	int queueLoopID = 0;
	int queueCounter = delayTime;
	
	int gameLoopID = 0;
	int gameCounter = limit;
	int alertCounter = 30;
	
	
	public Arena(String name, String startMessage, int numberOfTeams, int players, int kitTime, int delayTime, int pointTime, boolean respawn, 
										int limit, int pointsLimit, ArrayList<Location> spawnPoints, ArrayList<String> defaultClasses){
		this.name = name;
		this.startMessage = startMessage;
		this.numberOfTeams = numberOfTeams;
		this.players = players;
		this.kitTime = kitTime;
		this.delayTime = delayTime;
		this.pointTime = pointTime;
		queueCounter = delayTime;
		this.respawn = respawn;
		this.limit = limit;
		gameCounter = limit;
		this.pointsLimit = pointsLimit;
		this.sideBar = new SideBarDisplay(queueCounter, gameCounter);
		
		Color colors = new Color();
		for(int i = 0; i < this.numberOfTeams; i++){
			color Color = colors.next();
			teams.put(Color, new Team(Color, this.players, spawnPoints.get(i), defaultClasses.get(i)));
		}
		sideBar.addSlots(teams.values());
	}
	public void sendMessage(String s){
		for(Team t: teams.values()){
			for(Player p: t.getPlayers()){
				p.sendMessage(s);
			}
		}
	}
	public boolean inProgress(){
		return inProgress;
	}
	public boolean respawn(){
		return respawn;
	}
	public int getkitTime(){
		return kitTime;
	}
	
	public void startGame(){
		if(!inProgress){
			
			inProgress = true;
			if(kitTime > -1)
				startTimer(kitTime);
			
			Bukkit.getScheduler().cancelTask(queueLoopID);
			
			sideBar.setDisplayGameMode(teams.values());
			listener = new PlayerDeathListener(this.pointTime);
			Bukkit.getServer().getPluginManager().registerEvents(listener, SimpleArena.getInstance());
			
			Iterator<Team> iterator = teams.values().iterator();
	    	while(iterator.hasNext()){
	    		Team t = iterator.next();
	    		if(t.getPlayers().size() <= 0){
	    			iterator.remove();
	    		}
	    	}
			
			for(Team team: teams.values()){
				for(Player p: team.getPlayers()){
					PlayerFiles.savePlayerInven(p);
					ArenaUtils.resetPlayer(this, p, team);
					
					if(p.isInsideVehicle())
						p.leaveVehicle();
					
					p.teleport(team.getSpawn().clone().add(.5, 0, .5));
					this.setRespawn(p);
				}
			}
			
			this.sendMessage(startMessage);
			
			gameLoopID = Bukkit.getScheduler().scheduleSyncRepeatingTask(SimpleArena.getInstance(), new Runnable() {
			    public void run() {
					if(gameCounter == 0){
						endGame(true);
					}else if(pointsLimit > -1 && getTotalScore() >= pointsLimit){
						endGame(true);
					}else{
						if(alertCounter == 0){
							if(!isTie()){
								Team t = getHighestScoringTeam();
								sendMessage(Messages.get("simplearena.alert.lead")
										.replace("%team%", Color.getChatColor(t.getColor())+WordUtils.capitalize(t.getColor().name().toLowerCase().replace("_", " ")+" Team"))
										.replace("%points%", getStringPoints(getTotalScore()))
										.replace("%teamList%", getTeamScore()));
							}else{
								sendMessage(Messages.get("simplearena.alert.tie")
										.replace("%points%",  getStringPoints(getTotalScore()))
										.replace("%teamList%", getTeamScore()));
							}
							alertCounter = 30;
						}
						if(getSize() < 2){
							endGame(true);
						}
						gameCounter--;
						alertCounter--;
						sideBar.decrementTimeCounter(gameCounter);
					}
			    }
			}, 1, 20L);
		}
	}
	
	public void endGame(boolean b){
		Bukkit.getScheduler().cancelTask(gameLoopID);
		HandlerList.unregisterAll(listener);
		
		declareWinner();
		
		for(Team team: teams.values()){
			for(Player p: team.getPlayers()){
				this.removePlayer(p, b);
			}
			team.resetScore();
		}
		sideBar.resetDisplay();
		resetGameLoopTime();
		
		inProgress = false;
		timeSinceRespawn = new LinkedHashMap<Player, Integer>();
	}
	
	public void declareWinner(){
		
		String message = null;
		
		if(isTie()){
			message = Messages.get("simplearena.tie")
					.replace("%arena%", this.getName()).replace("%points%",  getStringPoints(getTotalScore()))
					.replace("%mvp%", getTopMVP()).replace("%score%", String.valueOf(getTopMVPScore()));
					
		}else{
			Team highestScore = (Team) teams.values().toArray()[0];
			for(Team team: teams.values()){
				if(team.getScore() > highestScore.getScore()){
					highestScore = team;
				}
			}
			message = Messages.get("simplearena.win")
					.replace("%team%", highestScore.getName())
					.replace("%arena%", this.getName()).replace("%points%",  getStringPoints(highestScore.getScore())).replace("%mvp%", addPunctuation(highestScore.getMVP()))
					.replace("%score%", String.valueOf(highestScore.getMVPScore()));
		}
		 
		for(Player p: Bukkit.getOnlinePlayers()){
			p.sendMessage("§8§m <> ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ <>");
			p.sendMessage(message);
			p.sendMessage("§8§m <> ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ <>");
		}
	}
	
	private boolean isTie(){
		Team highestScore = getHighestScoringTeam();
		for(Team team: teams.values()){
			if(!highestScore.getName().equals(team.getName()) && team.getScore() == highestScore.getScore()){
				return true;
			}
		}
		return false;
	}
	
	private Team getHighestScoringTeam(){
		Team highestScore = (Team) teams.values().toArray()[0];
		for(Team team: teams.values()){
			if(team.getScore() > highestScore.getScore()){
				highestScore = team;
			}
		}
		return highestScore;
	}
	
	private String getTeamScore(){
		StringBuilder teamScores = new StringBuilder();
		for(Team t: teams.values()){
			teamScores.append(t.getName()+": "+getStringPoints(t.getScore())+" ");
		}
		return teamScores.toString();
	}
	
	private String getTopMVP(){
		StringBuilder mvps = new StringBuilder();
		int i = getTopMVPScore();
		for(Team t: teams.values()){
			if(t.getMVPScore() == i){
				mvps.append(t.getMVP());
			}
		}
		return addPunctuation(mvps.toString());
	}
	
	private Integer getTopMVPScore(){
		Team MVT = (Team) teams.values().toArray()[0];
		for(Team t: teams.values()){
			if(t.getMVPScore() > MVT.getMVPScore()){
				MVT = t;
			}
		}
		return MVT.getMVPScore();
	}
	
	public void addPoints(Player p, int i, boolean b){
		for(Team team: teams.values()){
			if(team.contains(p)){
				if(b){
					team.incrementScore(p, i);
					sideBar.changeScore(p, team, i);
				}else{
					team.decrementScore(p, i);
					sideBar.changeScore(p, team, -i);
				}
			}
		}
	}
	public int getTotalScore(){
		Team highestScore = (Team) teams.values().toArray()[0];
		for(Team team: teams.values()){
			if(team.getScore() > highestScore.getScore()){
				highestScore = team;
			}
		}
		return highestScore.getScore();
	}
	public Team getTeam(Player p){
		for(Team team: teams.values()){
			if(team.contains(p))
				return team;
		}
		return null;
	}
	
	public void stopCountDown(){
		if(!inProgress){
			for(Team t: teams.values()){
				for(Player p: t.getPlayers()){
					this.removePlayer(p, true);
				}
			}
		}
	}
	
	public void startCountDown(){
		queueLoopID = Bukkit.getScheduler().scheduleSyncRepeatingTask(SimpleArena.getInstance(), new Runnable() {
		    public void run() {
				if(getSize() >= 2){
					if(queueCounter == 0){
						resetQueueLoopTime();
						Bukkit.getScheduler().cancelTask(queueLoopID);
						if(!inProgress){
							startGame();
						}
					}
					queueCounter--;
					sideBar.decrementCounter(queueCounter);
				}else{
					resetQueueLoopTime();
					sideBar.decrementCounter(queueCounter);
					Bukkit.getScheduler().cancelTask(queueLoopID);
					if(getSize() == 0){
						sideBar.resetDisplay();
					}
				}
		    }
		}, 1, 20L);
	}
	public synchronized void addPlayer(Player p){
		Team team = getSmallestTeam();
		team.addPlayer(p);
		sideBar.addPlayer(p, team);
		
		if(this.getSize() == 2){
			startCountDown();
		}
	}
	public synchronized void removePlayer(Player p, boolean threading){
		if(this.inProgress()){
			for(Team team: teams.values()){
				if(team.contains(p)){
					if(PlayerFiles.hasFile(p))
						if(threading)
							PlayerFiles.loadPlayerInven(p);
						else
							PlayerFiles.loadPlayerInvenWithoutThreading(p);
					if(SimpleArena.usePvPChoice)
						PVPChoiceAPI.setPVPEnabled(p, false);
					team.removePlayer(p);
					sideBar.removePlayer(p, team);
				}
			}
		}else{
			for(Team team: teams.values()){
				if(team.contains(p)){
					team.removePlayer(p);
					sideBar.removePlayer(p, team);
				}
			}
		}
	}
	public synchronized boolean contains(Player p){
		for(Team team: teams.values()){
			for(Player p2: team.getPlayers()){
				if(p2.equals(p)){
					return true;
				}
			}
		}
		return false;
	}
	
	public String getName(){
		return this.name;
	}
	public void resetQueueLoopTime(){
		this.queueCounter = delayTime;
	}
	public void resetGameLoopTime(){
		this.gameCounter = limit;
	}
	public boolean isMaxed(){
		if(players > -1){
			int i = 0;
			for(Team team: teams.values()){
				if(team.isMaxed())
					i++;
			}
			if(i == teams.size())
				return true;
			else
				return false;
		}else{
			return false;
		}
	}
	public Team getSmallestTeam(){
		Team smallest = (Team) teams.values().toArray()[0];
		for(Team team: teams.values()){
			if(team.size() < smallest.size()){
				smallest = team;
			}
		}
		return smallest;
	}
	
	public String getStringPoints(int i){
		if(i == 1 || i == -1){
			return i + " Point";
		}else{
			return i + " Points";
		}
	}
	@SuppressWarnings("unused")
	public int getSize(){
		int size = 0;
		for(Team team: teams.values()){
			for(Player player: team.getPlayers()){
				size++;
			}
		}
		return size;
	}
	
	public synchronized void setRespawn(Player p){
		timeSinceRespawn.put(p, 0);
	}
	
	public synchronized boolean canChangeClass(Player p){
		return timeSinceRespawn.containsKey(p);
	}
	
	public synchronized void setClass(Player p, Class c){
		classes.put(p, c);
	}
	public synchronized Class getClass(Player p){
		return classes.get(p);
	}
	public synchronized void startTimer(final int time){
		Bukkit.getScheduler().scheduleSyncRepeatingTask(SimpleArena.getInstance(), new Runnable() {
		    public void run() {
		    	synchronized(timeSinceRespawn){
			    	Iterator<Player> iterator = timeSinceRespawn.keySet().iterator();
			    	while(iterator.hasNext()){
			    		Player player = iterator.next();
			    		timeSinceRespawn.put(player, timeSinceRespawn.get(player)+1);
			    		if(timeSinceRespawn.get(player) > time){
			    			iterator.remove();
			    		}
			    	}
		    	}
		    }
		}, 1, 20L);
	}
	private String addPunctuation(String s){
		StringBuilder string = new StringBuilder(s);
		if(string.toString().trim().split(" ").length > 2){
			string = new StringBuilder(string.toString().trim().replace(" ", ", "));
			string.replace(string.lastIndexOf(", "), string.lastIndexOf(", ") + 1, ", and");
		}else if(string.toString().trim().split(" ").length > 1){
			string = new StringBuilder(string.toString().trim().replace(" ", " and "));
		}
		return string.toString();
	}
}
