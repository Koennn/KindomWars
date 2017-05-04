package me.koenn.kingdomwars.util;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public enum Team {

    BLUE(0), RED(1);

    int index;

    Team(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public Team getOpponent() {
        return this == RED ? BLUE : RED;
    }
}
