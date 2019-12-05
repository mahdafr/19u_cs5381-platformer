package edu.utep.cs5381.platformer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import java.util.ArrayList;
import edu.utep.cs5381.platformer.gameobject.*;
import edu.utep.cs5381.platformer.levels.*;
import edu.utep.cs5381.platformer.visuals.Background;
import edu.utep.cs5381.platformer.visuals.BackgroundData;

class LevelManager {
    int mapWidth;
    int mapHeight;
    private Player player;
    int playerIndex;
    private boolean playing;
    float gravity;
    private LevelData levelData;
    ArrayList<GameObject> gameObjects;
    ArrayList<Background> backgrounds;
    Bitmap[] bitmapsArray;

    LevelManager(Context context, int pixelsPerMetre, int screenWidth, InputController ic, String level, float px, float py) {
        switch (level) {
            case "LevelCave":
                levelData = new LevelCave();
                break;
            case "LevelCity":
                levelData = new LevelCity();
                break;
            case "LevelForest":
                levelData = new LevelForest();
                break;
            case "LevelMountain":
                levelData = new LevelMountain();
                break;
        }

        // To hold all our GameObjects
        gameObjects = new ArrayList<>();
        // To hold 1 of every Bitmap
        bitmapsArray = new Bitmap[25];
        // Load all the GameObjects and Bitmaps
        loadMapData(context, pixelsPerMetre, px, py);
        loadBackgrounds(context, pixelsPerMetre, screenWidth);
        // Set waypoints for our guards
        setWaypoints();
        //playing = true;
    }

    private void loadBackgrounds(Context context, int pixelsPerMetre, int screenWidth) {
        backgrounds = new ArrayList<Background>();
        //load the background data into the Background objects and
        // place them in our GameObject arraylist
        for ( BackgroundData bgData : levelData.bgData() )
            backgrounds.add(new Background(context, pixelsPerMetre, screenWidth, bgData));
    }

    private void setWaypoints() {
        // Loop through all game objects looking for Guards
        for (GameObject guard : this.gameObjects) {
            if (guard.getType() == 'g') {
                // Set waypoints for this guard
                // find the tile beneath the guard
                // this relies on the designer putting the guard in sensible location

                int startTileIndex = -1;
                int startGuardIndex = 0;
                float waypointX1 = -1;
                float waypointX2 = -1;
                //Log.d("yay","found a guard");
                //Log.d("before fors x1 = ", "" + waypointX1);
                //Log.d("before fors x2 = ", "" + waypointX2);

                for (GameObject tile : this.gameObjects) {
                    startTileIndex++;
                    if (tile.getWorldLocation().Y() == guard.getWorldLocation().Y() + 2) {
                        //tile is two space below current guard
                        // Now see if has same x coordinate
                        if (tile.getWorldLocation().X() == guard.getWorldLocation().X()) {
                            // Found the tile the guard is "standing" on
                            // Now go left as far as possible before non travers-able tile is found
                            // Either on guards row or tile row
                            // upto a maximum of 5 tiles. (5 is arbitrary value)
                            for (int i = 0; i < 5; i++) {// left for loop
                                if (!gameObjects.get(startTileIndex - i).isTraversable()) {
                                    //set the left waypoint
                                    waypointX1 = gameObjects.get(startTileIndex - (i + 1)).getWorldLocation().X();
                                    Log.d("set x1 = ", "" + waypointX1);
                                    break;// Leave left for loop
                                } else {
                                    //set to max 5 tiles as no non traversible tile found
                                    waypointX1 = gameObjects.get(startTileIndex - 5).getWorldLocation().X();
                                }
                            }// end get left waypoint
                            for (int i = 0; i < 5; i++) {// right for loop
                                if (!gameObjects.get(startTileIndex + i).isTraversable()) {
                                 //set the right waypoint
                                    waypointX2 = gameObjects.get(startTileIndex + (i - 1)).getWorldLocation().X();
                                    //Log.d("set x2 = ", "" + waypointX2);
                                    break;// Leave right for loop
                                } else {
                                    //set to max 5 tiles away
                                    waypointX2 = gameObjects.get(startTileIndex + 5).getWorldLocation().X();
                                }
                            }// end get right waypoint
                            Guard g = (Guard) guard;
                            g.setWaypoints(waypointX1, waypointX2);
                            //Log.d("after fors x1 = ", "" + waypointX1);
                        }
                    }
                }
            }
        }
    }

    void switchPlayingStatus() {
        playing = !playing;
        gravity = playing ? 6 : 0;
    }

    boolean isPlaying() {
        return playing;
    }

    // Each index Corresponds to a bitmap
    Bitmap getBitmap(char blockType) {
        int index;
        switch (blockType) {
            case '.':
                index = 0;
                break;
            case '1':
                index = 1;
                break;
            case 'p':
                index = 2;
                break;
            case 'c':
                index = 3;
                break;
            case 'u':
                index = 4;
                break;
            case 'e':
                index = 5;
                break;
            case 'd':
                index = 6;
                break;
            case 'g':
                index = 7;
                break;
            case 'f':
                index = 8;
                break;
            case '2':
                index = 9;
                break;
            case '3':
                index = 10;
                break;
            case '4':
                index = 11;
                break;
            case '5':
                index = 12;
                break;
            case '6':
                index = 13;
                break;
            case '7':
                index = 14;
                break;
            case 'w':
                index = 15;
                break;
            case 'x':
                index = 16;
                break;
            case 'l':
                index = 17;
                break;
            case 'r':
                index = 18;
                break;
            case 's':
                index = 19;
                break;
            case 'm':
                index = 20;
                break;
            case 'z':
                index = 21;
                break;
            case 't':
                index = 22;
                break;
            default:
                index = 0;
                break;
        }
        return bitmapsArray[index];
    }

