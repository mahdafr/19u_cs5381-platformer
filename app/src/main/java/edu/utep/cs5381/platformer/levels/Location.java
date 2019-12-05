package edu.utep.cs5381.platformer.levels;

public class Location {
    private String level;
    private float x;
    private float y;

    public Location(String level, float x, float y){
        this.level = level;
        this.x = x;
        this.y = y;
    }

    public float X() {
        return x;
    }

    public float Y() {
        return y;
    }

    public String level() {
        return level;
    }
}
