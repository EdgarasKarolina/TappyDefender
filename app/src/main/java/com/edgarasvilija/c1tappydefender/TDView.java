package com.edgarasvilija.c1tappydefender;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Edgaras on 02/08/2016.
 */
public class TDView extends SurfaceView implements Runnable {

    volatile boolean playing;
    Thread gameThread = null;

    private PlayerShip player;
    public EnemyShip enemy1;
    public EnemyShip enemy2;
    public EnemyShip enemy3;

    //make some random space dust
    public ArrayList<SpaceDust> dustList = new ArrayList<SpaceDust>();

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;

    private int screenX;
    private int screenY;

    private Context context;

    private boolean gameEnded;

    //dealing with sounds
    private SoundPool soundPool;
    int start = -1;
    int bump = -1;
    int destroyed = -1;
    int win = -1;

    //writing data to the file
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;


    public TDView(Context context, int x, int y) {
        super(context);
        this.context = context;

        //get a reference to a file called HiScores
        //if id doesnt exist one is created
        prefs = context.getSharedPreferences("HiScores", context.MODE_PRIVATE);

        //initialize the editor ready
        editor = prefs.edit();

        //load fastest time from a entry in the file
        //labeled "fastest" time
        //if not available highscore = 1000000
        fastestTime = prefs.getLong("fastestTime", 1000000);

        //this sound pool is deprecated but dont work
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        try {
            //create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            //create our three fx in memory ready for use
            descriptor = assetManager.openFd("start.ogg");
            start = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("win.ogg");
            win = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("bump.ogg");
            bump = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("destroyed.ogg");
            destroyed = soundPool.load(descriptor, 0);
        }
        catch (IOException e)
        {
            Log.e("error", "failed to load sound files");
        }




        screenX = x;
        screenY = y;
        ourHolder = getHolder();
        paint = new Paint();

        //this code is not neccesary any more since we started using startGame() method
     //   player = new PlayerShip(context, x, y);
       // enemy1 = new EnemyShip(context, x, y);
       // enemy2 = new EnemyShip(context, x, y);
       // enemy3 = new EnemyShip(context, x, y);

     //   int numSpecs = 40;

     //   for (int i = 0; i < numSpecs; i++)
      //  {
      //      //where will the dust spawn?
      //      SpaceDust spec = new SpaceDust(x, y);
      //      dustList.add(spec);
      //  }
        startGame();
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    //surfaceView allows us to handle the onTouchEvent
    @Override
    public boolean onTouchEvent (MotionEvent motionEvent)
    {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK)
        {
            //has the player lifted their finger up?
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;

            //has the player touched the screen?
            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                //if we are currently on the pause screen, start a new game
                if (gameEnded)
                {
                    startGame();
                }
                break;
        }
            return true;
    }


    //we are calling playerShip class method "update"
    private void update() {

        //colision detection on new positions
        //before move because we are testing last frames
        // position which has just been drawn

        //if you are using images in excess of 100 pixels
        //wide then increase the -100 value accordingly

        boolean hitDetected = false;
        if (Rect.intersects(player.getHitBox(), enemy1.getHitBox()))
        {
            hitDetected = true;
            enemy1.setX(-400);
        }

        if (Rect.intersects(player.getHitBox(), enemy2.getHitBox()))
        {
            hitDetected = true;
            enemy2.setX(-400);
        }

        if (Rect.intersects(player.getHitBox(), enemy3.getHitBox()))
        {
            hitDetected = true;
            enemy3.setX(-400);
        }

        if (hitDetected)
        {
            soundPool.play(bump, 1,1,0,0,1);
            player.reduceShieldStrength();
            if (player.getShieldStrength() < 0)
            {
                soundPool.play(destroyed, 1, 1, 0, 0, 1);
                gameEnded = true;
            }
        }



        player.update();

        //update the enemies
        enemy1.update(player.getSpeed());
        enemy2.update(player.getSpeed());
        enemy3.update(player.getSpeed());

        for (SpaceDust sd: dustList)
        {
            sd.update(player.getSpeed());
        }

        if (!gameEnded)
        {
            //substract distance to home planet based on current speed
            distanceRemaining -= player.getSpeed();

            //how long has the player been flying
            timeTaken = System.currentTimeMillis() - timeStarted;
        }

        //completed the game!
        if (distanceRemaining < 0)
        {
            soundPool.play(win, 1, 1, 0, 0, 1);
            //check for new fastest time
            if (timeTaken < fastestTime)
            {
                //save high score
                editor.putLong("fastestTime", timeTaken);
                editor.commit();
                fastestTime = timeTaken;
            }
            //avoid ugly negative numbers
            //in the HUD
            distanceRemaining = 0;
            //now end the game
            gameEnded = true;
        }
    }

