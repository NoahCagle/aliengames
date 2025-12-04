package com.decacagle.aliensmc.games;

import com.decacagle.aliensmc.AliensGames;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Game {

    public World world;
    public List<Player> participants = new ArrayList<Player>();

    public Location spawnpoint;
    public AliensGames plugin;
    public Player host;

    public boolean gameStarted = false;

    public static String PRETTY_TITLE;

    public Game(Location spawnpoint, AliensGames plugin, Player host) {
        this.spawnpoint = spawnpoint;
        this.host = host;
        participants.add(host);
        this.world = spawnpoint.getWorld();
        this.plugin = plugin;
    }

    public void nextStep() {

    }

    public void determineParticipants() {

    }

    public void endGame() {

    }

    public void prepareGame() {

    }

    public void startGame() {

    }

    public void broadcastToPlayers(String richMessage) {
        for (Player p : participants) {
            p.sendRichMessage(richMessage);
        }
    }

    public void addParticipant(Player player) {
        participants.add(player);
        player.teleport(spawnpoint);
        player.setGameMode(GameMode.ADVENTURE);
    }

}
