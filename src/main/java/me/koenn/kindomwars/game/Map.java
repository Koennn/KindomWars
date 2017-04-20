package me.koenn.kindomwars.game;

import org.bukkit.Location;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class Map {

    private final Location blueSpawn;
    private final Location redSpawn;

    public Map(Location blueSpawn, Location redSpawn) {
        this.blueSpawn = blueSpawn;
        this.redSpawn = redSpawn;
    }

    public Location getBlueSpawn() {
        return blueSpawn;
    }

    public Location getRedSpawn() {
        return redSpawn;
    }
}
