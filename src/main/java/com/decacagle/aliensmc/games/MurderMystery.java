package com.decacagle.aliensmc.games;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.games.participants.MurderMysteryPlayer;
import com.decacagle.aliensmc.games.participants.roles.MurderMysteryRole;
import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.*;

public class MurderMystery extends Game {

    public Location mapLoc;

    public List<MurderMysteryPlayer> players = new ArrayList<MurderMysteryPlayer>();

    public Map<Location, Material> cleanupLocations = new HashMap<>();
    public List<TextDisplay> textDisplays = new ArrayList<TextDisplay>();

    public MurderMysteryPlayer lawman, murderer;

    public final int TIME_BEFORE_START = 10;

    public final int TOTAL_DURATION_SECONDS = 60;
    public int timeRemaining = TOTAL_DURATION_SECONDS;

    public MurderMystery(AliensGames plugin, Player host) {
        super(new Location(plugin.getServer().getWorld("murdermystery"), 85.5, 69, 71.5), plugin, host, 3);
        mapLoc = new Location(world, 85.5, -18, 71.5);
        this.prettyTitle = "Murder Mystery";
    }

    public void startGame() {
        this.gameRunning = true;
        clearAllInventories();
        healAll();
        queueGameStart();
        teleportPlayersToMap();
    }

