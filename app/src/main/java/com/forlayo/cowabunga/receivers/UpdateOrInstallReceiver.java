package com.forlayo.cowabunga.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import com.forlayo.cowabunga.R;
import com.forlayo.cowabunga.db.utils.Database;
import com.forlayo.cowabunga.utils.preferences.AbstractPrefsPersistStrategy;
import com.forlayo.cowabunga.utils.preferences.ObjectSerializer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Miguel
 * Date: 18/06/13
 * Time: 23:04
 * To change this template use File | Settings | File Templates.
 */
public class UpdateOrInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED) && intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) return;
        if (!intent.getDataString().contains("com.forlayo.cowabunga")) return;

        Log.d("UpdatingAppReceiver", "La aplicación se acaba de actualizar, lanzando intentService data:" + intent.getDataString());

        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        long installedVersion = Database.getPrefInstalled(context);
        if(installedVersion==-1||packageInfo==null||packageInfo.versionCode != installedVersion)
        {
            ArrayList<String> packagesToDefaultBan = new ArrayList<String>();
            packagesToDefaultBan.add("com.android.phone");
            packagesToDefaultBan.add("com.android.bluetooth");
            saveValues(context, packagesToDefaultBan);

            if(packageInfo!=null)
                Database.setPrefInstalled(context,packageInfo.versionCode);
        }

    }

    private void saveValues(Context ctx, ArrayList<String> bannedPackages)
    {
        String PREFERENCES_FILE = ctx.getString(R.string.preferences_file);
        String BANNED_ARRAY = ctx.getString(R.string.banned_packages);

        SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE).edit();

        try {
            editor.putString(BANNED_ARRAY, ObjectSerializer.serialize(bannedPackages));
        } catch (IOException e) {
            e.printStackTrace();
        }

        AbstractPrefsPersistStrategy.persist(editor);
    }

}