package com.decacagle.aliensmc.games.participants;

import org.bukkit.entity.Player;

public class GlassBridgePlayer {

    public Player player;
    public int order;
    public boolean eliminated = false;
    public boolean crossed = false;
    public int timeCrossed = -1;
    public int timeOfElimination = -1;

    public int lives = 3;

    public boolean connected = true;

    public boolean takenFirstLeap = false;

    public int points = 0;

    public GlassBridgePlayer(Player player, int order, int startingLives) {
        this.player = player;
        this.order = order;
        this.lives = startingLives;
    }

}