    private void queueGameStart() {
        broadcastMessageToAllPlayers("<green><bold>Game starts in " + TIME_BEFORE_START + " seconds!");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            broadcastTitleToAllPlayers(Component.text("3", NamedTextColor.GOLD, TextDecoration.BOLD), Component.text(""), Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO);
            playSoundToAllPlayers(Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 1.0f);
        }, (TIME_BEFORE_START * 20) - 60);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            broadcastTitleToAllPlayers(Component.text("2", NamedTextColor.GOLD, TextDecoration.BOLD), Component.text(""), Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO);
            playSoundToAllPlayers(Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 1.0f);
        }, (TIME_BEFORE_START * 20) - 40);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            broadcastTitleToAllPlayers(Component.text("1", NamedTextColor.GOLD, TextDecoration.BOLD), Component.text(""), Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO);
            playSoundToAllPlayers(Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 1.0f);
        }, (TIME_BEFORE_START * 20) - 20);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            this.gameStarted = true;
            Bukkit.getScheduler().runTaskLater(plugin, this::timer, 20);
            assignRoles();
        }, (TIME_BEFORE_START * 20));

    }

    private void assignRoles() {
        Collections.shuffle(participants);

        assignMurdererRole(participants.get(0));

        assignLawmanRole(participants.get(1));

        for (int i = 2; i < participants.size(); i++) {
            assignCivilianRole(participants.get(i));
        }

    }

    private void assignMurdererRole(Player player) {
        Component title = Component.text("You are the Murderer", NamedTextColor.RED, TextDecoration.BOLD);
        Component subtitle = Component.text("Kill as many players as you can without getting caught", NamedTextColor.GOLD);

        player.showTitle(Title.title(title, subtitle));

        murderer = new MurderMysteryPlayer(player, MurderMysteryRole.MURDERER);
        players.add(murderer);

        int targetSwordSlot = player.getInventory().getHeldItemSlot();
        targetSwordSlot++;
        if (targetSwordSlot >= 9) targetSwordSlot = 0;

        ItemStack netheriteSword = new ItemStack(Material.NETHERITE_SWORD, 1);

        player.getInventory().setItem(targetSwordSlot, netheriteSword);

    }

    private void assignLawmanRole(Player player) {
        Component title = Component.text("You are the Lawman", NamedTextColor.BLUE, TextDecoration.BOLD);
        Component subtitle = Component.text("Identify the Murderer and take them out", NamedTextColor.GOLD);

        player.showTitle(Title.title(title, subtitle));

        lawman = new MurderMysteryPlayer(player, MurderMysteryRole.LAWMAN);
        players.add(lawman);

        ItemStack crossbow = new ItemStack(Material.CROSSBOW, 1);
        ItemStack arrows = new ItemStack(Material.ARROW, 64);

        player.getInventory().addItem(crossbow);
        player.getInventory().addItem(arrows);

    }

    private void assignCivilianRole(Player player) {
        Component title = Component.text("You are a Civilian", NamedTextColor.GREEN, TextDecoration.BOLD);
        Component subtitle = Component.text("Stay alive for as long as possible", NamedTextColor.GOLD);

        player.showTitle(Title.title(title, subtitle));
        players.add(new MurderMysteryPlayer(player, MurderMysteryRole.CIVILIAN));

    }

    private void teleportPlayersToMap() {
        for (Player p : participants) {
            p.teleport(mapLoc);
        }
    }

    private void timer() {
        if (gameRunning) {
            timeRemaining--;

            Bukkit.getScheduler().runTaskLater(plugin, this::timer, 20);

            checkGameStatus();
        }
    }

    private void checkGameStatus() {
        if (timeRemaining <= 0) {
            broadcastMessageToAllPlayers("<yellow>Time is up, game is over!");
            this.gameRunning = false;
            this.gameEnded = true;
            plugin.gameManager.stopGame();
        } else if (lawman.eliminated) {
            broadcastMessageToAllPlayers("<yellow>The lawman has been eliminated, game is over!");
            this.gameRunning = false;
            this.gameEnded = true;
            plugin.gameManager.stopGame();
        } else if (murderer.eliminated) {
            broadcastMessageToAllPlayers("<yellow>The murderer has been eliminated, game is over!");
            this.gameRunning = false;
            this.gameEnded = true;
            plugin.gameManager.stopGame();
        } else if (allCiviliansEliminated()) {
            broadcastMessageToAllPlayers("<yellow>The murderer has killed all the civilians, game is over!");
            this.gameRunning = false;
            this.gameEnded = true;
            plugin.gameManager.stopGame();
        }
    }

    private boolean allCiviliansEliminated() {
        for (MurderMysteryPlayer p : players) {
            if (p.role == MurderMysteryRole.CIVILIAN && !p.eliminated) return false;
        }
        return true;
    }

    public MurderMysteryPlayer getCorrespondingPlayer(Player player) {
        for (MurderMysteryPlayer mmp : players) {
            if (mmp.player.getUniqueId().compareTo(player.getUniqueId()) == 0) {
                return mmp;
            }
        }

        return null;
    }

    public void cleanup() {
        for (Player p : participants) {
            p.teleport(world.getSpawnLocation());
        }
        replaceHeadLocations();
        removeTextDisplays();
        clearAllInventories();
        setAllGamemodes(GameMode.ADVENTURE);
    }

    public void registerElimination(Player player) {
        MurderMysteryPlayer mmp = getCorrespondingPlayer(player);
        if (mmp != null) {
            mmp.eliminated = true;
            placePlayersHeadAtPlayerLocation(player);
        }

    }

    public void registerKill(Player killed, Player killer) {
        MurderMysteryPlayer killedMMP = getCorrespondingPlayer(killed);
        MurderMysteryPlayer killerMMP = getCorrespondingPlayer(killer);

        if (killedMMP != null) {
            killedMMP.eliminated = true;
            placePlayersHeadAtPlayerLocation(killedMMP.player);
        }

    }

    // TODO: move this to Game class, as it will become handy for more games later
    public void placePlayersHeadAtPlayerLocation(Player player) {
        PlayerProfile skullProfile = Bukkit.createProfile(UUID.randomUUID());
        skullProfile.getTextures().setSkin(player.getPlayerProfile().getTextures().getSkin());

        Location skullLocation = player.getLocation();

        // TODO: scan downward to find floor below player

        Block currentBlock = skullLocation.getBlock();

        if (blockIsCarpet(currentBlock) || blockIsGrass(currentBlock) || blockIsAir(currentBlock)) {

            cleanupLocations.putIfAbsent(skullLocation, currentBlock.getType());

            currentBlock.setType(Material.PLAYER_HEAD, false);

            Skull skull = (Skull) currentBlock.getState();

            skull.setPlayerProfile(player.getPlayerProfile());

            skull.update();

            // place text display

            plugin.logger.info("Skull location: " + currentBlock.getLocation().toString());

        }

        randomBloodSplatterAroundLocation(skullLocation);

        TextDisplay textDisplay = world.spawn(currentBlock.getLocation().toCenterLocation().add(0, 0.5, 0), TextDisplay.class, display -> {
            display.text(Component.text(player.getName()));
            display.setBillboard(TextDisplay.Billboard.CENTER);
            display.setSeeThrough(false);
            display.setShadowed(true);
            display.setAlignment(TextDisplay.TextAlignment.CENTER);
        });

        textDisplays.add(textDisplay);

    }

    private void randomBloodSplatterAroundLocation(Location location) {

        for (int z = -1; z <= 1; z++) {
            for (int x = -1; x <= 1; x++) {

                Location currentLoc = new Location(location.getWorld(), location.getX() + x, location.getY(), location.getZ() + z);

                if (x != 0 && z != 0) {
                    int rand = (int) (Math.random() * 4);
                    if (rand != 0) {

                        Block currentBlock = currentLoc.getBlock();
                        if (blockIsCarpet(currentBlock) || blockIsGrass(currentBlock) || blockIsAir(currentBlock)) {

                            plugin.logger.info("rand: " + rand);
                            plugin.logger.info("Placing redstone wire!");

                            cleanupLocations.putIfAbsent(currentLoc, currentLoc.getBlock().getType());

                            world.setBlockData(currentLoc, Material.REDSTONE_WIRE.createBlockData());
                        }
                    }
                }

            }
        }

    }

    private boolean blockIsCarpet(Block block) {
        return Tag.WOOL_CARPETS.isTagged(block.getType());
    }

    private boolean blockIsGrass(Block block) {
        return block.getType() == Material.TALL_GRASS || block.getType() == Material.SHORT_GRASS;
    }

    private boolean blockIsAir(Block block) {
        return block.getType() == Material.AIR;
    }

    private void replaceHeadLocations() {
        for (Map.Entry<Location, Material> entry : cleanupLocations.entrySet()) {
            Location loc = entry.getKey();
            Material mat = entry.getValue();

            world.setBlockData(loc, mat.createBlockData());

        }
    }

    private void removeTextDisplays() {
        for (TextDisplay display : textDisplays) {
            display.remove();
        }
    }

    public void reportPlayerDeparture(Player player) {
        super.reportPlayerDeparture(player);

        if (gameRunning) {

            MurderMysteryPlayer mmPlayer = null;

            for (MurderMysteryPlayer p : players) {
                if (p.player.getUniqueId().compareTo(player.getUniqueId()) == 0) {
                    mmPlayer = p;
                    players.remove(p);
                    break;
                }
            }

            if (mmPlayer != null) {

                mmPlayer.eliminated = true;
                mmPlayer.connected = false;
                mmPlayer.points = 0;

                spectators.add(mmPlayer.player);

            } else {
                plugin.logger.severe("Tried to find " + player.getName() + " for a departure report, but couldn't find them!");
            }

        }

    }

}
