package com.edgarasvilija.c1tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Created by Edgaras on 02/08/2016.
 */

//class for our players space ship
public class PlayerShip {

    private Bitmap bitmap;
    private int x, y;
    private int speed = 0;

    private boolean boosting;

    private final int GRAVITY = -12;

    //stop ship leaving the screen
    private int maxY;
    private int minY;

    //limit the bounds of the ships speed
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 20;

    private Rect hitBox;

    private int shieldStrength;


    //creating a constructor for PlayerShip class
    public PlayerShip(Context context, int screenX, int screenY)
    {
        x = 50;
        y = 50;
        speed = 1;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.playership);
        boosting = false;
        maxY = screenY - bitmap.getHeight();
        minY = 0;
        hitBox = new Rect(x,y,bitmap.getWidth(), bitmap.getHeight());
        shieldStrength = 2;
    }

    //increases the speed of our space ship
    public void update()
    {
       //are we boosting?
        if (boosting)
        {
            //speed up
            speed +=2;
        }

        else {
            //slow down
            speed -= 5;
        }

        //constrains top speed
        if (speed > MAX_SPEED)
        {
            speed = MAX_SPEED;
        }

        //never stop complitely
        if (speed < MIN_SPEED)
        {
            speed = MIN_SPEED;
        }

        //move the ship up or down
        y -= speed + GRAVITY;

        //but dont let the ship stray off screen
        if (y < minY)
        {
            y = minY;
        }

        if (y > maxY)
        {
            y = maxY;
        }

        //refresh hit box location
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }

    //get picture
    public Bitmap getBitmap() {
        return bitmap;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }

    public void setBoosting()
    {
        boosting = true;
    }

    public void stopBoosting()
    {
        boosting = false;
    }

    public Rect getHitBox()
    {
        return hitBox;
    }

    public int getShieldStrength()
    {
        return shieldStrength;
    }

    public void reduceShieldStrength()
    {
        shieldStrength--;
    }
}
