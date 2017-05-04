package me.koenn.kingdomwars.deployables;

import org.bukkit.Material;
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

    public DeployableBlock(Material type, byte data, Vector offset) {
        this.type = type;
        this.data = data;
        this.offset = offset;
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
}
