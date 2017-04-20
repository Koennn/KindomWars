package me.koenn.kindomwars.game;

import me.koenn.kindomwars.KingdomWars;
import me.koenn.kindomwars.util.JSONManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.json.simple.JSONObject;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class MapLoader {

    public static void loadMap(String file) {
        JSONManager manager = new JSONManager(KingdomWars.getInstance(), file);
        JSONObject blueSpawn = (JSONObject) manager.getFromBody("blueSpawn");
        JSONObject redSpawn = (JSONObject) manager.getFromBody("redSpawn");
        Location blue = new Location(Bukkit.getWorld((String) blueSpawn.get("world")), (double) blueSpawn.get("x"), (double) blueSpawn.get("y"), (double) blueSpawn.get("z"));
        Location red = new Location(Bukkit.getWorld((String) redSpawn.get("world")), (double) redSpawn.get("x"), (double) redSpawn.get("y"), (double) redSpawn.get("z"));
        String name = (String) manager.getFromBody("name");

        Map.maps.add(new Map(name, blue, red));
    }
}
