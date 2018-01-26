package me.koenn.kingdomwars.commands;

import me.koenn.core.command.Command;
import me.koenn.core.player.CPlayer;
import me.koenn.kingdomwars.discord.DiscordBot;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

public class LinkCommand extends Command {

    public LinkCommand() {
        super("link", "/kingdomwars link <id>");
    }

    @Override
    public boolean execute(CPlayer cPlayer, String[] args) {
        if (args.length < 1) {
            return false;
        }

        if (!DiscordBot.VERIFICATION.containsKey(cPlayer.getUUID())) {
            cPlayer.sendMessage("&c&lYou don't have any pending links!");
            return true;
        }

        List<User> links = DiscordBot.VERIFICATION.get(cPlayer.getUUID());
        links.forEach(user -> {
            if (user.getId().equals(args[1])) {
                DiscordBot.LINKS.put(cPlayer.getUUID(), user.getIdLong());
                cPlayer.sendMessage(String.format("&a&lSuccessfully linked Discord user %s to you!", user.getName()));

                cPlayer.set("discord_id", user.getId());
            }
        });

        DiscordBot.VERIFICATION.get(cPlayer.getUUID()).clear();
        DiscordBot.VERIFICATION.remove(cPlayer.getUUID());

        return true;
    }
}
