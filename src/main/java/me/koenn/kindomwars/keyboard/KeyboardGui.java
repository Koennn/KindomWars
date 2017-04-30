package me.koenn.kindomwars.keyboard;

import me.koenn.core.gui.Gui;
import me.koenn.core.gui.Option;
import me.koenn.core.misc.FancyString;
import me.koenn.core.misc.ItemHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KeyboardGui extends Gui {

    private final ItemStack result;
    private final boolean fancyFormatting;
    private String input = "";

    public KeyboardGui(Player player, String subject, InputListener listener) {
        this(player, subject, listener, true);
    }

    public KeyboardGui(Player player, String subject, InputListener listener, boolean fancyFormatting) {
        super(player, "Enter " + subject, 54);
        this.fancyFormatting = fancyFormatting;

        this.result = ItemHelper.makeItemStack(
                Material.PAPER, 1, (short) 0, subject + ": ", null
        );
        this.setResult();

        for (int i = 0; i < 26; i++) {
            short meta = i % 2 == 0 ? (short) 8 : (short) 10;
            final String letter = Character.toString((char) (i + 65));
            this.setOption(i + 18, new Option(ItemHelper.makeItemStack(
                    Material.INK_SACK, 1, meta, ChatColor.WHITE + letter, null
            ), () -> {
                this.input += letter;
                this.displayData(subject);
                this.setResult();
            }));
        }
        this.setOption(44, new Option(ItemHelper.makeItemStack(
                Material.INK_SACK, 1, (short) 8, ChatColor.WHITE + "SPACE", null
        ), () -> {
            this.input += " ";
            this.displayData(subject);
            this.setResult();
        }));

        this.setOption(48, new Option(ItemHelper.makeItemStack(
                Material.WOOL, 1, (short) 14, ChatColor.WHITE + "BACKSPACE", null
        ), () -> {
            if (this.input.length() == 0) {
                return;
            }
            this.input = this.input.substring(0, this.input.length() - 1);
            this.displayData(subject);
            this.setResult();
        }));

        this.setOption(50, new Option(ItemHelper.makeItemStack(
                Material.WOOL, 1, (short) 5, ChatColor.WHITE + "CONFIRM", null
        ), () -> {
            player.closeInventory();
            listener.run(this.input);
        }));
    }

    private void displayData(String subject) {
        ItemMeta meta = result.getItemMeta();
        meta.setDisplayName(subject + ": " + (this.fancyFormatting ? new FancyString(this.input).toString() : this.input));
        result.setItemMeta(meta);
    }

    private void setResult() {
        this.setOption(4, new Option(this.result, this::setResult));
    }
}
