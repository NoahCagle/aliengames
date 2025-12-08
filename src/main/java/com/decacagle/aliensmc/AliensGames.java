package com.decacagle.aliensmc;

import com.decacagle.aliensmc.commands.*;
import com.decacagle.aliensmc.listeners.SquidGameEvents;
import com.decacagle.aliensmc.utilities.GameManager;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class AliensGames extends JavaPlugin {

    public Logger logger;
    public GameManager gameManager;

    public Song congratulationsSong;

    @Override
    public void onEnable() {
        logger = this.getLogger();
        logger.info("AliensGames plugin has launched successfully!");

        congratulationsSong = NBSDecoder.parse(new File("./plugins/Songs/congratulations.nbs"));

        gameManager = new GameManager(this);

        registerCommand("alienkey", new KeyCommand());
        registerCommand("agames", new GamesCommand(this, gameManager));

        getServer().getPluginManager().registerEvents(new SquidGameEvents(this, gameManager), this);
    }

    @Override
    public void onDisable() {
        logger.info("AliensGames plugin has been disabled!");
    }

}
