package com.decacagle.aliensmc.commands;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.games.Game;
import com.decacagle.aliensmc.games.GlassBridge;
import com.decacagle.aliensmc.games.HideAndSeek;
import com.decacagle.aliensmc.games.RedLightGreenLight;
import com.decacagle.aliensmc.utilities.GameManager;
import com.decacagle.aliensmc.utilities.Globals;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// TODO: clean this shit up what the fuck have i done here...
public class GamesCommand implements BasicCommand {

    private final String ADMIN_PERMS = "aliensgames.agames.admin";

    private GameManager gameManager;
    private AliensGames plugin;

    public GamesCommand(AliensGames plugin, GameManager gameManager) {
        this.gameManager = gameManager;
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender sender = commandSourceStack.getSender();

        if (args.length == 0) {
            sendAllCommandsList(sender);
        } else {
            if (args[0].equalsIgnoreCase("host")) {
                if (sender instanceof Player host) {
                    if (args.length >= 2) {
                        if (args[1].equalsIgnoreCase("rlgl") || args[1].equalsIgnoreCase("redlightgreenlight")) {
                            hostGame(new RedLightGreenLight(plugin, host));
                        } else if (args[1].equalsIgnoreCase("hns") || args[1].equalsIgnoreCase("hideandseek")) {
                            hostGame(new HideAndSeek(plugin, host));
                        } else if (args[1].equalsIgnoreCase("gb") || args[1].equalsIgnoreCase("glassbridge")) {
                            hostGame(new GlassBridge(plugin, host));
                        } else {
                            sendHostableGamesMessage(host);
                        }
                    } else {
                        sendHostableGamesMessage(host);
                    }
                } else {
                    sender.sendRichMessage("<red><bold>Only players can play games!");
                }
            } else if (args[0].equalsIgnoreCase("start")) {
                if (sender instanceof Player player) {
                    Game currentGame = gameManager.getCurrentGame();
                    if (currentGame != null) {
                        if (player.getUniqueId().compareTo(currentGame.host.getUniqueId()) == 0) {
                            if (!currentGame.gameRunning) {
//                                if (currentGame.participants.size() >= currentGame.minPlayers) {
                                    sender.sendRichMessage("<green><bold>Starting the game!");
                                    gameManager.getCurrentGame().startGame();
//                                } else {
//                                    sender.sendRichMessage("<red><bold>There are not enough players to start yet!");
//                                    sender.sendRichMessage("<red><bold>This game requires a minimum of " + currentGame.minPlayers + " players!");
//                                    sender.sendRichMessage("<red><bold>You only have " + currentGame.participants.size() + " players!");
//                                }
                            } else {
                                player.sendRichMessage("<red><bold>The game has already started!");
                            }
                        } else {
                            player.sendRichMessage("<red><bold>You are not the host of this game!");
                        }
                    } else {
                        sender.sendRichMessage("<red><bold>There is no active game!");
                    }
                } else {
                    sender.sendRichMessage("<red><bold>Only players can play games!");
                }
            } else if (args[0].equalsIgnoreCase("join")) {
                if (sender instanceof Player player) {
                    Game currentGame = gameManager.getCurrentGame();
                    if (currentGame != null) {
                        if (!currentGame.gameRunning) {
                            if (!Globals.playerInList(player, currentGame.participants)) {
                                currentGame.addParticipant(player);
                            } else {
                                player.sendRichMessage("<red><bold>You are already in this game!");
                            }
                        } else {
                            player.sendRichMessage("<red><bold>The game has already started!");
                        }
                    } else {
                        player.sendRichMessage("<red><bold>There is no active game!");
                    }
                } else {
                    sender.sendRichMessage("<red><bold>Only players can play games!");
                }
            } else if (args[0].equalsIgnoreCase("admin")) {
                if (sender.hasPermission(ADMIN_PERMS)) {
                    if (args.length >= 2) {
                        if (args[1].equalsIgnoreCase("forcestop")) {
                            gameManager.forceStop();
                        }
                    } else {
                        sendAdminCommandsList(sender);
                    }
                } else {
                    sender.sendRichMessage("<red>You don't have access to that command!");
                }
            }
        }

    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String @NotNull [] args) {

        if (args.length == 1) {
            List<String> completions = new ArrayList<String>();
            completions.add("host");
            completions.add("join");
            completions.add("start");
            if (stack.getSender().hasPermission(ADMIN_PERMS)) {
                completions.add("admin");
            }
            return completions;
        } else if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("host"))
                return List.of("rlgl", "hns", "gb");
            else if (args[0].equalsIgnoreCase("admin") && stack.getSender().hasPermission(ADMIN_PERMS)) {
                return List.of("forcestop");
            }
        }

        return List.of();
    }

    public void sendHostableGamesMessage(Player player) {
        player.sendRichMessage("<gold><bold>Hostable Games:");
        player.sendRichMessage("<green>/agames host rlgl - Host a game of Red Light Green Light");
        player.sendRichMessage("<green>/agames host hns - Host a game of Hide And Seek");
        player.sendRichMessage("<green>/agames host gb - Host a game of Glass Bridge");
    }

    public void sendAdminCommandsList(CommandSender sender) {

    }

    public void sendAllCommandsList(CommandSender sender) {

    }

    public void hostGame(Game game) {
        gameManager.prepareGame(game);
        game.host.sendRichMessage("Preparing to play " + HideAndSeek.PRETTY_TITLE);
        game.host.sendRichMessage("<yellow>When you're ready to start, type <bold>/agames start");
    }

}
