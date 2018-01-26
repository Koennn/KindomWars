package me.koenn.kingdomwars.game.events;

import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.event.HandlerList;

import java.util.List;
import java.util.UUID;

public class GamePointCapEvent extends GameEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Team captured;
    private final List<UUID>[] onPoint;

    public GamePointCapEvent(Game game, Team captured, List<UUID>[] onPoint) {
        super(game);
        this.captured = captured;
        this.onPoint = onPoint;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public List<UUID>[] getOnPoint() {
        return onPoint;
    }

    public Team getCaptured() {
        return captured;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
