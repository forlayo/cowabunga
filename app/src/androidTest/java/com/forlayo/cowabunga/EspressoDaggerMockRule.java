package com.forlayo.cowabunga;

import android.support.test.InstrumentationRegistry;

import com.forlayo.cowabunga.di.components.AppComponent;
import com.forlayo.cowabunga.di.modules.AppModule;

import io.reactivex.Single;
import it.cosenonjaviste.daggermock.DaggerMockRule;

public class EspressoDaggerMockRule extends DaggerMockRule<AppComponent> {
  public EspressoDaggerMockRule() {
    super(AppComponent.class, new AppModule(getApp()));
    set(component -> getApp().setAppComponent(Single.just(component)));
  }

  private static AndroidApplication getApp() {
    return (AndroidApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
  }
}