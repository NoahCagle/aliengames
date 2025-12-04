package com.decacagle.aliensmc.commands;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.games.Game;
import com.decacagle.aliensmc.games.HideAndSeek;
import com.decacagle.aliensmc.games.RedLightGreenLight;
import com.decacagle.aliensmc.utilities.GameManager;
import com.decacagle.aliensmc.utilities.Globals;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

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
            if (args[0].equalsIgnoreCase("host")) {
                if (gameManager.getCurrentGame() != null) {
                    commandSourceStack.getSender().sendRichMessage("<red><bold>There is already a game running!");
                } else {
                    if (commandSourceStack.getSender() instanceof Player host) {
                        if (args[1].equalsIgnoreCase("rlgl") || args[1].equalsIgnoreCase("redlightgreenlight")) {
                            gameManager.prepareGame(new RedLightGreenLight(plugin, host));
                            host.sendRichMessage("<yellow>Preparing to play " + RedLightGreenLight.PRETTY_TITLE);
                            host.sendRichMessage("<yellow>When you're ready to start, type <bold>/agames start");
                        } else if (args[1].equalsIgnoreCase("hns") || args[1].equalsIgnoreCase("hideandseek")) {
                            gameManager.prepareGame(new HideAndSeek(plugin, host));
                            host.sendRichMessage("Preparing to play " + HideAndSeek.PRETTY_TITLE);
                            host.sendRichMessage("<yellow>When you're ready to start, type <bold>/agames start");
                        } else {
                            host.sendRichMessage("<gold><bold>Hostable Games:");
                            host.sendRichMessage("<green>/agames host rlgl - Host a game of Red Light Green Light");
                            host.sendRichMessage("<green>/agames host hns - Host a game of Hide And Seek");
                        }
                    } else {
                        commandSourceStack.getSender().sendRichMessage("<red><bold>Only players can play games!");
                    }
                }
            }
        } else if (args.length == 1) {
            if (commandSourceStack.getSender() instanceof Player sender) {
                if (args[0].equalsIgnoreCase("start")) {
                    if (gameManager.getCurrentGame() != null) {
                        Game currentGame = gameManager.getCurrentGame();
                        if (sender.getUniqueId().compareTo(currentGame.host.getUniqueId()) == 0) {
                            commandSourceStack.getSender().sendRichMessage("Starting active game!");
                            gameManager.getCurrentGame().startGame();
                        } else {
                            sender.sendRichMessage("<red><bold>You are not the host of this game!");
                            sender.sendRichMessage("<red>" + currentGame.host.getName() + " is the host of this game!");
                        }
                    } else {
                        sender.sendRichMessage("<red>There is no active game!");
                    }
                } else if (args[0].equalsIgnoreCase("join")) {
                    if (gameManager.getCurrentGame() != null) {
                        if (!Globals.playerInList(sender, gameManager.getCurrentGame().participants)) {
                            gameManager.getCurrentGame().addParticipant(sender);
                        } else {
                            sender.sendRichMessage("<red>You've already joined this game!");
                        }
                    } else {
                        sender.sendRichMessage("<red>There is no active game!");
                    }
                } else if (args[0].equalsIgnoreCase("host")) {
                    sender.sendRichMessage("<gold><bold>Hostable Games:");
                    sender.sendRichMessage("<green>/agames host rlgl - Host a game of Red Light Green Light");
                    sender.sendRichMessage("<green>/agames host hns - Host a game of Hide And Seek");
                }
            } else {
                commandSourceStack.getSender().sendRichMessage("<red><bold>Only players can play games!");
            }
        } else if (args.length == 0) {
            // send list of command options
        }

    }
}
