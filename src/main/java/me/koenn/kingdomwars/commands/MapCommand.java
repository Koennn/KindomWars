package me.koenn.kingdomwars.commands;

import me.koenn.core.command.Command;
import me.koenn.core.player.CPlayer;
import me.koenn.kingdomwars.mapcreator.MapTool;

public class MapCommand extends Command {

    public MapCommand() {
        super("map", "/kingdomwars map");
    }

    @Override
    public boolean execute(CPlayer cPlayer, String[] strings) {
        new MapTool(cPlayer.getPlayer());
        return true;
    }
}
