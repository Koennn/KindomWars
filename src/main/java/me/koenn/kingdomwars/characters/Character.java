package me.koenn.kingdomwars.characters;

import me.koenn.core.misc.ItemHelper;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

public class Character {

    private final String name;
    private final String description;
    private final ItemStack icon;

    public Character(JSONObject json) {
        this.name = (String) json.get("name");
        this.description = (String) json.get("description");
        this.icon = ItemHelper.stringToItem((String) json.get("icon"));
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ItemStack getIcon() {
        return icon;
    }
}
