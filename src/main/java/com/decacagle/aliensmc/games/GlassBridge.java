package com.decacagle.aliensmc.games;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.games.blocks.GlassBridgeSpace;
import com.decacagle.aliensmc.games.participants.GlassBridgePlayer;
import com.decacagle.aliensmc.utilities.Globals;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GlassBridge extends Game {

    public final long GAME_LOOP = 5L;

    public Location bridgeSpawnpoint;
    public Location vipLoungeSpawnpoint;

    public World world;

    public List<GlassBridgePlayer> players = new ArrayList<GlassBridgePlayer>();
    public GlassBridgeSpace[] spaces = new GlassBridgeSpace[18];

    public double secondsPassed = 0;
    public final int TOTAL_GAME_TIME_SECONDS = 300;

    public GlassBridge(AliensGames plugin, Player host) {
        super(new Location(plugin.getServer().getWorld("squidgame"), 962.5, 96, 1167, -90, 0), plugin, host, 1);
        this.world = spawnpoint.getWorld();
        this.bridgeSpawnpoint = new Location(world, 971, 96, 1202);
        this.vipLoungeSpawnpoint = new Location(world, 1020, 95, 1246.5, 90, 0);
        this.PRETTY_TITLE = "Glass Bridge";
    }

    public void timer() {
        secondsPassed += GAME_LOOP / 20.0;

        if (secondsPassed >= TOTAL_GAME_TIME_SECONDS) {
            gameRunning = false;
        }

        if (secondsPassed % 1 == 0) {
            updateTimer(Component.text("Time Remaining: "), TOTAL_GAME_TIME_SECONDS - ((int) secondsPassed));
        }

        checkPlayerPositions();
        checkGameStatus();

        if (gameRunning) Bukkit.getScheduler().runTaskLater(plugin, this::timer, GAME_LOOP);
    }

    public void checkGameStatus() {

        if (gameRunning && allPlayersCrossedOrEliminated()) {
            broadcastTitleToAllPlayers(Component.text("Game Over!", NamedTextColor.GREEN, TextDecoration.BOLD), Component.text(""));
            endGame();
        } else if (gameRunning && secondsPassed >= TOTAL_GAME_TIME_SECONDS) {
            broadcastTitleToAllPlayers(Component.text("Game Over!", NamedTextColor.GREEN, TextDecoration.BOLD), Component.text("Time is up!"));
            endGame();
        }

    }

    public boolean allPlayersCrossedOrEliminated() {
        boolean check = true;
        for (GlassBridgePlayer p : players) {
            if (!p.crossed && !p.eliminated) {
                check = false;
                break;
            }
        }
        return check;
    }

    public void endGame() {
        if (gameRunning) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> bombBridge(), 60);
            Bukkit.getScheduler().runTaskLater(plugin, () -> goToLeaderboard(), 124*2);
            Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.gameManager.stopGame(), 130*2);

            gameRunning = false;
        }
    }

    private void bombBridge() {

        for (int i = 0; i < spaces.length; i++) {
            GlassBridgeSpace obj = spaces[i];
            Bukkit.getScheduler().runTaskLater(plugin, () -> obj.explode(), i * 3);
        }

    }

    private void replaceBridge() {
        int y = 95;
        int startZ = 1211;

        int rx1 = (int) (GlassBridgeSpace.RIGHT_X1 + 1);
        int rx2 = (int) (GlassBridgeSpace.RIGHT_X2 - 1);

        int lx1 = (int) (GlassBridgeSpace.LEFT_X1 + 1);
        int lx2 = (int) (GlassBridgeSpace.LEFT_X2 - 1);

        // replace end rods
        boolean south = false;

        for (int z = 1209; z <= 1282; z++) {

            Directional end_rod = (Directional) Material.END_ROD.createBlockData();
            end_rod.setFacing(south ? BlockFace.SOUTH : BlockFace.NORTH);
            world.setBlockData(967, 95, z, end_rod);
            world.setBlockData(970, 95, z, end_rod);
            world.setBlockData(971, 95, z, end_rod);
            world.setBlockData(974, 95, z, end_rod);

            south = !south;
        }

        for (int i = 0; i < spaces.length; i++) {
            int z1 = startZ + (i * 4);

            // replace right pane
            world.setBlockData(rx1, y, z1, Material.WHITE_STAINED_GLASS.createBlockData());
            world.setBlockData(rx2, y, z1, Material.WHITE_STAINED_GLASS.createBlockData());
            world.setBlockData(rx1, y, z1 + 1, Material.WHITE_STAINED_GLASS.createBlockData());
            world.setBlockData(rx2, y, z1 + 1, Material.WHITE_STAINED_GLASS.createBlockData());

            // replace left pane
            world.setBlockData(lx1, y, z1, Material.WHITE_STAINED_GLASS.createBlockData());
            world.setBlockData(lx2, y, z1, Material.WHITE_STAINED_GLASS.createBlockData());
            world.setBlockData(lx1, y, z1 + 1, Material.WHITE_STAINED_GLASS.createBlockData());
            world.setBlockData(lx2, y, z1 + 1, Material.WHITE_STAINED_GLASS.createBlockData());
        }
    }

    public void checkPlayerPositions() {

        for (int i = 0; i < players.size(); i++) {
            GlassBridgePlayer p = players.get(i);
            if (p.player.getGameMode() == GameMode.ADVENTURE) {
                if (!p.crossed && !p.eliminated) {
                    for (GlassBridgeSpace space : spaces) {
                        if (space.playerIsOnSpace(p.player)) {
                            if (!p.takenFirstLeap || p.eliminated) p.takenFirstLeap = true;
                            if (!space.playerOnSafeSide(p.player)) {
                                space.shatter();
                            }
                            break;
                        }
                    }

                    if (p.player.getZ() >= 1284) {
                        p.crossed = true;
                        p.timeCrossed = (int) secondsPassed;
                        updatePlayerLine(p);
                    }
                }
            } else {
                if (i != 0) {
                    if (players.get(i - 1).takenFirstLeap) {
                        p.player.teleport(bridgeSpawnpoint);
                        p.player.setGameMode(GameMode.ADVENTURE);
                    }
                }
            }
        }

    }

    public void sortPlayersByTimeCrossed() {
        Collections.sort(players, (o1, o2) -> {
            if (o1.timeCrossed == o2.timeCrossed)
                return 0;
            return o1.timeCrossed < o2.timeCrossed ? 1 : -1;
        });

        for (int i = 0; i < players.size(); i++) {
            GlassBridgePlayer p = players.get(i);
            if (p.crossed) {
                if (i == 0) p.points = 20;
                else if (i == 1) p.points = 15;
                else if (i == 2) p.points = 10;
                else p.points = 5;
            }
        }

    }

    public void goToLeaderboard() {
        sortPlayersByTimeCrossed();

        broadcastMessageToAllPlayers("<underlined><gold><bold>Glass Bridge Results\n");

        List<Player> orderedPlayers = new ArrayList<Player>();

        int numWinners = 0;

        for (int i = 0; i < players.size(); i++) {
            GlassBridgePlayer p = players.get(i);
            orderedPlayers.add(p.player);
            if (p.crossed) {
                numWinners++;
                broadcastMessageToAllPlayers("<green>" + Globals.numberToPosition(i + 1) + ": " + p.player.getName() + " - Crossed in " + Globals.secondsToFormattedTime(p.timeCrossed) + " - " + p.points + " points");
            } else {
                broadcastMessageToAllPlayers("<green>" + Globals.numberToPosition(i + 1) + ": " + p.player.getName() + " - Did not cross - " + p.points + " points");
            }
        }

        Globals.goToLeaderboard(orderedPlayers, world, numWinners, plugin, plugin.congratulationsSong);

        cleanup();

    }

    public void startGame() {
        for (Player p : participants) {
            p.teleport(new Location(world, bridgeSpawnpoint.getX(), bridgeSpawnpoint.getY(), bridgeSpawnpoint.getZ()));
        }

        // assign positions, put all players in spectator until their position comes

        healAll();
        initBridge();
        initPlayers();
        initScoreboard();

        gameRunning = true;
        Bukkit.getScheduler().runTaskLater(plugin, this::timer, GAME_LOOP);
    }

    public void initBridge() {
        int y = 95;
        int startZ = 1211;

        int rx1 = (int) (GlassBridgeSpace.RIGHT_X1 + 1);
        int rx2 = (int) (GlassBridgeSpace.RIGHT_X2 - 1);

        int lx1 = (int) (GlassBridgeSpace.LEFT_X1 + 1);
        int lx2 = (int) (GlassBridgeSpace.LEFT_X2 - 1);

        boolean south = false;

        for (int z = 1209; z <= 1282; z++) {

            Directional end_rod = (Directional) Material.END_ROD.createBlockData();
            end_rod.setFacing(south ? BlockFace.SOUTH : BlockFace.NORTH);
            world.setBlockData(967, 95, z, end_rod);
            world.setBlockData(970, 95, z, end_rod);
            world.setBlockData(971, 95, z, end_rod);
            world.setBlockData(974, 95, z, end_rod);

            south = !south;
        }

        for (int i = 0; i < spaces.length; i++) {
            int z1 = startZ + (i * 4);

            // build right pane (even if it already exists, just in case)
            world.setBlockData(rx1, y, z1, Material.WHITE_STAINED_GLASS.createBlockData());
            world.setBlockData(rx2, y, z1, Material.WHITE_STAINED_GLASS.createBlockData());
            world.setBlockData(rx1, y, z1 + 1, Material.WHITE_STAINED_GLASS.createBlockData());
            world.setBlockData(rx2, y, z1 + 1, Material.WHITE_STAINED_GLASS.createBlockData());

            // build left pane (even if it already exists, just in case)
            world.setBlockData(lx1, y, z1, Material.WHITE_STAINED_GLASS.createBlockData());
            world.setBlockData(lx2, y, z1, Material.WHITE_STAINED_GLASS.createBlockData());
            world.setBlockData(lx1, y, z1 + 1, Material.WHITE_STAINED_GLASS.createBlockData());
            world.setBlockData(lx2, y, z1 + 1, Material.WHITE_STAINED_GLASS.createBlockData());

            int rightSafe = (int) (Math.random() * 2);
            spaces[i] = new GlassBridgeSpace(z1 - 0.3, z1 + 2.3, world, rightSafe == 1);
        }

    }

    public void initPlayers() {
        Collections.shuffle(participants);

        for (int i = 0; i < participants.size(); i++) {
            Player participant = participants.get(i);
            Globals.fullyClearInventory(participant);
            players.add(new GlassBridgePlayer(participant, i));
            String position = Globals.numberToPosition(i + 1);
            NamedTextColor posCol = determineOrderColor(i + 1);

            Component title = Component.text("You are " + position, posCol, TextDecoration.BOLD);
            Component subtitle = Component.text("", NamedTextColor.GOLD);

            participant.showTitle(Title.title(title, subtitle));

            if (i != 0) {
                participant.sendRichMessage("<gold>You are going <bold>" + position + "</bold>. You will not be entered into the game until the person ahead of you has taken their first leap.");
                participant.sendRichMessage("<gold>Until then, you will remain in spectator mode.");
                participant.setGameMode(GameMode.SPECTATOR);
            }

        }

    }

    public void registerElimination(Player eliminated) {
        for (GlassBridgePlayer p : players) {
            if (p.player.getUniqueId().compareTo(eliminated.getUniqueId()) == 0) {
                p.eliminated = true;

                Component title = Component.text("You have been eliminated!", NamedTextColor.RED, TextDecoration.BOLD);
                Component subtitle = Component.text("You can watch the rest of the game from the VIP lounge", NamedTextColor.GOLD);

                p.player.showTitle(Title.title(title, subtitle));

                updatePlayerLine(p);

                if (!p.takenFirstLeap) p.takenFirstLeap = true;

                Bukkit.getScheduler().runTaskLater(plugin, () -> p.player.teleport(vipLoungeSpawnpoint), 10); // wont teleport immediately for whatever reason, so im adding a half second delay

            }
        }
    }

    public NamedTextColor determineOrderColor(int order) {
        NamedTextColor ret = NamedTextColor.RED;
        if (order == 1) return ret;
        int divided = participants.size() / 3;
        if (order >= divided) ret = NamedTextColor.YELLOW;
        if (order >= (divided * 2)) ret = NamedTextColor.GREEN;

        return ret;
    }

    public void initScoreboard() {
        createScoreboardWithTimer(PRETTY_TITLE);
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

    public void updatePlayerLine(GlassBridgePlayer player) {

        Team playerLine = playerLines.get(player.player);

        if (player.crossed) {
            playerLine.suffix(Component.text("⭐", NamedTextColor.GOLD, TextDecoration.BOLD));
        } else if (player.eliminated) {
            playerLine.suffix(Component.text("✘", NamedTextColor.RED, TextDecoration.BOLD));
        } else {
            playerLine.suffix(Component.text("✔", NamedTextColor.GREEN, TextDecoration.BOLD));
        }
    }

    public void cleanup() {
        replaceBridge();
        removeScoreboard();
    }

}
