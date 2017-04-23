package me.koenn.kindomwars.game.classes;

import me.koenn.core.gui.Gui;
import me.koenn.core.gui.Option;
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
        super(player, "Class Select", 36);

        for (Class cl : ClassLoader.getClasses()) {
            this.addOption(new Option(cl.getIcon(), () -> {

            }));
        }
    }


}
