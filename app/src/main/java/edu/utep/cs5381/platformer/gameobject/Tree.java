package edu.utep.cs5381.platformer.gameobject;

import java.util.Random;

import edu.utep.cs5381.platformer.gameobject.GameObject;

public class Tree extends GameObject {

    public Tree(float worldStartX, float worldStartY, char type) {
        final float HEIGHT = 4;
        final float WIDTH = 2;
        setWidth(WIDTH);
        setHeight(HEIGHT);
        setType(type);
        setBitmapName("tree1");
        setActive(false);
        Random rand = new Random();
        if ( rand.nextInt(2)==0 )
            setWorldLocation(worldStartX, worldStartY, -1);
        else
            setWorldLocation(worldStartX, worldStartY, 1);
    }

    public void update(long fps, float gravity) {
    }
}
