package com.decacagle.aliensmc.listeners;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.games.Game;
import com.decacagle.aliensmc.games.GlassBridge;
import com.decacagle.aliensmc.games.HideAndSeek;
import com.decacagle.aliensmc.games.RedLightGreenLight;
import com.decacagle.aliensmc.utilities.GameManager;
import com.decacagle.aliensmc.utilities.Globals;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

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
        if (gameManager.getCurrentGame() instanceof RedLightGreenLight redLightGreenLight) {
            if (player.getGameMode() == GameMode.ADVENTURE) {
                if (redLightGreenLight.getRedLight() && redLightGreenLight.getSecondsInRedlight() > 1 && player.getLocation().getZ() > RedLightGreenLight.LINE_Z) {
                    Location from = event.getFrom();
                    Location to = event.getTo();

                    int fromX = (int) from.getX();
                    int fromY = (int) from.getY();
                    int fromZ = (int) from.getZ();

                    int toX = (int) to.getX();
                    int toY = (int) to.getY();
                    int toZ = (int) to.getZ();

                    if (fromX != toX || fromY != toY || fromZ != toZ) {
                        plugin.logger.info(player.getName() + " moved during red light!");
                        Bukkit.getScheduler().runTaskLater(plugin, () -> redLightGreenLight.killPlayer(player), (int) (Math.random() * 40));
                    }
                }
            }
        } else if (gameManager.getCurrentGame() instanceof HideAndSeek hns) {

            if (hns.gameRunning) {

                if (hns.playerIsHider(player)) {

                    int playerX = (int) player.getLocation().getX();
                    int playerY = (int) player.getLocation().getY();
                    int playerZ = (int) player.getLocation().getZ();

                    if ((playerX == ((int) HideAndSeek.ESCAPE_POINT_1.getX()) && playerY == ((int) HideAndSeek.ESCAPE_POINT_1.getY()) && playerZ == ((int) HideAndSeek.ESCAPE_POINT_1.getZ())) || (playerX == ((int) HideAndSeek.ESCAPE_POINT_2.getX()) && playerY == ((int) HideAndSeek.ESCAPE_POINT_2.getY()) && playerZ == ((int) HideAndSeek.ESCAPE_POINT_2.getZ()))) {
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
                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    Directional end_rod = (Directional) Material.END_ROD.createBlockData();
                                    end_rod.setFacing(BlockFace.NORTH);
                                    gb.world.setBlockData((int) x, 95, (int) player.getZ(), end_rod);
                                }, 5);
                            } else if (x >= 970.025 && x <= 970.925) {
                                Location l = new Location(gb.world, (int) x, 95, (int) player.getZ());
                                if (l.getBlock().getType() != Material.AIR) {
                                    gb.world.setBlockData((int) x, 95, (int) player.getZ(), Material.AIR.createBlockData());
                                }
                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    Directional end_rod = (Directional) Material.END_ROD.createBlockData();
                                    end_rod.setFacing(BlockFace.NORTH);
                                    gb.world.setBlockData((int) x, 95, (int) player.getZ(), end_rod);
                                }, 5);
                            } else if (x >= 971.025 && x <= 971.925) {
                                Location l = new Location(gb.world, (int) x, 95, (int) player.getZ());
                                if (l.getBlock().getType() != Material.AIR) {
                                    gb.world.setBlockData((int) x, 95, (int) player.getZ(), Material.AIR.createBlockData());
                                }
                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    Directional end_rod = (Directional) Material.END_ROD.createBlockData();
                                    end_rod.setFacing(BlockFace.NORTH);
                                    gb.world.setBlockData((int) x, 95, (int) player.getZ(), end_rod);
                                }, 5);
                            } else if (x >= 974.025 && x <= 974.925) {
                                Location l = new Location(gb.world, (int) x, 95, (int) player.getZ());
                                if (l.getBlock().getType() != Material.AIR) {
                                    gb.world.setBlockData((int) x, 95, (int) player.getZ(), Material.AIR.createBlockData());
                                }
                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    Directional end_rod = (Directional) Material.END_ROD.createBlockData();
                                    end_rod.setFacing(BlockFace.NORTH);
                                    gb.world.setBlockData((int) x, 95, (int) player.getZ(), end_rod);
                                }, 5);
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
        } else if (gameManager.getCurrentGame() instanceof GlassBridge gb) {
            if (Globals.playerInList(player, gb.participants)) {
                event.setCancelled(true);
                gb.registerElimination(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (gameManager.getCurrentGame() instanceof HideAndSeek hns) {
            if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player defender) {
                if (Globals.playerInList(attacker, hns.participants) && Globals.playerInList(defender, hns.participants)) {
                    if (isSeeker(attacker) && isSeeker(defender)) {
                        attacker.sendRichMessage("<red>You can't attack another seeker!");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    public boolean isSeeker(Player player) {
        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate != null) {

            if (chestplate.getType() == Material.LEATHER_CHESTPLATE) {
                LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();

                if (meta.getColor().asRGB() == Color.RED.asRGB()) {
                    return true;
                } else {
                    System.out.println("Chestplate color is " + Integer.toHexString(meta.getColor().asRGB()));
                }

            } else {
                System.out.println("Chestplate type is " + chestplate.getType().name());
            }

        } else {
            System.out.println("Chestplate is null!");
        }

        return false;

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (gameManager.getCurrentGame() instanceof HideAndSeek hns) {
            Player player = event.getPlayer();
            if (Globals.playerInList(player, hns.participants)) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Block block = event.getClickedBlock();

                    if (block != null && block.getType().name().contains("DOOR")) {
                        ItemStack key = player.getInventory().getItemInMainHand();

                        if (block.getBlockData().getMaterial() == Material.CRIMSON_DOOR && !isPurpleKey(key)) {
                            player.sendRichMessage("<red>You don't have the key to this door!");
                            event.setCancelled(true);
                        } else if (block.getBlockData().getMaterial() == Material.WARPED_DOOR && !isTielKey(key)) {
                            player.sendRichMessage("<red>You don't have the key to this door!");
                            event.setCancelled(true);
                        } else if (block.getBlockData().getMaterial() == Material.SPRUCE_DOOR && !isBrownKey(key)) {
                            player.sendRichMessage("<red>You don't have the key to this door!");
                            event.setCancelled(true);
                        }

                    }
                }
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

}
