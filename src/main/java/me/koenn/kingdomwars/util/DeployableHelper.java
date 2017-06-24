package me.koenn.kingdomwars.util;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, May 2017
 */
public final class DeployableHelper {

    private static final BlockFace[] AXIS = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    public static BlockFace getPlayerDirection(Player player) {
        return yawToFace(player.getLocation().getYaw());
    }

    public static Vector rotateOffsetTowards(Vector offset, BlockFace towards) {
        final int index = Arrays.asList(AXIS).indexOf(towards);
        if (index == 0) {
            return offset;
        }
        return rotate(offset, index);
    }

    private static Vector rotate(Vector vector, int times) {
        vector = rotate(vector.getX(), vector.getY(), vector.getZ());

        times--;
        if (times > 0) {
            vector = rotate(vector, times);
        }

        return vector;
    }

    private static Vector rotate(double x, double y, double z) {
        return new Vector(z == 0 ? 0 : -z, y, x);
    }

    private static BlockFace yawToFace(float yaw) {
        return AXIS[Math.round(yaw / 90F) & 0x3];
    }
}
