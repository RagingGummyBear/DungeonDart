package com.grizzlypenguins.dungeondart.Activities;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.grizzlypenguins.dungeondart.PackedLevel;
import com.grizzlypenguins.dungeondart.R;

public class GamePlayActivity extends Activity implements SensorEventListener {


    private SensorManager sensorManager;
    private long lastUpdate;

    Button move_up;
    Button move_down;
    Button move_left;
    Button move_right;

    PackedLevel level ;
    GamePanel gamePanel;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            if(gamePanel.gameFinished)
                change_Activity();
            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // turn off title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // set up full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_play);
        initialize();
        set_listeners();
        timerHandler.postDelayed(timerRunnable, 500);


        //System.out.println(level.cameraControl.player_position.x + " "+ level.difficulty.starNumber + " " +level.mainCharacter.speed + "  "+ level.levelMap.tileNumber );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    void initialize()
    {
        level = (PackedLevel) getIntent().getSerializableExtra("PackedLevel");
        gamePanel = (GamePanel) findViewById(R.id.GamePanel);

        move_up = (Button)findViewById(R.id.moveUp);
        move_down = (Button)findViewById(R.id.moveDown);
        move_left = (Button)findViewById(R.id.moveRight);
        move_right = (Button)findViewById(R.id.moveRight);

        gamePanel.level = level;
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();




    }
    void set_listeners()
    {

        move_up.setOnTouchListener(new Button.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        gamePanel.pressedButton("up");
                        // PRESSED
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        gamePanel.pressedButton("none");
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        move_down.setOnTouchListener(new Button.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        gamePanel.pressedButton("down");
                        // PRESSED
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        gamePanel.pressedButton("none");
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        move_left.setOnTouchListener(new Button.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        gamePanel.pressedButton("left");
                        // PRESSED
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        gamePanel.pressedButton("none");
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });
        move_right.setOnTouchListener(new Button.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        gamePanel.pressedButton("right");
                        // PRESSED
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        gamePanel.pressedButton("none");
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            getAccelerometer(event);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y+z*z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelationSquareRoot >= 3) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
            //Toast.makeText(this, "Device was shuffed", Toast.LENGTH_SHORT).show();
            gamePanel.shake_shake();
        }
    }

    /**
     * Mora da se popolnat za da imame oporavuvanje od pad
     * t.e. da se stavi vo bundle packedLevel
     */


    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        sensorManager.unregisterListener(this);
    }

    void change_Activity()
    {

        timerHandler.removeCallbacks(timerRunnable);
        Intent myIntent = new Intent(GamePlayActivity.this,
                MainMenu.class);
        myIntent.putExtra("scoring", this.gamePanel.level.playerScoring);
        MainMenuSettings mainMenuSettings = new MainMenuSettings();
        mainMenuSettings.scoreScreen = true;
        myIntent.putExtra("mainMenuSettings", mainMenuSettings);
        startActivity(myIntent);
    }



}

