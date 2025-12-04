package com.decacagle.aliensmc.games;

import com.decacagle.aliensmc.AliensGames;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class KnifeGame extends Game {

    private World world;

    public KnifeGame(AliensGames plugin) {
        super(new Location(plugin.getServer().getWorld("squidgame"), 903, 16, 1159));
        world = plugin.getServer().getWorld("squidgame");
    }

    public void prepareGame() {
        List<Player> allPlayers = world.getPlayers();
        for (Player p : allPlayers) {
            p.teleport(spawnpoint);
        }
    }

}
