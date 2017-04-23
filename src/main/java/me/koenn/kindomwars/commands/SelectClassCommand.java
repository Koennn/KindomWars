package me.koenn.kindomwars.commands;

import me.koenn.core.gui.Gui;
import me.koenn.kindomwars.KingdomWars;
import me.koenn.kindomwars.game.classes.ClassGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class SelectClassCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        ClassGui gui = new ClassGui((Player) commandSender);
        Gui.registerGui(gui, KingdomWars.getInstance());
        gui.open();
        return true;
    }
}
