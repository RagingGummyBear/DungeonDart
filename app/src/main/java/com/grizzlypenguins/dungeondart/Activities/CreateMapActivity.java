package com.grizzlypenguins.dungeondart.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.FragmentActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.grizzlypenguins.dungeondart.Activities.uiScalingClasses.ScaleCreateMapActivity;

import com.grizzlypenguins.dungeondart.CameraControl;
import com.grizzlypenguins.dungeondart.Entities.DungeonDartDBHandler;
import com.grizzlypenguins.dungeondart.LevelMap;
import com.grizzlypenguins.dungeondart.MyPoint;
import com.grizzlypenguins.dungeondart.NetworkClasses.OnlineMap;
import com.grizzlypenguins.dungeondart.NetworkClasses.ServerCommands;
import com.grizzlypenguins.dungeondart.R;

import com.grizzlypenguins.dungeondart.ScrollViewPackage.ListElementAdapter;
import com.grizzlypenguins.dungeondart.ScrollViewPackage.ListInput;
import com.grizzlypenguins.dungeondart.myFactory;


import java.util.ArrayList;

public class CreateMapActivity extends FragmentActivity {

    CameraControl cameraControl; //controls the movement in the map
    LevelMap levelMap; //The map that we are making

    Handler timerHandler = new Handler(); //waits for the activity to start in order to calculate the zoom correctly for the cameraControl
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
                render();
           if(shouldRun) timerHandler.postDelayed(this, 500);
        }
    };

    private int mapWidth=100;  //predefined size of the map, just for safety
    private int mapHeight=100;
    private String mapName;

    float cameraZoom;

    ScaleCreateMapActivity scaleCreateMapActivity;

    SurfaceView mapSurfaceView;
    ListView tileSelect;
    ListFragment listFragment;

    Button moveUp;
    Button moveLeft;
    Button moveRight;
    Button moveDown;
    Button selectTile;

    ArrayList<ListInput> listViewTiles = new ArrayList<ListInput>();
    String selectedTileType;

    Canvas canvas;

    private boolean once = true;
    private boolean shouldRun = true;
    private boolean listViewFF = true;

    private boolean hasStart = false;
    private boolean hasFinish = false;
    private boolean hasMonster = false;


    //DATABASHEE
    DungeonDartDBHandler dungeonDartDBHandler;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // turn off title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // set up full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_create_map);
        initialize();
        set_listeners();
        if(cameraControl ==null)
        Log.v("Creating map","The tiles are null");
        render(); //refreshes the drawing of the surfaceView
        timerHandler.postDelayed(timerRunnable, 500);
        scaleCreateMapActivity = (ScaleCreateMapActivity) getIntent().getSerializableExtra("ScaleCreateMapActivity");



        resizeLayouts();
    }

    void initialize()
    {

        mapSurfaceView = (SurfaceView) findViewById(R.id.mapSurfaceView);
        canvas =  mapSurfaceView.getHolder().lockCanvas();
        moveDown = (Button) findViewById(R.id.moveDown);
        moveLeft = (Button) findViewById(R.id.moveLleft);
        moveRight = (Button) findViewById(R.id.moveRight);
        moveUp = (Button) findViewById(R.id.moveUp);
        selectTile = (Button) findViewById(R.id.selectTile);

        listFragment = (ListFragment)getSupportFragmentManager().findFragmentById(R.id.listFragment);
        if(getSupportFragmentManager().findFragmentById(R.id.listFragment) == null) {
            Log.v("CreateMapActivity","In initialization the listFragment is null");
            initialize();
        }
        listFragment.getView().setVisibility(View.INVISIBLE);


        mapHeight = (int)getIntent().getSerializableExtra("mapHeight");
        mapWidth = (int)getIntent().getSerializableExtra("mapWidth");
        mapName = (String)getIntent().getSerializableExtra("mapName");

        levelMap = myFactory.getInstance().newBlankMovableMap(mapWidth,mapHeight,"CoolTestingMap");

        //double scaleX, double scaleY,double Ts,MyPoint poz,int playerMovement
        float screenSize = mapSurfaceView.getWidth();
        if(screenSize>mapSurfaceView.getHeight())
            screenSize = mapSurfaceView.getHeight();
         cameraZoom =  (float)screenSize / (float)(myFactory.TILESIZE * myFactory.TILENUMBER);
        cameraControl = new CameraControl(cameraZoom,cameraZoom, myFactory.TILESIZE,new MyPoint((mapHeight/2)+10,(mapWidth/2)+10),1);

        cameraControl.tiles = levelMap.getShowingTiles(cameraControl.player_position);
        Log.v("CreateMap","player_position:" + cameraControl.player_position.y  + "   " + cameraControl.player_position.x);


        if(cameraControl.tiles == null)
            Log.v("Creating map "," The tiles are null into the camera control : " + cameraControl.player_position.x + " y: "+cameraControl.player_position.y );

        selectedTileType = "notmovabletile";
        //mapSurfaceView.getHolder().unlockCanvasAndPost(canvas);
        tileSelect = (ListView) findViewById(R.id.tileListView);
        listViewTiles.add(new ListInput("movabletile", 0, 0));
        listViewTiles.add(new ListInput( "notmovabletile",0,0));
        listViewTiles.add(new ListInput( "finishtile",0,0));
        listViewTiles.add(new ListInput( "starttile",0,0));
        listViewTiles.add(new ListInput( "monsterstarttile",0,0));
        listViewTiles.add(new ListInput( "poweruptile",0,0));
        listViewTiles.add(new ListInput( "traptile",0,0));
        listViewTiles.add(new ListInput( "powerupandtraptile",0,0));
        tileSelect.setAdapter(new ListElementAdapter(CreateMapActivity.this, listViewTiles));
        listFragment.setListAdapter(new ListElementAdapter(CreateMapActivity.this, listViewTiles));

    }

    void set_listeners()
    {

        moveDown.setOnTouchListener(new Button.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        moveDown();
                        // PRESSED
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        //gamePanel.pressedButton("none");
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });


        moveUp.setOnTouchListener(new Button.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        moveUp();
                        // PRESSED
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        //gamePanel.pressedButton("none");
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });


        moveRight.setOnTouchListener(new Button.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        moveRight();
                        // PRESSED
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        //gamePanel.pressedButton("none");
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });


        moveLeft.setOnTouchListener(new Button.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        moveLeft();
                        // PRESSED
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        //gamePanel.pressedButton("none");
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        selectTile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        toggleTileFragment();
                        // PRESSED
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        //gamePanel.pressedButton("none");
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });


        mapSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //String temp = String.format("Touch coordinates : " +String.valueOf(event.getX()) + "x" + String.valueOf(event.getY()));

                    // Log.v("ontouch",temp);
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    String temp = String.format("Moving coordinates : x: " + (event.getX()) / cameraZoom + "  x:  " + (event.getY()));
                   // Log.v("ontouch", temp);
                    float x = event.getX() / cameraZoom;
                    float y = event.getY() / cameraZoom;
                    if (x > 0 && y > 0 && y < myFactory.TILESIZE*myFactory.TILENUMBER && x < myFactory.TILESIZE*myFactory.TILENUMBER) {
                        drawTileAtLocation(x, y);
                        render();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });
    }



    void resizeLayouts()
    {

        RelativeLayout.LayoutParams layout;
        /*
        move_left.setHeight((int) (getWindow().getDecorView().getHeight()*0.05));
        move_left.setWidth((int) (getWindow().getDecorView().getWidth()*0.05));
        */
        //layout = (RelativeLayout.LayoutParams) moveLeft.getLayoutParams();
        moveLeft.getLayoutParams().width = scaleCreateMapActivity.middleButtonWidth;   //(int) (getResources().getDisplayMetrics().density*(getWindow().getDecorView().getWidth()*0.05));
        moveLeft.getLayoutParams().height = scaleCreateMapActivity.middleButtonHeight; //(int)  (getResources().getDisplayMetrics().density*(getWindow().getDecorView().getHeight()*0.02));


        moveUp.getLayoutParams().width = scaleCreateMapActivity.notMiddleButtonWidth;
        moveUp.getLayoutParams().height = scaleCreateMapActivity.notMiddleButtonHeight;

        moveDown.getLayoutParams().width = scaleCreateMapActivity.notMiddleButtonWidth;
        moveDown.getLayoutParams().height = scaleCreateMapActivity.notMiddleButtonHeight;

        moveRight.getLayoutParams().width = scaleCreateMapActivity.middleButtonWidth;
        moveRight.getLayoutParams().height = scaleCreateMapActivity.middleButtonHeight;

        mapSurfaceView.getLayoutParams().width = scaleCreateMapActivity.GamePanel;
        mapSurfaceView.getLayoutParams().height = scaleCreateMapActivity.GamePanel;

        listFragment.getView().getLayoutParams().width = scaleCreateMapActivity.GamePanel;
        listFragment.getView().getLayoutParams().height = scaleCreateMapActivity.GamePanel;

        ((RelativeLayout.LayoutParams) moveLeft.getLayoutParams()).setMargins(scaleCreateMapActivity.leftButtonLeftMargin , scaleCreateMapActivity.leftButtonTopMargin,0,0);
        ((RelativeLayout.LayoutParams) moveRight.getLayoutParams()).setMargins(scaleCreateMapActivity.rightButtonLeftMargin,0,0,0);
        ((RelativeLayout.LayoutParams) moveDown.getLayoutParams()).setMargins(scaleCreateMapActivity.downButtonLeftMargin,0,0,0);
        ((RelativeLayout.LayoutParams) moveUp.getLayoutParams()).setMargins(0,0,0,0);


        if(android.os.Build.VERSION.SDK_INT < 16) {
            moveLeft.setBackgroundDrawable(new BitmapDrawable(getResources(), myFactory.getInstance().arrowL));
            moveUp.setBackgroundDrawable(new BitmapDrawable(getResources(), myFactory.getInstance().arrowR));
            moveDown.setBackgroundDrawable(new BitmapDrawable(getResources(), myFactory.getInstance().arrowD));
            moveRight.setBackgroundDrawable(new BitmapDrawable(getResources(), myFactory.getInstance().arrowU));
        }
        else {
            moveLeft.setBackground(new BitmapDrawable(getResources(),myFactory.getInstance().arrowL));
            moveUp.setBackground(new BitmapDrawable(getResources(),myFactory.getInstance().arrowU));
            moveDown.setBackground(new BitmapDrawable(getResources(),myFactory.getInstance().arrowD));
            moveRight.setBackground(new BitmapDrawable(getResources(),myFactory.getInstance().arrowR));

        }

    }


    public void showTileFragment()
    {
        listFragment.getView().setVisibility(View.VISIBLE);
        mapSurfaceView.setVisibility(View.INVISIBLE);
        //getFragmentManager().popBackStack();
    }
    public void hideTileFragment()
    {
        listFragment.getView().setVisibility(View.INVISIBLE);
        mapSurfaceView.setVisibility(View.VISIBLE);
        render();
    }

    public void toggleTileFragment() //Toggles surfaceView and the list to select tiles
    {
        if(listViewFF)
        {
            showTileFragment();
            listViewFF = false;
        }
        else
        {
            listViewFF = true;
            hideTileFragment();
        }
    }

    void drawTileAtLocation(float x,float y) //draw te specific choosed tile to the selected location
    {
        if(selectedTileType== null) {
            Log.v("CreatingMap","SelectedTile is null");
            return;
        }
        x=x/myFactory.TILESIZE;
        y=y/myFactory.TILESIZE;
        int xPressed = cameraControl.tiles[(int)x][(int)y].getX();
        int yPressed = cameraControl.tiles[(int)x][(int)y].getY();
        //Log.v("CreateMap ", "DrawingTiles the coordinates are x: " + x + " y: " + y);
        //cameraControl.tiles[(int)x][(int)y] = myFactory.;

        if(xPressed <= 10 || yPressed <= 10  || yPressed> (mapWidth+10 ) ||  xPressed> (mapHeight+10 )) return;

        levelMap.tiles[xPressed][yPressed] = myFactory.getInstance().getTileOfType(selectedTileType);
        levelMap.tiles[xPressed][yPressed].setX(xPressed);
        levelMap.tiles[xPressed][yPressed].setY(yPressed);

       // updateShowingTiles();
    }

    void updateShowingTiles()
    {
        cameraControl.tiles = levelMap.getShowingTiles(cameraControl.player_position);
    }

    void moveUp()
    {
        MyPoint temp = cameraControl.player_position;
        if(temp.y>10)
        {
            cameraControl.player_position.y--;
        }
        render();
    }
    void moveDown()
    {
        MyPoint temp = cameraControl.player_position;
        if(temp.y<mapWidth +10)
        {
            cameraControl.player_position.y++;
        }
        render();
    }
    void moveLeft()
    {
        MyPoint temp = cameraControl.player_position;
        if(temp.x>10)
        {
            cameraControl.player_position.x--;
        }
        render();
    }
    void moveRight()
    {
        MyPoint temp = cameraControl.player_position;
        if(temp.x<mapHeight+10)
        {
            cameraControl.player_position.x++;
        }
        render();
    }

    public void chooseTile(String s)
    {
        selectedTileType = s;
        //og.v("CreatingMap","You have selected tile type: "+s);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if(id == R.id.uploadMap)
        {
            if(!validateMap()) return true;
            OnlineMap onlineMap = new OnlineMap();
            onlineMap.levelMap = this.levelMap;
            onlineMap.levelMap.setMapName(mapName);
            onlineMap.command = ServerCommands.cAddNewMap;
            onlineMap.execute();
            while(!onlineMap.finish);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            StringBuilder sb = new StringBuilder();
            sb.append("Upload to server finished");
            alertDialogBuilder.setTitle("You uploaded the map");
            alertDialogBuilder.setMessage(sb.toString());
            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }


        if (id == R.id.saveMap) {
            if(validateMap()) {


                dungeonDartDBHandler = new DungeonDartDBHandler(this.getApplicationContext(), null, null, 1);
                db = dungeonDartDBHandler.getWritableDatabase();

                this.levelMap.setMapName(mapName);
                dungeonDartDBHandler.addMap(this.levelMap);

                dungeonDartDBHandler.close();
                db.close();

            }



            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void render()  //Draws on the surfaceView
    {
        //Log.v("CreateMap","Rendering");
        canvas =  mapSurfaceView.getHolder().lockCanvas();
        if (canvas != null)   {
            if(once){
                float screenSize = mapSurfaceView.getWidth();
                if (screenSize > mapSurfaceView.getHeight())
                    screenSize = mapSurfaceView.getHeight();
                cameraZoom = (float) screenSize / (float) (myFactory.TILESIZE * myFactory.TILENUMBER);
            }
            updateShowingTiles();
            //Log.v("CreateMap","Can enter the if");
            canvas.scale(cameraZoom, cameraZoom, 1, 1);
            canvas.save();
            try {
                cameraControl.render(canvas,null);
            } catch (Exception e) {

                e.printStackTrace();
            }
            //mapSurfaceView.draw(canvas);
            //canvas.restore();
            shouldRun = false;
          mapSurfaceView.getHolder().unlockCanvasAndPost(canvas);
        }
    }


    boolean validateMap()
    {
        for(int i=0 ;i < levelMap.tiles.length;i++)
        {
            for(int y= 0; y < levelMap.tiles[0].length; y++)
            {
                //defines the tile with : 0 wall,1 movable,2 start,3 finish, 4 choosenStart,5 working exit, 6 not working exit, 7 monsterDen
                if(levelMap.tiles[i][y].getDefine() == 3) hasFinish = true;
                if(levelMap.tiles[i][y].getDefine() == 7) hasMonster = true;
                if(levelMap.tiles[i][y].getDefine() == 2) hasStart = true;
            }
        }
        if(hasMonster && hasStart && hasFinish) {
            return true;

        }
        else
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            StringBuilder sb = new StringBuilder();
            if(!hasFinish)
                sb.append("Please add FinishTile\n");
            if(!hasStart)
                sb.append("Please add StartTile \n");
            if(!hasMonster)
                sb.append("Please add MonsterTIle\n");
            alertDialogBuilder.setTitle("Please add the required tiles");
            alertDialogBuilder.setMessage(sb.toString());
            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return false;
        }

    }
}
