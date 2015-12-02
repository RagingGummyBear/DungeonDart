package com.grizzlypenguins.dungeondart.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grizzlypenguins.dungeondart.Difficulty;
import com.grizzlypenguins.dungeondart.Level;
import com.grizzlypenguins.dungeondart.PlayerScoring;
import com.grizzlypenguins.dungeondart.R;
import com.grizzlypenguins.dungeondart.myFactory;

public class MainMenu extends Activity {

    RelativeLayout mainScreen;
    FrameLayout createGameScreen;
    RelativeLayout scoreScreen;


    Button newGame;
    Button exitGame;
    Button startGame;
    Button back;
    Button okScore;

    TextView timePlayed;
    TextView scoredPoints;

    RatingBar ratingBar;
    Level startNewLevel;
    Window window;

    MainMenuSettings mainMenuSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        initialize();
        set_listeners();
        window = this.getWindow();

    }

    private void initialize() {



        mainScreen = (RelativeLayout)findViewById(R.id.firstScreen);
        createGameScreen = (FrameLayout)findViewById(R.id.createGameScreen);
        scoreScreen = (RelativeLayout)findViewById(R.id.scoreScreenLayout);

        newGame = (Button)findViewById(R.id.newGameButton);
        exitGame = (Button)findViewById(R.id.startGameButton);
        startGame = (Button) findViewById(R.id.startGameButton);
        back = (Button) findViewById(R.id.backButton);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        okScore = (Button) findViewById(R.id.scoreOkButton);

        timePlayed = (TextView) findViewById(R.id.timeFinished);
        scoredPoints = (TextView) findViewById(R.id.scoreText);

        mainMenuSettings = (MainMenuSettings) getIntent().getSerializableExtra("mainMenuSettings");
        if(mainMenuSettings!=null)
        {
            toggle_layout();
        }
        else
        {
            mainMenuSettings = new MainMenuSettings();
            mainMenuSettings.mainmenu = true;
            toggle_layout();
        }
        PlayerScoring playerScoring = (PlayerScoring) getIntent().getSerializableExtra("scoring");
        if(playerScoring!=null)
        {
            timePlayed.setText(playerScoring.getTime());
            scoredPoints.setText(String.format(playerScoring.score+""));

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
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

    void toggle_layout()
    {




        if(mainMenuSettings.mainmenu)
        {

            mainScreen.setVisibility(View.VISIBLE);
            mainScreen.setClickable(true);
        }
        else
        {

            mainScreen.setClickable(false);
            mainScreen.setVisibility(View.INVISIBLE);
        }
        if(mainMenuSettings.newGameScree)
        {

            createGameScreen.setVisibility(View.VISIBLE);
            createGameScreen.setClickable(true);
        }
        else
        {
            createGameScreen.setClickable(false);
            createGameScreen.setVisibility(View.INVISIBLE);

        }
        if(mainMenuSettings.createMapScreen)
        {

        }
        else
        {

        }
        if(mainMenuSettings.scoreScreen)
        {

            scoreScreen.setVisibility(View.VISIBLE);
            scoreScreen.setClickable(true);
        }
        else
        {
            scoreScreen.setClickable(false);
            scoreScreen.setVisibility(View.INVISIBLE);

        }

    }

    void set_listeners()
    {
        newGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
            System.out.println("clicked newGame");

                mainMenuSettings.mainmenu = false;
                mainMenuSettings.createMapScreen = false;
                mainMenuSettings.newGameScree = true;
                mainMenuSettings.scoreScreen = false;
                toggle_layout();
            }
        });

        exitGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                System.out.println("clicked exitGame");


            }
        });
        startGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                System.out.println("clicked startGame");

                Intent myIntent = new Intent(MainMenu.this,
                        GamePlayActivity.class);
                Difficulty dif = new Difficulty((int) ratingBar.getRating());

                startNewLevel = new Level(dif, myFactory.getInstance().test_map_4(),window.getDecorView().getWidth(),window.getDecorView().getHeight());
                startNewLevel.start();
                initializeBitmaps();
                while(startNewLevel.running){
                }
                myIntent.putExtra("PackedLevel",startNewLevel.packedLevel);
                startActivity(myIntent);


            }
        });
        back.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        System.out.println("clicked backButton");
                        mainMenuSettings.mainmenu = true;
                        mainMenuSettings.createMapScreen = false;
                        mainMenuSettings.newGameScree = false;
                        mainMenuSettings.scoreScreen = false;
                        toggle_layout();

                    }
        });

        okScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mainMenuSettings.mainmenu = true;
                mainMenuSettings.createMapScreen = false;
                mainMenuSettings.newGameScree = false;
                mainMenuSettings.scoreScreen = false;
                toggle_layout();
            }
        });

    }        private void initializeBitmaps()
            {

                myFactory.getInstance().TileNotMovable = BitmapFactory.decodeResource(getResources(), R.drawable.notmovabletile);
                myFactory.getInstance().TileMovable = BitmapFactory.decodeResource(getResources(), R.drawable.movabletile);
                myFactory.getInstance().TorchLight = BitmapFactory.decodeResource(getResources(),R.drawable.beginingfog);
                myFactory.getInstance().TileStart = BitmapFactory.decodeResource(getResources(),R.drawable.choosenstart);
                myFactory.getInstance().TileFinish = BitmapFactory.decodeResource(getResources(),R.drawable.finishworking);
                myFactory.getInstance().TileNFinish = BitmapFactory.decodeResource(getResources(),R.drawable.finishnotworking);

                myFactory.getInstance().EvilMonster = BitmapFactory.decodeResource(getResources(),R.drawable.evilmonster);
                myFactory.getInstance().Character = BitmapFactory.decodeResource(getResources(),R.drawable.character);

                myFactory.getInstance().PowerUpR = BitmapFactory.decodeResource(getResources(),R.drawable.powerupr);
                myFactory.getInstance().PowerUpB = BitmapFactory.decodeResource(getResources(),R.drawable.powerupb);
                myFactory.getInstance().PowerUpG = BitmapFactory.decodeResource(getResources(),R.drawable.powerupg);
                myFactory.getInstance().PowerUpP = BitmapFactory.decodeResource(getResources(),R.drawable.powerupp);
                myFactory.getInstance().PowerUpY = BitmapFactory.decodeResource(getResources(),R.drawable.powerupy);

                myFactory.getInstance().TrapR = BitmapFactory.decodeResource(getResources(),R.drawable.trapr);
                myFactory.getInstance().TrapY = BitmapFactory.decodeResource(getResources(),R.drawable.trapy);
                myFactory.getInstance().TrapB = BitmapFactory.decodeResource(getResources(),R.drawable.trapb);

                myFactory.getInstance().resize();

            }

}
