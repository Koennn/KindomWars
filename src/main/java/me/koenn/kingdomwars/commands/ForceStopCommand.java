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
 * Written by Koen Willemse, May 2017
 */
public class ForceStopCommand extends Command {

    public ForceStopCommand() {
        super("forcestop", "/forcestop");
    }

    @Override
    public boolean execute(CPlayer cPlayer, String[] strings) {
        Player player = cPlayer.getPlayer();
        if (!player.isOp()) {
            return false;
        }

        Game game = PlayerHelper.getGame(player);
        if (game == null) {
            return false;
        }

        game.stop();
        return true;
    }
}
