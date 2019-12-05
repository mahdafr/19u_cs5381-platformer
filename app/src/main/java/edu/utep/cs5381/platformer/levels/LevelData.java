package edu.utep.cs5381.platformer.levels;

import java.util.ArrayList;

import edu.utep.cs5381.platformer.visuals.BackgroundData;

public class LevelData {
    private Location info;
    ArrayList<String> tiles;
    ArrayList<BackgroundData> backgroundDataList;
    ArrayList<Location> locations;

    public ArrayList<Location> locations() {
        return locations;
    }

    public ArrayList<String> tiles() {
        return tiles;
    }

    public ArrayList<BackgroundData> bgData() {
        return backgroundDataList;
    }

    // This class will evolve along with the project
    // Tile types
    // . = no tile
    // 1 = Grass
    // 2 = Snow
    // 3 = Brick
    // 4 = Coal
    // 5 = Concrete
    // 6 = Scorched
    // 7 = Stone

    //Active objects
    // g = guard
    // d = drone
    // t = teleport
    // c = coin
    // u = upgrade
    // f = fire
    // e  = extra life

    //Inactive objects
    // w = tree
    // x = tree2 (snowy)
    // l = lampost
    // r = stalactite
    // s = stalacmite
    // m = mine cart
    // z = boulders
}