    private void draw() {

        if (ourHolder.getSurface().isValid()) {
            //first we lock the are of memory we will be drawing
            canvas = ourHolder.lockCanvas();


            //Rub out the last frame
            canvas.drawColor(Color.argb(255, 0, 0, 0));

            //white specs of dust
            paint.setColor(Color.argb(255, 255, 255, 255));

      /*      //draw the hit boxes
            canvas.drawRect(player.getHitBox().left,
                    player.getHitBox().top,
                    player.getHitBox().right,
                    player.getHitBox().bottom,
                    paint);

            canvas.drawRect(enemy1.getHitBox().left,
                    enemy1.getHitBox().top,
                    enemy1.getHitBox().right,
                    enemy1.getHitBox().bottom,
                    paint);

            canvas.drawRect(enemy2.getHitBox().left,
                    enemy2.getHitBox().top,
                    enemy2.getHitBox().right,
                    enemy2.getHitBox().bottom,
                    paint);

            canvas.drawRect(enemy3.getHitBox().left,
                    enemy3.getHitBox().top,
                    enemy3.getHitBox().right,
                    enemy3.getHitBox().bottom,
                    paint);  */

            //draw the dust from our array list
            for (SpaceDust sd: dustList)
            {
                canvas.drawPoint(sd.getX(), sd.getY(), paint);
            }
            //draw the player
            canvas.drawBitmap(
                    player.getBitmap(), player.getX(), player.getY(), paint
            );

            //drawing the enemies
            canvas.drawBitmap(
                    enemy1.getBitmap(), enemy1.getX(), enemy1.getY(), paint
            );

            canvas.drawBitmap(
                    enemy2.getBitmap(), enemy2.getX(), enemy2.getY(), paint
            );

            canvas.drawBitmap(
                    enemy3.getBitmap(),enemy3.getX(), enemy3.getY(), paint
            );


            //draw the HUD
            if (!gameEnded) {
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(25);
                canvas.drawText("Fastest: " + fastestTime + "s", 10, 20, paint);
                canvas.drawText("Time: " + timeTaken + "s", 10, 20, paint);
                canvas.drawText("Distance: " + distanceRemaining / 1000 + "KM", screenX / 3, screenY - 20, paint);
                canvas.drawText("Shield: " + player.getShieldStrength(), 10, screenY - 20, paint);
                canvas.drawText("Speed: " + player.getSpeed() * 60 + "MPS", (screenX / 3) * 2, screenY - 20, paint);
            }

            else {
                //show pause screen
                paint.setTextSize(80);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Game Over", screenX/2, 100, paint);

                paint.setTextSize(25);
                canvas.drawText("Fastest: " + fastestTime + "s", screenX/2, 160, paint);
                canvas.drawText("Time: " + timeTaken + "s", screenX/2, 200, paint);
                canvas.drawText("Distance Remaining: " + distanceRemaining/1000 + "KM", screenX/2, 240, paint);

                paint.setTextSize(80);
                canvas.drawText("Tap tp replay!", screenX/2, 350, paint);

            }


            //unlock and draw the scene
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {

        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {

        }
    }


    //clean up the thread if the game is interrupted or the player quits
    public void pause()
    {
        playing = false;
        try {
            gameThread.join();
        }
        catch (InterruptedException e)
        {

        }
    }

    //make a new Thread and start it
    public void resume()
    {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void startGame()
    {
        //initialize game objects
        player = new PlayerShip(context, screenX, screenY);
        enemy1 = new EnemyShip(context, screenX, screenY);
        enemy2 = new EnemyShip(context, screenX, screenY);
        enemy3 = new EnemyShip(context, screenX, screenY);

        int numSpecs = 40;
        for (int i = 0; i < numSpecs; i++)
        {
            //where will the dust spawn?
            SpaceDust spec = new SpaceDust(screenX, screenY);
            dustList.add(spec);
        }
        //reset time and distance
        distanceRemaining = 10000; //10km

        //get start time
        timeStarted = System.currentTimeMillis();

        gameEnded = false;

        soundPool.play(start, 1, 1, 0, 0, 1 );
    }
}



