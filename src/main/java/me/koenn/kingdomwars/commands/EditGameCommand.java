package me.koenn.kingdomwars.commands;

import me.koenn.core.command.Command;
import me.koenn.core.gui.Gui;
import me.koenn.core.player.CPlayer;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.EditGameGui;
import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.util.PlayerHelper;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, June 2017
 */
public class EditGameCommand extends Command {

    public EditGameCommand() {
        super("editgame", "You're not in a game");
    }

    @Override
    public boolean execute(CPlayer cPlayer, String[] strings) {
        if (!cPlayer.getPlayer().isOp()) {
            return true;
        }

        Game game = PlayerHelper.getGame(cPlayer.getPlayer());
        if (game == null) {
            return false;
        }

        Gui gui = new EditGameGui(cPlayer.getPlayer(), game);
        Gui.registerGui(gui, KingdomWars.getInstance());
        gui.open();
        return true;
    }
}
