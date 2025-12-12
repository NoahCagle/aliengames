package com.decacagle.aliensmc.games;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.utilities.Globals;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {

    private Team timerLine;
    public Scoreboard scoreboard;
    public Objective scoreboardObjective;
    public Map<Player, Team> playerLines = new HashMap<>();

    public World world;
    public List<Player> participants = new ArrayList<Player>();
    public List<Player> spectators = new ArrayList<Player>();

    public Location boundsA, boundsB;

    public Location spawnpoint;
    public AliensGames plugin;
    public Player host;

    public boolean gameRunning = false;
    public boolean gameEnded = false;

    public String prettyTitle;

    public int minPlayers;

    public Game(Location spawnpoint, AliensGames plugin, Player host, int minPlayers) {
        this.spawnpoint = spawnpoint;
        this.host = host;
        this.minPlayers = minPlayers;
        this.world = spawnpoint.getWorld();
        this.plugin = plugin;

        addParticipant(host);
    }

    public void endGame() {

    }

    public void prepareGame() {

    }

    public void startGame() {

    }

    public void broadcastMessageToAllPlayers(String richMessage) {
        for (Player p : participants) {
            p.sendRichMessage(richMessage);
        }
    }

    public void broadcastTitleToAllPlayers(Component title, Component subtitle) {
        for (Player p : participants) {
            p.showTitle(Title.title(title, subtitle));
        }
    }

    public void broadcastTitleToAllPlayers(Component title, Component subtitle, Duration fadeIn, Duration onScreen, Duration fadeOut) {
        for (Player p : participants) {
            p.showTitle(Title.title(title, subtitle, Title.Times.times(
                    fadeIn, onScreen, fadeOut
            )));
        }
    }

    public void playSoundToAllPlayers(Sound sound, float volume, float pitch) {
        for (Player p : participants) {
            p.playSound(p, sound, volume, pitch);
        }
    }

    public void addParticipant(Player player) {
        participants.add(player);
        player.teleport(spawnpoint);
        player.setGameMode(GameMode.ADVENTURE);
        broadcastMessageToAllPlayers("<green>" + player.getName() + " has joined your mini-game!");
    }

    public void cleanup() {

    }

    public void clearAllInventories() {
        for (Player p : participants) {
            Globals.fullyClearInventory(p);
        }
    }

    public void healAll() {
        for (Player p : participants) {
            p.setHealth(20);
            p.setFoodLevel(20);
        }
    }

    public void updateTimer(Component prefix, int seconds) {
        timerLine.prefix(prefix);
        timerLine.suffix(Component.text(Globals.secondsToFormattedTime(seconds)));
    }

    public void createScoreboardWithTimer(String title) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        this.scoreboard = manager.getNewScoreboard();

        this.scoreboardObjective = scoreboard.registerNewObjective("game", Criteria.DUMMY, Component.text(title, NamedTextColor.GOLD, TextDecoration.BOLD));
        scoreboardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        this.timerLine = createScoreboardLine(scoreboard, scoreboardObjective, "9", 10, false);
        timerLine.prefix(Component.text(" --"));
        timerLine.suffix(Component.text("-- "));

        for (Player p : participants) {
            p.setScoreboard(scoreboard);
        }

    }

    public Team createScoreboardLine(Scoreboard board, Objective obj, String key, int score, boolean keyVisible) {
        Team team = board.registerNewTeam("line_" + score);
        String invisiKey = "§a";

        if (!keyVisible) {
            for (int i = 0; i < key.length(); i++) {
                invisiKey += ("§" + key.charAt(i));
            }
        }

        team.addEntry(keyVisible ? key : invisiKey);
        obj.getScore(keyVisible ? key : invisiKey).setScore(score);
        obj.getScore(keyVisible ? key : invisiKey).numberFormat(NumberFormat.blank());
        return team;
    }

    public void initScoreboard() {

    }

    public void removeScoreboard() {
        try {
            scoreboardObjective.unregister();
        } catch (Exception e) {
            plugin.logger.severe("Error caught when trying to unregister scoreboard objective!");
            plugin.logger.severe("e: " + e);
            plugin.logger.severe(scoreboardObjective == null ? "scoreboardObjective is null!" : "scoreboardObject is NOT null!");
        }

        Scoreboard empty = Bukkit.getScoreboardManager().getNewScoreboard();
        for (Player p : participants) {
            p.setScoreboard(empty);
        }
    }

    public void removeFromScoreboard(Player player) {
        Scoreboard empty = Bukkit.getScoreboardManager().getNewScoreboard();
        player.setScoreboard(empty);

    }

    public void reportPlayerDeparture(Player player) {

    }

}
