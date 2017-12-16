package me.koenn.kingdomwars.characters;

import me.koenn.core.gui.Gui;
import me.koenn.core.gui.Option;
import me.koenn.core.misc.ColorHelper;
import me.koenn.core.misc.ItemHelper;
import me.koenn.core.misc.LoreHelper;
import me.koenn.core.player.CPlayerRegistry;
import me.koenn.kingdomwars.util.Messager;
import me.koenn.kingdomwars.util.References;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class CharacterGui extends Gui {

    public CharacterGui(Player player) {
        super(player, "Select a character", 36);

        CharacterLoader.CHARACTER_REGISTRY.getRegisteredObjects().forEach(character -> {
            ItemStack raw = character.getIcon();
            ItemStack icon = ItemHelper.makeItemStack(
                    raw.getType(), raw.getAmount(), raw.getDurability(),
                    ColorHelper.readColor("&e&l" + character.getName()),
                    LoreHelper.makeLore(String.format(ChatColor.RESET + "Click to select %s!", character.getName()))
            );
            icon.getItemMeta().addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            this.addOption(new Option(icon, () -> {
                CPlayerRegistry.getCPlayer(player.getUniqueId()).set("character", character.getName());
                player.closeInventory();
                Messager.playerMessage(player, References.SAVED_PREFERENCE);
            }));
        });
    }
}
