package com.grizzlypenguins.dungeondart.NetworkClasses;

import android.os.AsyncTask;
import android.util.Log;

import com.grizzlypenguins.dungeondart.Entities.DungeonDartDBHandler;
import com.grizzlypenguins.dungeondart.LevelMap;
import com.grizzlypenguins.dungeondart.ScrollViewPackage.ListInput;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Darko on 05.02.2016.
 *
 * This class grants us access to the online server
 */
public class OnlineMap extends AsyncTask {

    public String command = "";
    public int mapID = 0;

    public ArrayList<ListInput> mapList = null;
    public LevelMap levelMap = null;

    Socket s;
    BufferedReader br;
    BufferedWriter bw;
    public boolean finish = false;



    public OnlineMap()
    {
    }

    private ArrayList<ListInput> getAllOnlineMaps()
    {
        ArrayList<ListInput> list = new ArrayList<>();
        try {

            bw.write(ServerCommands.onlineMapGetAllMaps);
            bw.flush();
            String line = "";
            boolean startedEntering = false;
            boolean finishedSending = false;
            boolean flag = false;
            String mapName = "";
            int mapID = 0;

            while((line =br.readLine()) != null ){
                if(startedEntering)
                {

                    if(line.equals(ServerCommands.rOnlineMapEndSendingAllMaps))
                    {
                        finishedSending = true;
                        break;
                    }
                    else
                    {
                        if(!flag)
                        {

                            mapName = line;
                            flag = true;

                        }
                        else {

                            mapID = Integer.parseInt(line);
                            list.add(new ListInput(mapName,mapID,mapID));
                            flag = false;

                        }
                    }

                }
                else
                if(line.equals(ServerCommands.rOnlineMapStartSendingAllMaps))
                {
                    startedEntering = true;
                }
            }
            if(!finishedSending)
            {
                Log.v("OnlineMap"," Haven't received the maps");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return list;
    }

    private LevelMap getMapWithID()
    {
       LevelMap levelMap = null;
        String line = "";
        boolean flag = false;
        String mapName = "Test";
        try {

            bw.write(ServerCommands.onlineMapGetMapWIthID);
            bw.write(mapID + "\n");
            bw.flush();
            while(true)
            {
                line = br.readLine();
                if(line.equals(ServerCommands.rOnlineMapStartRecieve))
                {
                    line = null;
                   // while(line == null)

                    line = br.readLine();
                   if(flag == false) {
                      mapName = line;
                       flag = true;
                       Log.v("OnlineMap"," THE MAPNAME IN OnlineMap: "+mapName);
                   }


                    line = null;
                    while(line == null)
                        line = br.readLine();

                    String serializedMap = line;

                    try {

                        levelMap = (LevelMap) DungeonDartDBHandler.fromString(serializedMap);
                        levelMap.setMapName(mapName);

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    line = null;
                    while(line == null)
                        line = br.readLine();

                    if(line.equals(ServerCommands.rOnlineMapEndRecieve))
                    {

                        break;
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return levelMap;
    }


    private void addNewMap()
    {

        String serializedMap;
        try {
            serializedMap = DungeonDartDBHandler.toString(levelMap)+"\n";
        } catch (IOException e) {

            e.printStackTrace();
            return;
        }


        String mapName = levelMap.getMapName()+"\n";
        try {
            bw.write(ServerCommands.onlineMapNewMap);
            bw.flush();
            String line = "";

            line = br.readLine();
            if(line.equals(ServerCommands.rOnlineMapNewMapStart))
            {

                bw.write(ServerCommands.onlineMapNewMapName);
                bw.write(mapName);
                bw.flush();

                bw.write(serializedMap);
                bw.flush();

                bw.write(ServerCommands.onlineMapNewMapFinish);
                bw.flush();

                line = br.readLine();

                if(line.equals(ServerCommands.rOnlineMapNewMapEnd))
                {
                    System.out.println("Added new map to the server");
                }


            }
            else
            {

                System.out.println("Wrong Input");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {

            try {
                s = new Socket("testingout.ddns.net",25565);
                br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
                finish = true;
                return null;
            }

            if(command.equals(ServerCommands.cGetAllMaps))
            {
                System.out.println("Started getting MapList");
                ArrayList<ListInput> list = this.getAllOnlineMaps();

                System.out.println("Finished with maplist");
                mapList = list;
                //da se isprati na activity-to sto ke ja povika ovaa commanda (smisli nachin)
            }
            else
            if(command.equals(ServerCommands.cAddNewMap))
            {
                System.out.println("Started adding new map");
                addNewMap();
                System.out.println("Finished adding new map");
            }
            else
            if(command.equals(ServerCommands.cGetMapWithID))
            {
                System.out.println("Started getting map");
                LevelMap levelMap = getMapWithID();
                this.levelMap = levelMap;
                System.out.println("Finished getting map");
                //da se isprati kon potrebnoto activity
            }
            finish = true;
            bw.write(ServerCommands.onlineMapFinish);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
