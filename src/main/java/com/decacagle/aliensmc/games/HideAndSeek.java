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
            new Vector(254, 227, 1009),
            new Vector(256, 227, 1018),
            new Vector(264, 227, 1011),
            new Vector(269, 227, 1004),
            new Vector(228, 227, 999),
            new Vector(294, 227, 989),
            new Vector(297, 227, 992),
            new Vector(308, 227, 994),
            new Vector(302, 227, 1003),
            new Vector(302, 227, 999),
            new Vector(308, 227, 998),
            new Vector(307, 227, 1009),
            new Vector(291, 227, 1011),
            new Vector(292, 227, 1025),
            new Vector(282, 227, 1025),
            new Vector(271, 227, 1024),
            new Vector(273, 227, 1018),
            new Vector(249, 227, 1008),
            new Vector(276, 227, 1000),
            new Vector(283, 227, 998),
            new Vector(288, 227, 984),
            new Vector(309, 227, 979),
            new Vector(309, 227, 966),
            new Vector(326, 227, 962),
            new Vector(319, 227, 967),
            new Vector(332, 227, 967),
            new Vector(324, 227, 980),
            new Vector(330, 227, 978),
            new Vector(324, 227, 986),
            new Vector(331, 227, 985),
            new Vector(320, 227, 989),
            new Vector(319, 227, 971),
            new Vector(315, 227, 973),
            new Vector(307, 227, 972),
            new Vector(338, 227, 957),
            new Vector(336, 227, 967),
            new Vector(337, 227, 980),
            new Vector(344, 227, 978),
            new Vector(343, 227, 989),
            new Vector(337, 227, 995),
            new Vector(337, 227, 987),
            new Vector(329, 227, 988),
            new Vector(323, 227, 992),
            new Vector(331, 227, 992),
            new Vector(331, 227, 998),
            new Vector(323, 227, 998),
            new Vector(314, 227, 998),
            new Vector(315, 227, 987),
            new Vector(307, 227, 988),
            new Vector(299, 227, 986),
            new Vector(289, 227, 1005),
            new Vector(281, 227, 1004),
            new Vector(283, 227, 989),
            new Vector(281, 227, 983),
            new Vector(279, 227, 978),
            new Vector(292, 227, 977),
            new Vector(285, 227, 974),
            new Vector(292, 227, 971),
            new Vector(291, 227, 963),
            new Vector(283, 227, 963),
            new Vector(287, 227, 959),
            new Vector(285, 227, 955),
            new Vector(290, 227, 957),
            new Vector(293, 227, 960),
            new Vector(295, 227, 955),
            new Vector(298, 227, 958),
            new Vector(306, 227, 958),
            new Vector(304, 227, 964),
            new Vector(304, 227, 968),
            new Vector(301, 227, 955),
            new Vector(302, 227, 950),
            new Vector(306, 227, 951),
            new Vector(293, 227, 951),
            new Vector(293, 227, 944),
            new Vector(282, 227, 945),
            new Vector(288, 227, 946),
            new Vector(283, 227, 934),
            new Vector(294, 227, 935),
            new Vector(288, 227, 936),
            new Vector(293, 227, 941),
            new Vector(284, 227, 940),
            new Vector(292, 227, 929),
            new Vector(293, 227, 923),
            new Vector(297, 227, 920),
            new Vector(302, 227, 919),
            new Vector(301, 227, 930),
            new Vector(308, 227, 929),
            new Vector(304, 227, 928),
            new Vector(307, 227, 937),
            new Vector(311, 227, 934),
            new Vector(306, 227, 933),
            new Vector(315, 227, 932),
            new Vector(314, 227, 939),
            new Vector(315, 227, 946),
            new Vector(308, 227, 945),
            new Vector(324, 227, 934),
            new Vector(323, 227, 926),
            new Vector(325, 227, 919),
            new Vector(313, 227, 920),
            new Vector(319, 227, 921),
            new Vector(314, 227, 928),
            new Vector(319, 227, 927),
            new Vector(315, 227, 914),
            new Vector(314, 227, 906),
            new Vector(325, 227, 908),
            new Vector(337, 227, 907),
            new Vector(335, 227, 914),
            new Vector(336, 227, 923),
            new Vector(330, 227, 921),
            new Vector(331, 227, 934),
            new Vector(330, 227, 927),
            new Vector(337, 227, 933),
            new Vector(338, 227, 945),
            new Vector(341, 227, 950),
            new Vector(341, 227, 958),
            new Vector(342, 227, 963),
            new Vector(351, 227, 961),
            new Vector(359, 227, 962),
            new Vector(357, 227, 970),
            new Vector(358, 227, 975),
            new Vector(349, 227, 980),
            new Vector(348, 227, 973),
            new Vector(348, 227, 987),
            new Vector(353, 227, 985),
            new Vector(257, 227, 985),
            new Vector(355, 227, 992),
            new Vector(360, 227, 996),
            new Vector(367, 227, 998),
            new Vector(374, 227, 997),
            new Vector(368, 227, 990),
            new Vector(366, 227, 983),
            new Vector(372, 227, 985),
            new Vector(377, 227, 984),
            new Vector(375, 227, 981),
            new Vector(386, 227, 981),
            new Vector(381, 227, 982),
            new Vector(375, 227, 975),
            new Vector(376, 227, 969),
            new Vector(370, 227, 971),
            new Vector(369, 227, 963),
            new Vector(370, 227, 953),
            new Vector(379, 227, 954),
            new Vector(378, 227, 944),
            new Vector(370, 227, 946),
            new Vector(371, 227, 937),
            new Vector(362, 227, 939),
            new Vector(364, 227, 931),
            new Vector(363, 227, 922),
            new Vector(370, 227, 923),
            new Vector(355, 227, 924),
            new Vector(348, 227, 924),
            new Vector(349, 227, 916),
            new Vector(362, 227, 955),
            new Vector(354, 227, 955),
            new Vector(354, 227, 947),
            new Vector(355, 227, 937),
            new Vector(341, 227, 938),
            new Vector(301, 227, 907),
            new Vector(303, 227, 911),
            new Vector(302, 227, 916),
            new Vector(294, 227, 916),
            new Vector(287, 227, 915),
            new Vector(288, 227, 902),
            new Vector(289, 227, 909),
            new Vector(281, 227, 904),
            new Vector(274, 227, 903),
            new Vector(276, 227, 913),
            new Vector(265, 227, 914),
            new Vector(257, 227, 912),
            new Vector(256, 227, 916),
            new Vector(271, 227, 931),
            new Vector(269, 227, 935),
            new Vector(270, 227, 941),
            new Vector(261, 227, 941),
            new Vector(251, 227, 939),
            new Vector(253, 227, 948),
            new Vector(252, 227, 957),
            new Vector(248, 227, 956),
            new Vector(255, 227, 963),
            new Vector(255, 227, 955),
            new Vector(261, 227, 957),
            new Vector(269, 227, 956),
            new Vector(267, 227, 960),
            new Vector(272, 227, 961),
            new Vector(277, 227, 960),
            new Vector(275, 227, 956),
            new Vector(284, 227, 948),
            new Vector(275, 227, 971),
            new Vector(280, 227, 972),
            new Vector(271, 227, 982),
            new Vector(277, 227, 983),
            new Vector(299, 227, 999),
            new Vector(298, 227, 1005),
            new Vector(300, 227, 1008),
            new Vector(301, 227, 1017),
            new Vector(302, 227, 1012),
            new Vector(297, 227, 1015),
            new Vector(318, 227, 1008),
            new Vector(318, 227, 1015),
            new Vector(309, 227, 1014),
            new Vector(310, 227, 1020),
            new Vector(318, 227, 1024),
            new Vector(311, 227, 1023),
            new Vector(303, 227, 1024),
            new Vector(305, 227, 1016),
            new Vector(316, 227, 1027),
            new Vector(329, 227, 1040),
            new Vector(327, 227, 1043),
            new Vector(337, 227, 1044),
            new Vector(342, 227, 1043),
            new Vector(348, 227, 1042),
            new Vector(346, 227, 1047),
            new Vector(354, 227, 1046),
            new Vector(347, 227, 1055),
            new Vector(336, 227, 1053),
            new Vector(335, 227, 1063),
            new Vector(336, 227, 1071),
            new Vector(358, 227, 1064),
            new Vector(326, 227, 1069),
            new Vector(316, 227, 1070),
            new Vector(317, 227, 1060),
            new Vector(318, 227, 1080),
            new Vector(318, 227, 1087),
            new Vector(308, 227, 1085),
            new Vector(299, 227, 1086),
            new Vector(300, 227, 1075),
            new Vector(288, 227, 1077),
            new Vector(289, 227, 1072),
            new Vector(282, 227, 1076),
            new Vector(284, 227, 1085),
            new Vector(279, 227, 1083),
            new Vector(269, 227, 1083),
            new Vector(262, 227, 1084),
            new Vector(264, 227, 1080),
            new Vector(263, 227, 1074),
            new Vector(252, 227, 1075),
            new Vector(254, 227, 1056),
            new Vector(271, 227, 1069),
            new Vector(262, 227, 1068),
            new Vector(269, 227, 1061),
            new Vector(275, 227, 1060),
            new Vector(283, 227, 1062),
            new Vector(294, 227, 1051),
            new Vector(288, 227, 1042),
            new Vector(202, 227, 1036),
            new Vector(300, 227, 1034),
            new Vector(305, 227, 1032),
            new Vector(315, 227, 1042),
            new Vector(315, 227, 1051),
            new Vector(307, 227, 1049),
            new Vector(301, 227, 1049),
            new Vector(301, 227, 1061),
            new Vector(302, 227, 1069),
            new Vector(288, 227, 1067),
            new Vector(281, 227, 1069),
            new Vector(274, 227, 1068),
            new Vector(276, 227, 1075),
            new Vector(275, 227, 1085),
            new Vector(263, 227, 1030),
            new Vector(270, 227, 1028),
            new Vector(280, 227, 1028),
            new Vector(280, 227, 1038),
            new Vector(275, 227, 1037),
            new Vector(269, 227, 1038),
            new Vector(270, 227, 1047),
            new Vector(276, 227, 1045),
            new Vector(274, 227, 1054),
            new Vector(282, 227, 1054),
            new Vector(280, 227, 1047),
            new Vector(278, 227, 1041),
            new Vector(361, 227, 970),
            new Vector(261, 227, 961),
            new Vector(255, 227, 968),
            new Vector(250, 227, 970),
            new Vector(246, 227, 966),
            new Vector(269, 227, 969),
            new Vector(250, 227, 912),
            new Vector(252, 227, 920),
            new Vector(362, 227, 1048),
            new Vector(360, 227, 1040),
            new Vector(361, 227, 1033),
            new Vector(366, 227, 1035),
            new Vector(366, 227, 1025),
            new Vector(352, 227, 1026),
            new Vector(354, 227, 1020),
            new Vector(252, 227, 1014),
            new Vector(361, 227, 1016),
            new Vector(352, 227, 1007),
            new Vector(359, 227, 1004),
            new Vector(364, 227, 1019),
            new Vector(374, 227, 1021),
            new Vector(372, 227, 1010),
            new Vector(372, 227, 1002),
            new Vector(379, 227, 1001),
            new Vector(386, 227, 1003),
            new Vector(384, 227, 992),
            new Vector(323, 227, 1015),
            new Vector(332, 227, 1017),
            new Vector(330, 227, 1008),
            new Vector(332, 227, 1001),
            new Vector(325, 227, 1003),
            new Vector(316, 227, 1001),
            new Vector(337, 227, 1009),
            new Vector(337, 227, 1001),
    };
    public List<Location> usedKeyLocations = new ArrayList<Location>();
    public List<Location> unlockedDoorsLocations = new ArrayList<Location>();

    public List<HideAndSeekPlayer> hiders = new ArrayList<HideAndSeekPlayer>();
    public List<HideAndSeekPlayer> seekers = new ArrayList<HideAndSeekPlayer>();

    public Location mapLoc;

    public boolean seekersSpawnedIn = false;

    public int secondsPassed = 0;
    public final int SEEKER_SPAWN_TIME_SECONDS = 30;
    public final int TOTAL_GAME_TIME_SECONDS = 300;

    public HideAndSeek(AliensGames plugin, Player host) {
        super(new Location(plugin.getServer().getWorld("squidgame"), 183, 266, 1053, 90, 0), plugin, host, 2);
        this.mapLoc = new Location(spawnpoint.getWorld(), 247.5, 227, 994.5, -90, 0);
        this.PRETTY_TITLE = "Hide and Seek";
    }

    public void timer() {
        secondsPassed++;

        updateTimer(Component.text("Time Remaining: "), TOTAL_GAME_TIME_SECONDS - secondsPassed);

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
            goToLeaderboard();
        }

    }

    public void givePointsToRemainingHiders() {
        for (HideAndSeekPlayer p : hiders) {
            if (!p.eliminated && !p.escaped) {
                p.player.sendRichMessage("<gold>Survived until the end!");
                p.player.sendRichMessage("<bold><green>You've earned $5.00!");
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
        givePointsToRemainingHiders();

        List<HideAndSeekPlayer> finalResults = sortPlayersByPoints();

        List<Player> orderedPlayers = new ArrayList<Player>();

        int numWinners = 0;

        broadcastMessageToAllPlayers("<underlined><green><bold>Hide And Seek Rankings\n");

        for (int i = 0; i < finalResults.size(); i++) {
            HideAndSeekPlayer p = finalResults.get(i);
            Player player = p.player;
            player.setGameMode(GameMode.ADVENTURE);
            Globals.fullyClearInventory(player);

            if (p.points > 0 && !p.eliminated) numWinners++;
            orderedPlayers.add(player);

            String numberColor = i == 0 ? ("<#D4AF37>") : (i == 1 ? ("<#C0C0C0>") : (i == 2 ? ("<#CD7F32>") : ("<gray>")));
            String teamColor = p.seeker ? ("<red>") : ("<blue>");

            if (p.eliminated) {
                broadcastMessageToAllPlayers(numberColor + "<bold>" + Globals.numberToPosition(i + 1) + ": " + teamColor + p.player.getName() + "<white> - <red><bold>Eliminated</bold></red><white> - $0");
            } else if (p.escaped) {
                broadcastMessageToAllPlayers(numberColor + "<bold>" + Globals.numberToPosition(i + 1) + ": " + teamColor + p.player.getName() + " - <gold><bold>Escaped</bold></gold><white> - $" + p.points);
                plugin.economy.depositPlayer(p.player, p.points);
            } else if (p.seeker) {
                if (p.kills > 0) {
                    broadcastMessageToAllPlayers(numberColor + "<bold>" + Globals.numberToPosition(i + 1) + ": " + teamColor + p.player.getName() + "<white> - " + p.kills + " Kill" + (p.kills > 1 ? "s" : "") + " - <green><bold>PASS</bold></green><white> - $" + p.points);
                    plugin.economy.depositPlayer(p.player, p.points);
                } else {
                    broadcastMessageToAllPlayers(numberColor + "<bold>" + Globals.numberToPosition(i + 1) + ": " + teamColor + p.player.getName() + "<white> - 0 Kills - <red><bold>FAIL</bold></red><white> - $0");
                }
            } else {
                broadcastMessageToAllPlayers(numberColor + "<bold>" + Globals.numberToPosition(i + 1) + ": " + teamColor + p.player.getName() + "<white> - <green><bold>Survived</bold></green><white> - $" + p.points);
                plugin.economy.depositPlayer(p.player, p.points);
            }
        }

        broadcastMessageToAllPlayers(" ");

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
        } else if (allSeekersEliminated()) {
            gameRunning = false;
            // hiders win
            broadcastTitleToAllPlayers(Component.text("Game Over!", NamedTextColor.GREEN, TextDecoration.BOLD), Component.text(""));
            plugin.gameManager.stopGame();
        } else if (secondsPassed >= TOTAL_GAME_TIME_SECONDS && gameRunning) {
            gameRunning = false;
            // seekers win
            broadcastTitleToAllPlayers(Component.text("Game Over!", NamedTextColor.GREEN, TextDecoration.BOLD), Component.text(""));
            plugin.gameManager.stopGame();
        }

    }

    public boolean allHidersEscapedOrEliminated() {
        for (HideAndSeekPlayer p : hiders) {
            if (!p.eliminated && !p.escaped) return false;
        }
        return true;
    }

    public boolean allSeekersEliminated() {
        for (HideAndSeekPlayer p : seekers) {
            if (!p.eliminated) return false;
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

        initScoreboard();

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
                p.player.sendRichMessage("<bold><green>You've earned $10.00!");
                p.points += 10;

                updatePlayerLine(p);

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
            hnsPlayer.eliminated = true;

            Component title = Component.text("You have been eliminated!", NamedTextColor.RED, TextDecoration.BOLD);
            Component subtitle = Component.text("You are now a spectator until the end of the game", NamedTextColor.GOLD);

            hnsPlayer.player.showTitle(Title.title(title, subtitle));

            // broadcast killed announcement
            broadcastMessageToAllPlayers("<red><bold>Player " + eliminated.getName() + " has been eliminated!");

            updatePlayerLine(hnsPlayer);

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
            broadcastMessageToAllPlayers("<red><bold>Player " + killed.getName() + " has been eliminated!");

            // give points
            killedHNSP.eliminated = true;

            killerHNSP.kills++;
            killerHNSP.killedPlayers.add(killed);

            if (killedHNSP.seeker && !killerHNSP.seeker) {
                killer.sendRichMessage("<gold>You killed a seeker!");
                killer.sendRichMessage("<bold><green>You've earned $20.00!");
                killerHNSP.points += 20;
            } else if (!killedHNSP.seeker && !killerHNSP.seeker) {
                killer.sendRichMessage("<gold>You killed a fellow hider!");
                killer.sendRichMessage("<bold><green>You've earned $5.00");
                killerHNSP.points += 5;
            } else {
                killer.sendRichMessage("<gold>You killed a hider!");
                killer.sendRichMessage("<bold><green>You've earned $5.00");
                killerHNSP.points += 5;
            }

            updatePlayerLine(killedHNSP);
            updatePlayerLine(killerHNSP);

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

    public boolean playerIsSeeker(Player player) {
        for (HideAndSeekPlayer p : seekers) {
            if (p.player.getUniqueId().compareTo(player.getUniqueId()) == 0) {

                return true;

            }
        }
        return false;
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

        purpleMeta.lore(List.of(
                Component.text("This key can open any purple", NamedTextColor.DARK_PURPLE),
                Component.text("Crimson Door you may find!", NamedTextColor.DARK_PURPLE)
        ));

        purpleKey.setItemMeta(purpleMeta);

        return purpleKey;
    }

    public ItemStack getTielKey() {
        ItemStack tielKey = new ItemStack(Globals.TIEL_KEY_TYPE);
        ItemMeta tielMeta = tielKey.getItemMeta();

        tielMeta.displayName(Component.text(Globals.TIEL_KEY_NAME)
                .color(NamedTextColor.BLUE)
                .decoration(TextDecoration.ITALIC, false));

        tielMeta.lore(List.of(
                Component.text("This key can open any tiel", NamedTextColor.DARK_BLUE),
                Component.text("Warped Door you may find!", NamedTextColor.DARK_BLUE)
        ));

        tielKey.setItemMeta(tielMeta);

        return tielKey;
    }

    public ItemStack getBrownKey() {
        ItemStack brownKey = new ItemStack(Globals.BROWN_KEY_TYPE);
        ItemMeta brownMeta = brownKey.getItemMeta();

        brownMeta.displayName(Component.text(Globals.BROWN_KEY_NAME)
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));

        brownMeta.lore(List.of(
                Component.text("This key can open any brown", NamedTextColor.DARK_GRAY),
                Component.text("Spruce Door you may find!", NamedTextColor.DARK_GRAY)
        ));

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

    public void initScoreboard() {
        createScoreboardWithTimer(PRETTY_TITLE);

        hideNameTags();

        Team hidersHeadline = createScoreboardLine(scoreboard, scoreboardObjective, "1000", 8, false);
        hidersHeadline.prefix(Component.text("-- Hiders --", NamedTextColor.BLUE, TextDecoration.BOLD));


        for (int i = 0; i < hiders.size(); i++) {
            Player player = hiders.get(i).player;

            Team playerLine = createScoreboardLine(scoreboard, scoreboardObjective, "" + i, -i, false);
            playerLines.put(player, playerLine);

            playerLine.prefix(Component.text(player.getName() + ": ", NamedTextColor.BLUE));
            playerLine.suffix(Component.text("✔", NamedTextColor.GREEN, TextDecoration.BOLD));

        }

        Team seekersHeadline = createScoreboardLine(scoreboard, scoreboardObjective, "1001", -hiders.size(), false);
        seekersHeadline.prefix(Component.text("-- Seekers --", NamedTextColor.RED, TextDecoration.BOLD));

        for (int i = 0; i < seekers.size(); i++) {
            Player player = seekers.get(i).player;

            int lineIndexOffset = hiders.size() + 1;

            Team playerLine = createScoreboardLine(scoreboard, scoreboardObjective, "" + (i + lineIndexOffset), -(i + lineIndexOffset), false);
            playerLines.put(player, playerLine);

            playerLine.prefix(Component.text(player.getName() + ": ", NamedTextColor.RED));
            playerLine.suffix(Component.text("FAIL", NamedTextColor.RED, TextDecoration.BOLD));

        }

    }

    // TODO: consider changing the logic of the scoreboard for this game
    // In the show, seekers only 'pass' if they kill a hider. In our game, seekers will receive 0 points if they do not get a kill
    // However, perhaps this could be reflected in the scoreboard. Instead of seekers showing a checkmark or an X, they could show a 'PASS' or 'FAIL'
    public void updatePlayerLine(HideAndSeekPlayer player) {
        Team playerLine = playerLines.get(player.player);

        if (player.seeker && !player.eliminated) {
            if (player.kills == 0) {
                playerLine.suffix(Component.text("FAIL", NamedTextColor.RED, TextDecoration.BOLD));
            } else {
                playerLine.suffix(Component.text("PASS", NamedTextColor.GREEN, TextDecoration.BOLD));
            }
        } else if (player.escaped) {
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
        removeKeyChests();
        closeOpenedDoors();
        if (this.scoreboard != null) {
            showNameTags();
            removeScoreboard();
        }
    }

    public void reportPlayerDeparture(Player player) {
        participants.remove(player);
        removeFromScoreboard(player);

        Globals.fullyClearInventory(player);

        player.teleport(world.getSpawnLocation());

        if (gameRunning) {

            HideAndSeekPlayer hnsPlayer = null;

            for (HideAndSeekPlayer p : hiders) {
                if (p.player.getUniqueId().compareTo(player.getUniqueId()) == 0) {
                    hnsPlayer = p;
                    hiders.remove(p);
                    break;
                }
            }

            if (hnsPlayer == null) {
                for (HideAndSeekPlayer p : seekers) {
                    if (p.player.getUniqueId().compareTo(player.getUniqueId()) == 0) {
                        hnsPlayer = p;
                        seekers.remove(p);
                        break;
                    }
                }
            }

            if (hnsPlayer != null) {

                hnsPlayer.eliminated = true;
                hnsPlayer.connected = false;
                hnsPlayer.kills = 0;
                hnsPlayer.points = 0;

                updatePlayerLine(hnsPlayer);

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
