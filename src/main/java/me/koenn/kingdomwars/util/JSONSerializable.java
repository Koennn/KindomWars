package me.koenn.kingdomwars.util;

import org.json.simple.JSONObject;

public interface JSONSerializable {

    void fromJSON(JSONObject json);

    JSONObject toJSON();
}
