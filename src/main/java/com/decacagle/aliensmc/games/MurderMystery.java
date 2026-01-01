package com.decacagle.aliensmc.games;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.games.participants.MurderMysteryPlayer;
import com.decacagle.aliensmc.games.participants.roles.MurderMysteryRole;
import com.decacagle.aliensmc.utilities.Globals;
import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.time.Duration;
import java.util.*;

public class MurderMystery extends Game {

    public Location mapLoc;

    public List<MurderMysteryPlayer> players = new ArrayList<MurderMysteryPlayer>();

    public Map<Location, Material> cleanupLocations = new HashMap<>();
    public List<TextDisplay> textDisplays = new ArrayList<TextDisplay>();

    public MurderMysteryPlayer lawman, murderer;

    public int TIME_BEFORE_START = 10;

    public int TOTAL_DURATION_SECONDS = 300;
    public int timeRemaining;

    public MurderMystery(AliensGames plugin, Player host) {
        super(new Location(plugin.getServer().getWorld(plugin.config.gameWorldTitleMM), plugin.config.spawnpointXMM, plugin.config.spawnpointYMM, plugin.config.spawnpointZMM, (float) plugin.config.spawnpointYawMM, (float) plugin.config.spawnpointPitchMM), plugin, host, 3);
        mapLoc = new Location(world, plugin.config.mapLocXMM, plugin.config.mapLocYMM, plugin.config.mapLocZMM, (float) plugin.config.mapLocYawMM, (float) plugin.config.mapLocPitchMM);
        this.prettyTitle = plugin.config.prettyTitleMM;
        this.TOTAL_DURATION_SECONDS = plugin.config.gameDurationSecondsMM;
        timeRemaining = TOTAL_DURATION_SECONDS;
        this.TIME_BEFORE_START = plugin.config.timeBeforeStartSecondsMM;
        this.experimental = plugin.config.mmExperimental;
    }

    public void startGame() {
        if (participants.size() >= minPlayers) {
            this.gameStarted = true;
            clearAllInventories();
            healAll();
            queueGameStart();
            teleportPlayersToMap();
        } else {
            host.sendRichMessage("<red>Even in debug mode, this game absolutely requires at least " + minPlayers + " players. Things will break otherwise.");
            host.sendRichMessage("<red>uwu sowwy :3");
        }
    }

