package me.koenn.kingdomwars.tracker;

import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.game.events.GamePointCapEvent;
import me.koenn.kingdomwars.game.events.PointDefendedEvent;
import me.koenn.kingdomwars.game.map.ControlPoint;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class PointTracker implements Listener, Runnable {

    private static final int REFRESH_RATE = 5;

    private final ControlPoint point;
    private final Game game;
    private final int taskId;

    private int highestProgress;
    private boolean started;
    private int timeTaken;

    public PointTracker(ControlPoint point, Game game) {
        this.point = point;
        this.game = game;

        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(KingdomWars.getInstance(), this, 0, REFRESH_RATE);
    }

    private void resetProgress() {
        this.started = false;
        this.highestProgress = 0;
    }

    private void updateProgress() {
        this.highestProgress = this.point.captureProgress;
        this.timeTaken += REFRESH_RATE;
    }

    public void disable() {
        Bukkit.getScheduler().cancelTask(this.taskId);
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGamePointCap(GamePointCapEvent event) {
        if (event.getGame().getMap().getPoints()[event.getCaptured().getIndex()].equals(this.point)) {
            this.resetProgress();
        }
    }

    @Override
    public void run() {
        if (this.started) {
            if (this.point.captureProgress > this.highestProgress) {
                this.updateProgress();
            } else if (this.point.captureProgress == 0) {
                Bukkit.getPluginManager().callEvent(
                        new PointDefendedEvent(this.game, this.point.owningTeam, this.highestProgress, this.timeTaken)
                );
                this.resetProgress();
            }
        } else {
            if (this.point.captureProgress > 0) {
                this.started = true;
                this.updateProgress();
            }
        }
    }
}
