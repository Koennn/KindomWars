package me.koenn.kingdomwars.party;

import me.koenn.core.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {

    public static final Registry<Party> REGISTRY = new Registry<>();

    private final UUID owner;
    private final String name;
    private final List<UUID> members;

    public Party(UUID owner, String name) {
        this.owner = owner;
        this.name = name;
        this.members = new ArrayList<>();
        this.members.add(owner);
    }

    public static Party getParty(UUID uuid) {
        for (Party party : REGISTRY.getRegisteredObjects()) {
            if (party.getMembers().contains(uuid)) {
                return party;
            }
        }
        return null;
    }

    public static boolean isInParty(UUID uuid) {
        return getParty(uuid) != null;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public void addMember(UUID uuid) {
        this.members.add(uuid);
    }
}
