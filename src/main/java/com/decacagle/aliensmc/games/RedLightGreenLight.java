package com.decacagle.aliensmc.games;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.games.participants.RedLightGreenLightPlayer;
import com.decacagle.aliensmc.utilities.Globals;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RedLightGreenLight extends Game {

    private Team scoreboardLightStatus;

    private int minToggleTime, maxToggleTime;

    // time before game starts, measured in seconds.
    private int timeBeforeStart;

    private final Duration lightFadeIn = Duration.ZERO;
    private final Duration lightOnScreen = Duration.ofSeconds(2);
    private final Duration lightFadeOut = Duration.ofMillis(500);

    private List<RedLightGreenLightPlayer> players = new ArrayList<RedLightGreenLightPlayer>();

    // games will last 1 minute
    private int gameDurationSeconds;
    private int gameCountdown;
    // all players with Z greater than 1010 have not crossed the line
    public final int LINE_Z = 1010;
    public final int MIDPOINT_Z = 1046;

    private int redLightCountdown = 5;
    private int greenLightCountdown = 5;

    private int gracePeriodTicks;

    public boolean redLight = false;

    private Vector[] cannonLocs = new Vector[]{
            new Vector(987, 151, 994),
            new Vector(976, 151, 994),
            new Vector(965, 151, 994),
            new Vector(954, 151, 994),
            new Vector(943, 151, 994),
            new Vector(988, 151, 1097),
            new Vector(977, 151, 1097),
            new Vector(966, 151, 1097),
            new Vector(955, 151, 1097),
            new Vector(944, 151, 1097)
    };

    public RedLightGreenLight(AliensGames plugin, Player host) {
        super(new Location(plugin.getServer().getWorld(plugin.config.gameWorldTitleRLGL), plugin.config.spawnpointXRLGL, plugin.config.spawnpointYRLGL, plugin.config.spawnpointZRLGL, (float) plugin.config.spawnpointYawRLGL, (float) plugin.config.spawnpointPitchRLGL), plugin, host, plugin.config.minimumPlayersRLGL);
        this.boundsA = new Location(world, plugin.config.boundsAXRLGL, plugin.config.boundsAYRLGL, plugin.config.boundsAZRLGL);
        this.boundsB = new Location(world, plugin.config.boundsBXRLGL, plugin.config.boundsBYRLGL, plugin.config.boundsBZRLGL);
        this.gameDurationSeconds = plugin.config.gameDurationSecondsRLGL;
        this.gameCountdown = this.gameDurationSeconds;
        this.minToggleTime = plugin.config.minimumToggleTimeSecondsRLGL;
        this.maxToggleTime = plugin.config.maximumToggleTimeSecondsRLGL;
        this.timeBeforeStart = plugin.config.timeBeforeStartSecondsRLGL;
        this.gracePeriodTicks = plugin.config.gracePeriodTicksRLGL;
        this.prettyTitle = plugin.config.prettyTitleRLGL;
    }

    public void startGame() {
        this.gameStarted = true;
        healAll();

        fillPlayerListAndTeleport();

        queueGameStart();

        gameRunning = true;

    }

    public void timer() {
        if (gameRunning) {

            if (!redLight) {
                redLightCountdown--;

                plugin.logger.info("Red light in " + redLightCountdown);

                if (redLightCountdown <= 0) {
                    activateRedLight();
                }

            } else {
                greenLightCountdown--;

                plugin.logger.info("Green light in " + greenLightCountdown);

                if (greenLightCountdown <= 0) {
                    activateGreenLight();
                }

            }

            plugin.logger.info("gameCountdown: " + gameCountdown);

            gameCountdown--;

            updateTimer(Component.text("Time Remaining: "), gameCountdown);

            checkGameStatus();

            Bukkit.getScheduler().runTaskLater(plugin, this::timer, 20);
        }
    }

    public void checkGameStatus() {
        if (gameRunning) {
            plugin.logger.info("game still running, checking status");
            for (RedLightGreenLightPlayer p : players) {
                if (!p.crossed && !p.eliminated) {
                    checkIfCrossed(p);

                    // Look, i know this is disgusting
                    // but this is probably a better way to check for player crossing more frequently, without ramping up the loop speed like ive done before
                    for (int i = 1; i <= 3; i++) {
                        Bukkit.getScheduler().runTaskLater(plugin, () -> checkIfCrossed(p), i * 5);
                    }
                }
            }

            if (allCrossedOrEliminated()) {
                plugin.logger.info("Everyone's out, ending game!");
                endGame();
            }

            if (gameCountdown <= 0) {
                plugin.logger.info("Time's up, ending game!");
                endGame();
            }

        }
    }

    public void checkIfCrossed(RedLightGreenLightPlayer player) {
        if (player.player.getZ() <= LINE_Z) {
            registerPlayerCrossing(player);
        }
    }

    public boolean allCrossedOrEliminated() {
        for (RedLightGreenLightPlayer p : players) {
            if (!p.crossed && !p.eliminated) return false;
        }
        return true;
    }

    public void sortPlayersByTimeCrossed() {
        Collections.sort(players, (o1, o2) -> {
            if (o1.timeCrossed == o2.timeCrossed)
                return 0;
            return o1.timeCrossed < o2.timeCrossed ? 1 : -1;
        });

        for (int i = 0; i < players.size(); i++) {
            RedLightGreenLightPlayer p = players.get(i);
            if (p.crossed) {
                if (i == 0) p.points = 20;
                else if (i == 1) p.points = 15;
                else if (i == 2) p.points = 10;
                else p.points = 5;
            } else p.points = 0;
        }

    }

    public void endGame() {
        if (gameRunning) {
            gameRunning = false;

            plugin.logger.info("ending game");

            broadcastTitleToAllPlayers(Component.text("Game Over!", NamedTextColor.GREEN, TextDecoration.BOLD), Component.text(""));

            Bukkit.getScheduler().runTaskLater(plugin, this::goToLeaderboard, 40);
        }
    }

    public void goToLeaderboard() {
        this.gameEnded = true;

        plugin.logger.info("scoring and going to leaderboard");

        sortPlayersByTimeCrossed();

        broadcastMessageToAllPlayers("<underlined><green><bold>Red Light Green Light Rankings\n");

        healAll();

        List<Player> orderedPlayers = new ArrayList<Player>();

        int numWinners = 0;

        for (int i = 0; i < players.size(); i++) {
            RedLightGreenLightPlayer p = players.get(i);
            orderedPlayers.add(p.player);

            String numberColor = i == 0 ? ("<#D4AF37>") : (i == 1 ? ("<#C0C0C0>") : (i == 2 ? ("<#CD7F32>") : ("<gray>")));

            if (p.crossed) {
                numWinners++;
                broadcastMessageToAllPlayers(numberColor + "<bold>" + Globals.numberToPosition(i + 1) + ": <white>" + p.player.getName() + " - <green><bold>Crossed in " + Globals.secondsToFormattedTime(p.timeCrossed) + " seconds</bold></green><white> - " + p.points + " points");
                plugin.pointsManager.addPoints(p.player, p.points);
            } else {
                broadcastMessageToAllPlayers(numberColor + "<bold>" + Globals.numberToPosition(i + 1) + ": <white>" + p.player.getName() + " - <red><bold>Eliminated</bold></red><white> - 0 points");
            }
        }

        broadcastMessageToAllPlayers("");

        removeScoreboard();
        Globals.goToLeaderboard(orderedPlayers, world, numWinners, plugin, plugin.congratulationsSong);

        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.gameManager.stopGame(), 20);
    }

    public void activateRedLight() {
        broadcastTitleToAllPlayers(Component.text("Red Light!", NamedTextColor.RED, TextDecoration.BOLD), Component.text(""), lightFadeIn, lightOnScreen, lightFadeOut);
        greenLightCountdown = 6;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            redLight = true;
//            updateScoreboardDot();
        }, gracePeriodTicks);

        playSoundToAllPlayers(Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.4f);
        Bukkit.getScheduler().runTaskLater(plugin, () -> playSoundToAllPlayers(Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.2f), 3);

    }

    public void activateGreenLight() {
        redLight = false;
//        updateScoreboardDot();
        broadcastTitleToAllPlayers(Component.text("Green Light!", NamedTextColor.GREEN, TextDecoration.BOLD), Component.text(""), lightFadeIn, lightOnScreen, lightFadeOut);
        redLightCountdown = randomToggleTimeSeconds();

        playSoundToAllPlayers(Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.8f);
        Bukkit.getScheduler().runTaskLater(plugin, () -> playSoundToAllPlayers(Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.2f), 8);

    }

    public void killPlayer(RedLightGreenLightPlayer player) {

        player.eliminated = true;

        spectators.add(player.player);

        int randomDelay = (int) (Math.random() * 2);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            world.playSound(player.player.getLocation(), Sound.ENTITY_PLAYER_DEATH, 1.0f, 1.0f);
            player.player.setGameMode(GameMode.SPECTATOR);
            player.player.showTitle(Title.title(Component.text("You've been eliminated!", NamedTextColor.RED), Component.text("")));
            randomCannonFire(player.player.getZ() > MIDPOINT_Z);
            updatePlayerLine(player);
            broadcastMessageToAllPlayers("<red><bold>Player " + player.player.getName() + " has been eliminated!");
        }, 30 + randomDelay);

        greenLightCountdown += randomDelay;

    }

    public void randomCannonFire(boolean north) {
        int indexOffset = 0;
        if (north) {
            indexOffset = 5;
        }

        int randIndex = (int) (Math.random() * 5);

        randIndex += indexOffset;

        Location cannonLoc = new Location(world, cannonLocs[randIndex].getX(), cannonLocs[randIndex].getY(), cannonLocs[randIndex].getZ());

        world.createExplosion(cannonLoc, 10, false, false);

    }

    public void fillPlayerListAndTeleport() {
        double distanceBetweenPlayers = participants.size() > 26 ? (53.0 / participants.size()) : 2;

        double totalSpace = distanceBetweenPlayers * participants.size();
        double startX = 965 - (totalSpace / 2);

        for (int i = 0; i < participants.size(); i++) {
            Player p = participants.get(i);
            players.add(new RedLightGreenLightPlayer(p));

            p.teleport(new Location(world, startX + (distanceBetweenPlayers * i), spawnpoint.getY(), spawnpoint.getZ(), spawnpoint.getYaw(), spawnpoint.getPitch()));

        }
    }

    public void queueGameStart() {
        placeBarriers();

        initScoreboard();

        broadcastMessageToAllPlayers("<green><bold>Game starts in " + timeBeforeStart + " seconds!");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            broadcastTitleToAllPlayers(Component.text("3", NamedTextColor.GOLD, TextDecoration.BOLD), Component.text(""), Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO);
            playSoundToAllPlayers(Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 1.0f);
        }, (timeBeforeStart * 20) - 60);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            broadcastTitleToAllPlayers(Component.text("2", NamedTextColor.GOLD, TextDecoration.BOLD), Component.text(""), Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO);
            playSoundToAllPlayers(Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 1.0f);
        }, (timeBeforeStart * 20) - 40);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            broadcastTitleToAllPlayers(Component.text("1", NamedTextColor.GOLD, TextDecoration.BOLD), Component.text(""), Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO);
            playSoundToAllPlayers(Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 1.0f);
        }, (timeBeforeStart * 20) - 20);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            removeBarriers();
            activateGreenLight();
            Bukkit.getScheduler().runTaskLater(plugin, this::timer, 20);
        }, (timeBeforeStart * 20));

    }

    public void placeBarriers() {
        for (int x = 935; x <= 994; x++) {
            Location loc = new Location(world, x, 124, 1082);
            loc.getBlock().setType(Material.BARRIER);
        }
    }

    public void removeBarriers() {
        for (int x = 935; x <= 994; x++) {
            Location loc = new Location(world, x, 124, 1082);
            loc.getBlock().setType(Material.AIR);
        }
    }

    public void registerElimination(Player eliminated) {
        for (RedLightGreenLightPlayer p : players) {
            if (p.player.getUniqueId().compareTo(eliminated.getUniqueId()) == 0) {
                if (!p.eliminated && !p.crossed) {
                    killPlayer(p);
                }
            }
        }
    }

    public int randomToggleTimeSeconds() {
        int diff = maxToggleTime - minToggleTime;

        int randomTime = (int) (Math.random() * diff);

        return minToggleTime + randomTime;

    }

    public void registerPlayerCrossing(RedLightGreenLightPlayer player) {
        player.crossed = true;
        player.timeCrossed = gameDurationSeconds - gameCountdown;

        updatePlayerLine(player);

    }

    public void initScoreboard() {
        createScoreboardWithTimer(prettyTitle);
//
//        scoreboardLightStatus = createScoreboardLine(scoreboard, scoreboardObjective, "2", 2, false);
//        scoreboardLightStatus.prefix(Component.text("§3", NamedTextColor.GOLD));
//        scoreboardLightStatus.suffix(Component.text("•", NamedTextColor.GRAY, TextDecoration.BOLD));
//
//        Team emptyLine = createScoreboardLine(scoreboard, scoreboardObjective, "1", 1, false);
//        emptyLine.prefix(Component.text("§3"));
//        emptyLine.suffix(Component.text("§3"));

        for (int i = 0; i < participants.size(); i++) {
            Player player = participants.get(i);

            Team playerLine = createScoreboardLine(scoreboard, scoreboardObjective, "" + i, -i, false);
            playerLines.put(player, playerLine);

            playerLine.prefix(Component.text(player.getName() + ": "));
            playerLine.suffix(Component.text("✔", NamedTextColor.GREEN, TextDecoration.BOLD));

        }

    }

    public void updatePlayerLine(RedLightGreenLightPlayer player) {

        Team playerLine = playerLines.get(player.player);

        if (player.crossed) {
            playerLine.suffix(Component.text("⭐", NamedTextColor.GOLD, TextDecoration.BOLD));
        } else if (player.eliminated) {
            playerLine.suffix(Component.text("✘", NamedTextColor.RED, TextDecoration.BOLD));
        } else {
            playerLine.suffix(Component.text("✔", NamedTextColor.GREEN, TextDecoration.BOLD));
        }

        if (!player.connected) {
            playerLine.prefix(Component.text(player.player.getName() + ": ", NamedTextColor.DARK_GRAY));
        }

    }

    public void cleanup() {
        if (this.scoreboard != null) {
            removeScoreboard();
        }
        clearAllInventories();
    }

    public void reportPlayerDeparture(Player player) {
        participants.remove(player);
        removeFromScoreboard(player);

        Globals.fullyClearInventory(player);

        if (gameRunning) {

            RedLightGreenLightPlayer rlglPlayer = null;

            for (RedLightGreenLightPlayer p : players) {
                if (p.player.getUniqueId().compareTo(player.getUniqueId()) == 0) {
                    rlglPlayer = p;
                    players.remove(p);
                    break;
                }
            }

            if (rlglPlayer != null) {

                rlglPlayer.eliminated = true;
                rlglPlayer.connected = false;
                rlglPlayer.points = 0;

                spectators.add(rlglPlayer.player);

                updatePlayerLine(rlglPlayer);

            } else {
                plugin.logger.severe("Tried to find " + player.getName() + " for a departure report, but couldn't find them!");
            }

        }

        broadcastMessageToAllPlayers("<red>" + player.getName() + " has left your mini-game!");

        if (participants.isEmpty()) {
            plugin.gameManager.forceStop();
        }

    }

}
