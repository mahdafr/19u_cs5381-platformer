package edu.utep.cs5381.platformer.gameobject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import edu.utep.cs5381.platformer.visuals.*;

public abstract class GameObject {
    private boolean traversable = false;

    // Most objects only have 1 frame
    // And don't need to bother with these
    private Animation anim = null;
    private boolean animated;
    private int animFps = 1;

    private RectHitbox rectHitbox = new RectHitbox();

    private float xVelocity;
    private float yVelocity;
    final int LEFT = -1;
    final int RIGHT = 1;
    private int facing;
    private boolean moves =false;

    private VectorPoint worldLocation;
    private float width;
    private float height;

    private boolean active = true;
    private boolean visible = true;
    private int animFrameCount = 1;
    private char type;

    private String bitmapName;

    public abstract void update(long fps, float gravity);

    void setTraversable(){
        traversable = true;
    }

    public boolean isTraversable(){
        return traversable;
    }

    void setAnimFps(int animFps) {
        this.animFps = animFps;
    }

    void setAnimFrameCount(int animFrameCount) {
        this.animFrameCount = animFrameCount;
    }

    public boolean isAnimated() {
        return animated;
    }

    void setAnimated(Context context, int pixelsPerMetre, boolean animated){
        this.animated = animated;
        this.anim = new Animation(bitmapName,
                height,
                width,
                animFps,
                animFrameCount,
                pixelsPerMetre );
    }

    public Rect getRectToDraw(long deltaTime){
        return anim.getCurrentFrame(deltaTime, xVelocity, isMoves());
    }

    public void setRectHitbox() {
        rectHitbox.setTop(worldLocation.Y());
        rectHitbox.setLeft(worldLocation.X());
        rectHitbox.setBottom(worldLocation.Y() + height);
        rectHitbox.setRight(worldLocation.X() + width);
    }

    public RectHitbox getHitbox(){
        return rectHitbox;
    }

    public void setWorldLocationY(float y) {
        this.worldLocation.setY(y);
    }

    public void setWorldLocationX(float x) {
        this.worldLocation.setX(x);
    }

    void move(long fps){
        if(xVelocity != 0) {
            this.worldLocation.setX(worldLocation.X() +xVelocity / fps);
        }

        if(yVelocity != 0) {
            this.worldLocation.setY(worldLocation.Y() +yVelocity / fps);
        }
    }

    public int getFacing() {
        return facing;
    }

    void setFacing(int facing) {
        this.facing = facing;
    }

    public float getxVelocity() {
        return xVelocity;
    }

    public void setxVelocity(float xVelocity) {
        // Only allow for objects that can move
        if(moves) {
            this.xVelocity = xVelocity;
        }
    }

    public float getyVelocity() {
        return yVelocity;
    }

    void setyVelocity(float yVelocity) {
        // Only allow for objects that can move
        if(moves) {
            this.yVelocity = yVelocity;
        }
    }

    private boolean isMoves() {
        return moves;
    }

    void setMoves(boolean moves) {
        this.moves = moves;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public String getBitmapName() {
        return bitmapName;
    }

    public Bitmap prepareBitmap(Context context, String bitmapName, int pixelsPerMetre) {

        // Make a resource id from a String that is the same name as the .png
        int resID = context.getResources().getIdentifier(bitmapName,
                "drawable", context.getPackageName());

        // Create the bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                resID);

        // Scale the bitmapSheet based on the number of pixels per metre
        // Multiply by the number of frames contained in the image file
        // Default 1 frame
        bitmap = Bitmap.createScaledBitmap(bitmap,
                (int) (width * animFrameCount * pixelsPerMetre),
                (int) (height * pixelsPerMetre),
                false);

        return bitmap;
    }

    public VectorPoint getWorldLocation() {
        return worldLocation;
    }

    public void setWorldLocation(float x, float y, int z) {
        this.worldLocation = new VectorPoint();
        this.worldLocation.setX(x);
        this.worldLocation.setY(y);
        this.worldLocation.setZ(z);
    }

    public void setBitmapName(String bitmapName){
        this.bitmapName = bitmapName;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

}
