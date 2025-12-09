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
                p.sendRichMessage("<red>The game has been forcefully terminated by an administrator.");
            }
            currentGame.gameRunning = false;
            currentGame.cleanup();
            currentGame = null;
        }
    }

}
