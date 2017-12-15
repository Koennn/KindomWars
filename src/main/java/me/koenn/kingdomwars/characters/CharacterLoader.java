package me.koenn.kingdomwars.characters;

import me.koenn.core.data.JSONManager;
import me.koenn.core.registry.Registry;
import me.koenn.kingdomwars.KingdomWars;

import java.io.File;

public final class CharacterLoader {

    public static final Registry<Character> CHARACTER_REGISTRY = new Registry<>(Character::getName);
    public static final String CHARACTERS_FOLDER = "characters";
    public static final String CHARACTER_EXTENSION = ".character";

    private static Character loadCharacter(String file) {
        return new Character(new JSONManager(KingdomWars.getInstance(), file).getBody());
    }

    public static void loadCharacters() {
        File folder = new File(KingdomWars.getInstance().getDataFolder(), CHARACTERS_FOLDER);
        if (!folder.exists()) {
            folder.mkdir();
        }

        for (File file : folder.listFiles()) {
            if (file.getName().endsWith(CHARACTER_EXTENSION)) {
                CHARACTER_REGISTRY.register(loadCharacter(file.getName()));
            }
        }
    }
}
