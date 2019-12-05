package edu.utep.cs5381.platformer.visuals;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

public class Background {
    private Bitmap bitmap;
    private Bitmap bitmapReversed;
    private int width;
    private int height;
    private boolean reversedFirst;
    private int xClip;// controls where we clip the bitmaps each frame
    private float y;
    private float endY;
    private int z;
    private float speed;
    private Rect fromRect1 = new Rect();
    private Rect toRect1 = new Rect();
    private Rect fromRect2 = new Rect();
    private Rect toRect2 = new Rect();

    public Background(Context context, int yPixelsPerMetre, int screenWidth, BackgroundData data){
        int resID = context.getResources().getIdentifier(data.bitmapName,
                "drawable", context.getPackageName());

        // For parallax
        bitmap = BitmapFactory.decodeResource(context.getResources(), resID);

        // Which version of background (reversed or regular) is currently drawn first (on left)
        reversedFirst = false;

        //Initialise animation variables.
        xClip = 0;  //always start at zero
        y = data.startY;
        endY = data.endY;
        z = data.layer;
        speed = data.speed; //Scrolling background speed

        bitmap = Bitmap.createScaledBitmap(bitmap, screenWidth,
                data.height * yPixelsPerMetre
                , true); //Scale background to fit the screen resolution.

        width = bitmap.getWidth();
        height = bitmap.getHeight();

        //Create a mirror image of the background (horizontal flip)
        Matrix matrix = new Matrix();  
        matrix.setScale(-1, 1); 
        bitmapReversed = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    public void draw(Canvas canvas, Paint paint, Viewport vp, int start, int stop, float vel) {
        if ( z < start && z > stop) {
            // Is this layer in the viewport?
            // Clip anything off-screen
            if (!vp.clipObjects(-1, y, 1000, height)) {
                float floatstartY = ((vp.getyCentre() - ((vp.getViewportWorldCentreY() - y) * vp.getPixelsPerMetreY())));
                int startY = (int) floatstartY;
                float floatendY = ((vp.getyCentre() - ((vp.getViewportWorldCentreY() - endY) * vp.getPixelsPerMetreY())));
                int endY = (int) floatendY;

                //define what portion of bitmaps to capture and what coordinates to draw them at
                fromRect1 = new Rect(0, 0, width-xClip, height);
                toRect1 = new Rect(xClip, startY, width, endY);
                fromRect2 = new Rect(width-xClip, 0, width, height);
                toRect2 = new Rect(0, startY, xClip, endY);
            }

            drawBGs(canvas, paint);

            updateClip(vel);
        }
    }

    private void drawBGs(Canvas c, Paint p) {
        if ( reversedFirst ) {
            c.drawBitmap(bitmap, fromRect1, toRect1, p);
            c.drawBitmap(bitmapReversed, fromRect2, toRect2, p);
        } else {
            c.drawBitmap(bitmap, fromRect2, toRect2, p);
            c.drawBitmap(bitmapReversed, fromRect1, toRect1, p);
        }
    }

    private void updateClip(float v) {
        xClip -= v/(20/speed);
        if ( xClip >= width ) {
            xClip = 0;
            reversedFirst = !reversedFirst;
        } else if (xClip <= 0) {
            xClip = width;
            reversedFirst = !reversedFirst;
        }
    }
}
