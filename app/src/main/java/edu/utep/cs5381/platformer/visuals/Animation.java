package edu.utep.cs5381.platformer.visuals;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class Animation {
    private Bitmap bitmapSheet;
    private String bitmapName;
    private Rect sourceRect;
    private int frameCount;
    private int currentFrame;
    private long frameTicker;
    private int framePeriod;
    private int frameWidth;
    private int frameHeight;

    public Animation(String bitmapName, float frameHeight, float frameWidth,
              int animFps, int frameCount, int pixelsPerMetre){

        this.currentFrame = 0;
        this.frameCount = frameCount;

        this.frameWidth = (int)frameWidth * pixelsPerMetre;
        this.frameHeight = (int)frameHeight * pixelsPerMetre;
        sourceRect = new Rect(0, 0, this.frameWidth, this.frameHeight);
        framePeriod = 1000 / animFps;
        frameTicker = 0l;
        this.bitmapName = "" + bitmapName;
    }

    public Rect getCurrentFrame(long time, float xVelocity, boolean moves) {
        //only if the object is moving or it is an object which doesn't move
        if ( xVelocity!=0 || !moves ) {
            if ( time>frameTicker+framePeriod ) {
                frameTicker = time;
                currentFrame++;
                if ( currentFrame>=frameCount )
                    currentFrame = 0;
            }
        }

        //update the left and right values of the source of
        //the next frame on the spritesheet
        this.sourceRect.left = currentFrame * frameWidth;
        this.sourceRect.right = this.sourceRect.left + frameWidth;

        return sourceRect;
    }
}