    private void queueGameStart() {
        createScoreboardWithTimer(prettyTitle);

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
            this.gameRunning = true;
            Bukkit.getScheduler().runTaskLater(plugin, this::timer, 20);
            assignRoles();
        }, (TIME_BEFORE_START * 20));

    }

    public void scoreAndSort() {

        for (MurderMysteryPlayer p : players) {
            if (p.role != MurderMysteryRole.MURDERER && p.role != MurderMysteryRole.LAWMAN) {
                int timePoints = p.secondsSurvived / 20;

                p.points += timePoints;

            }
        }

        Collections.sort(players, (o1, o2) -> {
            if (o1.points == o2.points)
                return 0;
            return o1.points < o2.points ? 1 : -1;
        });

    }

    public void endGame() {
        if (gameRunning) {
            this.gameRunning = false;
            this.gameEnded = true;

            broadcastMessageToAllPlayers("<red>" + murderer.player.getName() + " was the Murderer!");
            broadcastMessageToAllPlayers("<red>" + murderer.player.getName() + " got " + murderer.kills + " kills this game!");

            broadcastTitleToAllPlayers(Component.text("Game Over!", NamedTextColor.GREEN, TextDecoration.BOLD), Component.text(""));

            Bukkit.getScheduler().runTaskLater(plugin, this::goToLeaderboard, 50L);
        }
    }

    public void goToLeaderboard() {
        plugin.gameManager.stopGame();

        scoreAndSort();

        healAll();

        broadcastMessageToAllPlayers("<underlined><green><bold>Glass Bridge Rankings\n");

        List<Player> orderedPlayers = new ArrayList<Player>();

        int numWinners = 0;

        for (int i = 0; i < players.size(); i++) {
            MurderMysteryPlayer p = players.get(i);
            orderedPlayers.add(p.player);

            String numberColor = i == 0 ? ("<#D4AF37>") : (i == 1 ? ("<#C0C0C0>") : (i == 2 ? ("<#CD7F32>") : ("<gray>")));

            if (p.role == MurderMysteryRole.MURDERER) {
                broadcastMessageToAllPlayers(numberColor + "<bold>" + Globals.numberToPosition(i + 1) + ": <white>" + p.player.getName() + " - <red><bold>Murderer</bold></red><white> - " + p.kills + " kills - " + p.points + " points");
                plugin.pointsManager.addPoints(p.player, p.points);
            } else if (p.role == MurderMysteryRole.LAWMAN) {
                broadcastMessageToAllPlayers(numberColor + "<bold>" + Globals.numberToPosition(i + 1) + ": <white>" + p.player.getName() + " - <blue><bold>Lawman</bold></blue><white> - " + p.points + " points");
                plugin.pointsManager.addPoints(p.player, p.points);
            } else {
                broadcastMessageToAllPlayers(numberColor + "<bold>" + Globals.numberToPosition(i + 1) + ": <white>" + p.player.getName() + " - <green><bold>Civilian</bold></green><white> - " + p.points + " points");
                plugin.pointsManager.addPoints(p.player, p.points);
            }

            if (p.points > 0) numWinners++;

        }

        broadcastMessageToAllPlayers("");

        setAllGamemodes(GameMode.ADVENTURE);

        Globals.goToLeaderboard(orderedPlayers, numWinners, plugin, plugin.congratulationsSong);

        cleanup();

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
        Component title = Component.text("Murderer", NamedTextColor.RED, TextDecoration.BOLD);
        Component subtitle = Component.text("Kill as many players as you can without getting caught", NamedTextColor.GOLD);

        player.showTitle(Title.title(title, subtitle));

        this.murderer = new MurderMysteryPlayer(player, MurderMysteryRole.MURDERER);
        players.add(murderer);

        int targetSwordSlot = player.getInventory().getHeldItemSlot();
        targetSwordSlot++;
        if (targetSwordSlot >= 9) targetSwordSlot = 0;

        ItemStack netheriteSword = new ItemStack(Material.NETHERITE_SWORD, 1);

        player.getInventory().setItem(targetSwordSlot, netheriteSword);

    }

    private void assignLawmanRole(Player player) {
        Component title = Component.text("Lawman", NamedTextColor.BLUE, TextDecoration.BOLD);
        Component subtitle = Component.text("Identify the Murderer and take them out", NamedTextColor.GOLD);

        player.showTitle(Title.title(title, subtitle));

        this.lawman = new MurderMysteryPlayer(player, MurderMysteryRole.LAWMAN);
        players.add(lawman);

        ItemStack crossbow = new ItemStack(Material.CROSSBOW, 1);
        ItemStack arrows = new ItemStack(Material.ARROW, 64);

        player.getInventory().addItem(crossbow);
        player.getInventory().addItem(arrows);

        // give blue chestplate to lawman

        ItemStack lawmanChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta lawmanChestplateMeta = (LeatherArmorMeta) lawmanChestplate.getItemMeta();
        lawmanChestplateMeta.setColor(Color.BLUE);
        lawmanChestplateMeta.displayName(Component.text("Lawman's Chestplate"));
        lawmanChestplate.setItemMeta(lawmanChestplateMeta);

        player.getInventory().setChestplate(lawmanChestplate);

    }

    private void assignCivilianRole(Player player) {
        Component title = Component.text("Civilian", NamedTextColor.GREEN, TextDecoration.BOLD);
        Component subtitle = Component.text("Stay alive for as long as possible", NamedTextColor.GOLD);

        player.showTitle(Title.title(title, subtitle));
        players.add(new MurderMysteryPlayer(player, MurderMysteryRole.CIVILIAN));

        ItemStack woodenAxe = new ItemStack(Material.WOODEN_AXE, 1);
        player.getInventory().addItem(woodenAxe);

    }

    private void teleportPlayersToMap() {
        for (Player p : participants) {
            p.teleport(mapLoc);
        }
    }

    private void timer() {
        if (gameRunning) {
            timeRemaining--;

            updateTimer(Component.text("Time Remaining: "), timeRemaining);

            Bukkit.getScheduler().runTaskLater(plugin, this::timer, 20);

            checkGameStatus();
        }
    }

    private void checkGameStatus() {
        if (timeRemaining <= 0) {
            // time is up, game is over
            this.endGame();
        } else if (murderer.eliminated) {
            // murderer eliminated, game is over
            this.endGame();
        } else if (onlyMurdererRemains()) {
            // murderer killed everyone, game is over
            this.endGame();
        }
    }

    private boolean onlyMurdererRemains() {
        for (MurderMysteryPlayer p : players) {
            if ((p.role == MurderMysteryRole.CIVILIAN || p.role == MurderMysteryRole.LAWMAN) && !p.eliminated)
                return false;
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
        if (this.scoreboard != null) {
            removeScoreboard();
        }
        replaceHeadLocations();
        removeTextDisplays();
        clearAllInventories();
        setAllGamemodes(GameMode.ADVENTURE);
    }

    public void registerElimination(Player player) {
        MurderMysteryPlayer mmp = getCorrespondingPlayer(player);

        player.setGameMode(GameMode.SPECTATOR);

        spectators.add(player);

        if (mmp != null) {
            mmp.eliminated = true;
            placePlayersHeadAtPlayerLocation(player);
        }

    }

    public void registerKill(Player killed, Player killer) {
        killed.setGameMode(GameMode.SPECTATOR);
        spectators.add(killed);

        MurderMysteryPlayer killedMMP = getCorrespondingPlayer(killed);
        MurderMysteryPlayer killerMMP = getCorrespondingPlayer(killer);

        if (killedMMP != null) {
            killedMMP.eliminated = true;

            killedMMP.secondsSurvived = TOTAL_DURATION_SECONDS - timeRemaining;

            placePlayersHeadAtPlayerLocation(killedMMP.player);

            if (killerMMP != null) {
                killerMMP.kills++;

                if (killerMMP.role == MurderMysteryRole.LAWMAN && killedMMP.role == MurderMysteryRole.CIVILIAN) {

                    killer.sendRichMessage("<red>You shot a Civilian! You have been eliminated!");

                    registerElimination(killer);

                } else if (killerMMP.role == MurderMysteryRole.LAWMAN && killedMMP.role == MurderMysteryRole.MURDERER) {

                    killer.sendRichMessage("<green>You killed the Murderer!");
                    killer.sendRichMessage("<green>+10 points!");

                    lawman.points += 10;

                } else if (killerMMP.role == MurderMysteryRole.MURDERER && killedMMP.role == MurderMysteryRole.LAWMAN) {

                    broadcastMessageToAllPlayers("<red><bold>The Lawman has been eliminated!");

                    killer.sendRichMessage("<green>You killed the Lawman!");
                    killer.sendRichMessage("<green>+10 points!");

                    murderer.points += 10;

                } else if (killerMMP.role == MurderMysteryRole.MURDERER && killedMMP.role == MurderMysteryRole.CIVILIAN) {

                    killer.sendRichMessage("You killed " + killed.getName() + "!");
                    killer.sendRichMessage("<green>+5 points!");

                    murderer.points += 5;

                } else if (killerMMP.role == MurderMysteryRole.CIVILIAN && killedMMP.role == MurderMysteryRole.MURDERER) {

                    killer.sendRichMessage("You killed the Murderer!");
                    killer.sendRichMessage("<green>+20 points!");

                    killerMMP.points += 20;

                } else if (killerMMP.role == MurderMysteryRole.CIVILIAN && killedMMP.role == MurderMysteryRole.LAWMAN) {

                    broadcastMessageToAllPlayers("<red><bold>The Lawman has been eliminated!");

                    killer.sendRichMessage("<red>You killed the Lawman!");

                }

            }

        }

    }

    public boolean playerIsLawman(Player player) {
        return player.getUniqueId().compareTo(lawman.player.getUniqueId()) == 0;
    }

    public boolean playerIsCivilian(Player player) {
        return (player.getUniqueId().compareTo(lawman.player.getUniqueId()) != 0) && (player.getUniqueId().compareTo(murderer.player.getUniqueId()) != 0);
    }

    public boolean playerIsMurderer(Player player) {
        return player.getUniqueId().compareTo(murderer.player.getUniqueId()) == 0;
    }

    // TODO: move this to Game class, as it will become handy for more games later
    // of course, this functionality needs to be finished first...
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

            Rotatable rot = (Rotatable) currentBlock.getBlockData();
            rot.setRotation(yawToBlockFace(player.getLocation().getYaw()));
            currentBlock.setBlockData(rot);

            plugin.logger.info("Skull location: " + currentBlock.getLocation());

        }

        // place text display

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

    public BlockFace yawToBlockFace(float yaw) {
        yaw = (yaw % 360 + 360) % 360;

        if (yaw >= 337.5 || yaw < 22.5) return BlockFace.SOUTH;
        if (yaw < 67.5) return BlockFace.SOUTH_WEST;
        if (yaw < 112.5) return BlockFace.WEST;
        if (yaw < 157.5) return BlockFace.NORTH_WEST;
        if (yaw < 202.5) return BlockFace.NORTH;
        if (yaw < 247.5) return BlockFace.NORTH_EAST;
        if (yaw < 292.5) return BlockFace.EAST;
        return BlockFace.SOUTH_EAST;
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
