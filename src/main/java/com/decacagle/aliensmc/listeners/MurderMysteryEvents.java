package com.decacagle.aliensmc.listeners;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.games.MurderMystery;
import com.decacagle.aliensmc.games.participants.MurderMysteryPlayer;
import com.decacagle.aliensmc.games.participants.roles.MurderMysteryRole;
import com.decacagle.aliensmc.utilities.GameManager;
import com.decacagle.aliensmc.utilities.Globals;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.projectiles.ProjectileSource;

public class MurderMysteryEvents implements Listener {

    private GameManager gameManager;
    private AliensGames plugin;

    public MurderMysteryEvents(AliensGames plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @EventHandler
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

}
