package com.forlayo.cowabunga.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = "DataBaseHelper";

    private static final String DATABASE_NAME = "CowabungaDB";

    private static final int DATABASE_VERSION = 5;

    // Database creation sql statements
    public static final String NOTIFICATIONS_TABLE="notifications";
    private static final String CREATE_NOTIFICATIONS_TABLE="create table "+NOTIFICATIONS_TABLE+" ( " +
            BaseColumns._ID+" integer primary key autoincrement, " +
            "app_name text,"+
            "ticker_text text,"+
            "package_name text,"+
            "timestamp integer,"+
            "already_shown integer"+
            ");";

    public static final String PACKAGES_TABLE="app_packages";
    private static final String CREATE_PACKAGES_TABLE="create table "+PACKAGES_TABLE+" ( " +
            BaseColumns._ID+" integer primary key autoincrement, " +
            "package_name text,"+
            "app_name text,"+
            "icon blob,"+
            "banned integer"+
            ");";


    private static DatabaseHelper mInstance = null;
    public static DatabaseHelper getInstance(Context ctx) {
        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activities
         * context (see this article for more information:
         * http://developer.android.com/resources/articles/avoiding-memory-leaks.html)
         */
        if (mInstance == null) {
            mInstance = new DatabaseHelper (ctx.getApplicationContext());
        }
        return mInstance;
    }


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_NOTIFICATIONS_TABLE);
        database.execSQL(CREATE_PACKAGES_TABLE);
    }

    // Method is called during an upgrade of the database,
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        Log.w(LOG_TAG,
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS "+NOTIFICATIONS_TABLE);
        onCreate(database);
    }

}
