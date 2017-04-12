package com.forlayo.cowabunga.models;

import android.content.Context;

import com.forlayo.cowabunga.db.utils.Database;

import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;

@Singleton
public class SettingsModel {
  private Context context;

  public SettingsModel(Context context) {
    this.context = context;
  }

  public Completable setPrefNotificationList(boolean b) {
    return Completable.fromAction(() -> Database.setPrefNotificationList(context, b));
  }

  public Completable setPrefEnergySaving(boolean b) {
    return Completable.fromAction(() -> Database.setPrefEnergySaving(context, b));
  }

  public Single<Boolean> getPrefNotificationList() {
    return Single.fromCallable(() -> Database.getPrefNotificationList(context));
  }

  public Single<Boolean> getPrefEnergySaving() {
    return Single.fromCallable(() -> Database.getPrefEnergySaving(context));
  }
}
