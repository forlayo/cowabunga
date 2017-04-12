package com.forlayo.cowabunga.di.modules;

import android.support.v7.app.AppCompatActivity;

import com.forlayo.cowabunga.Navigator;
import com.forlayo.cowabunga.di.scopes.PerActivity;
import com.forlayo.cowabunga.models.InAppModel;

import dagger.Module;
import dagger.Provides;

@Module
@SuppressWarnings("WeakerAccess")
public class ActivityModule {
  private final AppCompatActivity activity;

  public ActivityModule(AppCompatActivity activity) {
    this.activity = activity;
  }

  @Provides
  @PerActivity
  protected AppCompatActivity provideActivity() {
    return this.activity;
  }

  @Provides
  @PerActivity
  protected Navigator provideNavigator() {
    return new Navigator(activity);
  }

  @Provides
  @PerActivity
  protected InAppModel provideInAppModel(AppCompatActivity activity) {
    return new InAppModel(activity);
  }
}
