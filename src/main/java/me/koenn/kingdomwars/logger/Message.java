package me.koenn.kingdomwars.logger;

import org.json.simple.JSONObject;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, May 2017
 */
public class Message extends JSONObject {

    public Message(String key, String value) {
        this(new String[]{key}, new String[]{value});
    }

    public Message(String[] keys, String[] values) {
        for (int i = 0; i < keys.length; i++) {
            this.put(keys[i], values[i]);
        }
    }
}
