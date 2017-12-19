package me.koenn.kingdomwars.game.map;

import me.koenn.kingdomwars.util.JSONSerializable;
import org.json.simple.JSONObject;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class Door implements JSONSerializable {

    private final double location;
    private final DoorType type;

    public Door(JSONObject json) {
        this.location = Double.parseDouble(String.valueOf(json.get("location")));
        this.type = DoorType.valueOf((String) json.get("type"));
    }

    public double getLocation() {
        return location;
    }

    public DoorType getType() {
        return type;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("location", this.location);
        json.put("type", this.type.name());
        return json;
    }

    public enum DoorType {
        X, Z
    }
}
