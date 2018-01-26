package me.koenn.kingdomwars.mapcreator;

import me.koenn.core.gui.Gui;
import me.koenn.core.keyboard.KeyboardGui;
import me.koenn.core.misc.FancyString;
import me.koenn.core.misc.LocationHelper;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.map.Door;
import me.koenn.kingdomwars.util.MapSaveGui;
import me.koenn.kingdomwars.util.Messager;
import me.koenn.kingdomwars.util.References;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential Written by Koen Willemse, September 2017
 */
public class MapTool implements Listener {

    private final Player player;
    private final JSONObject json;
    private JSONArray medkits = new JSONArray();

    private long lastUse = System.currentTimeMillis();

    public MapTool(Player player) {
        this.player = player;
        this.json = new JSONObject();

        this.json.put("spawns", new JSONObject());
        this.json.put("doors", new JSONObject());

        JSONObject points = new JSONObject();
        for (Team team : Team.values()) {
            JSONObject point = new JSONObject();
            point.put("corners", new JSONArray());
            points.put(team.name(), point);
        }
        this.json.put("points", points);

        Bukkit.getPluginManager().registerEvents(this, KingdomWars.getInstance());

        this.player.getInventory().addItem(References.MAPSTAFF.getItem());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getPlayer().equals(this.player) || event.getAction().equals(Action.PHYSICAL)) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || !item.getType().equals(Material.WOOD_HOE)) {
            return;
        }

        ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
        if (!meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES) || !meta.hasLore()) {
            return;
        }

        if (System.currentTimeMillis() - this.lastUse < 200) {
            return;
        }
        this.lastUse = System.currentTimeMillis();

        event.setCancelled(true);
        Bukkit.getPluginManager().callEvent(new MapToolUseEvent(event));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMapToolUse(MapToolUseEvent event) {
        if (event.getSelected() == null) {
            ItemMeta meta = event.getPlayer().getInventory().getItemInMainHand().getItemMeta();
            switch (event.getTeam()) {
                case RED:
                    Mode next = event.getMode().getNextMode();
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GOLD + "Mode: " + next.name());
                    meta.setLore(lore);
                    player.getInventory().getItemInMainHand().setItemMeta(meta);
                    Messager.playerMessage(player, References.MODE_CHANGE.replace("%mode%", next.name()));
                    break;
                case BLUE:
                    if (this.player.isSneaking()) {
                        System.out.println(this.json);
                        KeyboardGui gui = new KeyboardGui(this.player, "Map Name", input -> {
                            System.out.println(this.json);
                            MapSaveGui saveGui = new MapSaveGui(this.player, input, this.json);
                            Gui.registerGui(saveGui, KingdomWars.getInstance());
                            saveGui.open();
                        });
                        Gui.registerGui(gui, KingdomWars.getInstance());
                        gui.open();
                    }
                    HandlerList.unregisterAll(this);
                    break;
            }
            return;
        }

        String team = event.getTeam().name();
        switch (event.getMode()) {
            case Spawn:
                JSONObject spawns = (JSONObject) this.json.get("spawns");
                String spawn = LocationHelper.getString(event.getSelected().add(0, 1, 0));
                spawns.put(team, spawn);

                Messager.playerMessage(player,
                        References.SET_SPAWN
                                .replace("%team%", new FancyString(team).toString())
                                .replace("%coords%", spawn)
                );
                break;

            case Door:
                JSONObject doors = (JSONObject) this.json.get("doors");
                Door.DoorType type = event.getPlayer().isSneaking() ? Door.DoorType.Z : Door.DoorType.X;
                Door door = new Door(event.getSelected(), type);
                doors.put(team, door.toJSON());

                Messager.playerMessage(player,
                        References.SET_DOOR
                                .replace("%team%", new FancyString(team).toString())
                                .replace("%coords%", String.format("%s: %s", door.getType(), door.getLocation()))
                );
                break;

            case Point:
                JSONObject point = (JSONObject) ((JSONObject) this.json.get("points")).get(team);
                point.put("owningTeam", team);
                JSONArray corners = (JSONArray) point.get("corners");
                String corner = LocationHelper.getString(event.getSelected());
                corners.add(corner);

                Messager.playerMessage(player,
                        References.ADD_CORNER
                                .replace("%team%", new FancyString(team).toString())
                                .replace("%coords%", corner)
                );
                break;

            case Medkit:
                medkits.add(LocationHelper.getString(event.getSelected()));
                System.out.println(medkits.toJSONString());
                break;

            case Data:
                Block block = event.getSelected().getBlock();
                player.sendMessage(block.getType().name() + " " + block.getData());
                break;
        }
    }

    public enum Mode {
        Spawn, Door, Point, Medkit, Data;

        Mode getNextMode() {
            switch (this) {
                case Spawn:
                    return Mode.Door;
                case Door:
                    return Mode.Point;
                case Point:
                    return Mode.Medkit;
                case Medkit:
                    return Mode.Data;
                case Data:
                    return Mode.Spawn;
                default:
                    return Mode.Spawn;
            }
        }
    }
}
