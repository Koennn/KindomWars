package me.koenn.kingdomwars.game.events;

import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.event.HandlerList;

public class GameFinishEvent extends GameEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Team winner;

    public GameFinishEvent(Game game, Team winner) {
        super(game);
        this.winner = winner;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Team getWinner() {
        return winner;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
