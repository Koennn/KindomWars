package me.koenn.kindomwars.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class Map {

    public static List<Map> maps = new ArrayList<>();

    private final String name;
    private final Location blueSpawn;
    private final Location redSpawn;

    public Map(String name, Location blueSpawn, Location redSpawn) {
        this.name = name;
        this.blueSpawn = blueSpawn;
        this.redSpawn = redSpawn;
    }

    public Location getBlueSpawn() {
        return blueSpawn;
    }

    public Location getRedSpawn() {
        return redSpawn;
    }

    public String getName() {
        return name;
    }

    public static Map getRandomMap() {
        Bukkit.getLogger().info(Arrays.toString(maps.toArray()));
        return maps.get(new Random().nextInt(maps.size()));
    }
}
