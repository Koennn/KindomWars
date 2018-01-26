package me.koenn.kingdomwars.party;

import me.koenn.core.command.Command;
import me.koenn.core.player.CPlayer;
import me.koenn.kingdomwars.util.Messager;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class PartyInviteCommand extends Command {

    private static final HashMap<UUID, Party> INVITES = new HashMap<>();

    public PartyInviteCommand() {
        super("invite", "/party invite <player>");
    }

    @Override
    public boolean execute(CPlayer cPlayer, String[] args) {
        if (args.length != 2) {
            return false;
        }

        if (args[1].equals("accept")) {
            if (!INVITES.containsKey(cPlayer.getUUID())) {
                cPlayer.sendMessage("&c&lYou don't have any pending party invites!");
                return true;
            }

            Party party = INVITES.get(cPlayer.getUUID());
            INVITES.remove(cPlayer.getUUID());

            party.getMembers().stream()
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull)
                    .forEach(player -> Messager.playerMessage(player, String.format("&a&lPlayer %s joined your party!", cPlayer.getName())));

            party.addMember(cPlayer.getUUID());
            cPlayer.sendMessage(String.format("&a&lYou joined party %s!", party.getName()));
        } else {
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                cPlayer.sendMessage(String.format("&c&lCannot find player %s!", args[1]));
            } else {
                Party party = Party.getParty(cPlayer.getUUID());
                INVITES.put(player.getUniqueId(), party);
                FancyMessage message = new FancyMessage(String.format("You've been invited to party %s by %s, click to accept!", party.getName(), cPlayer.getName()))
                        .color(ChatColor.GREEN).style(ChatColor.BOLD).tooltip("Click to accept!").command("/party invite accept");
                message.send(player);
                cPlayer.sendMessage(String.format("&a&lInvited %s to your party!", player.getName()));
            }
        }
        return true;
    }
}
