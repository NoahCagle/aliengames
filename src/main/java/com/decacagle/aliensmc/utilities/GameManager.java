package com.decacagle.aliensmc.utilities;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.games.Game;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public class GameManager {

    private Game currentGame;
    private AliensGames plugin;

    public GameManager(AliensGames plugin) {
        this.plugin = plugin;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public void stopGame() {
        if (currentGame != null) {
            currentGame.endGame();
            currentGame = null;
        }
    }

    public void prepareGame(Game game) {
        this.currentGame = game;
        game.prepareGame();

        Component announceMessage = Component.text()
                .append(Component.text("NEW GAME STARTING", NamedTextColor.GOLD, TextDecoration.UNDERLINED, TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.text(game.host.getName() + " is preparing to host a game of " + game.PRETTY_TITLE + "!", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("To join, use the ", NamedTextColor.GREEN))
                .append(Component.text("/agames join", NamedTextColor.GREEN, TextDecoration.BOLD))
                .append(Component.text(" command!", NamedTextColor.GREEN))
                .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/agames join"))
                .hoverEvent(Component.text("Join " + game.PRETTY_TITLE))
                .build();

        plugin.getServer().broadcast(announceMessage);

    }

    public void forceStop() {
        if (currentGame != null) {
            for (Player p : currentGame.participants) {
                p.teleport(currentGame.spawnpoint);
                p.sendRichMessage("<red>The mini-game has been forcefully terminated by an administrator.");
            }
            currentGame.gameRunning = false;
            currentGame.cleanup();
            currentGame = null;
        }
    }

    public void hostCancel() {
        if (currentGame != null) {
            for (Player p : currentGame.participants) {
                p.teleport(currentGame.world.getSpawnLocation());
                p.sendRichMessage("<red><bold>This mini-game has been cancelled by the host.");
            }
            currentGame.gameRunning = false;
            currentGame.cleanup();
            currentGame = null;
        }
    }

    public void reportHostDisconnect() {
        if (currentGame != null) {
            if (!currentGame.participants.isEmpty()) {
                Player newHost = currentGame.participants.getFirst();
                currentGame.host = newHost;
                newHost.sendRichMessage("<yellow>You are now the host of this mini-game!");

                newHost.sendRichMessage("<yellow>If you'd like to leave this mini-game, you can do so with the <bold>/agames leave</bold> command!");

                if (!currentGame.gameRunning) {
                    newHost.sendRichMessage("<yellow>When you're ready to start, type <bold>/agames start");
                }

            } else {
                hostCancel();
            }
        }
    }

}
