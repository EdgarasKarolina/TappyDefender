package com.edgarasvilija.c1tappydefender;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;

public class GameActivity extends Activity
{
    private TDView gameView;
    //this is where the "Play" button from MainActivity sens us
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //here we will be trying to get the size of the actuall mobile phone that is using the game
        //get a display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        //Load the resoliution into a Point object
        Point size = new Point();
        display.getSize(size);

        gameView = new TDView(this, size.x, size.y);
        //make our gameView the view for the Activity
        setContentView(gameView);
    }

    //if the activity is paused make sure to pause our thread
    @Override
    protected void onPause()
    {
        super.onPause();
        gameView.pause();
    }

    //if the activity is resumed make sure to resume our thread
    @Override
    protected void onResume()
    {
        super.onResume();
        gameView.resume();
    }

    //if the player hits the back button, quit the app
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            finish();
            return true;
        }
        return false;
    }
}
