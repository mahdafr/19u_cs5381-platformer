package edu.utep.cs5381.platformer;

import android.graphics.PointF;

public class PlayerState {
    private int numCredits;
    private int mgFireRate;
    private int lives;
    private int shields;
    private float restartX;
    private float restartY;

    PlayerState() {
        lives = 3;
        mgFireRate = 1;
        numCredits = 0;
        shields = 5;
    }

    void saveLocation(PointF location) {
        // The location saves each time the player uses a teleport
        // But as this is a rogue-like if the player quits or dies then they need to start again
        restartX = location.x;
        restartY = location.y;
    }

    PointF loadLocation() { // Used every time the player loses a life
        return new PointF(restartX, restartY);
    }

    int getLives() {
        return lives;
    }

    int getFireRate(){
        return mgFireRate;
    }
	
	public void increaseFireRate(){
        mgFireRate += 2;
    }

    void gotCredit(){
        numCredits ++;
    }

    int getCredits(){
        return numCredits;
    }

    void getHit(){
        if ( shieldStrength()>0 )
            shields--;
        else
            lives--;
    }

    void addLife(){
        lives++;
    }

    int shieldStrength() {
        return shields;
    }

    boolean hasShields() {
        return shieldStrength()>0;
    }

    float startX() {
        return restartX;
    }

    float startY() {
        return restartY;
    }
}
