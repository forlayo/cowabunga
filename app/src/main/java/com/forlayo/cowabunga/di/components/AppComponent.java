package com.forlayo.cowabunga.di.components;

import com.forlayo.cowabunga.di.modules.ActivityModule;
import com.forlayo.cowabunga.di.modules.AppModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
  ActivityComponent activityComponent(ActivityModule module);
}
