package me.koenn.kingdomwars.tracker;

import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.game.events.GameKillEvent;
import me.koenn.kingdomwars.game.events.GamePointCapEvent;
import me.koenn.kingdomwars.game.events.GameStartEvent;
import me.koenn.kingdomwars.game.map.ControlPoint;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class GameTracker implements Listener {

    private static final SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

    private final Game game;
    private final List<String> log;

    public GameTracker(Game game) {
        this.game = game;
        this.log = new ArrayList<>();

        Bukkit.getPluginManager().registerEvents(this, KingdomWars.getInstance());
    }

    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameStart(GameStartEvent event) {
        if (!event.isGame(this.game)) {
            return;
        }

        this.log(
                String.format(
                        "[%s] Game %s on map %s has started! (0.%s.%s)",
                        timestamp.format(new Date()),
                        Integer.toHexString(this.game.hashCode()),
                        this.game.getMap().getName(), this.hashCode(),
                        convertToBase64(this.game.getMap().getName())
                )
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameKill(GameKillEvent event) {
        if (!event.isGame(this.game)) {
            return;
        }

        String killer = event.getKiller().getName();
        String killed = event.getKilled().getName();
        String action = event.getType().name();

        this.log(
                String.format(
                        "[%s] %s killed %s while %s (1.%s.%s.%s)",
                        timestamp.format(new Date()),
                        killer, killed, action,
                        killer, killed, action
                )
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGamePointCap(GamePointCapEvent event) {
        if (!event.isGame(this.game)) {
            return;
        }

        Team winner = event.getCaptured();
        ControlPoint point = this.game.getMap().getPoints()[winner.getIndex()];
        List<Player>[] captured = point.getPlayersOnPoint(game);
        int attackers = captured[winner.getIndex()].size();
        int defenders = captured[winner.getOpponent().getIndex()].size();

        this.log(
                String.format(
                        "[%s] Team %s captured the point with %s attackers and %s defenders (2.%s.%s.%s)",
                        timestamp.format(new Date()),
                        winner.getIndex(), attackers, defenders,
                        winner.getIndex(), attackers, defenders
                )
        );
    }

    private void log(String message) {
        this.log.add(message);
        System.out.println(message);
    }

    private String convertToBase64(String string) {
        return new String(Base64.getEncoder().encode(string.getBytes()));
    }
}
