package me.koenn.kingdomwars.game.events;

import me.koenn.kingdomwars.game.Game;
import org.bukkit.event.Event;

public abstract class GameEvent extends Event {

    private final Game game;

    public GameEvent(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public boolean isGame(Game game) {
        return this.game.equals(game);
    }
}
