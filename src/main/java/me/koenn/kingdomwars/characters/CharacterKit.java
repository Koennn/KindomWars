package me.koenn.kingdomwars.characters;

import me.koenn.core.misc.ItemHelper;
import me.koenn.kingdomwars.util.JSONSerializable;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CharacterKit implements JSONSerializable {

    private final String name;
    private final List<ItemStack> items = new ArrayList<>();

    public CharacterKit(JSONObject json) {
        this.name = (String) json.get("name");
        ((JSONArray) json.get("items")).forEach(item -> this.items.add(ItemHelper.stringToItem((String) item)));
    }

    public String getName() {
        return name;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        JSONArray items = new JSONArray();
        this.items.forEach(item -> items.add(ItemHelper.itemToString(item)));
        json.put("items", items);
        return json;
    }
}
