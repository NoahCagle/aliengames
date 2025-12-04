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
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class HideAndSeek extends Game {

    public static Vector ESCAPE_POINT_1 = new Vector(308, 218, 888);
    public static Vector ESCAPE_POINT_2 = new Vector(406, 236, 991);

    public List<HideAndSeekPlayer> hiders = new ArrayList<HideAndSeekPlayer>();
    public List<HideAndSeekPlayer> seekers = new ArrayList<HideAndSeekPlayer>();

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
            for (HideAndSeekPlayer p : seekers) {
                p.player.teleport(mapLoc);
            }
            broadcastToPlayers("<red><bold>The seekers have entered the map!");
            seekersSpawnedIn = true;
        }

        if (secondsPassed >= TOTAL_GAME_TIME_SECONDS) {
            broadcastToPlayers("<red><bold>The game has ended!");
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
            broadcastToPlayers("<green><bold>" + escapee.getName() + " has escaped!");
        }

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
            broadcastToPlayers("<red><bold>" + eliminated.getName() + " has been eliminated!");

        }
    }

    public void registerKill(Player killed, Player killer) {
        boolean foundKiller = false;
        boolean foundKilled = false;
        HideAndSeekPlayer killerHNSP = null;
        HideAndSeekPlayer killedHNSP = null;

        for (HideAndSeekPlayer p : seekers) {
            if (p.player.getUniqueId().compareTo(killer.getUniqueId()) == 0) {

                killerHNSP = p;

                foundKiller = true;
                break;

            }
        }

        if (!foundKiller) {
            for (HideAndSeekPlayer p : hiders) {
                if (p.player.getUniqueId().compareTo(killer.getUniqueId()) == 0) {

                    killerHNSP = p;

                    foundKiller = true;
                    break;

                }
            }

            if (!foundKiller) {
                plugin.logger.severe("Attempted to register the kill of " + killed.getName() + " by " + killer.getName() + " but was unable to find " + killer.getName() + " in either list!");
            }

        }

        for (HideAndSeekPlayer p : seekers) {
            if (p.player.getUniqueId().compareTo(killed.getUniqueId()) == 0) {
//
//                Component title = Component.text("You have been eliminated!", NamedTextColor.RED, TextDecoration.BOLD);
//                Component subtitle = Component.text("You are now a spectator until the end of the game", NamedTextColor.GOLD);
//
//                p.player.showTitle(Title.title(title, subtitle));

                killedHNSP = p;

                foundKilled = true;
                break;

            }
        }

        if (!foundKilled) {
            for (HideAndSeekPlayer p : hiders) {
                if (p.player.getUniqueId().compareTo(killer.getUniqueId()) == 0) {

                    Component title = Component.text("You have been eliminated!", NamedTextColor.RED, TextDecoration.BOLD);
                    Component subtitle = Component.text("You are now a spectator until the end of the game", NamedTextColor.GOLD);

                    p.player.showTitle(Title.title(title, subtitle));

                    killedHNSP = p;

                    foundKilled = true;
                    break;

                }
            }

            if (!foundKilled) {
                plugin.logger.severe("Attempted to register the kill of " + killed.getName() + " by " + killer.getName() + " but was unable to find " + killed.getName() + " in either list!");
            }

            if (foundKilled && foundKiller && killedHNSP != null && killerHNSP != null) {

                // Show title to killed player
                Component title = Component.text("You have been eliminated!", NamedTextColor.RED, TextDecoration.BOLD);
                Component subtitle = Component.text("You are now a spectator until the end of the game", NamedTextColor.GOLD);
                killed.showTitle(Title.title(title, subtitle));

                // broadcast killed announcement
                broadcastToPlayers("<red><bold>" + killed.getName() + " has been eliminated!");

                // give points
                killedHNSP.eliminiated = true;

                killerHNSP.kills++;
                killerHNSP.killedPlayers.add(killed);

            }

        }

    }

    public boolean playerIsHider(Player player) {
        for (HideAndSeekPlayer p : hiders) {
            if (p.player.getUniqueId().compareTo(player.getUniqueId()) == 0) {

                return true;

            }
        }
        return false;
    }

}
