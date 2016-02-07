package com.grizzlypenguins.dungeondart.Activities;

import android.accounts.NetworkErrorException;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.grizzlypenguins.dungeondart.Entities.DungeonDartDBHandler;
import com.grizzlypenguins.dungeondart.LevelMap;
import com.grizzlypenguins.dungeondart.NetworkClasses.OnlineMap;
import com.grizzlypenguins.dungeondart.NetworkClasses.ServerCommands;
import com.grizzlypenguins.dungeondart.R;
import com.grizzlypenguins.dungeondart.ScrollViewPackage.DownloadMapListAdapter;
import com.grizzlypenguins.dungeondart.ScrollViewPackage.ListElementAdapter;
import com.grizzlypenguins.dungeondart.ScrollViewPackage.ListInput;

import java.util.ArrayList;

public class DownloadMapsFromServerActivity extends AppCompatActivity {

    //Selected Map

    int selectedMapID = 0;
    String selectedMapName = "";

    //UI ELEMENTS
    Button download;
    TextView mapName;

    //for the list
    ListFragment listFragment;
  //  ArrayList<ListInput> mapList = new ArrayList<ListInput>();

    //DATABASHEE
    DungeonDartDBHandler dungeonDartDBHandler;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_download_maps_from_server);

        initialize();
        set_listeners();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_download_maps_from_server, menu);
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

          OnlineMap onlineMap = new OnlineMap();
          onlineMap.command = ServerCommands.cGetAllMaps;
            onlineMap.execute();
                ArrayList<ListInput> list;
                while (!onlineMap.finish) ;
                list = onlineMap.mapList;
            if(list != null)
                listFragment.setListAdapter(new DownloadMapListAdapter(DownloadMapsFromServerActivity.this, list));
            else
            Log.v("DownloadMaps", " the list for the fragment is null");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initialize()
    {
        download = (Button) findViewById(R.id.download);
        mapName = (TextView) findViewById(R.id.mapName);
        listFragment = (ListFragment)getSupportFragmentManager().findFragmentById(R.id.mapListFragment);


    }

    public void selectMap(String mapName,int id)
    {
        selectedMapName = mapName;
        selectedMapID = id;
        this.mapName.setText(selectedMapName);
    }
    private void downloadSelectedMap()
    {
        OnlineMap onlineMap = new OnlineMap();
        onlineMap.command = ServerCommands.cGetMapWithID;
        onlineMap.mapID = this.selectedMapID;
        onlineMap.execute();
        LevelMap levelMap;
        while(!onlineMap.finish);

        levelMap = onlineMap.levelMap;

        if(levelMap == null) return;

        DungeonDartDBHandler dungeonDartDBHandler;
        SQLiteDatabase db;

        dungeonDartDBHandler = new DungeonDartDBHandler(this.getApplicationContext(), null, null, 1);
        db = dungeonDartDBHandler.getWritableDatabase();

        dungeonDartDBHandler.addMap(levelMap);

        dungeonDartDBHandler.close();
        db.close();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        StringBuilder sb = new StringBuilder();
        sb.append("Download finished");
        alertDialogBuilder.setTitle("You downloaded the map : " + levelMap.getMapName());
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

    private void set_listeners()
    {
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                downloadSelectedMap();

            }
        });
    }

}
