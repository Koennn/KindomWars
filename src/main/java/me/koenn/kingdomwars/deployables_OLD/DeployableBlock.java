package me.koenn.kingdomwars.deployables_OLD;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, May 2017
 */
public class DeployableBlock {

    private final Material type;
    private final byte data;
    private Vector offset;
    private Location location;

    public DeployableBlock(Material type, byte data, Vector offset) {
        this.type = type;
        this.data = data;
        this.offset = offset;
    }

    @SuppressWarnings("deprecation")
    public DeployableBlock(Block block) {
        this.type = block.getType();
        this.data = block.getData();
        this.location = block.getLocation();
    }

    public Material getType() {
        return type;
    }

    public byte getData() {
        return data;
    }

    public Vector getOffset() {
        return offset;
    }

    public void setOffset(Vector offset) {
        this.offset = offset;
    }

    public Location getLocation() {
        return location;
    }

    @SuppressWarnings("deprecation")
    public void replace() {
        this.location.getBlock().setType(this.type);
        this.location.getBlock().setData(this.data);
    }
}
