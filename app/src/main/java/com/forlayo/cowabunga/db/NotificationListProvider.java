package com.forlayo.cowabunga.db;


import android.content.*;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

public class NotificationListProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "com.forlayo.cowabunga.Notifications";
    public static final Uri CONTENT_URI = Uri.parse("content://"+ PROVIDER_NAME + "/notifications");

    private static final String TAG = "NotificationsProvider";

    public static final String DB_ID = BaseColumns._ID;
    public static final String DB_APP_NAME = "app_name";
    public static final String DB_TICKER_TEXT = "ticker_text";
    public static final String DB_PACKAGE_NAME = "package_name";
    public static final String DB_TIMESTAMP = "timestamp";
    public static final String DB_SHOWN = "already_shown";

    public static final String[] PROYECTION = new String[]{
            NotificationListProvider.DB_ID,
            NotificationListProvider.DB_APP_NAME,
            NotificationListProvider.DB_TICKER_TEXT,
            NotificationListProvider.DB_PACKAGE_NAME,
            NotificationListProvider.DB_TIMESTAMP,
            NotificationListProvider.DB_SHOWN
    };

    private static final int NOTIFICATIONS = 1;
    private static final int NOTIFICATIONS_ID = 2;


    private static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "notifications", NOTIFICATIONS);
        uriMatcher.addURI(PROVIDER_NAME, "notifications/#", NOTIFICATIONS_ID);
    }

    private SQLiteDatabase dataBase = null;
    private DatabaseHelper mDbHelper = null;

    @Override
    public boolean onCreate() {
        Log.d(TAG, "Provider create");
        Context context = getContext();
        mDbHelper = DatabaseHelper.getInstance(context);
        dataBase = mDbHelper.getWritableDatabase();
        return (dataBase == null&&dataBase.isOpen())? false:true;

    }

    private SQLiteDatabase getOrOpenDatabase(){
        SQLiteDatabase db =null;
        if(mDbHelper==null)mDbHelper = DatabaseHelper.getInstance(getContext());

        if(this.dataBase!=null&&dataBase.isOpen())
        {
            db =this.dataBase;
        }
        else
        {
            db = mDbHelper.getWritableDatabase();
        }
        return db;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Log.d(TAG, "Provider query");

        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        sqlBuilder.setTables(DatabaseHelper.NOTIFICATIONS_TABLE);

        if (uriMatcher.match(uri) == NOTIFICATIONS_ID)
            sqlBuilder.appendWhere(DB_ID + " = " + uri.getPathSegments().get(1));

        Cursor c = sqlBuilder.query(
                getOrOpenDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case NOTIFICATIONS:
                return "vnd.android.cursor.item/vnd.cowabunga.notifications";
            case NOTIFICATIONS_ID:
                return "vnd.android.cursor.dir/vnd.cowabunga.notifications";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Log.d(TAG, "Provider insert");

        long rowID = getOrOpenDatabase().insert(DatabaseHelper.NOTIFICATIONS_TABLE, "", values);

        if (rowID>0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count=0;
        switch (uriMatcher.match(uri)){
            case NOTIFICATIONS:
                count = getOrOpenDatabase().delete(
                        DatabaseHelper.NOTIFICATIONS_TABLE,
                        selection,
                        selectionArgs);
                break;
            case NOTIFICATIONS_ID:
                String id = uri.getPathSegments().get(1);
                count = getOrOpenDatabase().delete(
                        DatabaseHelper.NOTIFICATIONS_TABLE,
                        DB_ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default: throw new IllegalArgumentException(
                    "Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case NOTIFICATIONS:
                count = getOrOpenDatabase().update(DatabaseHelper.NOTIFICATIONS_TABLE, values,selection, selectionArgs);
                break;
            case NOTIFICATIONS_ID:
                count = getOrOpenDatabase().update(DatabaseHelper.NOTIFICATIONS_TABLE, values,
                        DB_ID + " = " + uri.getPathSegments().get(1) + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default: throw new IllegalArgumentException(
                    "Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}

