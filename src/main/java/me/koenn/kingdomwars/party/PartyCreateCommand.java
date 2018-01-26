package me.koenn.kingdomwars.party;

import me.koenn.core.command.Command;
import me.koenn.core.player.CPlayer;

public class PartyCreateCommand extends Command {

    public PartyCreateCommand() {
        super("create", "/party create <name>");
    }

    @Override
    public boolean execute(CPlayer cPlayer, String[] args) {
        if (!cPlayer.getPlayer().isOp() || !cPlayer.getName().equalsIgnoreCase("koenn")) {
            cPlayer.sendMessage("&c&lYou don't have permission to use this command!");
            return true;
        }

        if (args.length != 2) {
            return false;
        }

        if (Party.isInParty(cPlayer.getUUID())) {
            cPlayer.sendMessage("&c&lYou're already in a party!");
            return true;
        }

        String name = args[1];
        Party party = new Party(cPlayer.getUUID(), name);
        Party.REGISTRY.register(party);
        cPlayer.sendMessage(String.format("&a&lCreated the party %s!", name));
        return true;
    }
}
