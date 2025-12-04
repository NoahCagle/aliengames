package com.decacagle.aliensmc.utilities;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

    public static void sendTitle(Player player, String title, String subtitle) {

    }

}
