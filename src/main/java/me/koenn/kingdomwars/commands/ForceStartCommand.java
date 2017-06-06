package me.koenn.kingdomwars.commands;

import me.koenn.core.command.Command;
import me.koenn.core.player.CPlayer;
import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.util.PlayerHelper;
import org.bukkit.entity.Player;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class ForceStartCommand extends Command {

    public ForceStartCommand() {
        super("forcestart", "/forcestart");
    }

    @Override
    public boolean execute(CPlayer cPlayer, String[] args) {
        Player player = cPlayer.getPlayer();
        if (!player.isOp()) {
            return false;
        }

        Game game = PlayerHelper.getGame(player);
        if (game == null) {
            return false;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("debug")) {
            game.setDebug(true);
        }
        game.load();
        return true;
    }
}
