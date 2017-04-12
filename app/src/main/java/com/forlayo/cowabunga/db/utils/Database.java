package com.forlayo.cowabunga.db.utils;


import android.content.*;
import android.os.RemoteException;
import com.forlayo.cowabunga.R;
import com.forlayo.cowabunga.db.NotificationListProvider;
import com.forlayo.cowabunga.utils.preferences.AbstractPrefsPersistStrategy;

public class Database {

    public static void saveNotification(Context ctx,
                                        String appName,
                                        String tickerText,
                                        String packageName)
    {
        ContentValues values = new ContentValues();
        values.put(NotificationListProvider.DB_APP_NAME,appName);
        values.put(NotificationListProvider.DB_TICKER_TEXT,tickerText);
        values.put(NotificationListProvider.DB_PACKAGE_NAME,packageName);
        values.put(NotificationListProvider.DB_TIMESTAMP,System.currentTimeMillis());
        values.put(NotificationListProvider.DB_SHOWN,0);

        saveNotification(ctx,values);

    }

    public static void saveNotification(Context ctx, ContentValues values  )
    {
        ContentResolver cResolver = ctx.getContentResolver();
        if(cResolver==null)return;

        ContentProviderClient cpClient = cResolver.acquireContentProviderClient(NotificationListProvider.CONTENT_URI);
        if (cpClient == null) return;

        // First try to update current notification ( if there's one with the same text and package  )
        int affectedRows=0;

        try {
            affectedRows = cpClient.update(NotificationListProvider.CONTENT_URI,values,
                    NotificationListProvider.DB_PACKAGE_NAME+"= ? AND "+
                    NotificationListProvider.DB_TICKER_TEXT+"= ?",
                    new String[]{ values.getAsString(NotificationListProvider.DB_PACKAGE_NAME) , values.getAsString(NotificationListProvider.DB_TICKER_TEXT)  });
        } catch (RemoteException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // If no one updated, insert a new one.
        if(affectedRows<=0)
        {
            try {
                cpClient.insert(NotificationListProvider.CONTENT_URI, values);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void clearAllNotifications(Context ctx)
    {
        ContentResolver cResolver = ctx.getContentResolver();
        if(cResolver==null)return;

        ContentProviderClient cpClient = cResolver.acquireContentProviderClient(NotificationListProvider.CONTENT_URI);
        if (cpClient == null) return;

        try {
            cpClient.delete(NotificationListProvider.CONTENT_URI,null,null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void setPrefNotificationList(Context ctx, boolean activated)
    {
        setBooleanPref(ctx,ctx.getString(R.string.notification_list),activated);
    }
    public static Boolean getPrefNotificationList(Context ctx)
    {
        return getBooleanPref(ctx,ctx.getString(R.string.notification_list));
    }

    public static void setPrefEnergySaving(Context ctx, boolean activated)
    {
        setBooleanPref(ctx,ctx.getString(R.string.energy_saving),activated);
    }
    public static Boolean getPrefEnergySaving(Context ctx)
    {
        return getBooleanPref(ctx,ctx.getString(R.string.energy_saving));
    }

    public static void setPrefInstalled(Context ctx, long version)
    {
        setLongPref(ctx,ctx.getString(R.string.installed_version),version);
    }
    public static long getPrefInstalled(Context ctx)
    {
        return getLongPref(ctx,ctx.getString(R.string.installed_version));
    }


    public static Boolean getBooleanPref(Context ctx, String fieldName)
    {
        String PREFERENCES_FILE = ctx.getString(R.string.preferences_file);
        String PREFS_FIELD = fieldName;
        SharedPreferences prefs = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return prefs.getBoolean(PREFS_FIELD,false);
    }

    public static void setBooleanPref(Context ctx,String fieldName, boolean value)
    {
        String PREFERENCES_FILE = ctx.getString(R.string.preferences_file);
        String PREFS_FIELD = fieldName;
        SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE).edit();

        editor.putBoolean(PREFS_FIELD, value);
        AbstractPrefsPersistStrategy.persist(editor);
    }

    public static Long getLongPref(Context ctx, String fieldName)
    {
        String PREFERENCES_FILE = ctx.getString(R.string.preferences_file);
        String PREFS_FIELD = fieldName;
        SharedPreferences prefs = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return prefs.getLong(PREFS_FIELD,-1);
    }

    public static void setLongPref(Context ctx,String fieldName, long value)
    {
        String PREFERENCES_FILE = ctx.getString(R.string.preferences_file);
        String PREFS_FIELD = fieldName;
        SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE).edit();

        editor.putLong(PREFS_FIELD, value);
        AbstractPrefsPersistStrategy.persist(editor);
    }


}
