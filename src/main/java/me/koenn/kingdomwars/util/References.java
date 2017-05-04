package me.koenn.kingdomwars.util;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class References {

    public static final String GAME_PREFIX = "&8[&9KingdomWars&8] ";

    public static final String GAME_ABOUT_TO_START = GAME_PREFIX + "&2The game is about to start!";
    public static final String GAME_STARTED = GAME_PREFIX + "&2The game has started!";
    public static final String DONT_SHOOT_ALLY = GAME_PREFIX + "&cPlease don't attempt to hurt your allies.";
    public static final String KILL = "&2+1 Kill!";
    public static final String SIGN_PREFIX = "[KingdomWars]";
    public static final String JOIN_MESSAGE = "&7You joined a game, please wait until it starts.";
    public static final String DEATH = GAME_PREFIX + "&4You died! Please wait 5 seconds.";
    public static final String RESPAWN = GAME_PREFIX + "&2You respawned! Try not to die this time.";
    public static final String CLASS = GAME_PREFIX + "&2You are in the &a&l%class% &2class!";
    public static final String MAP = GAME_PREFIX + "&2We're playing on the &a&l%map% &2map!";
    public static final String CAPTURE_LOSS_TITLE = "&c&lPoint lost!";
    public static final String CAPTURE_LOSS_SUBTITLE = "&c&lThe red team captured your point!";
    public static final String CAPTURE_WIN_TITLE = "&a&lPoint captured!";
    public static final String CAPTURE_WIN_SUBTITLE = "&a&lWe captured the red teams point!";

    public static final String MODE_CHANGE = GAME_PREFIX + "&7Changed mode to %mode%";
    public static final String SET_SPAWN = GAME_PREFIX + "&7Set the spawn for team %team% to %coords%!";
    public static final String SET_DOOR = GAME_PREFIX + "&7Set the door for team %team% to %coords%!";
    public static final String ADD_CORNER = GAME_PREFIX + "&7Added corner %coords% to the capture point for team %team%!";
    public static final String SAVE_SUCCESSFUL = GAME_PREFIX + "&7Successfully saved your map!";
    public static final String SAVE_ERROR = GAME_PREFIX + "&4It looks like you made a mistake while setting the areas, please try again!";

    public static final String SAVED_PREFERENCE = GAME_PREFIX + "&7Successfully saved your preference!";

    public static final int GAME_START_DELAY = 10;
    public static final int TEAM_SIZE = 10;
    public static final int RESPAWN_COOLDOWN = 10;
    public static final int UPDATE_RATE = 2;
}
