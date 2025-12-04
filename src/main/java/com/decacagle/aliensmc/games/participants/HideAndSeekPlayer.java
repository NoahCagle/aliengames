package com.decacagle.aliensmc.games.participants;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HideAndSeekPlayer {

    public Player player;
    public boolean eliminiated = false;
    public boolean escaped = false;
    public int escapeTime = 0;
    public int kills = 0;
    public boolean seeker;

    public List<Player> killedPlayers = new ArrayList<Player>();

    public HideAndSeekPlayer(Player player, boolean seeker) {
        this.player = player;
        this.seeker = seeker;
        // if seeker = false, player is a hider
    }

}
