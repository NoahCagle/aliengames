package com.decacagle.aliensmc.games;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.games.participants.HideAndSeekPlayer;
import com.decacagle.aliensmc.utilities.Globals;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HideAndSeek extends Game {

    public static Vector ESCAPE_POINT = new Vector(407, 227, 992);

    public static Vector[] KEY_LOCATIONS = new Vector[]{
            new Vector(270, 227, 1051),
            new Vector(252, 227, 1069),
            new Vector(252, 227, 1028),
            new Vector(263, 227, 1016),
            new Vector(283, 227, 1014),
            new Vector(279, 227, 994),
            new Vector(314, 227, 961),
            new Vector(318, 227, 953),
            new Vector(330, 227, 956),
            new Vector(251, 227, 928),
    };
    public List<Location> usedKeyLocations = new ArrayList<Location>();
    public List<Location> unlockedDoorsLocations = new ArrayList<Location>();

    public List<HideAndSeekPlayer> hiders = new ArrayList<HideAndSeekPlayer>();
    public List<HideAndSeekPlayer> seekers = new ArrayList<HideAndSeekPlayer>();

    public Location mapLoc;
    public Location leaderboardLoc, firstPlaceLoc, secondPlaceLoc, thirdPlaceLoc;

    public boolean seekersSpawnedIn = false;

    public int secondsPassed = 0;
    public final int SEEKER_SPAWN_TIME_SECONDS = 30;
    public final int TOTAL_GAME_TIME_SECONDS = 300;

    public HideAndSeek(AliensGames plugin, Player host) {
        super(new Location(plugin.getServer().getWorld("squidgame"), 183, 193, 1053, 90, 0), plugin, host, 2);
        this.mapLoc = new Location(spawnpoint.getWorld(), 247.5, 227, 994.5, -90, 0);
        this.leaderboardLoc = new Location(spawnpoint.getWorld(), 185, 191, 1119, 180, 0);
        this.firstPlaceLoc = new Location(spawnpoint.getWorld(), 185, 193, 1110, 0, 0);
        this.secondPlaceLoc = new Location(spawnpoint.getWorld(), 183, 192, 1110, 180, 0);
        this.thirdPlaceLoc = new Location(spawnpoint.getWorld(), 187, 191, 1110, 180, 0);
        this.PRETTY_TITLE = "Hide and Seek";
    }

    public void timer() {
        secondsPassed++;

        if (secondsPassed >= SEEKER_SPAWN_TIME_SECONDS && !seekersSpawnedIn) {
            for (HideAndSeekPlayer p : seekers) {
                p.player.teleport(mapLoc);
            }
            broadcastMessageToAllPlayers("<red><bold>The seekers have entered the map!");
            seekersSpawnedIn = true;
        }

        if (gameRunning) {
            if ((TOTAL_GAME_TIME_SECONDS - secondsPassed) % 60 == 0) {
                int minutes = (TOTAL_GAME_TIME_SECONDS - secondsPassed) / 60;
                if (minutes > 1)
                    broadcastMessageToAllPlayers("<gold><bold>" + minutes + " minutes remain!");
                else
                    broadcastMessageToAllPlayers("<gold><bold>" + minutes + " minute remains!");
            }

            checkGameStatus();

            Bukkit.getScheduler().runTaskLater(plugin, this::timer, 20);

        } else {
            showNameTags();
            goToLeaderboard();
        }

    }

    public void givePointsToRemainingHiders() {
        for (HideAndSeekPlayer p : hiders) {
            if (!p.eliminiated && !p.escaped) {
                p.player.sendRichMessage("<gold>Survived until the end!");
                p.player.sendRichMessage("<bold><green>+5 points!");
                p.points += 5;
            }
        }
    }

    public List<HideAndSeekPlayer> sortPlayersByPoints() {
        List<HideAndSeekPlayer> allPlayers = new ArrayList<HideAndSeekPlayer>();
        allPlayers.addAll(hiders);
        allPlayers.addAll(seekers);

        Collections.sort(allPlayers, (o1, o2) -> {
            if (o1.points == o2.points)
                return 0;
            return o1.points < o2.points ? 1 : -1;
        });

        return allPlayers;

    }

    public void goToLeaderboard() {
        removeKeyChests();
        closeOpenedDoors();

        givePointsToRemainingHiders();

        List<HideAndSeekPlayer> finalResults = sortPlayersByPoints();

        List<Player> orderedPlayers = new ArrayList<Player>();

        int numWinners = 0;

        broadcastMessageToAllPlayers("<underlined><gold><bold>Hide And Seek Results\n");

        for (int i = 0; i < finalResults.size(); i++) {
            HideAndSeekPlayer p = finalResults.get(i);
            Player player = p.player;
            player.setGameMode(GameMode.ADVENTURE);
            Globals.fullyClearInventory(player);

            if (p.points > 0) numWinners++;
            orderedPlayers.add(player);

            broadcastMessageToAllPlayers("<green>" + (i + 1) + ": " + p.player.getName() + " - " + p.points + " points");
        }

        Globals.goToLeaderboard(orderedPlayers, world, numWinners, plugin, plugin.congratulationsSong);

    }

    public void checkGameStatus() {

        if (allHidersEscapedOrEliminated()) {
            if (anyHidersEscaped()) {
                gameRunning = false;
                // hiders win
                broadcastTitleToAllPlayers(Component.text("Game Over!", NamedTextColor.GREEN, TextDecoration.BOLD), Component.text(""));
                plugin.gameManager.stopGame();
            } else {
                gameRunning = false;
                // seekers win
                broadcastTitleToAllPlayers(Component.text("Game Over!", NamedTextColor.GREEN, TextDecoration.BOLD), Component.text(""));
                plugin.gameManager.stopGame();
            }
//        } else if (allSeekersEliminated()) {
//            gameRunning = false;
//            // hiders win
//            broadcastTitleToAllPlayers(Component.text("Game Over!", NamedTextColor.GREEN, TextDecoration.BOLD), Component.text(""));
//            plugin.gameManager.stopGame();
        } else if (secondsPassed >= TOTAL_GAME_TIME_SECONDS && gameRunning) {
            gameRunning = false;
            // seekers win
            broadcastTitleToAllPlayers(Component.text("Game Over!", NamedTextColor.GREEN, TextDecoration.BOLD), Component.text(""));
            plugin.gameManager.stopGame();
        }

    }

    public boolean allHidersEscapedOrEliminated() {
        for (HideAndSeekPlayer p : hiders) {
            if (!p.eliminiated && !p.escaped) return false;
        }
        return true;
    }

    public boolean allSeekersEliminated() {
        for (HideAndSeekPlayer p : seekers) {
            if (!p.eliminiated) return false;
        }
        return true;
    }

    public boolean anyHidersEscaped() {
        for (HideAndSeekPlayer p : hiders) {
            if (p.escaped) return true;
        }
        return false;
    }

    public void prepareGame() {
        host.teleport(spawnpoint);
    }

    public void startGame() {

        hideNameTags();
        healAll();
        spawnKeyChests();

        gameRunning = true;

        // go through list of participants, assign every other player to be hiders, and the rest as seekers
        boolean hider = true;
        for (Player p : participants) {
            if (hider) hiders.add(new HideAndSeekPlayer(p, false));
            else seekers.add(new HideAndSeekPlayer(p, true));
            hider = !hider;
        }

        // assign hiders their role
        for (HideAndSeekPlayer p : hiders) {
            assignHiderRole(p.player);
        }

        // assign seekers their role
        for (HideAndSeekPlayer p : seekers) {
            assignSeekerRole(p.player);
        }

        // start game timer
        Bukkit.getScheduler().runTaskLater(plugin, () -> timer(), 20);

        for (Player p : participants) {
            p.sendRichMessage("<gold><bold>The game has started!");
            p.sendRichMessage("<green>Hiders will have 30 seconds to hide before the seekers enter the map");
            p.sendRichMessage("<green>Good luck!");
        }

    }

    public void assignHiderRole(Player player) {
        Component title = Component.text("You are a Hider", NamedTextColor.GREEN, TextDecoration.BOLD);
        Component subtitle = Component.text("Stay hidden from the seekers for as long as possible", NamedTextColor.GOLD);

        player.showTitle(Title.title(title, subtitle));

        // teleport hiders to map immediately
        player.teleport(mapLoc);

        // clear player's inventory
        Globals.fullyClearInventory(player);

        // give hiders their chestplate
        ItemStack hiderChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta hiderChestplateMeta = (LeatherArmorMeta) hiderChestplate.getItemMeta();
        hiderChestplateMeta.setColor(Color.BLUE);
        hiderChestplateMeta.displayName(Component.text("Hider's Chestplate"));
        hiderChestplate.setItemMeta(hiderChestplateMeta);

        player.getInventory().setChestplate(hiderChestplate);

    }

    public void assignSeekerRole(Player player) {
        Component title = Component.text("You are a Seeker", NamedTextColor.RED, TextDecoration.BOLD);
        Component subtitle = Component.text("Search for Hiders and eliminate them", NamedTextColor.GOLD);

        player.showTitle(Title.title(title, subtitle));

        // clear players inventory
        Globals.fullyClearInventory(player);

        // give seekers their chestplate
        ItemStack seekerChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta seekerChestplateMeta = (LeatherArmorMeta) seekerChestplate.getItemMeta();
        seekerChestplateMeta.setColor(Color.RED);
        seekerChestplateMeta.displayName(Component.text("Seeker's Chestplate"));
        seekerChestplate.setItemMeta(seekerChestplateMeta);

        player.getInventory().setChestplate(seekerChestplate);

        // give seekers their knife
        ItemStack seekerKnife = new ItemStack(Globals.DAGGER_TYPE);
        ItemMeta seekerKnifeMeta = seekerKnife.getItemMeta();

        seekerKnifeMeta.displayName(Component.text(Globals.DAGGER_NAME)
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false));

        seekerKnife.setItemMeta(seekerKnifeMeta);

        player.getInventory().addItem(seekerKnife);

    }

    public void registerEscape(Player escapee) {
        boolean foundPlayer = false;

        for (HideAndSeekPlayer p : hiders) {
            if (p.player.getUniqueId().compareTo(escapee.getUniqueId()) == 0) {
                p.escaped = true;
                p.escapeTime = secondsPassed;
                p.player.setGameMode(GameMode.SPECTATOR);

                Component title = Component.text("You have escaped!", NamedTextColor.GREEN, TextDecoration.BOLD);
                Component subtitle = Component.text("You are now a spectator until the end of the game", NamedTextColor.GOLD);

                p.player.showTitle(Title.title(title, subtitle));

                p.player.sendRichMessage("<gold>You have escaped!");
                p.player.sendRichMessage("<bold><green>+10 points!");
                p.points += 10;

                foundPlayer = true;
                break;

            }
        }

        if (!foundPlayer) {
            plugin.logger.severe("Attempted to register the escape of " + escapee.getName() + " but was unable to find the player in the list of hiders!");
        } else {
            broadcastMessageToAllPlayers("<green><bold>" + escapee.getName() + " has escaped!");
        }

        checkGameStatus();

    }

    public void registerElimination(Player eliminated) {
        boolean foundPlayer = false;

        HideAndSeekPlayer hnsPlayer = null;

        for (HideAndSeekPlayer p : hiders) {
            if (p.player.getUniqueId().compareTo(eliminated.getUniqueId()) == 0) {

                hnsPlayer = p;

                foundPlayer = true;
                break;

            }
        }

        if (!foundPlayer) {
            for (HideAndSeekPlayer p : seekers) {
                if (p.player.getUniqueId().compareTo(eliminated.getUniqueId()) == 0) {

                    hnsPlayer = p;

                    foundPlayer = true;
                    break;

                }
            }
            if (!foundPlayer) {
                plugin.logger.severe("Attempted to register the elimination of " + eliminated.getName() + " but was unable to find the player in the list of hiders!");
            }
        }

        if (hnsPlayer != null) {
            hnsPlayer.eliminiated = true;

            Component title = Component.text("You have been eliminated!", NamedTextColor.RED, TextDecoration.BOLD);
            Component subtitle = Component.text("You are now a spectator until the end of the game", NamedTextColor.GOLD);

            hnsPlayer.player.showTitle(Title.title(title, subtitle));

            // broadcast killed announcement
            broadcastMessageToAllPlayers("<red><bold>" + eliminated.getName() + " has been eliminated!");

        }

        checkGameStatus();
    }

    public void registerKill(Player killed, Player killer) {
        HideAndSeekPlayer killerHNSP = null;
        HideAndSeekPlayer killedHNSP = null;

        for (HideAndSeekPlayer p : seekers) {
            if (p.player.getUniqueId().compareTo(killer.getUniqueId()) == 0) {
                killerHNSP = p;
            } else if (p.player.getUniqueId().compareTo(killed.getUniqueId()) == 0) {
                killedHNSP = p;
            }
        }

        for (HideAndSeekPlayer p : hiders) {
            if (p.player.getUniqueId().compareTo(killer.getUniqueId()) == 0) {
                killerHNSP = p;
            } else if (p.player.getUniqueId().compareTo(killed.getUniqueId()) == 0) {
                killedHNSP = p;
            }
        }

        if (killedHNSP != null && killerHNSP != null) {

            // Show title to killed player
            Component title = Component.text("You have been eliminated!", NamedTextColor.RED, TextDecoration.BOLD);
            Component subtitle = Component.text("You are now a spectator until the end of the game", NamedTextColor.GOLD);
            killed.showTitle(Title.title(title, subtitle));

            // broadcast killed announcement
            broadcastMessageToAllPlayers("<red><bold>" + killed.getName() + " has been eliminated!");

            // give points
            killedHNSP.eliminiated = true;

            killerHNSP.kills++;
            killerHNSP.killedPlayers.add(killed);

            if (killedHNSP.seeker && !killerHNSP.seeker) {
                killer.sendRichMessage("<gold>You killed a seeker!");
                killer.sendRichMessage("<bold><green>+20 points!");
                killerHNSP.points += 20;
            } else if (!killedHNSP.seeker && !killerHNSP.seeker) {
                killer.sendRichMessage("<gold>You killed a fellow hider!");
                killer.sendRichMessage("<bold><green>+5 points!");
                killerHNSP.points += 5;
            } else {
                killer.sendRichMessage("<gold>You killed a hider!");
                killer.sendRichMessage("<bold><green>+5 points!");
                killerHNSP.points += 5;
            }

        } else if (killedHNSP == null) {
            plugin.logger.severe("Unable to find " + killed.getName() + " in either list!");
        } else {
            plugin.logger.severe("Unable to find " + killer.getName() + " in either list!");
        }

        checkGameStatus();

    }

    public boolean playerIsHider(Player player) {
        for (HideAndSeekPlayer p : hiders) {
            if (p.player.getUniqueId().compareTo(player.getUniqueId()) == 0) {

                return true;

            }
        }
        return false;
    }

    public void hideNameTags() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

        Team team = board.getTeam("hiddenNames");
        if (team == null) {
            team = board.registerNewTeam("hiddenNames");
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }

        for (Player player : participants) {
            team.addEntry(player.getName());
        }
    }

    public void showNameTags() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = board.getTeam("hiddenNames");

        if (team != null) {
            for (Player player : participants) {
                team.removeEntry(player.getName());
            }
        }
    }

    public void healAll() {
        for (Player p : participants) {
            p.setHealth(20);
            p.setFoodLevel(20);
        }
    }

    public void spawnKeyChests() {
        ItemStack purpleKey = getPurpleKey();
        ItemStack tielKey = getTielKey();
        ItemStack brownKey = getBrownKey();

        // tracking locations so two key locations arent used at the same time
        // its unlikely, but possible
        int[] usedIndex = new int[]{-1, -1, -1};

        for (int i = 0; i < 3; i++) {
            int index = (int) (Math.random() * KEY_LOCATIONS.length);

            while (index == usedIndex[0] || index == usedIndex[1] || index == usedIndex[2]) {
                index = (int) (Math.random() * KEY_LOCATIONS.length);
            }

            usedIndex[i] = index;

            Vector chosenLoc = KEY_LOCATIONS[index];
            Location loc = new Location(world, chosenLoc.getX(), chosenLoc.getY(), chosenLoc.getZ());

            usedKeyLocations.add(loc);

            Block b = loc.getBlock();
            b.setType(Material.CHEST);

            Chest chestInv = (Chest) b.getState();

            if (i == 0) {
                chestInv.getBlockInventory().setItem(13, purpleKey);
                plugin.logger.info("Placed purple key at: " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
            } else if (i == 1) {
                chestInv.getBlockInventory().setItem(13, tielKey);
                plugin.logger.info("Placed tiel key at: " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
            } else {
                chestInv.getBlockInventory().setItem(13, brownKey);
                plugin.logger.info("Placed brown key at: " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
            }

        }

    }

    public void removeKeyChests() {
        for (Location loc : usedKeyLocations) {
            world.setBlockData(loc, Material.SNOW.createBlockData());
        }
        usedKeyLocations.clear();
    }

    public void closeOpenedDoors() {
        for (Location l : unlockedDoorsLocations) {
            Block b = l.getBlock();
            if (b.getBlockData() instanceof Door door) {
                door.setOpen(false);
                b.setBlockData(door);
            }
        }
    }

    public ItemStack getPurpleKey() {
        ItemStack purpleKey = new ItemStack(Globals.PURPLE_KEY_TYPE);
        ItemMeta purpleMeta = purpleKey.getItemMeta();

        purpleMeta.displayName(Component.text(Globals.PURPLE_KEY_NAME)
                .color(NamedTextColor.LIGHT_PURPLE)
                .decoration(TextDecoration.ITALIC, false));
        purpleKey.setItemMeta(purpleMeta);

        return purpleKey;
    }

    public ItemStack getTielKey() {
        ItemStack tielKey = new ItemStack(Globals.TIEL_KEY_TYPE);
        ItemMeta tielMeta = tielKey.getItemMeta();

        tielMeta.displayName(Component.text(Globals.TIEL_KEY_NAME)
                .color(NamedTextColor.BLUE)
                .decoration(TextDecoration.ITALIC, false));

        tielKey.setItemMeta(tielMeta);

        return tielKey;
    }

    public ItemStack getBrownKey() {
        ItemStack brownKey = new ItemStack(Globals.BROWN_KEY_TYPE);
        ItemMeta brownMeta = brownKey.getItemMeta();

        brownMeta.displayName(Component.text(Globals.BROWN_KEY_NAME)
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));

        brownKey.setItemMeta(brownMeta);

        return brownKey;
    }

    public void reportDoorOpen(Player player, Location location) {
        if (!unlockedDoorsLocations.contains(location)) {
            // because the event is registered on whichever part of the door the player clicks, we need to ensure both y=227 AND y=228 are included in the list of unlocked doors
            if (location.getY() == 227) {
                unlockedDoorsLocations.add(location);
                unlockedDoorsLocations.add(new Location(location.getWorld(), location.getX(), 228, location.getZ()));
            } else if (location.getY() == 228) {
                unlockedDoorsLocations.add(location);
                unlockedDoorsLocations.add(new Location(location.getWorld(), location.getX(), 227, location.getZ()));
            }

            player.sendRichMessage("<green>This door has been unlocked!");
        }
    }

}
