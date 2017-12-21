package me.koenn.kingdomwars.game.events;

import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.event.HandlerList;

public class GamePointCapEvent extends GameEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Team captured;

    public GamePointCapEvent(Game game, Team captured) {
        super(game);
        this.captured = captured;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Team getCaptured() {
        return captured;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
