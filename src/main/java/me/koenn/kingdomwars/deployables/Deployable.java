package me.koenn.kingdomwars.deployables;

import org.bukkit.Location;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class Deployable {

    private final Location location;
    protected DeployableExecutor executor;

    public Deployable(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public DeployableExecutor getExecutor() {
        return executor;
    }
}
