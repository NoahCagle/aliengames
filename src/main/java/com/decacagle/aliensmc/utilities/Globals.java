package com.decacagle.aliensmc.utilities;

import com.decacagle.aliensmc.AliensGames;
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

    public static void goToLeaderboard(List<Player> players, World world, int winners, AliensGames plugin) {
        Location leaderboardLoc = new Location(world, 185, 191, 1119, 180, 0);
        Location firstPlaceLoc = new Location(world, 185, 193, 1110, 0, 0);
        Location secondPlaceLoc = new Location(world, 183, 192, 1110, 180, 0);
        Location thirdPlaceLoc = new Location(world, 187, 191, 1110, 180, 0);

        PositionSongPlayer psp = new PositionSongPlayer(plugin.congratulationsSong);
        psp.setTargetLocation(firstPlaceLoc);
        psp.setDistance(16);

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
                player.teleport(leaderboardLoc);
            }
            psp.addPlayer(player);
        }

        psp.setPlaying(true);
        Bukkit.getScheduler().runTaskLater(plugin, () -> psp.setPlaying(false), 1200L);

        for (int i = 0; i < 10; i++) {
            if (i % 4 != 0) {
                int fireworkIndex = (i - 1) % 4;
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Location loc = new Location(world, 183 + (fireworkIndex * 2), 191, 1108);

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

}
