package com.decacagle.aliensmc.commands;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.games.*;
import com.decacagle.aliensmc.utilities.GameManager;
import com.decacagle.aliensmc.utilities.Globals;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

// TODO: clean this shit up what the fuck have i done here...
public class GamesCommand implements BasicCommand {

    public static final String ADMIN_PERMS = "aliensgames.agames.admin";

    private boolean showingKeyLocs = false;

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
                        if (gameManager.getCurrentGame() == null) {
                            if (args[1].equalsIgnoreCase("rlgl") || args[1].equalsIgnoreCase("redlightgreenlight")) {
                                hostGame(new RedLightGreenLight(plugin, host));
                            } else if (args[1].equalsIgnoreCase("hns") || args[1].equalsIgnoreCase("hideandseek")) {
                                hostGame(new HideAndSeek(plugin, host));
                            } else if (args[1].equalsIgnoreCase("gb") || args[1].equalsIgnoreCase("glassbridge")) {
                                hostGame(new GlassBridge(plugin, host));
                            } else if (args[1].equalsIgnoreCase("sg") || args[1].equalsIgnoreCase("specialgame")) {
                                hostGame(new SpecialGame(plugin, host));
//                                sendHostableGamesMessage(host);
                            } else {
                                sendHostableGamesMessage(host);
                            }
                        } else {
                            host.sendRichMessage("<red><bold>There is already a game being played right now!");
                        }
                    } else {
                        sendHostableGamesMessage(host);
                    }
                } else {
                    sender.sendRichMessage("<red><bold>Only players can play mini-game!");
                }
            } else if (args[0].equalsIgnoreCase("start")) {
                if (sender instanceof Player player) {
                    Game currentGame = gameManager.getCurrentGame();
                    if (currentGame != null) {
                        if (player.getUniqueId().compareTo(currentGame.host.getUniqueId()) == 0) {
                            if (!currentGame.gameRunning) {
                                if (currentGame.participants.size() >= currentGame.minPlayers || plugin.config.debugMode) {
                                    sender.sendRichMessage("<green><bold>Starting the mini-game!");
                                    gameManager.getCurrentGame().startGame();
                                } else {
                                    sender.sendRichMessage("<red><bold>There are not enough players to start yet!");
                                    sender.sendRichMessage("<red><bold>This mini-game requires a minimum of " + currentGame.minPlayers + " players!");
                                    sender.sendRichMessage("<red><bold>You only have " + currentGame.participants.size() + " players!");
                                }
                            } else {
                                player.sendRichMessage("<red><bold>The game has already started!");
                            }
                        } else {
                            player.sendRichMessage("<red><bold>You are not the host of this mini-game!");
                            player.sendRichMessage("<red><bold>" + currentGame.host.getName() + " is the host of this mini-game!");
                        }
                    } else {
                        sender.sendRichMessage("<red><bold>There is no active mini-game!");
                    }
                } else {
                    sender.sendRichMessage("<red><bold>Only players can play mini-games!");
                }
            } else if (args[0].equalsIgnoreCase("join")) {
                if (sender instanceof Player player) {
                    Game currentGame = gameManager.getCurrentGame();
                    if (currentGame != null) {
                        if (!currentGame.gameRunning) {
                            if (!Globals.playerInList(player, currentGame.participants)) {
                                currentGame.addParticipant(player);
                            } else {
                                player.sendRichMessage("<red><bold>You are already in this mini-game!");
                            }
                        } else {
                            player.sendRichMessage("<red><bold>The mini-game has already started!");
                        }
                    } else {
                        player.sendRichMessage("<red><bold>There is no active mini-game!");
                    }
                } else {
                    sender.sendRichMessage("<red><bold>Only players can play mini-games!");
                }
            } else if (args[0].equalsIgnoreCase("leave")) {
                if (sender instanceof Player player) {
                    Game currentGame = gameManager.getCurrentGame();
                    if (currentGame != null) {

                        if (Globals.playerInList(player, currentGame.participants)) {

                            currentGame.reportPlayerDeparture(player);

                            player.teleport(player.getWorld().getSpawnLocation());

                            player.setGameMode(GameMode.ADVENTURE);

                            if (player.getUniqueId().compareTo(currentGame.host.getUniqueId()) == 0) {
                                gameManager.reportHostDisconnect();
                            }

                        } else {
                            player.sendRichMessage("<red><bold>You haven't joined the current mini-game!");
                        }

                    } else {
                        player.sendRichMessage("<red><bold>There is no active mini-game!");
                    }
                } else {
                    sender.sendRichMessage("<red><bold>Only players can play mini-games!");
                }
            } else if (args[0].equalsIgnoreCase("cancel")) {

                if (sender instanceof Player player) {
                    Game currentGame = gameManager.getCurrentGame();

                    if (currentGame != null) {

                        if (player.getUniqueId().compareTo(currentGame.host.getUniqueId()) == 0) {

                            if (!currentGame.gameRunning) {
                                gameManager.hostCancel();
                            } else {
                                player.sendRichMessage("<red><bold>This mini-game has already started!");
                                player.sendRichMessage("<yellow>If you'd like to leave this mini-game, you can do so with the <bold>/agames leave</bold> command!");
                            }

                        } else {
                            player.sendRichMessage("<red><bold>You are not the host of this mini-game!");
                            player.sendRichMessage("<red><bold>" + currentGame.host.getName() + " is the host of this mini-game!");
                            player.sendRichMessage("<yellow>If you'd like to leave this mini-game, you can do so with the <bold>/agames leave</bold> command!");
                        }

                    } else {
                        player.sendRichMessage("<red><bold>There is no active mini-game!");
                    }

                } else {
                    sender.sendRichMessage("<red><bold>Only players can play mini-games!");
                }

            } else if (args[0].equalsIgnoreCase("points")) {
                if (sender instanceof Player player) {
                    int points = plugin.pointsManager.getPoints(player);
                    player.sendRichMessage("\n<white>" + player.getName() + " - <green>" + points + " points\n");
                } else {
                    sender.sendRichMessage("<red><bold>Only players can earn points!");
                }
            } else if (args[0].equalsIgnoreCase("lb")) {
                sendPointsLeaderboard(sender);
            } else if (args[0].equalsIgnoreCase("admin")) {
                if (sender.hasPermission(ADMIN_PERMS)) {
                    if (args.length >= 2) {
                        if (args[1].equalsIgnoreCase("forcestop")) {
                            gameManager.forceStop();
                        } else if (args[1].equalsIgnoreCase("keylocs")) {
                            showingKeyLocs = !showingKeyLocs;

                            if (showingKeyLocs) {
                                showKeyLocations();
                                sender.sendRichMessage("<yellow>Showing hide and seek key locations!");
                            } else {
                                hideKeyLocations();
                                sender.sendRichMessage("<yellow>Hiding hide and seek key locations!");
                            }

                        } else if (args[1].equalsIgnoreCase("togglelights")) {
                            if (gameManager.getCurrentGame() instanceof SpecialGame sg) {
                                sg.toggleLights();
                            }
                        } else if (args[1].equalsIgnoreCase("reload")) {
                            plugin.reloadConfig(sender);
                        } else if (args[1].equalsIgnoreCase("setpoints")) {
                            if (args.length >= 4) {
                                String player = args[2];
                                String value = args[3];

                                if (Globals.checkParsable(value)) {
                                    plugin.pointsManager.setPoints(player, Integer.parseInt(value));
                                    sender.sendRichMessage("<yellow>Set value of points." + player + " to " + value + " in points.yml!");
                                } else {
                                    sender.sendRichMessage("<red>" + value + " is not a valid integer value!");
                                }

                            } else {
                                sender.sendRichMessage("<red>Correct usage: /agames admin setpoints <username> <points>");
                            }
                        } else if (args[1].equalsIgnoreCase("delpoints")) {
                            if (args.length >= 3) {
                                String player = args[2];
                                plugin.pointsManager.deletePoints(player);
                                sender.sendRichMessage("<yellow>Deleted points." + player + " from points.yml!");
                            } else {
                                sender.sendRichMessage("<red>Correct usage: /agames admin delpoints <username>");
                            }
                        } else if (args[1].equalsIgnoreCase("debug")) {
                            sender.sendRichMessage("<yellow>Note: this command does NOT change the debug_mode key in config.yml");
                            if (plugin.config.debugMode) {
                                plugin.config.debugMode = false;
                                sender.sendRichMessage("<yellow>Debug mode has been turned off!");
                            } else {
                                plugin.config.debugMode = true;
                                sender.sendRichMessage("<yellow>Debug mode has been turned on!");
                            }
                        } else {
                            sendAdminCommandsList(sender);
                        }
                    } else {
                        sendAdminCommandsList(sender);
                    }
                } else {
                    sender.sendRichMessage("<red>You don't have access to that command!");
                }
            } else {
                sendAllCommandsList(sender);
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
            completions.add("leave");
            completions.add("cancel");
            completions.add("lb");
            completions.add("points");
            if (stack.getSender().hasPermission(ADMIN_PERMS)) {
                completions.add("admin");
            }
            return completions;
        } else if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("host"))
                return List.of("rlgl", "hns", "gb", "sg");
            else if (args[0].equalsIgnoreCase("admin") && stack.getSender().hasPermission(ADMIN_PERMS)) {
                return List.of("forcestop", "keylocs", "togglelights", "reload", "setpoints", "delpoints", "debug");
            }
        }

        return List.of();
    }

    public void sendHostableGamesMessage(Player player) {
        player.sendRichMessage("<gold><bold>Hostable Mini-Games:");
        player.sendRichMessage("<green>/agames host rlgl - Host a game of Red Light Green Light");
        player.sendRichMessage("<green>/agames host hns - Host a game of Hide And Seek");
        player.sendRichMessage("<green>/agames host gb - Host a game of Glass Bridge");
        player.sendRichMessage("<green>/agames host sg - Host a Special Game");
    }

    public void sendAdminCommandsList(CommandSender sender) {
        sender.sendRichMessage("<red><bold><underlined>AlienGames Admin Commands");
        sender.sendRichMessage("<red>/agames admin forcestop - Force the current mini-game to halt");
        sender.sendRichMessage("<red>/agames admin keylocs - Show and hide possible key locations for hide and seek");
        sender.sendRichMessage("<red>/agames admin togglelights - Toggle the lights in Special Game (game must be running)");
        sender.sendRichMessage("<red>/agames admin reload - Reload values in config.yml");
        sender.sendRichMessage("<red>/agames admin setpoints - Manually set the value of a player's points");
        sender.sendRichMessage("<red>/agames admin delpoints - Manually delete a player from the points record");
        sender.sendRichMessage("<red>/agames admin debug - Toggle debug mode (ask Cagle for more details)");
    }

    public void sendAllCommandsList(CommandSender sender) {
        sender.sendRichMessage("<gold><bold><underlined>AlienGames Commands");
        sender.sendRichMessage("<yellow>/agames host <game> - Host a mini-game");
        sender.sendRichMessage("<yellow>/agames join - Join a mini-game that's currently being hosted");
        sender.sendRichMessage("<yellow>/agames start - Start the mini-game that you're hosting");
        sender.sendRichMessage("<yellow>/agames cancel - Cancel the mini-game that you're hosting");
        sender.sendRichMessage("<yellow>/agames leave - Leave the mini-game that you're currently playing");

        if (sender.hasPermission(ADMIN_PERMS)) {
            sendAdminCommandsList(sender);
        }

    }

    public void showKeyLocations() {
        World world = plugin.getServer().getWorld("squidgame");
        for (Vector vec : HideAndSeek.KEY_LOCATIONS) {
            Location loc = new Location(world, vec.getX(), vec.getY(), vec.getZ());
            loc.getBlock().setType(Material.GLOWSTONE);
        }
    }

    public void hideKeyLocations() {
        World world = plugin.getServer().getWorld("squidgame");
        for (Vector vec : HideAndSeek.KEY_LOCATIONS) {
            Location loc = new Location(world, vec.getX(), vec.getY(), vec.getZ());
            loc.getBlock().setType(Material.SNOW);
        }
    }

    public void hostGame(Game game) {
        gameManager.prepareGame(game);
        game.host.sendRichMessage("<yellow>When you're ready to start, type <bold>/agames start");
    }

    public void sendPointsLeaderboard(CommandSender sender) {
        List<Map.Entry<String, Integer>> leaderboard = plugin.pointsManager.getTopPlayers(10);

        sender.sendRichMessage("<gold><bold><underlined>Aliens Games Top 10 Leaderboard\n");

        for (int i = 0; i < leaderboard.size(); i++) {
            Map.Entry<String, Integer> entry = leaderboard.get(i);
            sender.sendRichMessage("<gray>" + (i + 1) + ". <white>" + entry.getKey() + " - <green>" + entry.getValue() + " points");
        }

    }

}
