package me.koenn.kingdomwars.party;

import me.koenn.core.command.Command;
import me.koenn.core.player.CPlayer;
import org.bukkit.Bukkit;

public class PartyCommand extends Command {

    public PartyCommand() {
        super("party", "/party [subcommand]");
    }

    @Override
    public boolean execute(CPlayer cPlayer, String[] args) {
        if (!Party.isInParty(cPlayer.getUUID())) {
            cPlayer.sendMessage("&c&lYou're not in a party!");
            return true;
        }

        Party party = Party.getParty(cPlayer.getUUID());
        cPlayer.sendMessage("&b&l-----------------------------------------");
        cPlayer.sendMessage(String.format("&e&lParty: &a%s", party.getName()));
        cPlayer.sendMessage(String.format("&e&lOwner: &a%s", Bukkit.getOfflinePlayer(party.getOwner()).getName()));
        cPlayer.sendMessage("&e&lMembers:");
        party.getMembers().forEach(player -> cPlayer.sendMessage(String.format("&e&l- &a%s", Bukkit.getOfflinePlayer(player).getName())));
        cPlayer.sendMessage("&b&l-----------------------------------------");
        return true;
    }
}
