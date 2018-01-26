package me.koenn.kingdomwars.characters;

import me.koenn.core.misc.ItemHelper;
import me.koenn.core.misc.ReflectionHelper;
import me.koenn.kingdomwars.traits.Trait;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

public class Character {

    private final String name;
    private final String description;
    private final ItemStack icon;
    private final CharacterKit kit;
    private final boolean partyOnly;
    private Class<? extends Trait> trait;
    private String grenade;

    public Character(JSONObject json) {
        this.name = (String) json.get("name");
        this.description = (String) json.get("description");
        this.icon = ItemHelper.stringToItem((String) json.get("icon"));
        this.kit = new CharacterKit((JSONObject) json.get("kit"));
        if (json.containsKey("trait")) {
            this.trait = ReflectionHelper.getClass("me.koenn.kingdomwars.traits." + json.get("trait"));
        }
        if (json.containsKey("grenade")) {
            this.grenade = (String) json.get("grenade");
        }
        this.partyOnly = json.containsKey("partyOnly");
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

    public CharacterKit getKit() {
        return kit;
    }

    public boolean isPartyOnly() {
        return partyOnly;
    }

    public Class<? extends Trait> getTrait() {
        return trait;
    }

    public String getGrenade() {
        return grenade;
    }
}
