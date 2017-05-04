package me.koenn.kingdomwars.deployables;

import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.util.DeployableHelper;
import me.koenn.kingdomwars.util.NBTUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.Tag;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
@SuppressWarnings("deprecation")
public class TurretDeployable extends Deployable {

    private final CompoundTag main;
    private final CompoundTag properties;

    public TurretDeployable(final Location location) {
        super(location);
        this.main = DeployableLoader.deployables.get("turret");
        this.properties = NBTUtil.getChildTag(this.main.getValue(), "properties", CompoundTag.class);
        final ListTag phases = NBTUtil.getChildTag(NBTUtil.getChildTag(this.main.getValue(), "construction", CompoundTag.class).getValue(), "phases", ListTag.class);

        this.executor = new DeployableExecutor() {
            private boolean constructed = false;
            private Player owner;
            private Player locked;

            @Override
            public void construct(Player player) {
                this.owner = player;

                final BlockFace facing = DeployableHelper.getPlayerDirection(player);
                int highestDelay = 0;

                for (Tag phase : phases.getValue()) {
                    CompoundTag phaseTag = (CompoundTag) phase;
                    int delay = NBTUtil.getChildTag(phaseTag.getValue(), "delay", IntTag.class).getValue();
                    final ListTag blocks = NBTUtil.getChildTag(phaseTag.getValue(), "blocks", ListTag.class);

                    highestDelay = delay > highestDelay ? delay : highestDelay;

                    Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), () -> {
                        for (Tag blockTag : blocks.getValue()) {
                            DeployableBlock block = NBTUtil.getBlock((CompoundTag) blockTag);
                            Vector offset = DeployableHelper.rotateOffsetTowards(block.getOffset(), facing);
                            Location place = location.clone().add(offset);
                            place.getBlock().setType(block.getType());
                            place.getBlock().setData(block.getData());
                        }
                    }, delay);
                }

                Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), this::constructComplete, highestDelay);
            }

            @Override
            public void constructComplete() {
                this.constructed = true;
            }

            @Override
            public void tick() {

            }

            @Override
            public void damage(int amount, Player damager) {

            }

            @Override
            public void destroy() {

            }
        };
    }

    public CompoundTag getMain() {
        return main;
    }

    public CompoundTag getProperties() {
        return properties;
    }
}
