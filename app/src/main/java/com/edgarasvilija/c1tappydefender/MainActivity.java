package com.edgarasvilija.c1tappydefender;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //here we set our UI layout as the view
        setContentView(R.layout.activity_main);

        //prepare to load fastest time
        SharedPreferences prefs;
        SharedPreferences.Editor editor;
        prefs = getSharedPreferences("HiScores", MODE_PRIVATE);

        //getting reference to the button in the layour/hml file
        final Button buttonPlay = (Button) findViewById(R.id.buttonPlay);

        //get a reference to the textView in our layout
        final TextView textFastestTime = (TextView) findViewById(R.id.textHighScore);

        //listener for clicks
        buttonPlay.setOnClickListener(this);

        //load fastest time
        //if not available our high score = 1000000
        long fastestTime = prefs.getLong("fastestTime", 1000000);

        //put the high score in our text view
        textFastestTime.setText("Fastest time: " + fastestTime);
    }

    //here we handle what happends when the button is cliked
    @Override
    public void onClick(View view) {
        //create new Intent class object
        Intent i = new Intent(this, GameActivity.class);
        //start gameActivity class via the Intent
        startActivity(i);
        finish();

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
