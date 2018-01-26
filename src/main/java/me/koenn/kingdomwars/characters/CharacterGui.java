package me.koenn.kingdomwars.characters;

import me.koenn.core.gui.Gui;
import me.koenn.core.gui.Option;
import me.koenn.core.misc.ColorHelper;
import me.koenn.core.misc.ItemHelper;
import me.koenn.core.player.CPlayerRegistry;
import me.koenn.kingdomwars.party.Party;
import me.koenn.kingdomwars.util.Messager;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.References;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CharacterGui extends Gui {

    public CharacterGui(Player player) {
        super(player, "Select a character", 36);

        String previous = PlayerHelper.getPreviousCharacter(player.getUniqueId());

        CharacterLoader.CHARACTER_REGISTRY.getRegisteredObjects().forEach(character -> {
            ItemStack raw = character.getIcon();

            List<String> lore = new ArrayList<>();
            if (character.getName().equalsIgnoreCase(previous)) {
                lore.add(ChatColor.AQUA + "" + ChatColor.BOLD + "SELECTED!");
            } else {
                lore.add(String.format(ChatColor.RESET + "Click to select %s!", character.getName()));
            }
            if (character.isPartyOnly()) {
                lore.add(ChatColor.RED + "" + ChatColor.BOLD + "Can only be played with party member!");
            }
            Collections.addAll(lore, wrapString(character.getDescription(), 30).split("\n"));


            ItemStack icon = ItemHelper.makeItemStack(
                    raw.getType(), raw.getAmount(), raw.getDurability(),
                    ColorHelper.readColor("&e&l" + character.getName()),
                    lore
            );

            ItemMeta meta = icon.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            if (character.getName().equalsIgnoreCase(previous)) {
                meta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
            }

            icon.setItemMeta(meta);

            this.addOption(new Option(icon, () -> {
                if (!Party.isInParty(player.getUniqueId()) && character.isPartyOnly()) {
                    Messager.playerMessage(player, String.format("&c&l%s can only be played with a party member!", character.getName()));
                    return;
                }

                CPlayerRegistry.getCPlayer(player.getUniqueId()).set("character", character.getName());
                Messager.playerMessage(player, String.format(References.SELECTED, character.getName()));
                player.closeInventory();
            }));
        });
    }

    public static String wrapString(String string, int charWrap) {
        int lastBreak = 0;
        int nextBreak = charWrap;
        if (string.length() > charWrap) {
            StringBuilder setString = new StringBuilder();
            setString.append("\n");
            do {
                while (string.charAt(nextBreak) != ' ' && nextBreak > lastBreak) {
                    nextBreak--;
                }
                if (nextBreak == lastBreak) {
                    nextBreak = lastBreak + charWrap;
                }
                setString
                        .append(ChatColor.GRAY)
                        .append(ChatColor.ITALIC)
                        .append(string.substring(lastBreak, nextBreak).trim())
                        .append("\n")
                        .append(ChatColor.GRAY)
                        .append(ChatColor.ITALIC);
                lastBreak = nextBreak;
                nextBreak += charWrap;

            } while (nextBreak < string.length());
            setString.append(string.substring(lastBreak).trim());
            return setString.toString();
        } else {
            return string;
        }
    }
}
