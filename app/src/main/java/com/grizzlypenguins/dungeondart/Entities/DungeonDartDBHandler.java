package com.grizzlypenguins.dungeondart.Entities;

/**
 * Created by Nikola on 28-Nov-15.
 */

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Base64;

import com.grizzlypenguins.dungeondart.LevelMap;
import com.grizzlypenguins.dungeondart.ScrollViewPackage.ListInput;
import com.grizzlypenguins.dungeondart.Tile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class DungeonDartDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dungeondartDB.db"; // check

    private static final String TABLE_USERS = "users";
    private static final String TABLE_MAPS = "maps";
    private static final String TABLE_TILES = "tiles";
    // deleted stats
    //private static final String TABLE_STATS = "stats";
    private static final String TABLE_USERMAPSCORES = "scores";

    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_JOINDATE = "joindate";

    public static final String COLUMN_MAP_ID = "_id";
    public static final String COLUMN_MAPNAME = "mapname";
    public static final String COLUMN_MAP_LEVELMAP = "levelMap";
    //public static final String COLUMN_USERID_FK = "user_id";
    // added attribute
    public static final String COLUMN_MAPSTATS = "mapstats";

    public static final String COLUMN_TILE_ID = "_id";
    public static final String COLUMN_TILE_TYPE = "type";
    public static final String COLUMN_TILE_X = "x";
    public static final String COLUMN_TILE_Y = "y";
    public static final String COLUMN_MAPID_FK = "map_id";
    public static final String COLUMN_POWERUP = "powerUp";
    public static final String COLUMN_TRAP = "trap";

    //public static final String COLUMN_TIME_PLAYED = "time_played";
    //public static final String COLUMN_NOPOWERUPS = "number_powerups";
    //public static final String COLUMN_NOPLAYS = "number_plays";

    public static final String COLUMN_SCORES_ID = "_id";
    public static final String COLUMN_SCORES_SCORE = "score";
    public static final String COLUMN_SCORES_TIME = "time";
    // added attributes
    public static final String COLUMN_SCORES_NOPOWERUPS = "number_powerups";
    public static final String COLUMN_SCORES_NOPLAYS = "number_plays";
    public static final String COLUMN_SCORES_WIN = "win";
    // added foreign keys
    public static final String COLUMN_USERID_FK = "user_id";


    public DungeonDartDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public DungeonDartDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // the SQL strings we'll be using

        String CREATE_USERS_TABLE = "CREATE TABLE " +
                TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY," + COLUMN_USERNAME
                + " TEXT," + COLUMN_JOINDATE + " TEXT" + ")";

        /* old version of CREATE_MAPS_TABLE
        String CREATE_MAPS_TABLE = "CREATE TABLE " +
                TABLE_MAPS + "("
                + COLUMN_MAP_ID + " INTEGER PRIMARY KEY," + COLUMN_MAPNAME
                + " TEXT," + COLUMN_USERID_FK + " INTEGER,"
                + " FOREIGN KEY (" + COLUMN_USERID_FK + ") REFERENCES " + TABLE_USERS
                + "(" + COLUMN_USER_ID + "));";
        */

        // new version of CREATE_MAPS_TABLE
        String CREATE_MAPS_TABLE = "CREATE TABLE " +
                TABLE_MAPS + "("
                + COLUMN_MAP_ID + " integer primary key autoincrement," + COLUMN_MAPNAME
                + " TEXT , " +COLUMN_MAP_LEVELMAP + " varchar(123456789) "+")";


        String CREATE_TILES_TABLE = "CREATE TABLE " +
                TABLE_TILES + "("
                + COLUMN_TILE_ID + " integer primary key autoincrement," + COLUMN_POWERUP+ " integer, "
                +COLUMN_TRAP+" integer, "+ COLUMN_TILE_TYPE
                + " INTEGER," + COLUMN_TILE_X + " INTEGER," + COLUMN_TILE_Y + " INTEGER,"
                + COLUMN_MAPID_FK + " INTEGER,"
                + " FOREIGN KEY (" + COLUMN_MAPID_FK + ") REFERENCES " + TABLE_MAPS
                + "(" + COLUMN_MAP_ID + "));";

       /* String CREATE_STATS_TABLE = "CREATE TABLE " +
                TABLE_STATS + "("
                + COLUMN_USERID_FK + " INTEGER," + COLUMN_MAPID_FK + " INTEGER,"
                + COLUMN_TIME_PLAYED + " TEXT," + COLUMN_NOPOWERUPS + " INTEGER,"
                + COLUMN_NOPLAYS + " INTEGER," + COLUMN_POWERUP + " INTEGER,"
                + COLUMN_TRAP + " INTEGER,"
                + " PRIMARY KEY (" + COLUMN_USERID_FK + "," + COLUMN_MAPID_FK + "),"
                + " FOREIGN KEY (" + COLUMN_USERID_FK + ") REFERENCES " + TABLE_USERS
                + "(" + COLUMN_USER_ID + "),"
                + " FOREIGN KEY (" + COLUMN_MAPID_FK + ") REFERENCES " + TABLE_MAPS
                + "(" + COLUMN_MAP_ID + "));";
        */

        String CREATE_SCORES_TABLE = "CREATE TABLE " +
                TABLE_USERMAPSCORES + "("
                + COLUMN_SCORES_ID + " INTEGER," + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_MAP_ID + " INTEGER," + COLUMN_SCORES_SCORE + " INTEGER,"
                + COLUMN_SCORES_TIME + " TEXT," + COLUMN_SCORES_NOPOWERUPS + " INTEGER,"
                + COLUMN_SCORES_NOPLAYS + " INTEGER,"
                + COLUMN_SCORES_WIN + " INTEGER,"
                + " PRIMARY KEY (" + COLUMN_SCORES_ID + "), "
                + " FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS
                + "(" + COLUMN_USER_ID + "),"
                + " FOREIGN KEY (" + COLUMN_MAP_ID + ") REFERENCES " + TABLE_MAPS
                + "(" + COLUMN_MAP_ID + "));";

        // execute the SQL statements

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_MAPS_TABLE);
        db.execSQL(CREATE_TILES_TABLE);
        //db.execSQL(CREATE_STATS_TABLE);
