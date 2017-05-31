package me.koenn.kingdomwars.js;

import me.koenn.kingdomwars.KingdomWars;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, May 2017
 */
public class JSReader {

    private static ScriptEngine objectLoader;

    public static ScriptEngine read(String script, Class... load) {
        ClassLoader cl = KingdomWars.getInstance().getClass().getClassLoader();
        Thread.currentThread().setContextClassLoader(cl);
        try {
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            engine.eval(script);

            for (Class toLoad : load) {
                engine.put(toLoad.getSimpleName().replace("[]", ""), getClassVar(toLoad.getName()));
            }
            return engine;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Object getClassVar(String path) throws ScriptException, NoSuchMethodException, FileNotFoundException {
        if (objectLoader == null) {
            objectLoader = new ScriptEngineManager().getEngineByName("nashorn");
            objectLoader.eval(new FileReader("objects.js"));
        }

        return ((Invocable) objectLoader).invokeFunction("getObject", path);
    }
}
