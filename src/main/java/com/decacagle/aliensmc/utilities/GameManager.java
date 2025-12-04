package com.decacagle.aliensmc.utilities;

import com.decacagle.aliensmc.games.Game;

public class GameManager {

    private Game currentGame;

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
    }

}
