package com.decacagle.aliensmc;

import com.decacagle.aliensmc.commands.*;
import com.decacagle.aliensmc.listeners.SquidGameEvents;
import com.decacagle.aliensmc.utilities.GameManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class AliensGames extends JavaPlugin {

    public Logger logger;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        logger = this.getLogger();
        logger.info("AliensGames plugin has launched successfully!");

        gameManager = new GameManager();

        registerCommand("alienkey", new KeyCommand());
        registerCommand("alienknife", new KnifeCommand());
        registerCommand("alienhider", new HiderCommand());
        registerCommand("alienseeker", new SeekerCommand());
        registerCommand("agames", new GamesCommand(this, gameManager));

        getServer().getPluginManager().registerEvents(new SquidGameEvents(this, gameManager), this);
    }

    @Override
    public void onDisable() {
        logger.info("AliensGames plugin has been disabled!");
    }

}
