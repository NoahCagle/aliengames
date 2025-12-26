package com.decacagle.aliensmc.games.participants;

import com.decacagle.aliensmc.games.participants.roles.MurderMysteryRole;
import org.bukkit.entity.Player;

public class MurderMysteryPlayer {

    public Player player;
    public MurderMysteryRole role;

    public boolean eliminated = false;
    public boolean connected = true;

    public int points = 0;

    public MurderMysteryPlayer(Player player, MurderMysteryRole role) {
        this.player = player;
        this.role = role;
    }

}
