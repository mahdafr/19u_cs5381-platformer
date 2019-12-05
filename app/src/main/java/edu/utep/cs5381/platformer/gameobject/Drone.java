package edu.utep.cs5381.platformer.gameobject;

import android.graphics.PointF;
import edu.utep.cs5381.platformer.visuals.VectorPoint;

public class Drone extends GameObject {
    private long lastWaypointSetTime;
    private PointF currentWaypoint;
    private final float MAX_X_VELOCITY = 3;
    private final float MAX_Y_VELOCITY = 3;
    private PointF waypoint;

    public Drone(float worldStartX, float worldStartY, char type) {
        final float HEIGHT = 1;
        final float WIDTH = 1;
        setHeight(HEIGHT); // 1 metre tall
        setWidth(WIDTH); // 1 metres wide
        setType(type);
        setBitmapName("drone");
        setMoves(true);
        setActive(true);
        setVisible(true);

        currentWaypoint = new PointF();

        // Where does the drone start
        // X and y locations from constructor parameters
        setWorldLocation(worldStartX, worldStartY, 0);
        setRectHitbox();
        setFacing(RIGHT);
    }


    public void update(long fps, float gravity) {
        if ( currentWaypoint.x > getWorldLocation().X() ) {
            setxVelocity(MAX_X_VELOCITY);
        } else if ( currentWaypoint.x < getWorldLocation().X() ) {
            setxVelocity(-MAX_X_VELOCITY);
        } else {
            setxVelocity(0);
        }

        if ( currentWaypoint.y >= getWorldLocation().Y() ) {
            setyVelocity(MAX_Y_VELOCITY);
        } else if ( currentWaypoint.y < getWorldLocation().Y() ) {
            setyVelocity(-MAX_Y_VELOCITY);
        } else {
            setyVelocity(0);
        }

        // update the drone hitbox
        setRectHitbox();
        move(fps);
    }

    public void setWaypoint(VectorPoint playerLocation) {
        //have 2 seconds passed?
        if ( System.currentTimeMillis() > lastWaypointSetTime+2000 ) {
            lastWaypointSetTime = System.currentTimeMillis();
            currentWaypoint.x = playerLocation.X();
            currentWaypoint.y = playerLocation.Y();
        }
    }
}
