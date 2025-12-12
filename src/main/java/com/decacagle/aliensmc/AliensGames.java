package com.decacagle.aliensmc;

import com.decacagle.aliensmc.commands.*;
import com.decacagle.aliensmc.listeners.SquidGameEvents;
import com.decacagle.aliensmc.utilities.GameManager;
import com.decacagle.aliensmc.utilities.PointsManager;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class AliensGames extends JavaPlugin {

    public ConfigManager config;
    public PointsManager pointsManager;

    public Economy economy;

    public Logger logger;
    public GameManager gameManager;

    public Song congratulationsSong;

    @Override
    public void onEnable() {
        logger = this.getLogger();
        logger.info("AliensGames plugin has launched successfully!");

        if (!economySetup()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            getLogger().info("Successfully hooked with Economy: " + economy.getName());
        }

        if (!noteblockAPISetup()) {
            getLogger().severe("Disabled due to no NoteblockAPI dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            getLogger().info("Initializing Noteblock Studio songs with Noteblock API!");

            congratulationsSong = NBSDecoder.parse(new File("./plugins/Songs/congratulations.nbs"));

        }

        saveDefaultConfig();
        this.config = new ConfigManager(this);

        this.pointsManager = new PointsManager(this, "points.yml");

        gameManager = new GameManager(this);

        registerCommand("agames", new GamesCommand(this, gameManager));

        getServer().getPluginManager().registerEvents(new SquidGameEvents(this, gameManager), this);
    }

    @Override
    public void onDisable() {
        logger.info("AliensGames plugin has been disabled!");
    }

    private boolean noteblockAPISetup() {
        boolean noteblockAPIfound = true;
        if (!Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")) {
            getLogger().severe("*** NoteBlockAPI is not installed or not enabled. ***");
            noteblockAPIfound = false;
        }
        return noteblockAPIfound;
    }

    private boolean economySetup() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer()
                .getServicesManager()
                .getRegistration(Economy.class);

        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    public void reloadConfig(CommandSender sender) {
        this.reloadConfig();
        this.config = new ConfigManager(this);
        sender.sendRichMessage("Reloading AliensGames config...");
    }

}
