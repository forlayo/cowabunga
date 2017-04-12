package com.forlayo.cowabunga.di.modules;

import com.forlayo.cowabunga.di.scopes.PerPInfo;
import com.forlayo.cowabunga.fragments.ban.PInfo;

import dagger.Module;
import dagger.Provides;

@Module
@PerPInfo
@SuppressWarnings("WeakerAccess")
public class PInfoModule {
  private final PInfo pInfo;

  public PInfoModule(PInfo pInfo) {
    this.pInfo = pInfo;
  }

  @Provides
  protected PInfo providePInfo() {
    return pInfo;
  }
}
