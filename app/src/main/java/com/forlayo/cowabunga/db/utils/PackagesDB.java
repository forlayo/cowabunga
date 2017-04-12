package com.forlayo.cowabunga.db.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;
import com.forlayo.cowabunga.fragments.ban.PInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Miguel
 * Date: 25/06/13
 * Time: 23:09
 * To change this template use File | Settings | File Templates.
 */
public class PackagesDB {

    public static ArrayList<PInfo> getInstalledApps(Context ctx) {
        ArrayList<PInfo> res = new ArrayList<PInfo>();
        List<ApplicationInfo> packs = ctx.getPackageManager().getInstalledApplications(0);

        for(ApplicationInfo p:packs)
        {
            PInfo newInfo = new PInfo();
            newInfo.appname = p.loadLabel(ctx.getPackageManager()).toString();
            if(TextUtils.isEmpty(newInfo.appname))continue;
            newInfo.pname = p.packageName;
            newInfo.icon = p.loadIcon(ctx.getPackageManager());

            if((p.flags & ApplicationInfo.FLAG_SYSTEM) != 0
                    && !newInfo.pname.equals("com.android.phone")
                    && !newInfo.pname.equals("com.android.mms")
                    && !newInfo.pname.equals("com.google.android.gm")
                    && !newInfo.pname.startsWith("com.google.android.apps")) continue;
            res.add(newInfo);
        }
        return res;
    }
}
