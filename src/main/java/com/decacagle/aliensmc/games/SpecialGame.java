package com.decacagle.aliensmc.games;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.games.participants.SpecialGamePlayer;
import com.decacagle.aliensmc.utilities.Globals;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpecialGame extends Game {

    public boolean lightsOn = true;

    public List<SpecialGamePlayer> players = new ArrayList<SpecialGamePlayer>();
    public List<Location> placedBlockLocations = new ArrayList<Location>();

    public int timeBeforeStart;

    // set to true after start period
    // pvp disabled when this is false
    public boolean gameStarted = false;

    public int secondsPassed = 0;
    public int gameDurationSeconds;

    public SpecialGame(AliensGames plugin, Player host) {
        super(new Location(plugin.getServer().getWorld(plugin.config.gameWorldTitleSG), plugin.config.spawnpointXSG, plugin.config.spawnpointYSG, plugin.config.spawnpointZSG, (float) plugin.config.spawnpointYawSG, (float) plugin.config.spawnpointPitchSG), plugin, host, plugin.config.minimumPlayersSG);
        this.world = spawnpoint.getWorld();
        this.gameDurationSeconds = plugin.config.gameDurationSecondsSG;
        this.timeBeforeStart = plugin.config.timeBeforeStartSecondsSG;
        this.prettyTitle = plugin.config.prettyTitleSG;
    }

    public void timer() {

        if (gameRunning) {
            secondsPassed++;
            updateTimer(Component.text("Time Remaining: "), gameDurationSeconds - secondsPassed);
            checkGameStatus();
            Bukkit.getScheduler().runTaskLater(plugin, this::timer, 20);
        }

    }

    public void startGame() {
        this.gameRunning = true;
        healAll();
        initPlayers();
        givePlayersEquipment();
        setGamemode();
        initScoreboard();
        queueGameStart();
    }

    public void queueGameStart() {
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
            Bukkit.getScheduler().runTaskLater(plugin, this::removeEndRods, 10);
            Bukkit.getScheduler().runTaskLater(plugin, this::removeSeaLanterns, 20);
            Bukkit.getScheduler().runTaskLater(plugin, this::removeLightBlocks, 30);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                playSoundToAllPlayers(Sound.AMBIENT_CAVE, 1.5f, 1.0f);
                applyDarknessEffectForAll();
                gameStarted = true;
            }, 40);
            Bukkit.getScheduler().runTaskLater(plugin, this::timer, 20);
        }, (timeBeforeStart * 20));

    }

    public void checkGameStatus() {
        if (gameRunning) {
            int remainingPlayers = playersAlive();

            if (remainingPlayers <= (plugin.config.debugMode ? 0 : 1) || secondsPassed >= gameDurationSeconds) {
                endGame();
            }
        }
    }

    public void endGame() {
        gameRunning = false;

        givePointsToRemainingPlayers();

        broadcastTitleToAllPlayers(Component.text("Game Over!", NamedTextColor.GREEN), Component.text(""));
        removeDarknessEffectForAll();
        removeNightVisionEffectForAll();

        // three seconds delay
        Bukkit.getScheduler().runTaskLater(plugin, this::goToLeaderboard, 60);

    }

    public List<SpecialGamePlayer> sortPlayersByPoints() {
        List<SpecialGamePlayer> allPlayers = new ArrayList<SpecialGamePlayer>();
        allPlayers.addAll(players);

        Collections.sort(allPlayers, (o1, o2) -> {
            if (o1.points == o2.points)
                return 0;
            return o1.points < o2.points ? 1 : -1;
        });

        return allPlayers;

    }

    public void givePointsToRemainingPlayers() {
        for (SpecialGamePlayer p : players) {
            if (!p.eliminated && p.connected) {
                p.player.sendRichMessage("<gold>Survived until the end!");
                p.player.sendRichMessage("<bold><green>You've earned $5.00!");
                p.points += 5;
            }
        }
    }

    public void goToLeaderboard() {

        List<SpecialGamePlayer> finalResults = sortPlayersByPoints();

        List<Player> orderedPlayers = new ArrayList<Player>();

        int numWinners = 0;

        broadcastMessageToAllPlayers("<underlined><green><bold>Special Game Rankings\n");

        for (int i = 0; i < finalResults.size(); i++) {
            SpecialGamePlayer p = finalResults.get(i);
            Player player = p.player;
            player.setGameMode(GameMode.ADVENTURE);
            Globals.fullyClearInventory(player);

            if (p.points > 0 && !p.eliminated) numWinners++;
            orderedPlayers.add(player);

            String numberColor = i == 0 ? ("<#D4AF37>") : (i == 1 ? ("<#C0C0C0>") : (i == 2 ? ("<#CD7F32>") : ("<gray>")));

            if (p.eliminated) {
                broadcastMessageToAllPlayers(numberColor + "<bold>" + Globals.numberToPosition(i + 1) + ": <white>" + p.player.getName() + " - <red><bold>Eliminated</bold></red><white> - " + p.kills + " Kill" + (p.kills > 1 ? "s" : "") + " - $" + p.points);
                plugin.economy.depositPlayer(p.player, p.points);
            } else {
                broadcastMessageToAllPlayers(numberColor + "<bold>" + Globals.numberToPosition(i + 1) + ": <white>" + p.player.getName() + " - <green><bold>Survived</bold></green><white> - " + p.kills + " Kill" + (p.kills > 1 ? "s" : "") + " - $" + p.points);
                plugin.economy.depositPlayer(p.player, p.points);
            }
        }

        broadcastMessageToAllPlayers(" ");

        plugin.gameManager.stopGame();

        Globals.goToLeaderboard(orderedPlayers, world, numWinners, plugin, plugin.congratulationsSong);

    }

    public int playersAlive() {
        int ret = 0;
        for (SpecialGamePlayer p : players) {
            if (!p.eliminated && p.connected) ret++;
        }
        return ret;
    }

    public void initPlayers() {
        for (Player p : participants) {
            players.add(new SpecialGamePlayer(p));
        }
    }

    public void givePlayersEquipment() {
        ItemStack woodenSword = new ItemStack(Material.WOODEN_SWORD);
        ItemStack walls = new ItemStack(Material.POLISHED_BLACKSTONE_BRICK_WALL, 5);

        for (Player p : participants) {
            p.getInventory().addItem(woodenSword);
            p.getInventory().addItem(walls);
        }

    }

    public void setGamemode() {
        for (Player p : participants) {
            p.setGameMode(GameMode.SURVIVAL);
        }
    }

    public void applyDarknessEffectForAll() {
        for (Player p : participants) {
            p.addPotionEffect(new PotionEffect(
                    PotionEffectType.DARKNESS,
                    20 * gameDurationSeconds,
                    2
            ));
        }
    }

    public void applyNightVisionEffect(SpecialGamePlayer player) {
        for (Player p : participants) {
            p.addPotionEffect(new PotionEffect(
                    PotionEffectType.NIGHT_VISION,
                    20 * gameDurationSeconds,
                    2
            ));
        }
    }

    public void removeDarknessEffect(Player player) {
        player.removePotionEffect(PotionEffectType.DARKNESS);
    }

    public void removeDarknessEffectForAll() {
        for (Player p : participants) {
            p.removePotionEffect(PotionEffectType.DARKNESS);
        }
    }

    public void removeNightVisionEffectForAll() {
        for (Player p : participants) {
            p.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }

    public void toggleLights() {
        if (lightsOn) {
            Bukkit.getScheduler().runTaskLater(plugin, this::removeEndRods, 10);
            Bukkit.getScheduler().runTaskLater(plugin, this::removeSeaLanterns, 20);
            Bukkit.getScheduler().runTaskLater(plugin, this::removeLightBlocks, 30);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, this::replaceEndRods, 10);
            Bukkit.getScheduler().runTaskLater(plugin, this::replaceSeaLanterns, 20);
            Bukkit.getScheduler().runTaskLater(plugin, this::replaceLightBlocks, 30);
        }

        lightsOn = !lightsOn;
    }

    public void removeEndRods() {

        // the four rows of rods
        for (int x = 804; x <= 807; x++) {
            Location rod1 = new Location(world, x, 24, 1154);
            Location rod2 = new Location(world, x, 24, 1157);
            Location rod3 = new Location(world, x, 24, 1160);
            Location rod4 = new Location(world, x, 24, 1163);

            rod1.getBlock().setType(Material.AIR);
            rod2.getBlock().setType(Material.AIR);
            rod3.getBlock().setType(Material.AIR);
            rod4.getBlock().setType(Material.AIR);

        }

        // the two rows hanging from chains
        for (int x = 799; x <= 800; x++) {
            Location rod1 = new Location(world, x, 24, 1157);
            Location rod2 = new Location(world, x, 24, 1160);

            rod1.getBlock().setType(Material.AIR);
            rod2.getBlock().setType(Material.AIR);

        }

    }

    public void removeSeaLanterns() {

        // two vertical pieces of arch
        for (int y = 19; y <= 23; y++) {
            Location lantern1 = new Location(world, 803, y, 1155);
            Location lantern2 = new Location(world, 803, y, 1162);

            lantern1.getBlock().setType(Material.AIR);
            lantern2.getBlock().setType(Material.AIR);

        }

        // top row
        for (int z = 1155; z <= 1162; z++) {

            Location lantern = new Location(world, 803, 24, z);

            lantern.getBlock().setType(Material.AIR);

        }

        // lanterns over doors at front
        for (int z = 0; z < 3; z++) {
            Location northLantern = new Location(world, 798, 18, 1138 + z);
            Location southLantern = new Location(world, 798, 19, 1177 + z);

            northLantern.getBlock().setType(Material.AIR);
            southLantern.getBlock().setType(Material.AIR);

        }

        // north and south lanterns on walls
        for (int i = 0; i < 5; i++) {
            for (int l = 0; l < 3; l++) {
                int x = 786 - (i * 8) - l;

                Location northLantern = new Location(world, x, 33, 1126);
                Location southLantern = new Location(world, x, 33, 1191);

                northLantern.getBlock().setType(Material.AIR);
                southLantern.getBlock().setType(Material.AIR);

            }
        }

        // western wall of lanterns
        for (int i = 0; i < 5; i++) {
            for (int l = 0; l < 3; l++) {
                int z = 1176 - (i * 8) - l;

                Location lantern = new Location(world, 737, 33, z);

                lantern.getBlock().setType(Material.AIR);

            }
        }

    }

    public void removeLightBlocks() {

        for (int y = 16; y <= 44; y++) {
            int finalY = y;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (int z = 1127; z <= 1190; z++) {
                    for (int x = 738; x <= 797; x++) {

                        Location l = new Location(world, x, finalY, z);
                        Block b = l.getBlock();

                        if (b.getType() == Material.LIGHT) {
                            b.setType(Material.AIR);
                        }

                    }
                }
            }, 20);
        }

    }

    public void replaceEndRods() {
        boolean east = true;

        Directional eastFacingRod = (Directional) Material.END_ROD.createBlockData();
        eastFacingRod.setFacing(BlockFace.EAST);

        Directional westFacingRod = (Directional) Material.END_ROD.createBlockData();
        westFacingRod.setFacing(BlockFace.WEST);

        for (int x = 804; x <= 807; x++) {
            Location rod1 = new Location(world, x, 24, 1154);
            Location rod2 = new Location(world, x, 24, 1157);
            Location rod3 = new Location(world, x, 24, 1160);
            Location rod4 = new Location(world, x, 24, 1163);

            rod1.getBlock().setBlockData(east ? eastFacingRod : westFacingRod);
            rod2.getBlock().setBlockData(east ? eastFacingRod : westFacingRod);
            rod3.getBlock().setBlockData(east ? eastFacingRod : westFacingRod);
            rod4.getBlock().setBlockData(east ? eastFacingRod : westFacingRod);

            east = !east;

        }

        east = true;

        // the two rows hanging from chains
        for (int x = 799; x <= 800; x++) {
            Location rod1 = new Location(world, x, 24, 1157);
            Location rod2 = new Location(world, x, 24, 1160);

            rod1.getBlock().setBlockData(east ? eastFacingRod : westFacingRod);
            rod2.getBlock().setBlockData(east ? eastFacingRod : westFacingRod);

            east = !east;

        }


    }

    public void replaceSeaLanterns() {

        // two vertical pieces of arch
        for (int y = 19; y <= 23; y++) {
            Location lantern1 = new Location(world, 803, y, 1155);
            Location lantern2 = new Location(world, 803, y, 1162);

            lantern1.getBlock().setType(Material.SEA_LANTERN);
            lantern2.getBlock().setType(Material.SEA_LANTERN);

        }

        // top row
        for (int z = 1155; z <= 1162; z++) {

            Location lantern = new Location(world, 803, 24, z);

            lantern.getBlock().setType(Material.SEA_LANTERN);

        }

        // lanterns over doors at front
        for (int z = 0; z < 3; z++) {
            Location northLantern = new Location(world, 798, 18, 1138 + z);
            Location southLantern = new Location(world, 798, 19, 1177 + z);

            northLantern.getBlock().setType(Material.SEA_LANTERN);
            southLantern.getBlock().setType(Material.SEA_LANTERN);

        }

        // north and south lanterns on walls
        for (int i = 0; i < 5; i++) {
            for (int l = 0; l < 3; l++) {
                int x = 786 - (i * 8) - l;

                Location northLantern = new Location(world, x, 33, 1126);
                Location southLantern = new Location(world, x, 33, 1191);

                northLantern.getBlock().setType(Material.SEA_LANTERN);
                southLantern.getBlock().setType(Material.SEA_LANTERN);

            }
        }

        // western wall of lanterns
        for (int i = 0; i < 5; i++) {
            for (int l = 0; l < 3; l++) {
                int z = 1176 - (i * 8) - l;

                Location lantern = new Location(world, 737, 33, z);

                lantern.getBlock().setType(Material.SEA_LANTERN);

            }
        }

    }

    public void replaceLightBlocks() {
        Light level14Light = (Light) Material.LIGHT.createBlockData();
        level14Light.setLevel(14);

        for (int y = 16; y <= 44; y++) {
            int finalY = y;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (int z = 1127; z <= 1190; z++) {
                    for (int x = 738; x <= 797; x++) {

                        Location l = new Location(world, x, finalY, z);
                        Block b = l.getBlock();

                        if (b.getType() == Material.AIR) {
                            b.setBlockData(level14Light);
                        }

                    }
                }
            }, 10);
        }

    }

    public void removePlacedBlocks() {
        for (Location l : placedBlockLocations) {
            if (l.getY() == 16) l.getBlock().setType(Material.SNOW);
            else l.getBlock().setType(Material.AIR);
        }
    }

    public void cleanup() {
        Bukkit.getScheduler().runTaskLater(plugin, this::replaceEndRods, 10);
        Bukkit.getScheduler().runTaskLater(plugin, this::replaceSeaLanterns, 20);
        Bukkit.getScheduler().runTaskLater(plugin, this::replaceLightBlocks, 30);
        removeDarknessEffectForAll();
        removeNightVisionEffectForAll();
        if (this.scoreboard != null) {
            removeScoreboard();
            showNameTags();
        }
        removePlacedBlocks();
    }

    public void updatePlayerLine(SpecialGamePlayer player) {

        Team playerLine = playerLines.get(player.player);

        if (player.eliminated) {
            playerLine.suffix(Component.text("✘", NamedTextColor.RED, TextDecoration.BOLD));
        } else {
            playerLine.suffix(Component.text("✔", NamedTextColor.GREEN, TextDecoration.BOLD));
        }

        if (!player.connected) {
            playerLine.prefix(Component.text(player.player.getName() + ": ", NamedTextColor.DARK_GRAY));
        }

    }

    public void hideNameTags() {
        Team team = scoreboard.getTeam("hiddenNames");
        if (team == null) {
            team = scoreboard.registerNewTeam("hiddenNames");
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }

        for (Player player : participants) {
            team.addEntry(player.getName());
        }
    }

    public void showNameTags() {
        Team team = scoreboard.getTeam("hiddenNames");

        if (team != null) {
            for (Player player : participants) {
                team.removeEntry(player.getName());
            }
        }
    }

    public void initScoreboard() {
        createScoreboardWithTimer(prettyTitle);

        hideNameTags();

        for (int i = 0; i < participants.size(); i++) {
            Player player = participants.get(i);

            Team playerLine = createScoreboardLine(scoreboard, scoreboardObjective, "" + i, -i, false);
            playerLines.put(player, playerLine);

            playerLine.prefix(Component.text(player.getName() + ": "));
            playerLine.suffix(Component.text("✔", NamedTextColor.GREEN, TextDecoration.BOLD));

        }

    }

    public void reportPlayerDeparture(Player player) {
        participants.remove(player);
        removeDarknessEffect(player);
        removeFromScoreboard(player);

        Globals.fullyClearInventory(player);

        player.teleport(world.getSpawnLocation());

        if (gameRunning) {

            SpecialGamePlayer sgPlayer = null;

            for (SpecialGamePlayer p : players) {
                if (p.player.getUniqueId().compareTo(player.getUniqueId()) == 0) {
                    sgPlayer = p;
                    players.remove(p);
                    break;
                }
            }

            if (sgPlayer != null) {

                sgPlayer.eliminated = true;
                sgPlayer.connected = false;
                sgPlayer.points = 0;

                updatePlayerLine(sgPlayer);

            } else {
                plugin.logger.severe("Tried to find " + player.getName() + " for a departure report, but couldn't find them!");
            }

        }

        broadcastMessageToAllPlayers("<red>" + player.getName() + " has left your mini-game!");

        if (participants.isEmpty()) {
            plugin.gameManager.forceStop();
        }

    }

    public void registerBlockPlace(Location location) {
        placedBlockLocations.add(location);
    }

    public void registerElimination(Player eliminated) {

        SpecialGamePlayer sgPlayer = null;

        for (SpecialGamePlayer p : players) {
            if (p.player.getUniqueId().compareTo(eliminated.getUniqueId()) == 0) {

                sgPlayer = p;

                break;

            }
        }

        if (sgPlayer != null) {
            sgPlayer.eliminated = true;

            Component title = Component.text("You have been eliminated!", NamedTextColor.RED, TextDecoration.BOLD);
            Component subtitle = Component.text("You are now a spectator until the end of the game", NamedTextColor.GOLD);

            sgPlayer.player.showTitle(Title.title(title, subtitle));

            applyNightVisionEffect(sgPlayer);
            removeDarknessEffect(sgPlayer.player);

            // broadcast killed announcement
            broadcastMessageToAllPlayers("<red><bold>Player " + eliminated.getName() + " has been eliminated!");

            updatePlayerLine(sgPlayer);

        }

        checkGameStatus();
    }

    public void registerKill(Player killed, Player killer) {
        SpecialGamePlayer killerSGP = null;
        SpecialGamePlayer killedSGP = null;

        for (SpecialGamePlayer p : players) {
            if (p.player.getUniqueId().compareTo(killer.getUniqueId()) == 0) {
                killerSGP = p;
            } else if (p.player.getUniqueId().compareTo(killed.getUniqueId()) == 0) {
                killedSGP = p;
            }
        }

        if (killedSGP != null && killerSGP != null) {

            // Show title to killed player
            Component title = Component.text("You have been eliminated!", NamedTextColor.RED, TextDecoration.BOLD);
            Component subtitle = Component.text("You are now a spectator until the end of the game", NamedTextColor.GOLD);
            killed.showTitle(Title.title(title, subtitle));

            applyNightVisionEffect(killedSGP);
            removeDarknessEffect(killed);

            // broadcast killed announcement
            broadcastMessageToAllPlayers("<red><bold>Player " + killed.getName() + " has been eliminated!");

            // give points
            killedSGP.eliminated = true;

            killerSGP.kills++;
            killerSGP.killedPlayers.add(killed);
            killerSGP.points += 5;

            killer.sendRichMessage("<gold>You killed " + killed.getName() + "!");
            killer.sendRichMessage("<bold><green>You've earned $5.00!");

            updatePlayerLine(killedSGP);
            updatePlayerLine(killerSGP);

        } else if (killedSGP == null) {
            plugin.logger.severe("Unable to find " + killed.getName() + " in either list!");
        } else {
            plugin.logger.severe("Unable to find " + killer.getName() + " in either list!");
        }

        checkGameStatus();

    }

}
