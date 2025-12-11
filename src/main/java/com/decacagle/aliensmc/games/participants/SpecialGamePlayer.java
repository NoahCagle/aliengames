package com.decacagle.aliensmc.games.participants;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpecialGamePlayer {

    public boolean connected = true;
    public boolean eliminated = false;

    public int kills = 0;
    public List<Player> killedPlayers = new ArrayList<Player>();
    public int points;

    public Player player;

    public SpecialGamePlayer(Player player) {
        this.player = player;
    }

}
