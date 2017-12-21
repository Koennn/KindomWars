package me.koenn.kingdomwars.game.events;

import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.game.map.Map;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class GameKillEvent extends GameEvent {

    private static final HandlerList handlers = new HandlerList();
    private static final int DETECTION_RANGE = 24;

    private final Player killer;
    private final Player killed;
    private final KillType type;

    public GameKillEvent(Game game, Player killer, Player killed) {
        super(game);
        this.killer = killer;
        this.killed = killed;

        Map map = game.getMap();
        Team team = PlayerHelper.getTeam(killer);
        Location location = killer.getLocation();
        Location allyPoint = map.getPoints()[team.getIndex()].corners[0];
        Location enemyPoint = map.getPoints()[team.getOpponent().getIndex()].corners[0];

        if (location.distance(allyPoint) < DETECTION_RANGE) {
            this.type = KillType.DEFENDING;
        } else if (location.distance(enemyPoint) < DETECTION_RANGE) {
            this.type = KillType.ATTACKING;
        } else {
            this.type = KillType.ROAMING;
        }
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getKiller() {
        return killer;
    }

    public Player getKilled() {
        return killed;
    }

    public KillType getType() {
        return type;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public enum KillType {
        ATTACKING, DEFENDING, ROAMING
    }
}
