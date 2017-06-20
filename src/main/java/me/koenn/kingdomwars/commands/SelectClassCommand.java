package me.koenn.kingdomwars.commands;

import me.koenn.core.command.Command;
import me.koenn.core.gui.Gui;
import me.koenn.core.player.CPlayer;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.classes.ClassGui;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class SelectClassCommand extends Command {

    public SelectClassCommand() {
        super("class", "/class");
    }

    @Override
    public boolean execute(CPlayer cPlayer, String[] strings) {
        ClassGui gui = new ClassGui(cPlayer.getPlayer());
        Gui.registerGui(gui, KingdomWars.getInstance());
        gui.open();
        return true;
    }
}
