package me.koenn.kingdomwars.deployables_OLD;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.BleedEffect;
import de.slikey.effectlib.util.DynamicLocation;
import me.koenn.core.cgive.CGiveAPI;
import me.koenn.core.misc.FancyString;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.effect.DisabledEffect;
import me.koenn.kingdomwars.js.ScriptHelper;
import me.koenn.kingdomwars.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jnbt.*;

import javax.script.ScriptEngine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class Deployable implements Listener, Runnable {

    private final Location location;
    private final DeployableExecutor executor;
    private final CompoundTag deployableInfo;
    private final ListTag phases;
    private final Deployable instance;
    private final List<Location> deployableBlocks = new ArrayList<>();

    private int health;
    private int dmgCooldown;
    private boolean disabled;
    private Effect disabledEffect;
    private boolean alive;

    public Deployable(final Location location, CompoundTag deployableInfo) {
        this.instance = this;
        this.location = location;
        this.deployableInfo = deployableInfo;
        this.phases = NBTUtil.getChildTag(NBTUtil.getChildTag(this.deployableInfo.getValue(), "construction", CompoundTag.class).getValue(), "phases", ListTag.class);
        this.health = NBTUtil.getChildTag(NBTUtil.getChildTag(this.deployableInfo.getValue(), "properties", CompoundTag.class).getValue(), "health", IntTag.class).getValue();

        this.executor = new DeployableExecutor() {
            private final List<DeployableBlock> oldBlocks = new ArrayList<>();

            private boolean constructed = false;
            private UUID owner;
            private int task;
            private ScriptEngine script;

            @SuppressWarnings("deprecation")
            @Override
            public boolean construct(Player player) {
                this.owner = player.getUniqueId();
                alive = true;

                HashMap<DeployableBlock, Integer> blocks = new HashMap<>();

                for (Tag phase : phases.getValue()) {
                    CompoundTag phaseTag = (CompoundTag) phase;
                    ListTag blockTags = NBTUtil.getChildTag(phaseTag.getValue(), "blocks", ListTag.class);

                    boolean skip = true;
                    for (Tag blockTag : blockTags.getValue()) {
                        DeployableBlock block = NBTUtil.getBlock((CompoundTag) blockTag);
                        block.setOffset(DeployableHelper.rotateOffsetTowards(block.getOffset(), DeployableHelper.getPlayerDirection(player)));
                        blocks.put(block, NBTUtil.getChildTag(phaseTag.getValue(), "delay", IntTag.class).getValue());

                        if (skip) {
                            skip = false;
                            continue;
                        }

                        Block replace = location.clone().add(block.getOffset()).getBlock();
                        if (!replace.getType().equals(Material.AIR) && !replace.getType().equals(Material.LONG_GRASS)) {
                            this.oldBlocks.clear();
                            return false;
                        }

                        this.oldBlocks.add(new DeployableBlock(replace));
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
                        byte oldData = place.getBlock().getData();
                        place.getBlock().setType(block.getType());
                        if (block.getData() == -1) {
                            place.getBlock().setData(oldData);
                        } else {
                            place.getBlock().setData(block.getData());
                        }
                        deployableBlocks.add(place);
                    }, delay);
                }

                Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), this::constructComplete, highestDelay);

                return true;
            }

            @Override
            public void constructComplete() {
                this.constructed = true;
                this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(KingdomWars.getInstance(), instance, 0, 1);
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
                this.oldBlocks.forEach(DeployableBlock::replace);
                deployableBlocks.clear();
            }

            @Override
            public Team getTeam() {
                return PlayerHelper.getTeam(this.owner);
            }

            @Override
            public UUID getOwner() {
                return this.owner;
            }
        };
    }


    public void damage(int amount, Player damager) {
        if (PlayerHelper.getTeam(damager.getUniqueId()).equals(this.executor.getTeam())) {
            return;
        }

        if (this.dmgCooldown > 0) {
            return;
        }

        this.executor.damage(amount, damager);
        this.dmgCooldown = 10;
        this.bleed();

        this.health -= amount;
        if (this.health <= 0) {
            this.health = Integer.MAX_VALUE;
            Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), () -> {
                this.remove();

                for (int i = 0; i < 5; i++) {
                    this.bleed();
                }

                final UUID owner = this.executor.getOwner();
                OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
                Messager.gameMessage(PlayerHelper.getGame(damager.getUniqueId()), String.format("%s's %s was destroyed by %s", player.getName(), new FancyString(this.deployableInfo.getName()), damager.getName()));

                final String itemName = ((StringTag) NBTUtil.getChildTag(this.getDeployableInfo().getValue(), "properties", CompoundTag.class).getValue().get("name")).getValue();
                final ItemStack item = CGiveAPI.getCItem(itemName).getItem();

                this.giveDeployable(owner, item);
            });
        }
    }

    private void giveDeployable(UUID uuid, ItemStack item) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), () -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                this.giveDeployable(uuid, item);
                return;
            }
            player.getInventory().addItem(item);
        }, 1600);
    }

    public void disable(Player damager) {
        if (!this.alive) {
            return;
        }

        this.damage(10, damager);

        if (this.disabled) {
            return;
        }
        this.disabled = true;

        this.disabledEffect = new DisabledEffect();
        this.disabledEffect.setDynamicOrigin(new DynamicLocation(this.location.clone().add(0.5, 0.0, 0.5)));
        this.disabledEffect.infinite();
        this.disabledEffect.start();

        Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), () -> {
            this.disabled = false;
            this.disabledEffect.cancel();
        }, 300);
    }

    private void bleed() {
        BleedEffect effect = new BleedEffect(new EffectManager(KingdomWars.getInstance()));
        effect.setDynamicOrigin(new DynamicLocation(this.location));
        effect.setDynamicTarget(new DynamicLocation(this.location));
        effect.iterations = 1;
        effect.height = 1.0;
        effect.start();
    }

    public boolean construct(Player constructor) {
        return this.executor.construct(constructor);
    }

    public void remove() {
        this.executor.destroy();
        this.alive = false;

        if (this.disabledEffect != null && !this.disabledEffect.isDone()) {
            this.disabledEffect.cancel();
        }
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

    public Team getTeam() {
        return this.executor.getTeam();
    }

    @Override
    public void run() {
        if (!this.disabled) {
            this.executor.tick();
        }

        if (this.dmgCooldown > 0) {
            this.dmgCooldown--;
        }
    }
}
