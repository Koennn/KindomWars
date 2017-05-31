package me.koenn.kingdomwars.deployables;

import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.js.ScriptHelper;
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

import javax.script.ScriptEngine;
import java.util.ArrayList;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class Deployable {

    private final Location location;
    private final DeployableExecutor executor;
    private final CompoundTag deployableInfo;
    private final ListTag phases;
    private final Deployable instance;

    public Deployable(final Location location, CompoundTag deployableInfo) {
        this.instance = this;
        this.location = location;
        this.deployableInfo = deployableInfo;
        this.phases = NBTUtil.getChildTag(NBTUtil.getChildTag(this.deployableInfo.getValue(), "construction", CompoundTag.class).getValue(), "phases", ListTag.class);
        executor = new DeployableExecutor() {
            private boolean constructed = false;
            private Player owner;
            private int task;
            private ScriptEngine script;

            @Override
            public void construct(Player player) {
                this.owner = player;

                this.script = DeployableLoader.loadDeployableScript(instance);
                ScriptHelper.invokeFunction(this.script, "onConstruct", instance, this.owner, location);

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
                this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(KingdomWars.getInstance(), this::tick, 0, 1);
                ScriptHelper.invokeFunction(this.script, "onConstructComplete", instance);
            }

            @Override
            public void tick() {
                ScriptHelper.invokeFunction(this.script, "onTick", instance, new ArrayList<>(Bukkit.getOnlinePlayers()));
            }

            @Override
            public void damage(int amount, Player damager) {
                ScriptHelper.invokeFunction(this.script, "onDamageTake", instance, amount, damager);
            }

            @Override
            public void destroy() {
                this.constructed = false;
                Bukkit.getScheduler().cancelTask(this.task);
                ScriptHelper.invokeFunction(this.script, "onDestroy", instance);
            }
        };
    }

    public void construct(Player constructor) {
        this.executor.construct(constructor);
    }

    public Location getLocation() {
        return location;
    }

    public CompoundTag getDeployableInfo() {
        return deployableInfo;
    }
}
