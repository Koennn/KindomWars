package me.koenn.kindomwars.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("unused")
public class Timer {

    private final int time;
    private final boolean repeat;
    private final Plugin plugin;

    public Timer(Plugin plugin) {
        this(0, false, plugin);
    }

    public Timer(int ticks, Plugin plugin) {
        this(ticks, false, plugin);
    }

    public Timer(int ticks, boolean repeat, Plugin plugin) {
        this.time = ticks;
        this.repeat = repeat;
        this.plugin = plugin;
    }

    public void start(Runnable callBack) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            callBack.run();
            if (this.repeat) {
                this.start(callBack);
            }
        }, this.time);
    }
}
