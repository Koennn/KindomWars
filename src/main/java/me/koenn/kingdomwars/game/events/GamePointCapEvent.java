package me.koenn.kingdomwars.game.events;

import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.List;

public class GamePointCapEvent extends GameEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Team captured;
    private final List<Player>[] onPoint;

    public GamePointCapEvent(Game game, Team captured, List<Player>[] onPoint) {
        super(game);
        this.captured = captured;
        this.onPoint = onPoint;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public List<Player>[] getOnPoint() {
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
