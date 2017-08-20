package me.koenn.kingdomwars.stats;

import org.json.simple.JSONObject;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, June 2017
 */
public class Level {

    private int level;
    private int exp;

    public Level(int level, int exp) {
        this.level = level;
        this.exp = exp;
    }

    public Level(JSONObject json) {
        this.level = Math.toIntExact((long) json.get("level"));
        this.exp = Math.toIntExact((long) json.get("exp"));
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("level", this.level);
        json.put("exp", this.exp);
        return json;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }
}