//        db.execSQL(CREATE_SCORES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // DATABASE_VERSION = newVersion; may need to be reimplemented
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TILES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERMAPSCORES);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATS);

        onCreate(db);
    }

    // Insertion methods

    public void addUser(User user) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_JOINDATE, user.getJoindate());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_USERS, null, values);
        db.close();

    }


    public static Object fromString( String s ) throws IOException ,
            ClassNotFoundException {
        byte [] data = Base64.decode( s,0 );
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
    }

    /** Write the object to a Base64 string. */
    public static String toString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject(o);
        oos.flush();
        oos.close();
        String temp = Base64.encodeToString(baos.toByteArray(),0);
        baos.close();
        return temp;
    }

    public void addMap(LevelMap map) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_MAPNAME, map.getMapName());
        try {
            values.put(COLUMN_MAP_LEVELMAP,toString(map));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //values.put(COLUMN_USERID_FK, map.getUserId());
        //values.put(COLUMN_MAP_ID,get_LastMapID());
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_MAPS, null, values);


    }


    private void addTile(Tile tile,int mapID) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TILE_TYPE, tile.getDefine());
        values.put(COLUMN_TILE_X, tile.getX());
        values.put(COLUMN_TILE_Y, tile.getY());
        values.put(COLUMN_POWERUP, tile.getPowerUp());
        values.put(COLUMN_TRAP, tile.get_trap());

     //   values.put(COLUMN_TILE_ID,get_LastTileID());
        values.put(COLUMN_MAPID_FK, mapID);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_TILES, null, values);
        db.close();
    }


    /*
    public void addStat(Stat stat) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME_PLAYED, stat.getTimePlayed());
        values.put(COLUMN_NOPOWERUPS, stat.getNoPowerUps());
        values.put(COLUMN_NOPLAYS, stat.getNoPlays());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_STATS, null, values);
        db.close();
    }
    */

    public void addScore(UserMapScore score) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERID_FK, score.get_userid());
        values.put(COLUMN_MAPID_FK, score.get_mapid());
        values.put(COLUMN_SCORES_SCORE, score.getScore());
        values.put(COLUMN_SCORES_TIME, score.getTime());
        values.put(COLUMN_SCORES_NOPOWERUPS, score.getNoPowerUps());
        values.put(COLUMN_SCORES_NOPLAYS, score.getNoPlays());
        values.put(COLUMN_SCORES_WIN, score.getWin());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_USERMAPSCORES, null, values);
        db.close();
    }

    // Query methods

    public User findUser(String username) {
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = \"" + username + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        User user = new User();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            user.setId(Integer.parseInt(cursor.getString(0)));
            user.setUsername(cursor.getString(1));
            user.setJoindate(cursor.getString(2));
            cursor.close();
        }
        else {
            user = null;
        }

        db.close();
        return user;
    }

    private LevelMap findMap(String mapname) {
        String query = "SELECT * FROM " + TABLE_MAPS + " WHERE " + COLUMN_MAPNAME + " = \"" + mapname + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        LevelMap map = new LevelMap();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            map.setId(Integer.parseInt(cursor.getString(0)));
            map.setMapName(cursor.getString(1));
            //map.setUserId(Integer.parseInt(cursor.getString(2)));
            cursor.close();
        }
        else {
            map = null;
        }

        db.close();
        return map;
    }

    private Tile[] getTilesFromMap(int mapID)
    {
        String query  = "SELECT * FROM " + TABLE_TILES + "WHERE "+ COLUMN_MAPID_FK +" = \"" + mapID + "\"";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Tile tiles[] = new Tile[cursor.getCount()];
        if(!cursor.moveToFirst())return null;

        int define = cursor.getInt(cursor.getColumnIndex(COLUMN_TILE_TYPE));
        int pu = cursor.getInt(cursor.getColumnIndex(COLUMN_POWERUP));
        int t = cursor.getInt(cursor.getColumnIndex(COLUMN_TRAP));
        int x = cursor.getInt(cursor.getColumnIndex(COLUMN_TILE_X));
        int y = cursor.getInt(cursor.getColumnIndex(COLUMN_TILE_Y));
        int TileID = cursor.getInt(cursor.getColumnIndex(COLUMN_TILE_ID));

        tiles[0] = new Tile(define,pu,t);
        tiles[0].x = x;
        tiles[0].y = y;
        tiles[0].setMapID(mapID);
        tiles[0].setId(TileID);
        int counter = 1;
        while(cursor.moveToNext())
        {

            define = cursor.getInt(cursor.getColumnIndex(COLUMN_TILE_TYPE));
            pu = cursor.getInt(cursor.getColumnIndex(COLUMN_POWERUP));
            t = cursor.getInt(cursor.getColumnIndex(COLUMN_TRAP));
            x = cursor.getInt(cursor.getColumnIndex(COLUMN_TILE_X));
            y = cursor.getInt(cursor.getColumnIndex(COLUMN_TILE_Y));
            TileID = cursor.getInt(cursor.getColumnIndex(COLUMN_TILE_ID));

            tiles[counter] = new Tile(define,pu,t);
            tiles[counter].x = x;
            tiles[counter].y = y;
            tiles[counter].setMapID(mapID);
            tiles[counter].setId(TileID);

            counter++;
        }


        return tiles;
    }

    Tile [][] sortTiles(Tile []tiles)
    {

        int x = 0;
        int y = 0;
        for(int i = 0; i < tiles.length ; i++)
        {
            if(x< tiles[i].x) x = tiles[i].x;
            if(y< tiles[i].y) y = tiles[i].y;
        }

        Tile [][]SortedTiles= new Tile[x][y];
        for(int i = 0; i < tiles.length; i++)
        {
            SortedTiles[tiles[i].x][tiles[y].y] = tiles[i];
        }

        return SortedTiles;

    }

    public LevelMap findMapById(int id) {
        String query = "SELECT * FROM " + TABLE_MAPS + " WHERE " + COLUMN_MAP_ID + " = \"" + id + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);


        if(!cursor.moveToFirst())return null;
        LevelMap map = null;

        String sMap = cursor.getString(cursor.getColumnIndex(COLUMN_MAP_LEVELMAP));

        try {
            map =(LevelMap) fromString(sMap);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        cursor.close();
        db.close();
        return map;
    }

    public ArrayList<ListInput> getAllMaps() {

        ArrayList<ListInput> list = new ArrayList<ListInput>();

        String query = "SELECT * FROM " + TABLE_MAPS;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        System.out.println(" MY COUNT " + cursor.getCount());

        int id;
        String name;
        /*
        for(int i=0;i<cursor.getCount()-1;i++)
        {
            cursor.move(i+1);
            id = cursor.getInt(cursor.getColumnIndex(COLUMN_MAP_ID));
            name = cursor.getString(cursor.getColumnIndex(COLUMN_MAPNAME));
            list.add(new ListInput(name,id,id));
        }
*/
        if (cursor.moveToFirst()) {
         //   int id;
        //    String name;

            id = cursor.getInt(cursor.getColumnIndex(COLUMN_MAP_ID));
            name = cursor.getString(cursor.getColumnIndex(COLUMN_MAPNAME));
            list.add(new ListInput(name,id,id));
            while (cursor.moveToNext()) {
                id = cursor.getInt(cursor.getColumnIndex(COLUMN_MAP_ID));
                name = cursor.getString(cursor.getColumnIndex(COLUMN_MAPNAME));
                list.add(new ListInput(name,id,id));
                //list.add(name);
            }

        }

        return list;
    }

    public Tile findTile(int X, int Y) {
        String query = "SELECT * FROM " + TABLE_TILES + " WHERE " + COLUMN_TILE_X + " =\"" + X + "\""
                + " AND " + COLUMN_TILE_Y + " =\"" + Y + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Tile tile = new Tile();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            tile.setId(Integer.parseInt(cursor.getString(0)));
            tile.setDefine(Integer.parseInt(cursor.getString(1)));
            tile.setX(Integer.parseInt(cursor.getString(2)));
            tile.setY(Integer.parseInt(cursor.getString(3)));
            tile.setPowerup(Integer.parseInt(cursor.getString(4)));
            tile.setTrap(Integer.parseInt(cursor.getString(5)));
            cursor.close();
        }
        else {
            tile = null;
        }

        db.close();
        return tile;
    }

    /*public Stat findStat(int user_id, int map_id) {
        String query = "SELECT * FROM " + TABLE_STATS + " WHERE " + COLUMN_USERID_FK + " =\"" + user_id + "\""
                + " AND " + COLUMN_MAPID_FK + " =\"" + map_id + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Stat stat = new Stat();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            stat.setUserId(Integer.parseInt(cursor.getString(0)));
            stat.setMapId(Integer.parseInt(cursor.getString(1)));
            stat.setTimePlayed(cursor.getString(2));
            stat.setNoPowerUps(Integer.parseInt(cursor.getString(3)));
            stat.setNoPowerUps(Integer.parseInt(cursor.getString(4)));
            cursor.close();
        }
        else {
            stat = null;
        }

        db.close();
        return stat;
    }*/

    public UserMapScore findUserMapScore(int id) {
        String query = "SELECT * FROM " + TABLE_USERMAPSCORES + " WHERE " + COLUMN_SCORES_ID + " = \"" + id + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        UserMapScore usermapscore = new UserMapScore();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            usermapscore.setId(Integer.parseInt(cursor.getString(0)));
            usermapscore.set_userid(Integer.parseInt(cursor.getString(1)));
            usermapscore.set_mapid(Integer.parseInt(cursor.getString(2)));
            usermapscore.setScore(Integer.parseInt(cursor.getString(3)));
            usermapscore.setTime(cursor.getString(4));
            usermapscore.setNoPowerUps(Integer.parseInt(cursor.getString(5)));
            usermapscore.setNoPlays(Integer.parseInt(cursor.getString(6)));
            usermapscore.setWin(Integer.parseInt(cursor.getString(7)));
        }
        else {
            usermapscore = null;
        }

        db.close();
        return usermapscore;
    }

    // Deletion methods

    public boolean deleteUser(String username) {
        boolean result = false;

        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = \"" + username + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        User user = new User();

        if(cursor.moveToFirst()) {
            user.setId(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_USERS, COLUMN_USER_ID + " = ?",
                    new String[] { String.valueOf(user.getId())});
            cursor.close();
            result = true;
        }

        db.close();
        return result;
    }

    public boolean deleteMap(String mapname) {
        boolean result = false;

        String query = "SELECT * FROM " + TABLE_MAPS + " WHERE " + COLUMN_MAPNAME + " = \"" + mapname + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        LevelMap map = new LevelMap();

        if(cursor.moveToFirst()) {
            map.setId(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_MAPS, COLUMN_MAP_ID + " = ?",
                    new String[] { String.valueOf(map.getId())});
            cursor.close();
            result = true;
        }

        db.close();
        return result;
    }

    public boolean deleteTile(int X, int Y) {
        boolean result = false;

        String query = "SELECT * FROM " + TABLE_TILES + " WHERE " + COLUMN_TILE_X + " =\"" + X + "\""
                + " AND " + COLUMN_TILE_Y + " =\"" + Y + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Tile tile = new Tile();

        if(cursor.moveToFirst()) {
            tile.setId(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_TILES, COLUMN_TILE_ID + " = ?",
                    new String[] { String.valueOf(tile.getId())});
            cursor.close();
            result = true;
        }

        db.close();
        return result;
    }

    public boolean deleteUserMapScore(int id) {
        boolean result = false;

        String query = "SELECT * FROM " + TABLE_USERMAPSCORES + " WHERE " + COLUMN_SCORES_ID + " = \"" + id + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        UserMapScore usermapscore = new UserMapScore();

        if (cursor.moveToFirst()) {
            usermapscore.setId(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_USERMAPSCORES, COLUMN_SCORES_ID + " =?",
                    new String[] { String.valueOf(usermapscore.getId())});
            cursor.close();
            return true;
        }

        db.close();
        return result;

    }

    /*public boolean deleteStat(int user_id, int map_id) {
        boolean result = false;

        String query = "SELECT * FROM " + TABLE_STATS + " WHERE " + COLUMN_USERID_FK + " =\"" + user_id + "\""
                + " AND " + COLUMN_MAPID_FK + " =\"" + map_id + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Stat stat = new Stat();

        if(cursor.moveToFirst()) {
            stat.setUserId(Integer.parseInt(cursor.getString(0)));
            stat.setMapId(Integer.parseInt(cursor.getString(1)));
            db.delete(TABLE_STATS, COLUMN_USERID_FK + " = ? AND " + COLUMN_MAPID_FK + " = ?",
                    new String[] { String.valueOf(stat.getUserId()), String.valueOf(stat.getMapId())});
            cursor.close();
            result = true;
        }

        db.close();
        return result;
    }*/

    // Update methods

}