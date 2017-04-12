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

/**
 * Created with IntelliJ IDEA.
 * User: Miguel
 * Date: 25/06/13
 * Time: 23:12
 * To change this template use File | Settings | File Templates.
 */
public class PackagesProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "com.forlayo.cowabunga.Packages";
    public static final Uri CONTENT_URI = Uri.parse("content://"+ PROVIDER_NAME + "/packages");

    private static final String TAG = "PackagesProvider";

    public static final String DB_ID = BaseColumns._ID;
    public static final String DB_APP_NAME = "app_name";
    public static final String DB_PACKAGE_NAME = "package_name";
    public static final String DB_ICON ="icon";
    public static final String DB_BANNED = "banned";

    public static final String[] PROYECTION = new String[]{
            PackagesProvider.DB_ID,
            PackagesProvider.DB_APP_NAME,
            PackagesProvider.DB_PACKAGE_NAME,
            PackagesProvider.DB_ICON,
            PackagesProvider.DB_BANNED
    };

    private static final int PACKAGES = 1;
    private static final int PACKAGES_ID = 2;


    private static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "packages", PACKAGES);
        uriMatcher.addURI(PROVIDER_NAME, "packages/#", PACKAGES_ID);
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
        sqlBuilder.setTables(DatabaseHelper.PACKAGES_TABLE);

        if (uriMatcher.match(uri) == PACKAGES_ID)
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
            case PACKAGES:
                return "vnd.android.cursor.item/vnd.cowabunga.packages";
            case PACKAGES_ID:
                return "vnd.android.cursor.dir/vnd.cowabunga.packages";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Log.d(TAG, "Provider insert");

        long rowID = getOrOpenDatabase().insert(DatabaseHelper.PACKAGES_TABLE, "", values);

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
            case PACKAGES:
                count = getOrOpenDatabase().delete(
                        DatabaseHelper.PACKAGES_TABLE,
                        selection,
                        selectionArgs);
                break;
            case PACKAGES_ID:
                String id = uri.getPathSegments().get(1);
                count = getOrOpenDatabase().delete(
                        DatabaseHelper.PACKAGES_TABLE,
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
            case PACKAGES:
                count = getOrOpenDatabase().update(DatabaseHelper.PACKAGES_TABLE, values,selection, selectionArgs);
                break;
            case PACKAGES_ID:
                count = getOrOpenDatabase().update(DatabaseHelper.PACKAGES_TABLE, values,
                        DB_ID + " = " + uri.getPathSegments().get(1) + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default: throw new IllegalArgumentException(
                    "Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}

