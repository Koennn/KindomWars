package me.koenn.kingdomwars.game.classes;

import me.koenn.core.gui.Gui;
import me.koenn.core.gui.Option;
import me.koenn.core.player.CPlayer;
import me.koenn.core.player.CPlayerRegistry;
import me.koenn.kingdomwars.util.Messager;
import me.koenn.kingdomwars.util.References;
import org.bukkit.entity.Player;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class ClassGui extends Gui {

    public ClassGui(Player player) {
        super(player, "Select class", 36);
        CPlayer cPlayer = CPlayerRegistry.getCPlayer(player.getUniqueId());

        for (Class cl : ClassLoader.getClasses()) {
            this.addOption(new Option(cl.getIcon(), () -> {
                cPlayer.set("most-preferred-class", cl.getName());
                Messager.playerMessage(player, References.SAVED_PREFERENCE);
                player.closeInventory();
            }));
        }
    }
}
