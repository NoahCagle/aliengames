package com.decacagle.aliensmc.games;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.games.participants.HideAndSeekPlayer;
import com.decacagle.aliensmc.utilities.Globals;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class HideAndSeek extends Game {

    public static Vector ESCAPE_POINT_1 = new Vector(308, 218, 888);
    public static Vector ESCAPE_POINT_2 = new Vector(406, 236, 991);

    public static Vector[] KEY_LOCATIONS = new Vector[]{
            new Vector(261, 236, 961),
            new Vector(246, 236, 965),
            new Vector(295, 236, 944),
            new Vector(306, 236, 958),
            new Vector(291, 236, 963),
            new Vector(283, 236, 990),
            new Vector(262, 236, 996),
            new Vector(252, 236, 1076),
            new Vector(262, 236, 1085),
            new Vector(288, 236, 1067),
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
            new Vector(250, 218, 1001),
            new Vector(255, 218, 987),
            new Vector(252, 218, 1076),
            new Vector(318, 218, 1087),
            new Vector(358, 218, 1054),
            new Vector(359, 218, 1064),
            new Vector(291, 218, 1011),
            new Vector(256, 218, 1018),
            new Vector(254, 218, 1009),
            new Vector(279, 218, 994),
    };

    public List<HideAndSeekPlayer> hiders = new ArrayList<HideAndSeekPlayer>();
    public List<HideAndSeekPlayer> seekers = new ArrayList<HideAndSeekPlayer>();

    public Location mapLoc;
    public Location leaderboardLoc;

    public boolean seekersSpawnedIn = false;

    public int secondsPassed = 0;
    public final int SEEKER_SPAWN_TIME_SECONDS = 30;
    public final int TOTAL_GAME_TIME_SECONDS = 300;

    public HideAndSeek(AliensGames plugin, Player host) {
        super(new Location(plugin.getServer().getWorld("squidgame"), 183, 193, 1053, 90, 0), plugin, host);
        this.mapLoc = new Location(spawnpoint.getWorld(), 247.5, 227, 994.5, -90, 0);
        this.leaderboardLoc = new Location(spawnpoint.getWorld(), 185, 191, 1119, 180, 0);
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


        if (secondsPassed >= TOTAL_GAME_TIME_SECONDS && gameStarted) {
            gameStarted = false;
            // hiders win
            broadcastTitleToAllPlayers(Component.text("Hiders win!", NamedTextColor.GREEN, TextDecoration.BOLD), Component.text(""));
            plugin.gameManager.stopGame();
        } else {
            if ((TOTAL_GAME_TIME_SECONDS - secondsPassed) % 60 == 0) {
                int minutes = (TOTAL_GAME_TIME_SECONDS - secondsPassed) / 60;
                if (minutes > 1)
                    broadcastMessageToAllPlayers("<gold><bold>" + minutes + " minutes remain!");
                else
                    broadcastMessageToAllPlayers("<gold><bold>" + minutes + " minute remains!");
            }
            plugin.logger.info("gameStarted: " + gameStarted);
            if (gameStarted) {
                Bukkit.getScheduler().runTaskLater(plugin, this::timer, 20);
            } else {
                showNameTags();
                goToLeaderboard();
            }
        }

    }

    public void goToLeaderboard() {
        for (Player p : participants) {
            p.setGameMode(GameMode.ADVENTURE);
            p.teleport(leaderboardLoc);
            Globals.fullyClearInventory(p);
        }
    }

    public void checkGameStatus() {

        if (allHidersEscapedOrEliminated()) {
            if (anyHidersEscaped()) {
                gameStarted = false;
                // hiders win
                broadcastTitleToAllPlayers(Component.text("Hiders win!", NamedTextColor.GREEN, TextDecoration.BOLD), Component.text(""));
                plugin.gameManager.stopGame();
            } else {
                gameStarted = false;
                // seekers win
                broadcastTitleToAllPlayers(Component.text("Seekers win!", NamedTextColor.RED, TextDecoration.BOLD), Component.text(""));
                plugin.gameManager.stopGame();
            }
        } else if (allSeekersEliminated()) {
            gameStarted = false;
            // hiders win
            broadcastTitleToAllPlayers(Component.text("Hiders win!", NamedTextColor.GREEN, TextDecoration.BOLD), Component.text(""));
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

        gameStarted = true;

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
        hiderChestplateMeta.setColor(Color.GREEN);
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
        ItemStack seekerKnife = new ItemStack(Material.IRON_SWORD);
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

}
