package com.forlayo.cowabunga.di.components;

import com.forlayo.cowabunga.activities.FAQActivity;
import com.forlayo.cowabunga.activities.MainActivity;
import com.forlayo.cowabunga.activities.NotificationActivity;
import com.forlayo.cowabunga.di.modules.ActivityModule;
import com.forlayo.cowabunga.di.modules.PInfoModule;
import com.forlayo.cowabunga.di.scopes.PerActivity;
import com.forlayo.cowabunga.models.AccessibilityModel;
import com.forlayo.cowabunga.models.InAppModel;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {
  PInfoComponent pInfoComponent(PInfoModule module);
  AccessibilityModel accessibilityModel();
  InAppModel inAppModel();

  void inject(MainActivity activity);
  void inject(FAQActivity activity);
  void inject(NotificationActivity activity);
}
