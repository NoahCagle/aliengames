package com.decacagle.aliensmc.listeners;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.games.*;
import com.decacagle.aliensmc.games.participants.MurderMysteryPlayer;
import com.decacagle.aliensmc.games.participants.roles.MurderMysteryRole;
import com.decacagle.aliensmc.utilities.GameManager;
import com.decacagle.aliensmc.utilities.Globals;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.projectiles.ProjectileSource;

public class MurderMysteryEvents implements Listener {

    private GameManager gameManager;
    private AliensGames plugin;

    public MurderMysteryEvents(AliensGames plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        if (gameManager.getCurrentGame() instanceof MurderMystery mm) {
            if (Globals.playerInList(player, mm.participants)) {
                event.setCancelled(true);
                player.setGameMode(GameMode.SPECTATOR);

                EntityDamageEvent lastDamageEvent = player.getLastDamageCause();
                if (lastDamageEvent == null) {
                    mm.registerElimination(player);
                } else {
                    if (lastDamageEvent instanceof EntityDamageByEntityEvent damageByEntityEvent) {
                        Entity damager = damageByEntityEvent.getDamager();

                        if (damager instanceof Player attacker) {
                            mm.registerKill(player, attacker);
                        } else {
                            mm.registerElimination(player);
                        }

                    } else {
                        mm.registerElimination(player);
                    }
                }

            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (gameManager.getCurrentGame() instanceof MurderMystery mm) {

            // block civilian hurting lawman

            if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player defender) {
                if (mm.playerIsCivilian(attacker) && mm.playerIsLawman(defender)) {
                    event.setCancelled(true);
                    attacker.sendRichMessage("<red>You can't attack the Lawman!");
                }
            }

            // check for shooting event
            if (!(event.getEntity() instanceof Player)) {
                return;
            }
            if (!(event.getDamager() instanceof Arrow arrow)) {
                return;
            }
            ProjectileSource source = arrow.getShooter();
            if (source instanceof Player shooter) {
                if (event.getEntity() instanceof Player victim) {
                    MurderMysteryPlayer mmShooter = mm.getCorrespondingPlayer(shooter);
                    MurderMysteryPlayer mmVictim = mm.getCorrespondingPlayer(victim);

                    if (mmShooter != null && mmVictim != null) {

                        if (mmShooter.role == MurderMysteryRole.LAWMAN && mmVictim.role == MurderMysteryRole.MURDERER) {

                            mm.registerKill(victim, shooter);
                            victim.setGameMode(GameMode.SPECTATOR);

                        } else if (mmShooter.role == MurderMysteryRole.LAWMAN) {

                            mm.registerKill(victim, shooter);

                            mm.registerElimination(shooter);

                            victim.setGameMode(GameMode.SPECTATOR);
                            shooter.setGameMode(GameMode.SPECTATOR);

                            shooter.sendRichMessage("<green>You killed a civilian! You have been eliminated!");

                        }

                    }

                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        if (gameManager.getCurrentGame() instanceof MurderMystery mm) {
            Player player = event.getPlayer();
            if (mm.spectators.contains(player)) {
                event.setCancelled(true);
                player.sendRichMessage("<yellow>Ssshhh... Spectators can't talk during this game.");
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Game currentGame = gameManager.getCurrentGame();
        if (currentGame instanceof MurderMystery mm) {
            if (event.getEntity() instanceof Player player) {
                if (Globals.playerInList(player, mm.participants)) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
