package com.forlayo.cowabunga.di.modules;

import android.content.ContentResolver;
import android.content.Context;

import com.forlayo.cowabunga.AdHelper;
import com.forlayo.cowabunga.AndroidApplication;
import com.forlayo.cowabunga.models.AccessibilityModel;
import com.forlayo.cowabunga.models.AppsModel;
import com.forlayo.cowabunga.models.NotificationsModel;
import com.forlayo.cowabunga.models.SettingsModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
@SuppressWarnings("WeakerAccess")
public class AppModule {
  private final AndroidApplication application;

  public AppModule(AndroidApplication application) {
    this.application = application;
  }

  @Provides
  @Singleton
  protected AndroidApplication provideAndroidApplication() {
    return application;
  }

  @Provides
  @Singleton
  protected Context provideApplicationContext() {
    return application.getApplicationContext();
  }

  @Provides
  @Singleton
  protected ContentResolver provideContentResolver(Context context) {
    return context.getContentResolver();
  }

  @Provides
  @Singleton
  protected AdHelper provideAdHelper(Context context) {
    return new AdHelper(context);
  }

  @Provides
  @Singleton
  protected AccessibilityModel provideAccessibility(ContentResolver contentResolver) {
    return new AccessibilityModel(contentResolver);
  }

  @Provides
  @Singleton
  protected AppsModel provideAppsModel(Context context) {
    return new AppsModel(context);
  }

  @Provides
  @Singleton
  protected SettingsModel provideSettingsModel(Context context) {
    return new SettingsModel(context);
  }

  @Provides
  @Singleton
  protected NotificationsModel provideNotificationsModel(Context context) {
    return new NotificationsModel(context);
  }
}