    // This method allows each GameObject which 'knows'
    // its type to get the correct index to its Bitmap
    // in the Bitmap array.
    int getBitmapIndex(char blockType) {
        int index;
        switch (blockType) {
            case '.':
                index = 0;
                break;
            case '1':
                index = 1;
                break;
            case 'p':
                index = 2;
                break;
            case 'c':
                index = 3;
                break;
            case 'u':
                index = 4;
                break;
            case 'e':
                index = 5;
                break;
            case 'd':
                index = 6;
                break;
            case 'g':
                index = 7;
                break;
            case 'f':
                index = 8;
                break;
            case '2':
                index = 9;
                break;
            case '3':
                index = 10;
                break;
            case '4':
                index = 11;
                break;
            case '5':
                index = 12;
                break;
            case '6':
                index = 13;
                break;
            case '7':
                index = 14;
                break;
            case 'w':
                index = 15;
                break;
            case 'x':
                index = 16;
                break;
            case 'l':
                index = 17;
                break;
            case 'r':
                index = 18;
                break;
            case 's':
                index = 19;
                break;
            case 'm':
                index = 20;
                break;
            case 'z':
                index = 21;
                break;
            case 't':
                index = 22;
                break;
            default:
                index = 0;
                break;
        }
        return index;
    }

    // For now we just load all the grass tiles
    // and the player. Soon we will have many GameObjects
    private void loadMapData(Context context, int pixelsPerMetre, float px, float py) {
        char c;
        int currentIndex = -1;
        int teleportIndex = -1;
        mapHeight = levelData.tiles().size();
        mapWidth = levelData.tiles().get(0).length();

        for (int i = 0; i < levelData.tiles().size(); i++) {
            for (int j = 0; j < levelData.tiles().get(i).length(); j++) {
                c = levelData.tiles().get(i).charAt(j);
                if ( c=='.' )
                    continue;
                currentIndex++;
                switch (c) {
                    case '1':
                        gameObjects.add(new Grass(j, i, c));
                        break;
                    case 'p':
                        gameObjects.add(new Player
                                (context, px, py, pixelsPerMetre));
                        player = (Player) gameObjects.get(playerIndex = currentIndex);
                        break;
                    case 'c':
                        gameObjects.add(new Coin(j, i, c));
                        break;
                    case 'u':
                        gameObjects.add(new MachineGunUpgrade(j, i, c));
                        break;
                    case 'e':
                        gameObjects.add(new ExtraLife(j, i, c));
                        break;
                    case 'd':
                        gameObjects.add(new Drone(j, i, c));
                        break;
                    case 'g':
                        gameObjects.add(new Guard(context, j, i, c, pixelsPerMetre));
                        break;
                    case 'f':
                        gameObjects.add(new Fire(context, j, i, c, pixelsPerMetre));
                        break;
                    case '2':
                        // Add a tile to the gameObjects
                        gameObjects.add(new Snow(j, i, c));
                        break;
                    case '3':
                        gameObjects.add(new Brick(j, i, c));
                        break;
                    case '4':
                        gameObjects.add(new Coal(j, i, c));
                        break;
                    case '5':
                        gameObjects.add(new Concrete(j, i, c));
                        break;
                    case '6':
                        gameObjects.add(new Scorched(j, i, c));
                        break;
                    case '7':
                        gameObjects.add(new Stone(j, i, c));
                        break;
                    case 'w':
                        gameObjects.add(new Tree(j, i, c));
                        break;
                    case 'x':
                        gameObjects.add(new Tree2(j, i, c));
                        break;
                    case 'l':
                        gameObjects.add(new Lampost(j, i, c));
                        break;
                    case 'r':
                        gameObjects.add(new Stalactite(j, i, c));
                        break;
                    case 's':
                        gameObjects.add(new Stalagmite(j, i, c));
                        break;
                    case 'm':
                        gameObjects.add(new Cart(j, i, c));
                        break;
                    case 'z':
                        gameObjects.add(new Boulders(j, i, c));
                        break;
                    case 't':
                        teleportIndex++;
                        gameObjects.add(new Teleport(j, i, c, levelData.locations().get(teleportIndex)));
                        break;
                }
                // If the bitmap isn't prepared yet
                if (bitmapsArray[getBitmapIndex(c)] == null) {
                    // Prepare it now and put it in the bitmapsArrayList
                    bitmapsArray[getBitmapIndex(c)] =
                            gameObjects.get(currentIndex).prepareBitmap(context,
                            gameObjects.get(currentIndex).getBitmapName(),
                            pixelsPerMetre);
                }
            }
        }
    }

    Player player() {
        return player;
    }
}
