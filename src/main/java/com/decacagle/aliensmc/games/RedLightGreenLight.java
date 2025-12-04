package com.decacagle.aliensmc.games;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.utilities.Globals;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RedLightGreenLight extends Game {

    // measured in ticks
    private int minTimeBetweenToggle = 40, maxTimeBetweenToggle = 200;

    private World world;

    private List<Player> toKill = new ArrayList<Player>();

    // 1 minute, in seconds
    private final int gameDuration = 60;
    private int gameCountdown = 60;

    private int redlightCountdown = 0;
    private int secondsInRedlight = 0;

    // all players with Z greater than 1010 have not crossed the line
    public static int LINE_Z = 1010;

    private boolean redLight = false;

    private boolean gameStarted = false;

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

    public RedLightGreenLight(AliensGames plugin) {
        // i hate calling getWorld twice but fuck it
        super(new Location(plugin.getServer().getWorld("squidgame"), 964, 123, 1091));
        world = plugin.getServer().getWorld("squidgame");
        this.plugin = plugin;
    }

    public void startGame() {
        for (Player p : world.getPlayers()) {
            p.sendRichMessage("<green><bold>Green light!");
        }
        gameCountdown = gameDuration;
        Bukkit.getScheduler().runTaskLater(plugin, () -> nextStep(), 20);
        Bukkit.getScheduler().runTaskLater(plugin, () -> redLight(), randomToggleTime());
        gameStarted = true;
    }

    public void stopGame() {
        gameStarted = false;
    }

    public int randomToggleTime() {
        return (int) (Math.random() * (maxTimeBetweenToggle - minTimeBetweenToggle)) + minTimeBetweenToggle;
    }

    @Override
    public void nextStep() {
        if (gameStarted) {
            if (!redLight) {
                redlightCountdown--;
                if (redlightCountdown == 0) {
                    redLight();
                }
            } else {
                secondsInRedlight++;
                plugin.logger.info("redlight for " + secondsInRedlight + " seconds!");
            }
            gameCountdown--;
            if (gameCountdown == 0) {
                killAllBehindLine();
                stopGame();
            } else if (gameCountdown % 10 == 0) {
                for (Player p : world.getPlayers()) {
                    p.sendRichMessage("<yellow><bold>" + gameCountdown + " seconds left!");
                }
                Bukkit.getScheduler().runTaskLater(plugin, () -> nextStep(), 20);
            } else {
                Bukkit.getScheduler().runTaskLater(plugin, () -> nextStep(), 20);
            }
        }
    }

    private void redLight() {
        secondsInRedlight = 0;
        worldBroadcast("<red><bold>Red light!");
        this.redLight = true;
        Bukkit.getScheduler().runTaskLater(plugin, () -> executeKillList(), 20);
        if (gameStarted) {
            int delay = randomToggleTime() + 50;
            plugin.logger.info("Green light in " + delay + " ticks!");
            Bukkit.getScheduler().runTaskLater(plugin, () -> greenLight(), delay);
        }
    }

    private void greenLight() {
        worldBroadcast("<green><bold>Green light!");
        redLight = false;
        if (gameStarted) {
            int delay = randomToggleTime();
            plugin.logger.info("Red light in " + delay + " ticks!");
            Bukkit.getScheduler().runTaskLater(plugin, () -> redLight(), delay);
        }
    }

    @Override
    public void prepareGame() {
        determineParticipants();
    }

    public void executeKillList() {

        for (Player p : toKill) {
            int delay = ((int) (Math.random() * 50));
            plugin.logger.info("Killing " + p.getName() + " in " + delay + " ticks!");
            Bukkit.getScheduler().runTaskLater(plugin, () -> killPlayer(p), delay);
        }
        toKill.clear();
    }

    public void killAllBehindLine() {
        for (Player p : participants) {
            if (p.getZ() > LINE_Z && p.getGameMode() == GameMode.ADVENTURE) toKill.add(p);
        }
        executeKillList();
    }

    private void worldBroadcast(String message) {
        for (Player p : world.getPlayers()) {
            p.sendRichMessage(message);
        }
    }

    public void killPlayer(Player p) {
        plugin.logger.info("Killing " + p.getName());

        int cannonIndex = (int) (Math.random() * 10);

        Vector cannonLoc = cannonLocs[cannonIndex];

        world.createExplosion(cannonLoc.getX(), cannonLoc.getY(), cannonLoc.getZ(), 4, false, false);

        world.playSound(p.getLocation(), Sound.ENTITY_PLAYER_DEATH, 1.0f, 1.0f);

        p.setGameMode(GameMode.SPECTATOR);

        worldBroadcast("<bold>" + p.getName() + " has been eliminated!");

    }

    public void testExplosion() {
        int cannonIndex = (int) (Math.random() * 10);

        plugin.logger.info("Firing cannon number " + cannonIndex);

        Vector cannonLoc = cannonLocs[cannonIndex];

        world.createExplosion(cannonLoc.getX(), cannonLoc.getY(), cannonLoc.getZ(), 10, false, false);
    }

    @Override
    public void determineParticipants() {
        List<Player> allPlayers = world.getPlayers();

        this.participants.clear();

        // loop through all players in the world
        // if they are still alive (ie in adventure mode), add them to list of participants
        // teleport all players, no matter of participation status, to the game's spawnpoint
        for (Player p : allPlayers) {
            if (p.getGameMode() == GameMode.ADVENTURE) participants.add(p);
            p.teleport(spawnpoint);
        }

    }

    public void setRedLight(boolean redLight) {
        this.redLight = redLight;
    }

    public boolean getRedLight() {
        return redLight;
    }

    public void addToKillList(Player p) {
        if (!Globals.playerInList(p, toKill)) {
            plugin.logger.info("Adding " + p.getName() + " to the kill list!");
            toKill.add(p);
        }
    }

    public int getSecondsInRedlight() {
        return secondsInRedlight;
    }

}
