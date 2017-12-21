package me.koenn.kingdomwars.game.events;

import me.koenn.kingdomwars.game.Game;
import org.bukkit.event.HandlerList;

public class GameStartEvent extends GameEvent {

    private static final HandlerList handlers = new HandlerList();

    public GameStartEvent(Game game) {
        super(game);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
