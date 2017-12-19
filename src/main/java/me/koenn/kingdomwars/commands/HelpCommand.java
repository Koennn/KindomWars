package me.koenn.kingdomwars.commands;

import me.koenn.core.command.Command;
import me.koenn.core.misc.ColorHelper;
import me.koenn.core.player.CPlayer;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.util.Messager;
import me.koenn.kingdomwars.util.References;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential Written by Koen Willemse, September 2017
 */
public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "/kingdomwars help");
    }

    @Override
    public boolean execute(CPlayer cPlayer, String[] strings) {
        StringBuilder builder = new StringBuilder();
        builder.append(ColorHelper.readColor(References.GAME_PREFIX + "&lCommands:"));
        KingdomWars.command.getSubCommands().forEach(command -> builder.append(command.getUsage()));
        Messager.playerMessage(cPlayer.getPlayer(), builder.toString());
        return true;
    }
}
