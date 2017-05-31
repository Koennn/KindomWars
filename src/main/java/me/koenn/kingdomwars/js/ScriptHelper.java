package me.koenn.kingdomwars.js;

import org.bukkit.Bukkit;

import javax.script.Invocable;
import javax.script.ScriptEngine;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, May 2017
 */
public final class ScriptHelper {

    public static void invokeFunction(ScriptEngine script, String name, Object... args) {
        if (script == null) {
            Bukkit.getLogger().severe("Unable to read script, cancelling execution of method \'" + name + "\'");
            return;
        }
        try {
            ((Invocable) script).invokeFunction(name, args);
        } catch (Exception e) {
            Bukkit.getLogger().severe("Error while running function \'" + name + "\': \'" + e.toString() + "\'");
        }
    }
}
