package edu.utep.cs5381.platformer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;

import edu.utep.cs5381.platformer.gameobject.*;
import edu.utep.cs5381.platformer.levels.*;
import edu.utep.cs5381.platformer.visuals.*;

public class PlatformView extends SurfaceView implements Runnable {
    private boolean debugging = false;
    private volatile boolean running;
    private Thread gameThread = null;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;
    Context context;
    private LevelManager lm;
    private Viewport vp;
    InputController ic;
    SoundManager sm;
    private PlayerState ps;
    long startFrameTime;
    long timeThisFrame;
    long fps;
    private ArrayList<Location> levelList;
    private int currentLevel = -1;
    private boolean gameOver = false;

    PlatformView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.context = context;
        ourHolder = getHolder();
        paint = new Paint();

        // Initialize the viewport
        vp = new Viewport(screenWidth, screenHeight);
        sm = new SoundManager(context);
        ps = new PlayerState();

        //add the levels for the game
        levelList = new ArrayList<>();
        levelList.add(new Location("LevelMountain",118,17));
        levelList.add(new Location("LevelForest", 1, 17));
        levelList.add(new Location("LevelCity", 118, 18));
        levelList.add(new Location("LevelCave", 1, 16));
        loadLevel(levelList.get(++currentLevel));
    }

    private void loadLevel(Location l) {
        String level = l.level();
        float px = l.X();
        float py = l.Y();

        lm = new LevelManager(context, vp.getPixelsPerMetreX(), vp.getScreenWidth(), ic, level, px, py);
        ic = new InputController(vp.getScreenWidth(), vp.getScreenHeight());

        PointF location = new PointF(px, py);
        ps.saveLocation(location);

        lm.player().bfg.setFireRate(ps.getFireRate());
        vp.setWorldCentre(lm.gameObjects.get(lm.playerIndex).getWorldLocation().X(),
                lm.gameObjects.get(lm.playerIndex).getWorldLocation().Y());
    }

    private void update() {
        for (GameObject go : lm.gameObjects) {
            if ( !go.isActive() )
                continue;
            // Clip anything off-screen
            if (!vp.clipObjects(go.getWorldLocation().X(), go.getWorldLocation().Y(), go.getWidth(), go.getHeight())) {

                // Set visible flag to true
                go.setVisible(true);
                // check collisions with player
                int hit = lm.player().checkCollisions(go.getHitbox());
                if (hit > 0) {
                    //collision! Now deal with different types
                    switch (go.getType()) {
                        case 'c':
                            sm.play(SoundManager.Sound.COIN);
                            go.setActive(false);
                            go.setVisible(false);
                            ps.gotCredit();
                            // Now restore velocity that was removed by collision detection
                            if (hit != 2)
                                lm.player().restorePreviousVelocity();
                            break;

                        case 'u':
                            sm.play(SoundManager.Sound.GUN_UPGRADE);//playSound("gun_upgrade");
                            go.setActive(false);
                            go.setVisible(false);
                            lm.player().bfg.upgradeRateOfFire();
                            if (hit != 2)
                                lm.player().restorePreviousVelocity();
                            break;

                        case 'e':
                            //extralife
                            go.setActive(false);
                            go.setVisible(false);
                            sm.play(SoundManager.Sound.EXTRA_LIFE);
                            ps.addLife();
                            if (hit != 2)
                                lm.player().restorePreviousVelocity();
                            break;

                        case 'd':
                            PointF location;
                            //hit by drone
                            sm.play(SoundManager.Sound.PLAYER_BURN);
                            ps.getHit();
                            location = new PointF(ps.loadLocation().x, ps.loadLocation().y);
                            lm.player().setWorldLocationX(location.x);
                            lm.player().setWorldLocationY(location.y);
                            lm.player().setxVelocity(0);
                            break;

                        case 'g': case 'f':
                            //hit by guard
                            sm.play(SoundManager.Sound.PLAYER_BURN);
                            ps.getHit();
                            location = new PointF(ps.loadLocation().x, ps.loadLocation().y);
                            lm.player().setWorldLocationX(location.x);
                            lm.player().setWorldLocationY(location.y);
                            lm.player().setxVelocity(0);
                            break;

                        case 't':
                            currentLevel++; // move to next level
                            loadLevel(((Teleport) go).getTarget());
                            sm.play(SoundManager.Sound.TELEPORT);
                            break;

                        default:// Probably a regular tile
                            if (hit == 1) {// Left or right
                                lm.player().setxVelocity(0);
                                lm.player().setPressingRight(false);
                            }
                            if (hit == 2) {// Feet
                                lm.player().isFalling = false;
                            }
                            break;
                    }
                }

                //Check bullet collisions
                for (int i = 0; i < lm.player().bfg.getNumBullets(); i++) {
                    //Make a hitbox out of the the current bullet
                    RectHitbox r = new RectHitbox();
                    r.setLeft(lm.player().bfg.getBulletX(i));
                    r.setTop(lm.player().bfg.getBulletY(i));
                    r.setRight(lm.player().bfg.getBulletX(i) + .1f);
                    r.setBottom(lm.player().bfg.getBulletY(i) + .1f);

                    if (go.getHitbox().intersects(r)) {
                        //collision detected
                        //make bullet disappear until it is respawned as a new bullet
                        lm.player().bfg.hideBullet(i);

                        //Now respond depending upon the type of object hit
                        if (go.getType() != 'g' && go.getType() != 'd') {
                            sm.play(SoundManager.Sound.RICOCHET);//playSound("ricochet");
                        } else if (go.getType() == 'g') {
                            // Knock the guard back
                            go.setWorldLocationX(go.getWorldLocation().X() + 2 * (lm.player().bfg.getDirection(i)));
                            sm.play(SoundManager.Sound.HIT_GUARD);//playSound("hit_guard");
                        } else if (go.getType() == 'd') {
                            //destroy the droid
                            sm.play(SoundManager.Sound.EXPLODE);//playSound("explode");
                            //permanently clip this drone
                            go.setWorldLocation(-100, -100, 0);
                        }
                    }
                }

                if (lm.isPlaying()) {
                    // Run any un-clipped updates
                    go.update(fps, lm.gravity);
                    if (go.getType() == 'd') {// Let any near by drones know where the player is
                        Drone d = (Drone) go;
                        d.setWaypoint(lm.player().getWorldLocation());
                    }
                }
            } else
                go.setVisible(false);
        }

        if (lm.isPlaying()) {
            //Reset the players location as the world centre of the viewport
            //if game is playing
            vp.setWorldCentre(lm.gameObjects.get(lm.playerIndex)
                            .getWorldLocation().X(),
                    lm.gameObjects.get(lm.playerIndex)
                            .getWorldLocation().Y());

            //Has player fallen out of the map?
            if (lm.player().getWorldLocation().X() < 0 ||
                    lm.player().getWorldLocation().X() > lm.mapWidth ||
                    lm.player().getWorldLocation().Y() > lm.mapHeight) {

                sm.play(SoundManager.Sound.PLAYER_BURN);//playSound("player_burn");
                ps.getHit();
                PointF location = new PointF(ps.loadLocation().x, ps.loadLocation().y);
                lm.player().setWorldLocationX(location.x);
                lm.player().setWorldLocationY(location.y);
                lm.player().setxVelocity(0);
            }

            // Check if game is over
            if ( ps.getLives() == 0 ) {
                ps = new PlayerState();
                loadLevel(levelList.get(currentLevel));
                gameOver = true;
            }
        }
    }

    /* ****************************** Draw methods ****************************** */

    private void draw() {
        if ( !ourHolder.getSurface().isValid() )
            return;
        canvas = ourHolder.lockCanvas();    //save canvas to be drawn on
        paint.setColor(Color.argb(255, 0, 0, 0));
        canvas.drawColor(Color.argb(255, 0, 0, 0));
        drawBackground(0, -3);

        Rect r = drawGameObjects(new Rect());
        if ( ps.hasShields() )
            drawShield();
        drawBullets(r);
        drawBackground(4, 0);   // from layer 1 to 3
        drawHUD();
        if ( debugging )
            drawDebugging();
        drawButtons();
        drawScreenOverlay();

        ourHolder.unlockCanvasAndPost(canvas);
    }

    private void drawBackground(int start, int stop) {
        for (Background bg : lm.backgrounds)
            bg.draw(canvas, paint, vp, start, stop, lm.player().getxVelocity());
    }

    private Rect drawGameObjects(Rect r) {
        for (int layer = -1; layer <= 1; layer++) {
            for (GameObject go : lm.gameObjects) {
                if ( !go.isVisible() || go.getWorldLocation().Z()!=layer )
                    continue;
                r.set(vp.worldToScreen
                        (go.getWorldLocation().X(),
                                go.getWorldLocation().Y(),
                                go.getWidth(),
                                go.getHeight()));
                if (go.isAnimated()) { // Get the next frame of the bitmap
                    if (go.getFacing() == 1) { //Rotate
                        Matrix flipper = new Matrix();
                        flipper.preScale(-1, 1);
                        Rect tmp = go.getRectToDraw(System.currentTimeMillis());
                        Bitmap b = Bitmap.createBitmap(lm.bitmapsArray[lm.getBitmapIndex(
                                go.getType())],
                                tmp.left, tmp.top,
                                tmp.width(), tmp.height(),
                                flipper, true);
                        canvas.drawBitmap(b,
                                r.left,
                                r.top, paint);
                    } else {
                        canvas.drawBitmap(lm.bitmapsArray[lm.getBitmapIndex(go.getType())],
                                go.getRectToDraw(System.currentTimeMillis()),r, paint);
                    }
                } else { // Just draw the whole bitmap
                    canvas.drawBitmap(lm.bitmapsArray[lm.getBitmapIndex(go.getType())],
                            r.left, r.top, paint);
                }
            }
        }
        return r;
    }

    private void drawShield() {
        Paint cir = new Paint();
        cir.setStyle(Paint.Style.STROKE);
        cir.setColor(Color.argb(255,0,138,245));
        cir.setStrokeWidth(4);
        float x = ps.startX() + lm.player().getWorldLocation().X();
        float y = ps.startY() + lm.player().getWorldLocation().Y() + vp.getViewportWorldCentreY();
        canvas.drawCircle(x,y,lm.player().getWidth()*10,cir);
    }

    private void drawBullets(Rect r) {
        paint.setColor(Color.argb(255, 255, 255, 255));
        for (int i = 0; i < lm.player().bfg.getNumBullets(); i++) {
            r.set(vp.worldToScreen(lm.player().bfg.getBulletX(i),
                    lm.player().bfg.getBulletY(i),
                    .25f,
                    .05f));
            canvas.drawRect(r, paint);
        }
    }

    private void drawHUD() {
        // This code relies on the bitmaps from the extra life, upgrade and coin
        // Therefore there must be at least one of each in the level
        int topSpace = vp.getPixelsPerMetreY() / 4;
        int iconSize = vp.getPixelsPerMetreX();
        int padding = vp.getPixelsPerMetreX() / 5;
        int centring = vp.getPixelsPerMetreY() / 6;
        paint.setTextSize(vp.getPixelsPerMetreY()/2);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.argb(100, 0, 0, 0));
        canvas.drawRect(0,0,iconSize * 10.0f, topSpace*2 + iconSize,paint);
        paint.setColor(Color.argb(255, 255, 255, 0));
        canvas.drawBitmap(lm.getBitmap('e'), 0, topSpace, paint);
        canvas.drawText("" + ps.getLives(), iconSize + padding, (iconSize) - centring, paint);
        canvas.drawBitmap(lm.getBitmap('c'), (iconSize * 2.5f) + padding, topSpace, paint);
        canvas.drawText("" + ps.getCredits(), (iconSize * 3.5f) + padding * 2, (iconSize) - centring, paint);
        canvas.drawBitmap(lm.getBitmap('u'), (iconSize * 5.0f) + padding, topSpace, paint);
        canvas.drawText("" + ps.getFireRate(), (iconSize * 6.0f) + padding * 2, (iconSize) - centring, paint);
        canvas.drawText("Shields: " + ps.shieldStrength(), (iconSize * 8.0f) + padding * 2, (iconSize) - centring, paint);
    }

    private void drawDebugging() {
        paint.setTextSize(16);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.argb(255, 255, 255, 255));
        canvas.drawText("fps:" + fps, 10, 60, paint);
        canvas.drawText("num objects:" + lm.gameObjects.size(), 10, 80, paint);
        canvas.drawText("num clipped:" + vp.getNumClipped(), 10, 100, paint);
        canvas.drawText("playerX:" + lm.gameObjects.get(lm.playerIndex).getWorldLocation().X(), 10, 120, paint);
        canvas.drawText("playerY:" + lm.gameObjects.get(lm.playerIndex).getWorldLocation().Y(), 10, 140, paint);
        canvas.drawText("Gravity:" + lm.gravity, 10, 160, paint);
        canvas.drawText("X velocity:" + lm.gameObjects.get(lm.playerIndex).getxVelocity(), 10, 180, paint);
        canvas.drawText("Y velocity:" + lm.gameObjects.get(lm.playerIndex).getyVelocity(), 10, 200, paint);
        vp.resetNumClipped();
    }

    private void drawButtons() {
        paint.setColor(Color.argb(80, 255, 255, 255));
        ArrayList<Rect> buttonsToDraw = ic.getButtons();
        for (Rect rect : buttonsToDraw)
            canvas.drawRoundRect(new RectF(rect.left, rect.top, rect.right, rect.bottom), 15f, 15f, paint);
    }

    private void drawScreenOverlay() {
        if (!this.lm.isPlaying()) {
            String text = "Paused";
            if ( gameOver )
                text = "Game Over!";
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.argb(255, 255, 255, 255));
            paint.setTextSize(120);
            canvas.drawText(text, vp.getScreenWidth() / 2, vp.getScreenHeight() / 2, paint);
        }
    }

    /* ****************************** Handle Touch Behavior ****************************** */

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if ( gameOver )
            gameOver = false;
        if ( lm!=null )
            ic.handleInput(motionEvent, lm, sm, vp);
        return true;
    }

    /* ****************************** Thread-handling behavior ****************************** */
    @Override
    public void run() {
        while ( running ) {
            startFrameTime = System.currentTimeMillis();
            update();
            draw();
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if ( timeThisFrame>=1 )
                fps = 1000 / timeThisFrame;
        }
    }

    public void pause() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("error", "failed to pause thread");
        }
    }

    public void resume() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}