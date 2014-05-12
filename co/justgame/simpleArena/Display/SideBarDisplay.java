package co.justgame.simpleArena.Display;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import co.justgame.simpleArena.Teams.Team;
import co.justgame.simpleArena.Teams.Color.Color;

public class SideBarDisplay {

    private enum state {
        Queue, Game
    }

    private state State = state.Queue;
    private int waitTime;
    private int gameTime;
    private Collection<Team> teams;

    private ScoreboardManager manager = Bukkit.getScoreboardManager();
    private Scoreboard queboard = manager.getNewScoreboard();
    private Scoreboard pointsboard = manager.getNewScoreboard();
    private Objective que = queboard.registerNewObjective("Players in Queue", "dummy");
    private Objective points = pointsboard.registerNewObjective("Points", "dummy");

    private ArrayList<Player> users = new ArrayList<Player>();

    public SideBarDisplay(int waitTime, int gameTime){
        que.setDisplaySlot(DisplaySlot.SIDEBAR);
        points.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score score = que.getScore(ChatColor.GREEN + "Game In:");
        this.waitTime = waitTime;
        score.setScore(waitTime);
        this.gameTime = gameTime;
        if(gameTime > 0){
            Score timeScore = points.getScore(ChatColor.GREEN + "Time Left:");
            timeScore.setScore(gameTime);
        }
    }

    public void decrementCounter(int i){
        if(State.equals(state.Queue)){
            Score score = que.getScore(ChatColor.GREEN + "Game In:");
            score.setScore(i);
        }
    }

    public void decrementTimeCounter(int i){
        if(State.equals(state.Game)){
            if(gameTime >= 0){
                Score score = points.getScore(ChatColor.GREEN + "Time Left:");
                score.setScore(i);
            }
        }
    }

    public void addSlots(Collection<Team> collection){
        this.teams = collection;
        for(Team team: collection){
            Score score = que.getScore(Color.getChatColor(team.getColor()) + "---");
            score.setScore(0);
        }
    };

    public void changeScore(Player p, Team t, int i){
        if(State.equals(state.Game)){
            Score score = points.getScore(Color.getChatColor(t.getColor()) + abbreviate(p.getName()));
            score.setScore(score.getScore() + i);
        }
    }

    public void addPlayer(Player p, Team t){
        if(State.equals(state.Queue)){
            Score score = que.getScore(Color.getChatColor(t.getColor()) + abbreviate(p.getName()));
            score.setScore(0);
            users.add(p);
            for(Player player: users){
                player.setScoreboard(manager.getNewScoreboard());
                player.setScoreboard(queboard);
            }
        }
    }

    public void removePlayer(Player p, Team t){
        if(State.equals(state.Game)){
            pointsboard.resetScores(Color.getChatColor(t.getColor()) + abbreviate(p.getName()));
            p.setScoreboard(manager.getNewScoreboard());
            users.remove(p);
        }else if(State.equals(state.Queue)){
            queboard.resetScores(Color.getChatColor(t.getColor()) + abbreviate(p.getName()));
            p.setScoreboard(manager.getNewScoreboard());
            users.remove(p);
        }
    }

    public void resetDisplay(){
        State = state.Queue;
        for(Player player: users){
            player.setScoreboard(manager.getNewScoreboard());
        }

        users = new ArrayList<Player>();

        pointsboard = manager.getNewScoreboard();
        que.unregister();
        points.unregister();
        que = queboard.registerNewObjective("Players in Queue", "dummy");
        points = pointsboard.registerNewObjective("Points", "dummy");

        que.setDisplaySlot(DisplaySlot.SIDEBAR);
        points.setDisplaySlot(DisplaySlot.SIDEBAR);

        addSlots(teams);

        Score score = que.getScore(ChatColor.GREEN + "Game In:");
        score.setScore(waitTime);

        if(gameTime > 0){
            Score timeScore = points.getScore(ChatColor.GREEN + "Time Left:");
            timeScore.setScore(gameTime);
        }
    }

    public void setDisplayGameMode(Collection<Team> collection){
        State = state.Game;
        for(Team team: collection){
            if(team.getPlayers().size() > 0){
                String teamName = WordUtils.capitalize(Color.getChatColor(team.getColor()) + " "
                        + (team.getColor().name().toLowerCase().replace("_", " ")));
                org.bukkit.scoreboard.Team sbTeam = pointsboard.registerNewTeam(teamName);
                sbTeam.setAllowFriendlyFire(false);

                for(Player player: team.getPlayers()){
                    sbTeam.addPlayer(player);

                    Score score = points.getScore(Color.getChatColor(team.getColor()) + abbreviate(player.getName()));
                    score.setScore(0);
                }
            }
        }

        for(Player player: users){
            player.setScoreboard(manager.getNewScoreboard());
            player.setScoreboard(pointsboard);
        }
    }

    public String abbreviate(String name){
        StringBuilder newName = new StringBuilder(name);
        if(name.length() > 15){
            for(int i = 0; i < (name.length() - 12); i++){
                newName.deleteCharAt(newName.length() - 1);
            }
            newName.append("..");
            return newName.toString();
        }
        return name;
    }
}
