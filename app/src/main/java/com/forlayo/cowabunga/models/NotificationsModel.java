package com.forlayo.cowabunga.models;

import android.content.Context;

import com.forlayo.cowabunga.db.utils.Database;

import javax.inject.Singleton;

import io.reactivex.Completable;

@Singleton
public class NotificationsModel {
  private Context context;

  public NotificationsModel(Context context) {
    this.context = context;
  }

  public Completable clearAll() {
    return Completable.fromAction(() -> Database.clearAllNotifications(context));
  }

  public Completable save(String appName, String tickerText, String packageName) {
    return Completable.fromAction(() -> Database.saveNotification(context, appName, tickerText, packageName));
  }
}
