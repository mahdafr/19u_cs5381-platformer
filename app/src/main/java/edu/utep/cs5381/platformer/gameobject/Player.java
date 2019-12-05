package edu.utep.cs5381.platformer.gameobject;

import android.content.Context;
import edu.utep.cs5381.platformer.*;
import edu.utep.cs5381.platformer.visuals.RectHitbox;

public class Player extends GameObject {
    public MachineGun bfg;
    private RectHitbox rectHitboxFeet;
    private RectHitbox rectHitboxHead;
    private RectHitbox rectHitboxLeft;
    private RectHitbox rectHitboxRight;
    private final float MAX_X_VELOCITY = 10;
    public boolean isFalling;
    private boolean isJumping;
    private long jumpTime;
    private long maxJumpTime = 700;// jump 7 10ths of second half up half down
    private boolean isPressingRight = false;
    private boolean isPressingLeft = false;

    public Player(Context context, float worldStartX, float worldStartY, int pixelsPerMetre) {
        bfg = new MachineGun();
        setHeight(2); // 2 metres tall
        setWidth(1); // 1 metre wide
        // Standing still to start with
        setxVelocity(0);
        setyVelocity(0);
        setFacing(LEFT);
        isFalling = false;

        setMoves(true);
        setActive(true);
        setVisible(true);
        setType('p');
        setBitmapName("player");

        final int ANIMATION_FPS = 16;
        final int ANIMATION_FRAME_COUNT = 5;
        // Set this object up to be animated
        setAnimFps(ANIMATION_FPS);
        setAnimFrameCount(ANIMATION_FRAME_COUNT);
        setAnimated(context, pixelsPerMetre, true);
        // X and y locations from constructor parameters
        setWorldLocation(worldStartX, worldStartY, 0);
        rectHitboxFeet = new RectHitbox();
        rectHitboxHead = new RectHitbox();
        rectHitboxLeft = new RectHitbox();
        rectHitboxRight = new RectHitbox();
    }

    public void update(long fps, float gravity) {
        if (isPressingRight) {
            setxVelocity(MAX_X_VELOCITY);
        } else if (isPressingLeft) {
            setxVelocity(-MAX_X_VELOCITY);
        } else {
            setxVelocity(0);
        }

        //what way is player facing?
        if (getxVelocity() > 0) {
            setFacing(RIGHT);
        } else if (getxVelocity() < 0) {
            setFacing(LEFT);
        }//if 0 then unchanged

        // Jumping and gravity
        if (isJumping) {
            long timeJumping = System.currentTimeMillis() - jumpTime;
            if (timeJumping < maxJumpTime) {
                if (timeJumping < maxJumpTime / 2) {
                    this.setyVelocity(-gravity);//on the way up
                } else if (timeJumping > maxJumpTime / 2) {
                    this.setyVelocity(gravity);//going down
                }
            } else
                isJumping = false;
        } else {
            this.setyVelocity(gravity);
            isFalling = true;
        }

        bfg.update(fps, gravity);
        move(fps);
        updateHitBoxes(getWorldLocation().X(), getWorldLocation().Y());
    }

    private void updateHitBoxes(float lx, float ly) {
        rectHitboxFeet.setTop(ly + (getHeight() * .95f));
        rectHitboxFeet.setLeft(lx + getWidth() * .2f);
        rectHitboxFeet.setBottom(ly + getHeight() * .98f);
        rectHitboxFeet.setRight(lx + getWidth() * .8f);

        rectHitboxHead.setTop(ly);
        rectHitboxHead.setLeft(lx + (getWidth() * .4f));
        rectHitboxHead.setBottom(ly + getHeight() * .2f);
        rectHitboxHead.setRight(lx + (getWidth() * .6f));

        rectHitboxLeft.setTop(ly + getHeight() * .2f);
        rectHitboxLeft.setLeft(lx + getWidth() * .2f);
        rectHitboxLeft.setBottom(ly + getHeight() * .8f);
        rectHitboxLeft.setRight(lx + (getWidth() * .3f));

        rectHitboxRight.setTop(ly + getHeight() * .2f);
        rectHitboxRight.setLeft(lx + (getWidth() * .8f));
        rectHitboxRight.setBottom(ly + getHeight() * .8f);
        rectHitboxRight.setRight(lx + getWidth() * .7f);
    }

    public boolean pullTrigger() {
        //Try and fire a shot
        return bfg.shoot(this.getWorldLocation().X(), this.getWorldLocation().Y(), getFacing(), getHeight());
    }

    public void restorePreviousVelocity() {
        if ( isJumping && isFalling )
            return;
        if (getFacing() == LEFT) {
            isPressingLeft = true;
            setxVelocity(-MAX_X_VELOCITY);
        } else {
            isPressingRight = true;
            setxVelocity(MAX_X_VELOCITY);
        }
    }

    public int checkCollisions(RectHitbox rectHitbox) {
        int collided = 0;//no collision

        //the left
        if (rectHitboxLeft.intersects(rectHitbox)) {
            setWorldLocationX(rectHitbox.getRight() - getWidth() * .2f);
            //setxVelocity(0);
            //setPressingLeft(false);
            collided = 1;
        }

        //the right
        if (rectHitboxRight.intersects(rectHitbox)) {
            setWorldLocationX(rectHitbox.getLeft() - getWidth() * .8f);
            //setxVelocity(0);
            //setPressingRight(false);
            collided = 1;
        }

        //the feet
        if (rectHitboxFeet.intersects(rectHitbox)) {
            setWorldLocationY(rectHitbox.getTop() - getHeight());
            collided = 2;
            //isFalling = false;
        }

        //Now the head
        if (rectHitboxHead.intersects(rectHitbox)) {
            setWorldLocationY(rectHitbox.getBottom());
            collided = 3;
        }

        return collided;
    }

    public void setPressingRight(boolean pR) {
        isPressingRight = pR;
    }

    public void setPressingLeft(boolean pL) {
        isPressingLeft = pL;
    }

    public void startJump(SoundManager sm) {
        if ( isFalling || isJumping )
            return;
        isJumping = true;
        jumpTime = System.currentTimeMillis();
        sm.play(SoundManager.Sound.JUMP);
    }
}