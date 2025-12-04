package com.decacagle.aliensmc.listeners;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.games.KnifeGame;
import com.decacagle.aliensmc.games.RedLightGreenLight;
import com.decacagle.aliensmc.utilities.GameManager;
import com.decacagle.aliensmc.utilities.Globals;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (gameManager.getCurrentGame() instanceof KnifeGame) {
            if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player defender) {

                if (isSeeker(attacker) && isSeeker(defender)) {
                    attacker.sendRichMessage("<red>You can't attack another seeker!");
                    event.setCancelled(true);
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
        if (gameManager.getCurrentGame() instanceof KnifeGame) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();

                if (block != null && block.getType().name().contains("DOOR")) {
                    Player player = event.getPlayer();
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

    public boolean isPurpleKey(ItemStack item) {
        return Globals.displayNameEquals(item, Globals.PURPLE_KEY_NAME) && item.getType() == Globals.PURPLE_KEY_TYPE;
    }


    public boolean isTielKey(ItemStack item) {
        return Globals.displayNameEquals(item, Globals.TIEL_KEY_NAME) && item.getType() == Globals.TIEL_KEY_TYPE;
    }

    public boolean isBrownKey(ItemStack item) {
        return Globals.displayNameEquals(item, Globals.BROWN_KEY_NAME) && item.getType() == Globals.BROWN_KEY_TYPE;
    }

    // Red Light Green Light Events

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.ADVENTURE) {
            if (gameManager.getCurrentGame() instanceof RedLightGreenLight redLightGreenLight) {
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
                        Bukkit.getScheduler().runTaskLater(plugin, () -> redLightGreenLight.killPlayer(player), (int)(Math.random() * 40));
                    }
                }
            }
        }
    }

}
