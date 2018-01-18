package me.koenn.kingdomwars.traits;

import me.koenn.kingdomwars.KingdomWars;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class Trait implements Runnable, Listener {

    private final int taskId;

    public Trait() {
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(KingdomWars.getInstance(), this, 0, 5);
        Bukkit.getPluginManager().registerEvents(this, KingdomWars.getInstance());
    }

    protected abstract void disable();

    public void stop() {
        Bukkit.getScheduler().cancelTask(this.taskId);
        HandlerList.unregisterAll(this);

        this.disable();
    }
}
