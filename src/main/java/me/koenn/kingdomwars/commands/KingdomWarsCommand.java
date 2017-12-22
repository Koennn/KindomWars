package me.koenn.kingdomwars.commands;

import me.koenn.core.command.Command;
import me.koenn.core.player.CPlayer;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.gadgets.CollectiveHealthPool;
import me.koenn.kingdomwars.util.References;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, June 2017
 */
public class KingdomWarsCommand extends Command {

    public KingdomWarsCommand() {
        super("kingdomwars", "/kingdomwars [subcommand]");
    }

    @Override
    public boolean execute(CPlayer cPlayer, String[] strings) {
        cPlayer.sendMessage(References.INFO_MESSAGE
                .replace("%version%", KingdomWars.getInstance().getDescription().getVersion())
                .replace("%author%", Arrays.toString(KingdomWars.getInstance().getDescription().getAuthors().toArray()))
        );

        CollectiveHealthPool pool = new CollectiveHealthPool(40, new Player[]{Bukkit.getPlayer(strings[0]), cPlayer.getPlayer()});
        cPlayer.giveItem(References.MAPSTAFF.getItem());
        return true;
    }
}
