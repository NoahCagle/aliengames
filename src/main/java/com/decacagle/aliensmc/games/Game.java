package com.decacagle.aliensmc.games;

import com.decacagle.aliensmc.AliensGames;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Game {

    public World gameWorld;
    public List<Player> participants = new ArrayList<Player>();
    public Location spawnpoint;

    public AliensGames plugin;

    public Game(Location spawnpoint) {
        this.spawnpoint = spawnpoint;
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

}
