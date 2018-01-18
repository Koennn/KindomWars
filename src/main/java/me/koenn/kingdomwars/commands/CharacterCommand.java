package me.koenn.kingdomwars.commands;

import me.koenn.core.command.Command;
import me.koenn.core.gui.Gui;
import me.koenn.core.player.CPlayer;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.characters.CharacterGui;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class CharacterCommand extends Command {

    public CharacterCommand() {
        super("character", "/kingdomwars character");
    }

    @Override
    public boolean execute(CPlayer cPlayer, String[] strings) {
        Gui gui = new CharacterGui(cPlayer.getPlayer());
        Gui.registerGui(gui, KingdomWars.getInstance());
        gui.open();
        return true;
    }
}
