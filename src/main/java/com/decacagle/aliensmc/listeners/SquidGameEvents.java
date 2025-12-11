package com.decacagle.aliensmc.listeners;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.games.*;
import com.decacagle.aliensmc.games.blocks.GlassBridgeSpace;
import com.decacagle.aliensmc.utilities.GameManager;
import com.decacagle.aliensmc.utilities.Globals;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class SquidGameEvents implements Listener {

    private GameManager gameManager;
    private AliensGames plugin;

    public SquidGameEvents(AliensGames plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    // Red Light Green Light Events

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (gameManager.getCurrentGame() instanceof RedLightGreenLight rlgl) {
            if (Globals.playerInList(player, rlgl.participants)) {
                if (rlgl.gameRunning && rlgl.redLight) {

                    Location from = event.getFrom();
                    Location to = event.getTo();

                    if ((((int) from.getX()) != ((int) to.getX())) || (((int) from.getY()) != ((int) to.getY())) || (((int) from.getZ()) != ((int) to.getZ()))) {

                        rlgl.registerElimination(player);

                    }

                }
            }
        } else if (gameManager.getCurrentGame() instanceof HideAndSeek hns) {

            if (hns.gameRunning) {

                if (hns.playerIsHider(player)) {

                    int playerX = (int) player.getLocation().getX();
                    int playerY = (int) player.getLocation().getY();
                    int playerZ = (int) player.getLocation().getZ();

                    if ((playerX == ((int) hns.escapePoint.getX()) && playerY == ((int) hns.escapePoint.getY()) && playerZ == ((int) hns.escapePoint.getZ()))) {
                        hns.registerEscape(player);
                        player.teleport(hns.spawnpoint);
                        player.setGameMode(GameMode.SPECTATOR);
                    }

                }

            }

        } else if (gameManager.getCurrentGame() instanceof GlassBridge gb) {
            if (gb.gameRunning) {
                if (Globals.playerInList(player, gb.participants)) {
                    if (player.getZ() > 1210 && player.getZ() < 1281) {
                        if (player.getY() >= 95 && player.getY() <= 96.1) {
                            double x = player.getX();
                            if (x >= 967.025 && x <= 967.925) {
                                Location l = new Location(gb.world, (int) x, 95, (int) player.getZ());
                                if (l.getBlock().getType() != Material.AIR) {
                                    gb.world.setBlockData((int) x, 95, (int) player.getZ(), Material.AIR.createBlockData());
                                }
                            } else if (x >= 970.025 && x <= 970.925) {
                                Location l = new Location(gb.world, (int) x, 95, (int) player.getZ());
                                if (l.getBlock().getType() != Material.AIR) {
                                    gb.world.setBlockData((int) x, 95, (int) player.getZ(), Material.AIR.createBlockData());
                                }
                            } else if (x >= 971.025 && x <= 971.925) {
                                Location l = new Location(gb.world, (int) x, 95, (int) player.getZ());
                                if (l.getBlock().getType() != Material.AIR) {
                                    gb.world.setBlockData((int) x, 95, (int) player.getZ(), Material.AIR.createBlockData());
                                }
                            } else if (x >= 974.025 && x <= 974.925) {
                                Location l = new Location(gb.world, (int) x, 95, (int) player.getZ());
                                if (l.getBlock().getType() != Material.AIR) {
                                    gb.world.setBlockData((int) x, 95, (int) player.getZ(), Material.AIR.createBlockData());
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    // Hide and Seek events

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        if (gameManager.getCurrentGame() instanceof HideAndSeek hns) {
            if (Globals.playerInList(player, hns.participants)) {
                event.setCancelled(true);
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(player.getLocation());

                EntityDamageEvent lastDamageEvent = player.getLastDamageCause();
                if (lastDamageEvent == null) {
                    hns.registerElimination(player);
                } else {
                    if (lastDamageEvent instanceof EntityDamageByEntityEvent damageByEntityEvent) {
                        Entity damager = damageByEntityEvent.getDamager();

                        if (damager instanceof Player attacker) {
                            hns.registerKill(player, attacker);
                        } else {
                            hns.registerElimination(player);
                        }

                    } else {
                        hns.registerElimination(player);
                    }
                }

            }
        } else if (gameManager.getCurrentGame() instanceof SpecialGame sg) {
            if (Globals.playerInList(player, sg.participants)) {
                event.setCancelled(true);
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(player.getLocation());

                EntityDamageEvent lastDamageEvent = player.getLastDamageCause();
                if (lastDamageEvent == null) {
                    sg.registerElimination(player);
                } else {
                    if (lastDamageEvent instanceof EntityDamageByEntityEvent damageByEntityEvent) {
                        Entity damager = damageByEntityEvent.getDamager();

                        if (damager instanceof Player attacker) {
                            sg.registerKill(player, attacker);
                        } else {
                            sg.registerElimination(player);
                        }

                    } else {
                        sg.registerElimination(player);
                    }
                }

            }
        } else if (gameManager.getCurrentGame() instanceof GlassBridge gb) {
            if (Globals.playerInList(player, gb.participants)) {
                event.setCancelled(true);
                gb.registerElimination(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Game currentGame = gameManager.getCurrentGame();

        // disabling PVP in pre-game lobby
        if (currentGame != null) {
            if (!currentGame.gameRunning) {
                if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player defender) {

                    if (Globals.playerInList(attacker, currentGame.participants) && Globals.playerInList(defender, currentGame.participants)) {
                        event.setCancelled(true);
                    }

                }
            }
        }

        if (gameManager.getCurrentGame() instanceof HideAndSeek hns) {
            if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player defender) {
                if (Globals.playerInList(attacker, hns.participants) && Globals.playerInList(defender, hns.participants)) {
                    if (hns.playerIsSeeker(attacker) && hns.playerIsSeeker(defender)) {
                        attacker.sendRichMessage("<red>You can't attack another seeker!");
                        event.setCancelled(true);
                    }
                }
            }
        } else if (gameManager.getCurrentGame() instanceof RedLightGreenLight rlgl) {
            if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player defender) {
                if (Globals.playerInList(attacker, rlgl.participants) && Globals.playerInList(defender, rlgl.participants)) {
                    event.setCancelled(true);
                }
            }
        } else if (gameManager.getCurrentGame() instanceof GlassBridge gb) {
            if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player defender) {
                if (Globals.playerInList(attacker, gb.participants) && Globals.playerInList(defender, gb.participants)) {
                    event.setCancelled(true);
                }
            }
        } else if (gameManager.getCurrentGame() instanceof SpecialGame sg) {
            if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player defender) {
                if (Globals.playerInList(attacker, sg.participants) && Globals.playerInList(defender, sg.participants)) {
                    if (!sg.gameStarted)
                        event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (gameManager.getCurrentGame() instanceof HideAndSeek hns) {
            Player player = event.getPlayer();
            if (Globals.playerInList(player, hns.participants)) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Block block = event.getClickedBlock();

                    if (block != null && block.getType().name().contains("DOOR") && !hns.unlockedDoorsLocations.contains(block.getLocation())) {
                        ItemStack key = player.getInventory().getItemInMainHand();

                        if (block.getBlockData().getMaterial() == Material.CRIMSON_DOOR && isPurpleKey(key)) {
                            hns.reportDoorOpen(player, block.getLocation());
                            plugin.logger.info("door opened at " + block.getX() + " " + block.getY() + " " + block.getZ());
                        } else if (block.getBlockData().getMaterial() == Material.WARPED_DOOR && isTielKey(key)) {
                            hns.reportDoorOpen(player, block.getLocation());
                            plugin.logger.info("door opened at " + block.getX() + " " + block.getY() + " " + block.getZ());
                        } else if (block.getBlockData().getMaterial() == Material.SPRUCE_DOOR && isBrownKey(key)) {
                            hns.reportDoorOpen(player, block.getLocation());
                            plugin.logger.info("door opened at " + block.getX() + " " + block.getY() + " " + block.getZ());
                        } else {
                            player.sendRichMessage("<red>You don't have the key to this door!");
                            event.setCancelled(true);
                        }

                    }
                }
            }
        } else if (gameManager.getCurrentGame() instanceof SpecialGame sg) {
            Player player = event.getPlayer();
            if (Globals.playerInList(player, sg.participants)) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Block block = event.getClickedBlock();
                    if (block != null && block.getType().name().contains("DOOR")) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (gameManager.getCurrentGame() instanceof SpecialGame sg) {
            Player player = event.getPlayer();
            if (Globals.playerInList(player, sg.participants)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (gameManager.getCurrentGame() instanceof SpecialGame sg) {
            Player player = event.getPlayer();
            if (Globals.playerInList(player, sg.participants)) {
                sg.registerBlockPlace(event.getBlock().getLocation());
            }
        }
    }

    public boolean isPurpleKey(ItemStack item) {
        return Globals.displayNameEquals(item, Globals.PURPLE_KEY_NAME) && item.getType() == Globals.PURPLE_KEY_TYPE;
    }


    public boolean isTielKey(ItemStack item) {
        return Globals.displayNameEquals(item, Globals.TIEL_KEY_NAME) && item.getType() == Globals.TIEL_KEY_TYPE;
    }

    public boolean isBrownKey(ItemStack item) {
        return Globals.displayNameEquals(item, Globals.BROWN_KEY_NAME) && item.getType() == Globals.BROWN_KEY_TYPE;
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Game currentGame = gameManager.getCurrentGame();
        if (currentGame instanceof HideAndSeek hns) {
            if (event.getEntity() instanceof Player player) {
                if (Globals.playerInList(player, hns.participants)) {
                    event.setCancelled(true);
                }
            }
        } else if (currentGame instanceof GlassBridge gb) {
            if (event.getEntity() instanceof Player player) {
                if (Globals.playerInList(player, gb.participants)) {
                    event.setCancelled(true);
                }
            }
        } else if (currentGame instanceof RedLightGreenLight rlgl) {
            if (event.getEntity() instanceof Player player) {
                if (Globals.playerInList(player, rlgl.participants)) {
                    event.setCancelled(true);
                }
            }
        } else if (currentGame instanceof SpecialGame sg) {
            if (event.getEntity() instanceof Player player) {
                if (Globals.playerInList(player, sg.participants)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplosionDamage(EntityDamageEvent event) {
        Game currentGame = gameManager.getCurrentGame();
        Entity entity = event.getEntity();
        if (currentGame instanceof GlassBridge gb && entity instanceof Player player) {
            if (Globals.playerInList(player, gb.participants)) {
                if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
                        event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                    event.setDamage(0);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Game currentGame = gameManager.getCurrentGame();
        if (currentGame != null) {
            Player player = event.getPlayer();

            if (Globals.playerInList(player, currentGame.participants)) {

                currentGame.reportPlayerDeparture(player);

                if (player.getUniqueId().compareTo(currentGame.host.getUniqueId()) == 0) {

                    gameManager.reportHostDisconnect();

                }

            }

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJump(PlayerJumpEvent event) {
        if (gameManager.getCurrentGame() instanceof GlassBridge gb) {
            if (gb.gameRunning) {
                Player player = event.getPlayer();
                if (Globals.playerInList(player, gb.participants)) {
                    for (GlassBridgeSpace space : gb.spaces) {
                        if (space.playerIsOnSpace(player)) {
                            if (!space.playerOnSafeSide(player)) {
                                event.setCancelled(true);
                                space.shatter();
                            }
                            break;
                        }
                    }

                }
            }
        }
    }

}
