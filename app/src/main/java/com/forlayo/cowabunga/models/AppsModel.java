package com.forlayo.cowabunga.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.forlayo.cowabunga.R;
import com.forlayo.cowabunga.db.utils.PackagesDB;
import com.forlayo.cowabunga.fragments.ban.PInfo;
import com.forlayo.cowabunga.fragments.ban.PInfoComparator;
import com.forlayo.cowabunga.utils.preferences.AbstractPrefsPersistStrategy;
import com.forlayo.cowabunga.utils.preferences.ObjectSerializer;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Singleton
public class AppsModel {
  private static final String TAG = AppsModel.class.getSimpleName();

  private Context context;
  private ArrayList<String> mBannedPackages;

  public AppsModel(Context context) {
    this.context = context;
    loadValues();
  }

  public Observable<PInfo> getInstalled() {
    return Observable.fromCallable(() -> PackagesDB.getInstalledApps(context) )
            .flatMapIterable(pkgs -> pkgs)
            .sorted(new PInfoComparator());
  }

  public Single<Boolean> isBanned(PInfo app) {
    return Single.fromCallable(() -> mBannedPackages.contains(app.pname));
  }

  public Completable setBanned(PInfo app, boolean banned) {
    return Completable.fromAction(() -> {
      if (banned) {
        mBannedPackages.add(app.pname);
      } else {
        mBannedPackages.remove(app.pname);
      }
      saveValues();
    });
  }

  private void loadValues() {
    String PREFERENCES_FILE = context.getString(R.string.preferences_file);
    String BANNED_ARRAY = context.getString(R.string.banned_packages);

    SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

    try {
      mBannedPackages = (ArrayList<String>) ObjectSerializer.deserialize(
              prefs.getString(BANNED_ARRAY, ObjectSerializer.serialize(new ArrayList<String>()))
      );
    } catch (IOException e) {
      e.printStackTrace();
    }

    Log.d(TAG,"Banned packages-> "+mBannedPackages.size());
    final int max = mBannedPackages.size();
    for (int i=0; i<max; i++) {
      Log.d(TAG, mBannedPackages.get(i));
    }

  }

  private void saveValues()
  {
    String PREFERENCES_FILE = context.getString(R.string.preferences_file);
    String BANNED_ARRAY = context.getString(R.string.banned_packages);

    SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE).edit();

    try {
      editor.putString(BANNED_ARRAY, ObjectSerializer.serialize(mBannedPackages));
    } catch (IOException e) {
      e.printStackTrace();
    }

    AbstractPrefsPersistStrategy.persist(editor);
  }
}
