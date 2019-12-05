package edu.utep.cs5381.platformer.gameobject;

import java.util.concurrent.CopyOnWriteArrayList;

import edu.utep.cs5381.platformer.Bullet;
import edu.utep.cs5381.platformer.gameobject.GameObject;

public class MachineGun extends GameObject {
    private int maxBullets = 10;
    private int numBullets;
    private int nextBullet;
    private int rateOfFire = 1;//bullets per second
    private long lastShotTime;
    private CopyOnWriteArrayList<Bullet> bullets;
    private int speed = 25;

    MachineGun(){
        bullets = new CopyOnWriteArrayList<Bullet>();
        lastShotTime = -1;
        nextBullet = -1;
    }

    public void update(long fps, float gravity){
        for ( Bullet bullet: bullets )
            bullet.update(fps);
    }
	
	public void setFireRate(int rate){
		rateOfFire = rate;
	}


    public int getNumBullets(){
        //tell the view how many bullets there are
        return numBullets;
    }

    public float getBulletX(int bulletIndex){
        if( bullets!=null && bulletIndex<numBullets )
            return bullets.get(bulletIndex).getX();
        return -1f;
    }

    public float getBulletY(int bulletIndex){
        if ( bullets!=null )
            return bullets.get(bulletIndex).getY();
        return -1f;
    }

    public void hideBullet(int index){
        bullets.get(index).hideBullet();
    }

    public int getDirection(int index){
        return bullets.get(index).getDirection();
    }

    boolean shoot(float ownerX, float ownerY, int ownerFacing, float ownerHeight){
        boolean shotFired = false;
        if ( System.currentTimeMillis()-lastShotTime>1000/rateOfFire ) {
            //spawn another bullet;
            nextBullet ++;
            //numBullets++;
            if ( numBullets >= maxBullets )
                numBullets = maxBullets;
            if ( nextBullet == maxBullets )
                nextBullet = 0;
            lastShotTime = System.currentTimeMillis();
            bullets.add(nextBullet, new Bullet(ownerX, (ownerY+ ownerHeight/3), speed, ownerFacing));
            shotFired = true;
            numBullets++;
        }
        return shotFired;
    }

    public void upgradeRateOfFire(){
        rateOfFire +=2;
    }
}

