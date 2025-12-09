package com.decacagle.aliensmc.games.participants;

import org.bukkit.entity.Player;

public class RedLightGreenLightPlayer {

    public Player player;

    public boolean crossed = false;
    public boolean eliminated = false;

    public boolean connected = true;

    public int timeCrossed = -1;

    public int points = 0;

    public RedLightGreenLightPlayer(Player p) {
        this.player = p;
    }

}
