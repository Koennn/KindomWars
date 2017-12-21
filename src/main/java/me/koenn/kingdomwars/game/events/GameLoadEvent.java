package me.koenn.kingdomwars.game.events;

import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.game.TeamInfo;
import org.bukkit.event.HandlerList;

public class GameLoadEvent extends GameEvent {

    private static final HandlerList handlers = new HandlerList();

    private final TeamInfo[] teams;

    public GameLoadEvent(Game game) {
        super(game);
        this.teams = game.teams;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public TeamInfo[] getTeams() {
        return teams;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
