package me.koenn.kingdomwars.stats;

import me.koenn.core.registry.Registry;
import me.koenn.kingdomwars.game.classes.Class;
import me.koenn.kingdomwars.game.classes.ClassLoader;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, June 2017
 */
public class PlayerStats {

    public static final Registry<PlayerStats> statsRegistry = new Registry<>(playerStats -> playerStats.uuid.toString());

    private final UUID uuid;
    private final HashMap<Class, Level> levels = new HashMap<>();
    private final HashMap<String, Integer> stats = new HashMap<>();

    public PlayerStats(UUID uuid) {
        this.uuid = uuid;
        ClassLoader.getClasses().forEach(cl -> this.levels.put(cl, new Level(1, 0)));
    }

    public PlayerStats(UUID uuid, JSONObject json) {
        this.uuid = uuid;
        JSONObject levels = (JSONObject) json.get("levels");
        JSONObject stats = (JSONObject) json.get("stats");

        levels.keySet().forEach(key -> this.levels.put(ClassLoader.getClass((String) key), new Level((JSONObject) levels.get(key))));
        stats.keySet().forEach(key -> this.stats.put((String) key, Math.toIntExact((Long) stats.get(key))));
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        JSONObject levels = new JSONObject();
        JSONObject stats = new JSONObject();

        this.levels.keySet().forEach(key -> levels.put(key.getName(), this.levels.get(key).toJSON()));
        this.stats.keySet().forEach(key -> stats.put(key, this.stats.get(key)));

        json.put("levels", levels);
        json.put("stats", stats);
        return json;
    }

    public Level getLevel(Class cl) {
        return this.levels.get(cl);
    }

    public int getStat(String name) {
        return this.stats.getOrDefault(name, 0);
    }

    public Set<String> getStats() {
        return stats.keySet();
    }
}
