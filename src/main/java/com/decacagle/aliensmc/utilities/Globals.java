package com.decacagle.aliensmc.utilities;

import com.decacagle.aliensmc.AliensGames;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.List;

public class Globals {

    public static String DAGGER_NAME = "Seeker's Dagger";
    public static Material DAGGER_TYPE = Material.IRON_SWORD;

    public static String PURPLE_KEY_NAME = "Purple Key";
    public static Material PURPLE_KEY_TYPE = Material.STONE_HOE;

    public static String TIEL_KEY_NAME = "Tiel Key";
    public static Material TIEL_KEY_TYPE = Material.DIAMOND_HOE;

    public static String BROWN_KEY_NAME = "Brown Key";
    public static Material BROWN_KEY_TYPE = Material.WOODEN_HOE;

    // this is for the leftmost firework. the other two points will be at x=185 and x=187
    public static Vector fireworksLocation = new Vector(183, 264, 1108);

    public static Vector leaderboardLocation = new Vector(185, 264, 1118);
    public static Vector firstPlaceLocation = new Vector(185, 267, 1110);
    public static Vector secondPlaceLocation = new Vector(183, 266, 1110);
    public static Vector thirdPlaceLocation = new Vector(187, 265, 1110);

    private static final int distanceFromPodium = 8;
    private static final double startingPosRadians = -(Math.PI / 8);

    public static boolean displayNameEquals(ItemStack item, String targetName) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasDisplayName()) {
            assert meta.displayName() != null;
            String plainText = PlainTextComponentSerializer.plainText().serialize(meta.displayName());

            if (plainText.equalsIgnoreCase(targetName)) {
                return true;
            }

        }

        return false;

    }

    public static boolean playerInList(Player player, List<Player> players) {
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(player.getName())) return true;
        }
        return false;
    }

    /**
     * Clears inventory entirely, including armor
     */
    public static void fullyClearInventory(Player player) {
        PlayerInventory inv = player.getInventory();
        inv.clear();
        inv.setHelmet(null);
        inv.setChestplate(null);
        inv.setLeggings(null);
        inv.setBoots(null);

        inv.setItemInOffHand(null);
    }

    public static String secondsToFormattedTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;

        return minutes + ":" + ((secs < 10 || secs == 0) ? "0" : "") + secs;
    }

    public static String numberToPosition(int num) {
        String ret = ("" + num);
        if (num == 0) return "zero | error";
        char lastNum = ret.charAt(ret.length() - 1);
        if (num != 11 && num != 12 && num != 13) {
            if (lastNum == '1') ret += "st";
            else if (lastNum == '2') ret += "nd";
            else if (lastNum == '3') ret += "rd";
            else ret += "th";
        } else ret += "th";
        return ret;
    }

    public static void goToLeaderboard(List<Player> players, World world, int winners, AliensGames plugin, Song song) {
        Location leaderboardLoc = new Location(world, leaderboardLocation.getX(), leaderboardLocation.getY(), leaderboardLocation.getZ(), 180, 0);
        Location firstPlaceLoc = new Location(world, firstPlaceLocation.getX(), firstPlaceLocation.getY(), firstPlaceLocation.getZ(), 0, 0);
        Location secondPlaceLoc = new Location(world, secondPlaceLocation.getX(), secondPlaceLocation.getY(), secondPlaceLocation.getZ(), 0, 0);
        Location thirdPlaceLoc = new Location(world, thirdPlaceLocation.getX(), thirdPlaceLocation.getY(), thirdPlaceLocation.getZ(), 0, 0);

        PositionSongPlayer psp = new PositionSongPlayer(song);
        psp.setTargetLocation(firstPlaceLoc);
        psp.setDistance(16);

        double radiansBetweenPlayers = (Math.PI / 4) / (players.size() - winners);
        int loserIndex = 0;

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);

            player.setGameMode(GameMode.ADVENTURE);
            fullyClearInventory(player);
            if (i == 0 && winners >= 1) {
                player.teleport(firstPlaceLoc);
            } else if (i == 1 && winners >= 2) {
                player.teleport(secondPlaceLoc);
            } else if (i == 2 && winners >= 3) {
                player.teleport(thirdPlaceLoc);
            } else {
                teleportToLeaderboard(player, world, radiansBetweenPlayers, loserIndex);
                loserIndex++;
            }
            psp.addPlayer(player);
        }

        psp.setPlaying(true);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            psp.setPlaying(false);
            psp.destroy();
        }, 1200L);

        for (int i = 0; i < 10; i++) {
            if (i % 4 != 0) {
                int fireworkIndex = (i - 1) % 4;
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Location loc = new Location(world, fireworksLocation.getX() + (fireworkIndex * 2), fireworksLocation.getY(), fireworksLocation.getZ());

                    Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);

                    FireworkMeta meta = firework.getFireworkMeta();
                    meta.addEffect(
                            FireworkEffect.builder()
                                    .withColor(Color.GREEN)
                                    .withFade(Color.LIME)
                                    .with(FireworkEffect.Type.BALL_LARGE)
                                    .flicker(true)
                                    .trail(true)
                                    .build()
                    );
                    meta.setPower(1);
                    firework.setFireworkMeta(meta);
                }, i * 5);
            }
        }

    }

    private static void teleportToLeaderboard(Player player, World world, double distanceBetweenPlayers, int loserIndex) {
        // coords based on unit circle
        double x = Math.cos(startingPosRadians + (distanceBetweenPlayers * loserIndex)) * distanceFromPodium;
        double y = Math.sin(startingPosRadians + (distanceBetweenPlayers * loserIndex)) * distanceFromPodium;

        double angleDegrees = (180.0 / Math.PI) * (startingPosRadians + (distanceBetweenPlayers * loserIndex));

        Location finalLoc = new Location(world, firstPlaceLocation.getX() + y, firstPlaceLocation.getY(), firstPlaceLocation.getZ() + x, (float) (180 - angleDegrees), 0);

        player.teleport(finalLoc);

    }

    public static boolean checkParsable(String num) {
        try {
            Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean playerWithinBounds(Player player, Location cuboidPoint1, Location cuboidPoint2) {
        if (!player.getWorld().equals(cuboidPoint1.getWorld())) return false;

        Location playerLoc = player.getLocation();

        double x = playerLoc.getX();
        double y = playerLoc.getY();
        double z = playerLoc.getZ();

        double x1 = Math.min(cuboidPoint1.getX(), cuboidPoint2.getX());
        double x2 = Math.max(cuboidPoint1.getX(), cuboidPoint2.getX());

        double y1 = Math.min(cuboidPoint1.getY(), cuboidPoint2.getY());
        double y2 = Math.max(cuboidPoint1.getY(), cuboidPoint2.getY());

        double z1 = Math.min(cuboidPoint1.getZ(), cuboidPoint2.getZ());
        double z2 = Math.max(cuboidPoint1.getZ(), cuboidPoint2.getZ());

        return x >= x1 && x <= x2
                && y >= y1 && y <= y2
                && z >= z1 && z <= z2;

    }

}
