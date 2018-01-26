package me.koenn.kingdomwars.discord;

import me.koenn.kingdomwars.KingdomWars;
import mkremins.fanciful.FancyMessage;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DiscordBot extends ListenerAdapter {

    public static final HashMap<UUID, List<User>> VERIFICATION = new HashMap<>();
    public static final HashMap<UUID, Long> LINKS = new HashMap<>();
    public static DiscordBot INSTANCE;
    private static Guild guild;
    private JDA jda;

    public DiscordBot() {
        INSTANCE = this;
        try {
            this.jda = new JDABuilder(AccountType.BOT)
                    .setToken("NDA0NjEwMTQwMTE3OTI1ODkw.DUYWFA.B5ANqdWsH6cF0p7OC2Gl-DRyO00")
                    .setGame(Game.playing("KingdomWars"))
                    .setAutoReconnect(true)
                    .buildBlocking();
        } catch (LoginException e) {
            KingdomWars.getInstance().getLogger().severe("Unable to login to Discord: " + e);
        } catch (InterruptedException e) {
            KingdomWars.getInstance().getLogger().severe("Interrupted while logging in to Discord: " + e);
        } catch (RateLimitedException e) {
            KingdomWars.getInstance().getLogger().severe("Rate limited by Discord! " + e);
        }
        this.jda.addEventListener(this);
        guild = this.jda.getGuilds().get(0);
    }

    public static void attemptMovePlayer(Player player, String channel) {
        if (!LINKS.containsKey(player.getUniqueId())) {
            return;
        }
        Member member = guild.getMember(INSTANCE.jda.getUserById(LINKS.get(player.getUniqueId())));
        if (!member.getVoiceState().inVoiceChannel()) {
            return;
        }
        guild.getController().moveVoiceMember(member, guild.getVoiceChannelById(channel)).queue(null, (ex) -> {
            KingdomWars.getInstance().getLogger().severe("Error while moving player " + ex);
            Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), () -> attemptMovePlayer(player, channel), 20);
        });
    }

    public static User getUser(String id) {
        return INSTANCE.jda.getUserById(id);
    }

    public void shutdown() {
        if (this.jda != null) {
            try {
                this.jda.shutdown();
            } catch (Exception ex) {
                KingdomWars.getInstance().getLogger().severe("Error while shutting down JDA: " + ex);
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getMessage().getContent().startsWith("!verify")) {
            return;
        }

        String[] split = event.getMessage().getContent().split(" ");
        if (split.length < 2) {
            return;
        }

        TextChannel channel = event.getTextChannel();
        User author = event.getAuthor();
        String username = split[1];
        Player player = Bukkit.getPlayerExact(username);
        if (player == null) {
            channel.sendMessage(new MessageBuilder().append(String.format("Player %s is not online!", username)).build()).queue();
            return;
        }

        if (LINKS.containsKey(player.getUniqueId())) {
            User user = this.jda.getUserById(LINKS.get(player.getUniqueId()));
            channel.sendMessage(
                    new MessageBuilder()
                            .append(String.format("Player %s is already linked to discord user ", username))
                            .append(user.getAsMention())
                            .build()
            ).queue();
            return;
        }

        if (!VERIFICATION.containsKey(player.getUniqueId())) {
            VERIFICATION.put(player.getUniqueId(), new ArrayList<>());
        }
        VERIFICATION.get(player.getUniqueId()).add(author);

        channel.sendMessage(new MessageBuilder().append("Please verify this request in Minecraft!").build()).queue();

        String text = "Click this message to link your Minecraft account to Discord user " + author.getName();
        FancyMessage message = new FancyMessage(text)
                .color(ChatColor.GREEN)
                .style(ChatColor.BOLD)
                .tooltip("Click to link!")
                .command("/kingdomwars link " + author.getId());
        message.send(player);
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Role role = event.getGuild().getRolesByName("player", true).get(0);
        guild.getController().addRolesToMember(event.getMember(), role);
    }
}
