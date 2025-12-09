package com.decacagle.aliensmc.games.participants;

import org.bukkit.entity.Player;

public class GlassBridgePlayer {

    public Player player;
    public int order;
    public boolean eliminated = false;
    public boolean crossed = false;
    public int timeCrossed = -1;

    public boolean connected = true;

    public boolean takenFirstLeap = false;

    public int points = 0;

    public GlassBridgePlayer(Player player, int order) {
        this.player = player;
        this.order = order;
    }

}
