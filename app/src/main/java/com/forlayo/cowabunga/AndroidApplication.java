package com.forlayo.cowabunga;

import android.app.Application;

import com.forlayo.cowabunga.di.components.AppComponent;
import com.forlayo.cowabunga.di.components.DaggerAppComponent;
import com.forlayo.cowabunga.di.modules.AppModule;

import io.reactivex.Single;

public class AndroidApplication extends Application {
  private Single<AppComponent> appComponent;

  @Override public void onCreate() {
    super.onCreate();
    this.initializeInjector();
  }

  protected void initializeInjector() {
    appComponent = Single.fromCallable(() -> DaggerAppComponent.builder()
            .appModule(getApplicationModule())
            .build()
    ).cache();
  }

  protected AppModule getApplicationModule() {
    return new AppModule(this);
  }

  public Single<AppComponent> getAppComponent() {
    return appComponent;
  }

  public void setAppComponent(Single<AppComponent> appComponent) {
    this.appComponent = appComponent;
  }
}
