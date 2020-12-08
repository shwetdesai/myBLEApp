package com.shwet.mybleapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.File;

import static com.shwet.mybleapplication.database.path.DATABASE_FOLDER;
import static com.shwet.mybleapplication.database.path.DATABASE_NAME;

public class DatabaseHelper extends SQLiteOpenHelper {

    SQLiteDatabase db;

    /**Activities Table*/
    private static final String activitiesTable = "activitiesTable";
    private static final String activitiesTable_Col_2 = "deviceId";
    private static final String activitiesTable_Col_3 = "activityName";
    private static final String activitiesTable_Col_4 = "dateAndTime";
    /**End */

    /**Session Table*/
    private static final String sessionTable = "sessionTable";
    private static final String sessionTable_Col_2 = "deviceId";
    private static final String sessionTable_Col_3 = "sessionId";
    private static final String sessionTable_Col_4 = "duration";
    private static final String sessionTable_Col_5 = "dateAndTime";
    /**End */

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        db = getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        /** Activity Log Table */
        db.execSQL("CREATE TABLE "+ activitiesTable + "(ID INTEGER PRIMARY KEY AUTOINCREMENT," + activitiesTable_Col_2 + " STRING,"+ activitiesTable_Col_3 + " STRING,"  + activitiesTable_Col_4 + " STRING)");
        /** End */

        /** Session Log Table */
        db.execSQL("CREATE TABLE "+ sessionTable + "(ID INTEGER PRIMARY KEY AUTOINCREMENT," + sessionTable_Col_2 + " STRING,"+ sessionTable_Col_3 + " STRING,"+ sessionTable_Col_4 + " STRING,"  + sessionTable_Col_5 + " STRING)");
        /** End */

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + activitiesTable);
    }


    public boolean addActivities (String deviceId, String activityName){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();


            contentValues.put(activitiesTable_Col_2, deviceId);
            contentValues.put(activitiesTable_Col_3, activityName);
            contentValues.put(activitiesTable_Col_4, ""+System.currentTimeMillis());

            long result = db.insert(activitiesTable, null, contentValues);

            if (result == -1) {
                return false;
            } else {
                return true;
            }
    }


    public boolean addSession (String sessionId,String deviceId, String duration){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put(sessionTable_Col_2, deviceId);
        contentValues.put(sessionTable_Col_3, sessionId);
        contentValues.put(sessionTable_Col_4, duration);
        contentValues.put(sessionTable_Col_5, ""+System.currentTimeMillis());

        long result = db.insert(sessionTable, null, contentValues);

        db.close();
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    public int sessionCheck(String deviceId){
        SQLiteDatabase db1 = this.getReadableDatabase();

        String lastDeviceId,lastDeviceTime;

        String lastSessionId = "SELECT " + activitiesTable_Col_2 + ","+ activitiesTable_Col_4 +" FROM "+ activitiesTable + " ORDER BY ID DESC LIMIT 1 ;";
        Cursor cursorBackLift = db1.rawQuery(lastSessionId, null);

        lastDeviceId = (cursorBackLift.moveToFirst() ? cursorBackLift.getString(0) : ""+0);
        lastDeviceTime = (cursorBackLift.moveToFirst() ? cursorBackLift.getString(1) : ""+0);
        cursorBackLift.close();

        db1.close();
        if(lastDeviceId.compareTo(deviceId) == 0)
            if(System.currentTimeMillis() - Long.parseLong(lastDeviceTime) > 60000)
                return 0;
            else
                return 1;
        else
            return 0;


    }

    public int getLastSessionId (String deviceId){
        SQLiteDatabase db1 = this.getReadableDatabase();

        int sessionId;
        long lastDeviceTime;

        String lastSessionId = "SELECT ID, "+ sessionTable_Col_5 + " FROM "+ sessionTable + " WHERE " + sessionTable_Col_2 + " = "+ deviceId +" ORDER BY ID DESC LIMIT 1 ;";
        Cursor cursorBackLift = db1.rawQuery(lastSessionId, null);

        sessionId = (cursorBackLift.moveToFirst() ? Integer.parseInt(cursorBackLift.getString(0)) : 0);
        lastDeviceTime = (cursorBackLift.moveToFirst() ? Long.parseLong(cursorBackLift.getString(1)) : 0);

        cursorBackLift.close();

        int duration = 30 * 60 * 1000;
        if(System.currentTimeMillis() - lastDeviceTime > duration){
            sessionId++;
        }


        db1.close();

        return sessionId;
    }

}
