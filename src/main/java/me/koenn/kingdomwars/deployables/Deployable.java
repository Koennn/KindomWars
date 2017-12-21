package me.koenn.kingdomwars.deployables;

import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.js.ScriptHelper;
import me.koenn.kingdomwars.util.DeployableHelper;
import me.koenn.kingdomwars.util.NBTUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.Tag;

import javax.script.ScriptEngine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private final List<Location> deployableBlocks = new ArrayList<>();

    public Deployable(final Location location, CompoundTag deployableInfo) {
        this.instance = this;
        this.location = location;
        this.deployableInfo = deployableInfo;
        this.phases = NBTUtil.getChildTag(NBTUtil.getChildTag(this.deployableInfo.getValue(), "construction", CompoundTag.class).getValue(), "phases", ListTag.class);

        this.executor = new DeployableExecutor() {
            private boolean constructed = false;
            private Player owner;
            private int task;
            private ScriptEngine script;

            @SuppressWarnings("deprecation")
            @Override
            public boolean construct(Player player) {
                this.owner = player;

                HashMap<DeployableBlock, Integer> blocks = new HashMap<>();

                for (Tag phase : phases.getValue()) {
                    CompoundTag phaseTag = (CompoundTag) phase;
                    ListTag blockTags = NBTUtil.getChildTag(phaseTag.getValue(), "blocks", ListTag.class);

                    for (Tag blockTag : blockTags.getValue()) {
                        DeployableBlock block = NBTUtil.getBlock((CompoundTag) blockTag);
                        block.setOffset(DeployableHelper.rotateOffsetTowards(block.getOffset(), DeployableHelper.getPlayerDirection(player)));
                        blocks.put(block, NBTUtil.getChildTag(phaseTag.getValue(), "delay", IntTag.class).getValue());

                        if (!location.clone().add(block.getOffset()).getBlock().getType().equals(Material.AIR)) {
                            return false;
                        }
                    }
                }

                this.script = DeployableLoader.loadDeployableScript(instance);
                ScriptHelper.invokeFunction(this.script, "onConstruct", instance, this.owner, location);

                int highestDelay = 0;
                for (DeployableBlock block : blocks.keySet()) {
                    int delay = blocks.get(block);
                    highestDelay = delay > highestDelay ? delay : highestDelay;

                    Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), () -> {
                        Location place = location.clone().add(block.getOffset());
                        place.getBlock().setType(block.getType());
                        place.getBlock().setData(block.getData());
                        deployableBlocks.add(place);
                    }, delay);
                }

                Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), this::constructComplete, highestDelay);

                return true;
            }

            @Override
            public void constructComplete() {
                this.constructed = true;
                this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(KingdomWars.getInstance(), this::tick, 0, 1);
                ScriptHelper.invokeFunction(this.script, "onConstructComplete", instance);
            }

            @Override
            public void tick() {
                if (this.constructed) {
                    ScriptHelper.invokeFunction(this.script, "onTick", instance, new ArrayList<>(Bukkit.getOnlinePlayers()));
                }
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
                deployableBlocks.forEach(block -> block.getBlock().setType(Material.AIR));
                deployableBlocks.clear();
            }
        };
    }

    public void damage(int amount, Player damager) {
        this.executor.damage(amount, damager);
        damager.sendMessage("Damaged!");
    }

    public boolean construct(Player constructor) {
        return this.executor.construct(constructor);
    }

    public void remove() {
        this.executor.destroy();
    }

    public Location getLocation() {
        return location;
    }

    public CompoundTag getDeployableInfo() {
        return deployableInfo;
    }

    public List<Location> getDeployableBlocks() {
        return deployableBlocks;
    }
}
