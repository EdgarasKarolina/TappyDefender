package com.edgarasvilija.c1tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by Edgaras on 03/08/2016.
 */
public class EnemyShip {

    private Bitmap bitmap;
    private int x, y;
    private int speed = 1;

    //detect enemies leaving the screen
    private int maxX;
    private int minX;

    //spawn (daugintis) enemies within screen bounds
    private int maxY;
    private int minY;

    private Rect hitBox;

    //constructor
    public EnemyShip(Context context, int screenX, int screenY)
    {
        Random generator = new Random();
        //here we choose which picture will enemy ship have
        int whichBitmap = generator.nextInt(3);
        switch (whichBitmap)
        {
            case 0:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy1);
                break;
            case 1:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy2);
                break;
            case 2:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy3);
                break;

        }

       // bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);
        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;


        speed = generator.nextInt(6) + 10;

        x = screenX;
        y = generator.nextInt(maxY) - bitmap.getHeight();

        hitBox = new Rect(x,y,bitmap.getWidth(), bitmap.getHeight());
    }

    //getters and setters
    //so the draw() method will know what needs to be drawn and where

    public Bitmap getBitmap()
    {
        return bitmap;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public void update(int playerSpeed) {
        //move to the left
        x -= playerSpeed;
        x -= speed;

        //respown (pagimdyti) when off screen
        if (x < minX - bitmap.getWidth())
        {
            Random generator = new Random();
            speed = generator.nextInt(10) + 10;
            x = maxX;
            y = generator.nextInt(maxY) - bitmap.getHeight();
        }

        //refresh hit box location
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();

    }

    public Rect getHitBox()
    {
        return hitBox;
    }

    //this is used by the TDView update() method to
    //make an enemy out of bounds and force a re-spawn
    public void setX(int x)
    {
        this.x = x;
    }
}
