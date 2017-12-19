package me.koenn.kingdomwars.game;

import me.koenn.core.data.JSONManager;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.map.Map;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class MapLoader {

    public static void loadMap(String file) {
        Map.maps.register(new Map(new JSONManager(KingdomWars.getInstance(), file).getBody()));
    }
}
