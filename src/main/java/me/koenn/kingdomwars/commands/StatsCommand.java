package me.koenn.kingdomwars.commands;

import me.koenn.core.command.Command;
import me.koenn.core.misc.ExpHelper;
import me.koenn.core.player.CPlayer;
import me.koenn.kingdomwars.game.classes.Class;
import me.koenn.kingdomwars.game.classes.ClassLoader;
import me.koenn.kingdomwars.stats.Level;
import me.koenn.kingdomwars.stats.PlayerStats;
import me.koenn.kingdomwars.util.Messager;
import me.koenn.kingdomwars.util.References;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, June 2017
 */
public class StatsCommand extends Command {

    public StatsCommand() {
        super("stats", "/kingdomwars stats [player]");
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean execute(CPlayer cPlayer, String[] args) {
        final Player player = args.length > 1 ? Bukkit.getPlayer(args[1]) : cPlayer.getPlayer();
        if (player == null) {
            return false;
        }

        PlayerStats stats = PlayerStats.statsRegistry.get(player.getUniqueId().toString());
        if (stats == null) {
            stats = new PlayerStats(player.getUniqueId());
            PlayerStats.statsRegistry.register(stats);
        }

        for (String line : References.STATS_MESSAGE) {
            for (Class cl : ClassLoader.getClasses()) {
                Level level = stats.getLevel(cl);
                line = line.replace("%" + cl.getName().toLowerCase() + "Level%", String.valueOf(level.getLevel()));
                line = line.replace("%" + cl.getName().toLowerCase() + "Exp%", String.valueOf(level.getExp()));
                line = line.replace("%" + cl.getName().toLowerCase() + "NeededExp%", String.valueOf(ExpHelper.getExpAtLevel(level.getLevel())));
            }

            for (String stat : stats.getStats()) {
                line = line.replace("%" + stat + "%", String.valueOf(stats.getStat(stat)));
            }

            Messager.playerMessage(player, line);
        }
        return true;
    }
}
