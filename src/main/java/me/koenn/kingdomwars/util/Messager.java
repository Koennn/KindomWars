package me.koenn.kingdomwars.util;

import me.koenn.core.misc.ActionBar;
import me.koenn.core.misc.ColorHelper;
import me.koenn.core.misc.Title;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.Game;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * Helper class for sending messages, titles, ect.
 */
public final class Messager {

    /**
     * Send a message to all players in a game.
     *
     * @param game    Game to send to
     * @param message Message to send
     */
    public static void gameMessage(Game game, String message) {
        game.getPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(player -> player.sendMessage(ColorHelper.readColor(message)));
    }

    /**
     * Send a message to a specific player.
     *
     * @param player  Player to send to
     * @param message Message to send
     */
    public static void playerMessage(Player player, String message) {
        player.sendMessage(ColorHelper.readColor(message));
    }

    /**
     * Send a clickable message to a specific player.
     *
     * @param player  Player to send to
     * @param message Message to send
     * @param tooltip Tooltip to show
     * @param url     Url to direct to
     */
    public static void clickableMessage(Player player, String message, String tooltip, String url) {
        new FancyMessage(ColorHelper.readColor(message)).tooltip(ColorHelper.readColor(tooltip)).link(url).send(player);
    }

    /**
     * Send a title to a specific team in a game.
     *
     * @param title    Title to send
     * @param subtitle Subtitle to send
     * @param team     Team to send to
     * @param game     Game to send in
     */
    public static void teamTitle(String title, String subtitle, Team team, Game game) {
        final Title titleObj = new Title(title, subtitle).setFade(1);
        game.getTeam(team).stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(titleObj::send);
    }

    /**
     * Send a title to a specific player.
     *
     * @param title    Title to send
     * @param subtitle Subtitle to send
     * @param player   Player to send to
     */
    public static void playerTitle(String title, String subtitle, Player player) {
        new Title(title, subtitle).setFade(1).send(player);
    }

    /**
     * Send an actionbar to a specific player.
     *
     * @param player  Player to send to
     * @param message Message to send
     */
    public static void playerActionBar(Player player, String message) {
        new ActionBar(message, KingdomWars.getInstance()).send(player);
    }

    /**
     * Clear the chat of a specific player.
     *
     * @param player Player to clear for
     */
    public static void clearChat(Player player) {
        for (int i = 0; i < 40; i++) {
            player.sendMessage("");
        }
    }
}
