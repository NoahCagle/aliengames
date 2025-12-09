package com.decacagle.aliensmc.commands;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.games.Game;
import com.decacagle.aliensmc.games.GlassBridge;
import com.decacagle.aliensmc.games.HideAndSeek;
import com.decacagle.aliensmc.games.RedLightGreenLight;
import com.decacagle.aliensmc.utilities.GameManager;
import com.decacagle.aliensmc.utilities.Globals;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

// TODO: clean this shit up what the fuck have i done here...
public class GamesCommand implements BasicCommand {

    private GameManager gameManager;
    private AliensGames plugin;

    private boolean showingKeyLocs = false;

    public GamesCommand(AliensGames plugin, GameManager gameManager) {
        this.gameManager = gameManager;
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("host")) {
                if (gameManager.getCurrentGame() != null) {
                    commandSourceStack.getSender().sendRichMessage("<red><bold>There is already a game running!");
                } else {
                    if (commandSourceStack.getSender() instanceof Player host) {
                        if (args[1].equalsIgnoreCase("rlgl") || args[1].equalsIgnoreCase("redlightgreenlight")) {
                            gameManager.prepareGame(new RedLightGreenLight(plugin, host));
                            host.sendRichMessage("Preparing to play " + RedLightGreenLight.PRETTY_TITLE);
                            host.sendRichMessage("<yellow>When you're ready to start, type <bold>/agames start");
//                            host.sendRichMessage("<red>Red Light Green Light is under construction!");
//                            host.sendRichMessage("<red>sowwy... :(");
                        } else if (args[1].equalsIgnoreCase("hns") || args[1].equalsIgnoreCase("hideandseek")) {
                            gameManager.prepareGame(new HideAndSeek(plugin, host));
                            host.sendRichMessage("Preparing to play " + HideAndSeek.PRETTY_TITLE);
                            host.sendRichMessage("<yellow>When you're ready to start, type <bold>/agames start");
                        } else if (args[1].equalsIgnoreCase("gb") || args[1].equalsIgnoreCase("glassbridge")) {
                            gameManager.prepareGame(new GlassBridge(plugin, host));
                            host.sendRichMessage("Preparing to play " + HideAndSeek.PRETTY_TITLE);
                            host.sendRichMessage("<yellow>When you're ready to start, type <bold>/agames start");
                        } else {
                            host.sendRichMessage("<gold><bold>Hostable Games:");
                            host.sendRichMessage("<green>/agames host rlgl - Host a game of Red Light Green Light");
                            host.sendRichMessage("<green>/agames host hns - Host a game of Hide And Seek");
                            host.sendRichMessage("<green>/agames host gb - Host a game of Glass Bridge");
                        }
                    } else {
                        commandSourceStack.getSender().sendRichMessage("<red><bold>Only players can play games!");
                    }
                }
            }
        } else if (args.length == 1) {
            if (commandSourceStack.getSender() instanceof Player sender) {
                if (args[0].equalsIgnoreCase("start")) {
                    if (gameManager.getCurrentGame() != null) {
                        Game currentGame = gameManager.getCurrentGame();
                        if (sender.getUniqueId().compareTo(currentGame.host.getUniqueId()) == 0) {
                            if (!currentGame.gameRunning) {
                                commandSourceStack.getSender().sendRichMessage("<green>Starting active game!");
                                gameManager.getCurrentGame().startGame();
                            } else {
                                commandSourceStack.getSender().sendRichMessage("<red>The game has already been started!");
                            }
                        } else {
                            sender.sendRichMessage("<red><bold>You are not the host of this game!");
                            sender.sendRichMessage("<red>" + currentGame.host.getName() + " is the host of this game!");
                        }
                    } else {
                        sender.sendRichMessage("<red>There is no active game!");
                    }
                } else if (args[0].equalsIgnoreCase("join")) {
                    if (gameManager.getCurrentGame() != null) {
                        if (!Globals.playerInList(sender, gameManager.getCurrentGame().participants)) {
                            gameManager.getCurrentGame().addParticipant(sender);
                        } else {
                            sender.sendRichMessage("<red>You've already joined this game!");
                        }
                    } else {
                        sender.sendRichMessage("<red>There is no active game!");
                    }
                } else if (args[0].equalsIgnoreCase("host")) {
                    sender.sendRichMessage("<gold><bold>Hostable Games:");
                    sender.sendRichMessage("<green>/agames host rlgl - Host a game of Red Light Green Light");
                    sender.sendRichMessage("<green>/agames host hns - Host a game of Hide And Seek");
                    sender.sendRichMessage("<green>/agames host gb - Host a game of Glass Bridge");
                } else if (args[0].equalsIgnoreCase("keylocs")) {
                    World world = plugin.getServer().getWorld("squidgame");

                    showingKeyLocs = !showingKeyLocs;

                    commandSourceStack.getSender().sendRichMessage(showingKeyLocs ? "Showing key locations!" : "Hiding key locations!");

                    for (Vector v : HideAndSeek.KEY_LOCATIONS) {
                        Material mat = (showingKeyLocs ? Material.GLOWSTONE : Material.SNOW);
                        world.setBlockData((int) v.getX(), (int) v.getY(), (int) v.getZ(), mat.createBlockData());
                    }

                } else if (args[0].equalsIgnoreCase("keychest")) {
                    sender.sendRichMessage("<gold>Placing key chest!");
                    placeKeyChest(sender);
                }
            } else if (args[0].equalsIgnoreCase("keylocs")) {
                World world = plugin.getServer().getWorld("squidgame");

                showingKeyLocs = !showingKeyLocs;

                commandSourceStack.getSender().sendRichMessage(showingKeyLocs ? "Showing key locations!" : "Hiding key locations!");

                for (Vector v : HideAndSeek.KEY_LOCATIONS) {
                    Material mat = (showingKeyLocs ? Material.GLOWSTONE : Material.SNOW);
                    world.setBlockData((int) v.getX(), (int) v.getY(), (int) v.getZ(), mat.createBlockData());
                }

            } else {
                commandSourceStack.getSender().sendRichMessage("<red><bold>Only players can play games!");
            }
        } else if (args.length == 0) {
            // send list of command options
        }

    }

    public void placeKeyChest(Player player) {
        ItemStack key = getPurpleKey();
        if (key == null) {
            plugin.getLogger().warning("Key item is null");
        } else if (key.getType() == Material.AIR) {
            plugin.getLogger().warning("Key item is AIR!");
        }

        Location loc = player.getLocation().subtract(1, 0, 0);

        Block b = loc.getBlock();

        b.setType(Material.CHEST);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BlockState state = b.getState();
            if (key == null) {
                plugin.getLogger().warning("Key item is null");
            } else if (key.getType() == Material.AIR) {
                plugin.getLogger().warning("Key item is AIR!");
            }
            if (state instanceof Chest chest) {
                plugin.logger.info("State is a barrel!");
                chest.getBlockInventory().setItem(13, key);
                player.getInventory().setItem(2, key);
            } else plugin.logger.info("State is NOT a barrel!");
        }, 60);

    }

    public ItemStack getPurpleKey() {
        ItemStack purpleKey = new ItemStack(Globals.PURPLE_KEY_TYPE);
        ItemMeta purpleMeta = purpleKey.getItemMeta();

        purpleMeta.displayName(Component.text(Globals.PURPLE_KEY_NAME)
                .color(NamedTextColor.LIGHT_PURPLE)
                .decoration(TextDecoration.ITALIC, false));
        purpleKey.setItemMeta(purpleMeta);

        return purpleKey;
    }
}
