package com.shwet.mybleapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;

import static com.shwet.mybleapplication.database.path.DATABASE_FOLDER;
import static com.shwet.mybleapplication.database.path.DATABASE_NAME;

public class DatabaseHelper extends SQLiteOpenHelper {

    SQLiteDatabase db;
    static final String TAG = "DatabaseHelper";

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

    private static int sessionId;

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
        Cursor cursorSessionCheck = db1.rawQuery(lastSessionId, null);

        lastDeviceId = (cursorSessionCheck.moveToFirst() ? cursorSessionCheck.getString(0) : ""+0);
        lastDeviceTime = (cursorSessionCheck.moveToFirst() ? cursorSessionCheck.getString(1) : ""+0);
        cursorSessionCheck.close();

        db1.close();
        if(lastDeviceId.compareTo(deviceId) == 0)
            if(System.currentTimeMillis() - Long.parseLong(lastDeviceTime) > 60000)
                return 0;
            else
                return 1;
        else
            return 0;


    }

    public String[] getLastSessionId (String deviceId,int status){
        SQLiteDatabase db1 = this.getReadableDatabase();

        String[] data = new String[2];

        String activeDuration;
        long lastDeviceTime;

        String lastSessionId = "SELECT " + sessionTable_Col_3 + ", "+ sessionTable_Col_4+ ", "+ sessionTable_Col_5 + " FROM "+ sessionTable + " WHERE " + sessionTable_Col_2 + " = "+ "'" + deviceId+"'" +" ORDER BY ID DESC LIMIT 1 ;";
        Cursor cursorLastSessionId = db1.rawQuery(lastSessionId, null);

        sessionId = (cursorLastSessionId.moveToFirst() ? Integer.parseInt(cursorLastSessionId.getString(0)) : 0);
        activeDuration = ""+(cursorLastSessionId.moveToFirst() ? Integer.parseInt(cursorLastSessionId.getString(1)) : 0);
        lastDeviceTime = (cursorLastSessionId.moveToFirst() ? Long.parseLong(cursorLastSessionId.getString(2)) : 0);

        Log.i(TAG,"Session "+sessionId);
        cursorLastSessionId.close();

        if(status == 1){
            int duration = 30 * 60 * 1000;
            if (System.currentTimeMillis() - lastDeviceTime > duration) {
                sessionId++;
            }
        }else{
            sessionId++;
        }


        db1.close();

        data[0] = ""+sessionId;
        data[1] = ""+activeDuration;

        return data;
    }

}
