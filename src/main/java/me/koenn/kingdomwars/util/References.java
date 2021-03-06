package me.koenn.kingdomwars.util;

import me.koenn.core.cgive.CItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class References {

    public static final String GAME_PREFIX = "&8[&9KingdomWars&8] &r";
    public static final String INFO_MESSAGE = GAME_PREFIX + " Version %version% by %author%";

    public static final String GAME_ABOUT_TO_START = GAME_PREFIX + "&2The game is about to start!";
    public static final String GAME_STARTED = GAME_PREFIX + "&2The game has started!";
    public static final String DONT_HURT_ALLY = GAME_PREFIX + "&cPlease don't attempt to hurt your allies.";
    public static final String KILL = "&2+1 Kill!";
    public static final String JOIN_MESSAGE = "&7You joined a game, please wait until it starts.";
    public static final String SELECTED = "&7You selected &e&l%s!";
    public static final String DEATH = GAME_PREFIX + "&4You died! Please wait 10 seconds.";
    public static final String DEATH_TITLE = "&c&lYou died!";
    public static final String RESPAWN = GAME_PREFIX + "&2You respawned! Try not to die this time.";
    public static final String CAPTURE_LOSS_TITLE = "&c&lPoint lost!";
    public static final String CAPTURE_LOSS_SUBTITLE = "&c&lThe red team captured your point!";
    public static final String CAPTURE_WIN_TITLE = "&a&lPoint captured!";
    public static final String CAPTURE_WIN_SUBTITLE = "&a&lWe captured the red teams point!";
    public static final String GAME_WIN_TITLE = "&a&lYou won!";
    public static final String GAME_WIN_SUBTITLE = "&a&lYour team captured 3 points";
    public static final String GAME_LOSS_TITLE = "&c&lYou lost!";
    public static final String GAME_LOSS_SUBTITLE = "&c&lThe red team captured 3 points";
    public static final String CAPTURE_PROGRESS_LAYOUT = "&1&l%bluepoints% points &f&m--&r &1&l%blue%%  &f&m--&r  &4&l%red%% &f&m--&r  &4&l%redpoints% points";
    public static final String NOT_ENOUGH_SPACE = GAME_PREFIX + "&c&lThere's not enough space to place that there!";

    public static final String MODE_CHANGE = GAME_PREFIX + "&7Changed mode to %mode%";
    public static final String SET_SPAWN = GAME_PREFIX + "&7Set the spawn for team %team% to %coords%!";
    public static final String SET_DOOR = GAME_PREFIX + "&7Set the door for team %team% to %coords%!";
    public static final String ADD_CORNER = GAME_PREFIX + "&7Added corner %coords% to the capture point for team %team%!";
    public static final String SAVE_SUCCESSFUL = GAME_PREFIX + "&7Successfully saved your map!";
    public static final String SAVE_ERROR = GAME_PREFIX + "&4It looks like you made a mistake while setting the areas, please try again!";
    public static final String MAPS_RELOADED = GAME_PREFIX + "&7Reloaded all maps!";

    public static final String SIGN_PREFIX = "KingdomWars";
    public static final String SIGN_0 = "&1[KingdomWars]";
    public static final String SIGN_1 = "&3%mapname%";
    public static final String SIGN_2 = "";
    public static final String SIGN_3 = "%color%%pcount%/%maxp%";
    public static final String LAST_SPOT = "&4&lONE SPOT LEFT!";
    public static final String FULL = "&4&lGAME FULL!";
    public static final String SPECTATE = "&a&lCLICK TO WATCH!";

    public static final String[] GAME_JOIN_MESSAGE = new String[]{
            "&b&m+===============================================+",
            "&3&lGame: &eKingdomWars",
            "%color%&lTeam: %team%",
            "&3&lMap: &e%map%",
            "&3&lCharacter: &e%class%",
            "&a %desc%",
            "%clickable%&3&l&nClick to view map lore!",
            "&b&m+===============================================+"
    };

    public static final String[] SERVER_JOIN_MESSAGE = new String[]{
            "&b&m+===============================================+",
            "",
            "&6&lWelcome to the KingdomWars Server!",
            "",
            "&3&lDiscord: &7https://discord.gg/hJAxEnP",
            "&3&lWebsite: &7http://kingdomwarsmc.net",
            "",
            "&b&m+===============================================+"
    };

    public static final String NOT_IN_GAME = GAME_PREFIX + "&4You're not in a game, so your building will not work!";
    public static final String BUILDING_COMPLETE = GAME_PREFIX + "&a&lBuilding construction complete!";

    public static final String DEPLOYABLE_FOLDER = "deployables_OLD";

    public static final int RESPAWN_COOLDOWN = 20;
    public static final int UPDATE_RATE = 3;
    public static final int WINNING_POINTS = 3;

    public static final CItem MAPSTAFF = new CItem() {
        @Override
        public ItemStack getItem() {
            ItemStack item = new ItemStack(Material.WOOD_HOE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE + "Map Creation Tool");
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.GOLD + "Mode: Spawn");
            meta.setLore(lore);
            item.setItemMeta(meta);
            return item;
        }

        @Override
        public String getName() {
            return "mapstaff";
        }
    };
}
