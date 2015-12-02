package com.grizzlypenguins.dungeondart;


import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Darko on 16.11.2015.
 */
public class LevelMap implements Serializable {


    public String mapName;
    public Tile tiles [][];  //tile [height][width]

   //public int tileNumber;
    boolean generatedMap = false;

    ArrayList<MyPoint> starts = new ArrayList<MyPoint>();    // start tiles
    ArrayList<MyPoint> finishs = new ArrayList<MyPoint>();   //finish tiles
    ArrayList<MyPoint> monsterStarts = new ArrayList<MyPoint>(); //monster start
    MyPoint start = new MyPoint(1,1);
    MyPoint end = new MyPoint(1,1);
    MyPoint monsterStart = new MyPoint(1,1);

    public  LevelMap (Tile Tiles [][],String mapName)
    {

        this.mapName = mapName;
        tiles = Tiles;

        //width = Width;
        //height = Height;
//        this.tileNumber = tileNumber;


    }

    public LevelMap (LevelMap lm)
    {

        this.mapName = lm.mapName;
        this.tiles = lm.tiles;
        //this.width = lm.width;
        //this.tileNumber = lm.tileNumber;
        //this.height = lm.height;
        //this.tileSize = lm.tileSize;

        this.mapName = lm.mapName;
    }

    public  void createMap() {
        for(int i=0;i<tiles.length;i++)
        {
            for(int y=0;y<tiles[0].length;y++)
            {

                if(tiles[i][y] == null)
                {
                    Log.v("LevelMap "," null tiles");
                    break;
                }
                if(tiles[i][y].define == 2)
                {
                    // Implement the drawing of start and finish tiles
                    starts.add(new MyPoint(i, y));

                }
                if(tiles[i][y].define == 3)
                {
                    finishs.add(new MyPoint(i, y));
                }
                if(tiles[i][y].define == 7)
                {
                    monsterStarts.add(new MyPoint(i,y));
                }

            }

        }

       this.end = choose_End();
       this.start = choose_Start();
        this.monsterStart = choose_monsterStart();
        if(!checkStartAndEnd())
        {
           // throw new Exception("Couldn't find suitable start or end");
        }
        this.generatedMap = true;

    }


    boolean checkStartAndEnd()
    {
        boolean good1= false ,good2 = false,good3 = false;
        for(int i=0;i<starts.size();i++)
        {
                Tile [][]temp = this.getShowingTiles(starts.get(i));
                if(temp!=null)
                {
                    good1 = true;
                    start = starts.get(i);
                    tiles[start.x][start.y].define=4;
                    //System.out.println("Tile x: "+ start.x + " Tile y:"+start.y +" keyword: banana");
                    break;
                }
        }
        for(int i=0;i<finishs.size();i++)
        {
            Tile [][]temp = this.getShowingTiles(finishs.get(i));
            if(temp!=null)
            {
                good2 = true;
                end = finishs.get(i);
                tiles[finishs.get(i).x][finishs.get(i).y].define = 5;
                break;
            }

        }
        for(int i=0;i<monsterStarts.size();i++)
        {
            Tile [][]temp = this.getShowingTiles(monsterStarts.get(i));
            if(temp!=null)
            {
                good3 = true;
                monsterStart = monsterStarts.get(i);
                tiles[monsterStarts.get(i).x][monsterStarts.get(i).y].define = 7;
                break;
            }

        }
        return good1 && good2 && good3;
    }

    MyPoint choose_Start()
    {
        Collections.shuffle(starts);
        return starts.get(0);
    }

    MyPoint choose_End()
    {
        Collections.shuffle(finishs);
       // for(int i=0;i<finishs.size();i++)tiles[finishs.get(i).x][finishs.get(i).y].define = 6;
        return finishs.get(0);
    }

    MyPoint choose_monsterStart()
    {
        Collections.shuffle(monsterStarts);
        return  monsterStarts.get(0);
    }


    public Tile[][] getShowingTiles(MyPoint location)
    {

        int x = location.x;
        int y = location.y;
        x-=(int)Math.floor(myFactory.TILENUMBER/2);
        y-=(int)Math.floor(myFactory.TILENUMBER/2);;
        Tile[][] temp = new Tile[myFactory.TILENUMBER][myFactory.TILENUMBER];

        for(int i=0;i<myFactory.TILENUMBER;i++)
        {
            for(int z=0;z<myFactory.TILENUMBER;z++)
            {

               if(x+i<tiles.length-5 && y+z<tiles.length-5&& x+i >5 && y+z > 5) {
                   tiles[x + i][y + z].monster = false;
                   temp[i][z] = this.tiles[x + i][y + z];

               }
                else{
                    System.out.println("The location of the nulls x: " + x + " y:" + y + "  i:" + i + "  z: " +z);
                   return null;
                }
            }
        }

        return temp;

    }



}
