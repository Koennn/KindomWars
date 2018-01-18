package me.koenn.kingdomwars.characters;

import me.koenn.core.gui.Gui;
import me.koenn.core.gui.Option;
import me.koenn.core.misc.ColorHelper;
import me.koenn.core.misc.ItemHelper;
import me.koenn.core.misc.LoreHelper;
import me.koenn.core.player.CPlayerRegistry;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CharacterGui extends Gui {

    public CharacterGui(Player player) {
        super(player, "Select a character", 36);

        CharacterLoader.CHARACTER_REGISTRY.getRegisteredObjects().forEach(character -> {
            ItemStack raw = character.getIcon();
            ItemStack icon = ItemHelper.makeItemStack(
                    raw.getType(), raw.getAmount(), raw.getDurability(),
                    ColorHelper.readColor("&e&l" + character.getName()),
                    LoreHelper.makeLore(
                            String.format(ChatColor.RESET + "Click to select %s!", character.getName()),
                            wrapString(character.getDescription(), 30)
                    )
            );
            ItemMeta meta = icon.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            icon.setItemMeta(meta);

            this.addOption(new Option(icon, () -> {
                CPlayerRegistry.getCPlayer(player.getUniqueId()).set("character", character.getName());
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
                        .append("\n");
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
