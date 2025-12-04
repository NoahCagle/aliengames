package com.decacagle.aliensmc.commands;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.games.KnifeGame;
import com.decacagle.aliensmc.games.RedLightGreenLight;
import com.decacagle.aliensmc.utilities.GameManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;

public class GamesCommand implements BasicCommand {

    private GameManager gameManager;
    private AliensGames plugin;

    public GamesCommand(AliensGames plugin, GameManager gameManager) {
        this.gameManager = gameManager;
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("prepare")) {
                if (args[1].equalsIgnoreCase("rlgl")) {
                    gameManager.prepareGame(new RedLightGreenLight(plugin));
                    commandSourceStack.getSender().sendRichMessage("Preparing red light green light!");
                } else if (args[1].equalsIgnoreCase("knife")) {
                    gameManager.prepareGame(new KnifeGame(plugin));
                    commandSourceStack.getSender().sendRichMessage("Preparing knife game!");
                }
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("testExplosions")) {
                plugin.logger.info("Testing explosions!");
                if (gameManager.getCurrentGame() instanceof RedLightGreenLight rlgl) {
                    Bukkit.getScheduler().runTaskLater(plugin, rlgl::testExplosion, 30);
                    Bukkit.getScheduler().runTaskLater(plugin, rlgl::testExplosion, 40);
                    Bukkit.getScheduler().runTaskLater(plugin, rlgl::testExplosion, 50);
                    commandSourceStack.getSender().sendRichMessage("Testing explosions!");
                }
            } else if (args[0].equalsIgnoreCase("redlight")) {
                if (gameManager.getCurrentGame() instanceof RedLightGreenLight rlgl) {
                    commandSourceStack.getSender().sendRichMessage("Red light: true");
                    rlgl.setRedLight(true);
                }
            } else if (args[0].equalsIgnoreCase("greenlight")) {
                if (gameManager.getCurrentGame() instanceof RedLightGreenLight rlgl) {
                    commandSourceStack.getSender().sendRichMessage("Red light: false");
                    rlgl.setRedLight(false);
                }
            } else if (args[0].equalsIgnoreCase("start")) {
                if (gameManager.getCurrentGame() != null) {
                    commandSourceStack.getSender().sendRichMessage("Starting active game!");
                    gameManager.getCurrentGame().startGame();
                }
                commandSourceStack.getSender().sendRichMessage("There is no active game!");
            } else if (args[0].equalsIgnoreCase("stop")) {
                commandSourceStack.getSender().sendRichMessage("Stopping any active game!");
                gameManager.stopGame();
            }
        }

    }
}
