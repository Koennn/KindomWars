package me.koenn.kingdomwars.game;

import me.koenn.core.gui.Gui;
import me.koenn.core.gui.Option;
import me.koenn.core.misc.ItemHelper;
import me.koenn.kingdomwars.util.Messager;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, June 2017
 */
public class EditGameGui extends Gui {

    public EditGameGui(Player player, Game game) {
        super(player, "Edit Game", Gui.getRequiredRows(game.getClass().getDeclaredFields().length + game.getClass().getMethods().length));

        Field[] fields = game.getClass().getDeclaredFields();
        Method[] methods = game.getClass().getMethods();

        for (Field field : fields) {
            this.addOption(new Option(ItemHelper.makeItemStack(Material.PAPER, 1, (short) 0, field.getName(), new ArrayList<>()), () -> {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                try {
                    Object value = field.get(game);
                    if (value instanceof Object[]) {
                        Messager.playerMessage(player, Arrays.toString((Object[]) value));
                    } else if (value instanceof List) {
                        Messager.playerMessage(player, Arrays.toString(((List) value).toArray()));
                    } else if (value instanceof int[]) {
                        Messager.playerMessage(player, Arrays.toString((int[]) value));
                    } else {
                        Messager.playerMessage(player, String.valueOf(value));
                    }
                } catch (Exception e) {
                    Messager.playerMessage(player, "&4Unable to get field: " + e);
                    player.closeInventory();
                }
                field.setAccessible(accessible);
                player.closeInventory();
            }));
        }

        for (Method method : methods) {
            if (method.getParameterCount() > 0) {
                continue;
            }

            this.addOption(new Option(ItemHelper.makeItemStack(Material.COMMAND, 1, (short) 0, method.getName(), new ArrayList<>()), () -> {
                try {
                    Object value = method.invoke(game);
                    if (value != null) {
                        Messager.playerMessage(player, String.valueOf(value));
                    }
                    player.closeInventory();
                } catch (Exception e) {
                    Messager.playerMessage(player, "&4Unable to call method: " + e);
                    e.printStackTrace();
                    player.closeInventory();
                }
            }));
        }
    }
}
