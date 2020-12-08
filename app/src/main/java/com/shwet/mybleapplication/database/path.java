package com.shwet.mybleapplication.database;


import android.os.Environment;

public class path {

    public static final String DATABASE_NAME = "bleapp.db";

    public static final String MAIN_FOLDER = Environment.getExternalStorageDirectory()+"/BLEApp";
    public static final String DATABASE_FOLDER = MAIN_FOLDER+"/Database";
}
