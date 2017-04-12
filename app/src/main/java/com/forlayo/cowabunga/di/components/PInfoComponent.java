package com.forlayo.cowabunga.di.components;

import com.forlayo.cowabunga.di.modules.PInfoModule;
import com.forlayo.cowabunga.di.scopes.PerPInfo;
import com.forlayo.cowabunga.viewmodels.PInfoViewModel;

import dagger.Subcomponent;

@PerPInfo
@Subcomponent(modules = PInfoModule.class)
public interface PInfoComponent {
  PInfoViewModel pInfoViewModel();
}
