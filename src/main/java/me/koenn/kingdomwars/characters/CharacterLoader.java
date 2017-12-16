package me.koenn.kingdomwars.characters;

import me.koenn.core.data.JSONManager;
import me.koenn.core.registry.Registry;
import me.koenn.kingdomwars.KingdomWars;

import java.io.File;

public final class CharacterLoader {

    public static final Registry<Character> CHARACTER_REGISTRY = new Registry<>(Character::getName);

    private static final File CHARACTERS_FOLDER = new File(KingdomWars.getInstance().getDataFolder(), "characters");
    private static final String CHARACTER_EXTENSION = ".character";

    private static Character loadCharacter(String file) {
        JSONManager manager = new JSONManager(KingdomWars.getInstance(), new File(CHARACTERS_FOLDER, file).getPath(), false);
        return new Character(manager.getBody());
    }

    public static void load() {
        if (!CHARACTERS_FOLDER.exists()) {
            CHARACTERS_FOLDER.mkdir();
        }

        for (File file : CHARACTERS_FOLDER.listFiles()) {
            if (file.getName().endsWith(CHARACTER_EXTENSION)) {
                CHARACTER_REGISTRY.register(loadCharacter(file.getName()));
            }
        }
    }
}
