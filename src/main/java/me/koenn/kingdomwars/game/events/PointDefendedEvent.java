package me.koenn.kingdomwars.game.events;

import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.event.HandlerList;

public class PointDefendedEvent extends GameEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Team team;
    private final int highestProgress;
    private final int timeTaken;

    public PointDefendedEvent(Game game, Team team, int highestProgress, int timeTaken) {
        super(game);
        this.team = team;
        this.highestProgress = highestProgress;
        this.timeTaken = timeTaken;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Team getTeam() {
        return team;
    }

    public int getHighestProgress() {
        return highestProgress;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
