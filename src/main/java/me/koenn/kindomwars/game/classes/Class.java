package me.koenn.kindomwars.game.classes;

import java.util.Arrays;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class Class {

    private final String name;
    private final Kit[] kits;

    protected Class(String name, Kit[] kits) {
        this.name = name;
        this.kits = kits;
    }

    public String getName() {
        return name;
    }

    public Kit[] getKits() {
        return kits;
    }

    @Override
    public String toString() {
        return "Class{Name: " + name + ", Kits: " + Arrays.toString(kits) + "}";
    }
}
