package com.decacagle.aliensmc.games;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.utilities.Globals;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;

public class HideAndSeek extends Game {

    public List<Player> hiders = new ArrayList<Player>();
    public List<Player> seekers = new ArrayList<Player>();

    public Location mapLoc;

    public boolean seekersSpawnedIn = false;

    public int secondsPassed = 0;
    public final int SEEKER_SPAWN_TIME_SECONDS = 30;
    public final int TOTAL_GAME_TIME_SECONDS = 300;

    public HideAndSeek(AliensGames plugin, Player host) {
        super(new Location(plugin.getServer().getWorld("squidgame"), 183, 193, 1053, 90, 0), plugin, host);
        this.mapLoc = new Location(spawnpoint.getWorld(), 247.5, 227, 994.5, -90, 0);
        this.PRETTY_TITLE = "Hide and Seek";
    }

    public void timer() {
        secondsPassed++;

        if (secondsPassed >= SEEKER_SPAWN_TIME_SECONDS && !seekersSpawnedIn) {
            for (Player p : seekers) {
                p.teleport(mapLoc);
            }
            for (Player p : participants) {
                p.sendRichMessage("<red><bold>The seekers have entered the map!");
            }
            seekersSpawnedIn = true;
        }

        if (secondsPassed >= TOTAL_GAME_TIME_SECONDS) {
            for (Player p : participants) {
                p.sendRichMessage("<red><bold>The game has ended!");
            }
            plugin.gameManager.stopGame();
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, () -> timer(), 20);
        }

    }

    public void prepareGame() {
        host.teleport(spawnpoint);
    }

    public void startGame() {

        gameStarted = true;

        // go through list of participants, assign every other player to be hiders, and the rest as seekers
        boolean hider = true;
        for (Player p : participants) {
            if (hider) hiders.add(p);
            else seekers.add(p);
            hider = !hider;
        }

        // assign hiders their role
        for (Player p : hiders) {
            assignHiderRole(p);
        }

        // assign seekers their role
        for (Player p : seekers) {
            assignSeekerRole(p);
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
        player.getInventory().clear();

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
        player.getInventory().clear();

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

}
