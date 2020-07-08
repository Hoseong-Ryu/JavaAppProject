package com.example.javaappproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

class DataBase {
    public static final class CreateDB implements BaseColumns {
        public static final String DATE = "date";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String TITLE = "title";
        public static final String CONTENT = "content";
        public static final String _TABLENAME0 = "picture";
        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +_ID+" integer primary key autoincrement, "
                +DATE+" text not null , "
                +LATITUDE+" real not null , "
                +LONGITUDE+" real not null , "
                +TITLE+" text not null , "
                +CONTENT+" text );";
    }


}

public class DbOpenHelper {

    private static final String DATABASE_NAME = "InnerDatabase(SQLite).db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DataBase.CreateDB._CREATE0);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+DataBase.CreateDB._TABLENAME0);
            onCreate(db);
        }
    }

    public DbOpenHelper(Context context){
        this.mCtx = context;
    }

    public DbOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void create(){
        mDBHelper.onCreate(mDB);
    }

    public void close(){
        mDB.close();
    }

    public long insertColumn(String date, double latitude, double longitude, String title, String content){
        ContentValues values = new ContentValues();
        values.put(DataBase.CreateDB.DATE, date);
        values.put(DataBase.CreateDB.LATITUDE, latitude);
        values.put(DataBase.CreateDB.LONGITUDE, longitude);
        values.put(DataBase.CreateDB.TITLE, title);
        values.put(DataBase.CreateDB.CONTENT, content);
        return mDB.insert(DataBase.CreateDB._TABLENAME0, null, values);
    }

    public Cursor selectColumns(){
        return mDB.query(DataBase.CreateDB._TABLENAME0, null, null, null, null, null, null);
    }

    public Cursor sortColumn(String sort){
        Cursor c = mDB.rawQuery( "SELECT * FROM + DataBase.CreateDB._TABLENAME0 + ORDER BY " + sort + ";", null);
        return c;
    }

    public boolean updateColumn(long id, String date, double latitude, double longitude, String title, String content){
        ContentValues values = new ContentValues();
        values.put(DataBase.CreateDB.DATE, date);
        values.put(DataBase.CreateDB.LATITUDE, latitude);
        values.put(DataBase.CreateDB.LONGITUDE, longitude);
        values.put(DataBase.CreateDB.TITLE, title);
        values.put(DataBase.CreateDB.CONTENT, content);
        return mDB.update(DataBase.CreateDB._TABLENAME0, values, "_id=" + id, null) > 0;
    }

    // Delete All
    public void deleteAllColumns() {
        mDB.delete(DataBase.CreateDB._TABLENAME0, null, null);
    }

    // Delete Column
    public boolean deleteColumn(long id){
        return mDB.delete(DataBase.CreateDB._TABLENAME0, "_id="+id, null) > 0;
    }


}
