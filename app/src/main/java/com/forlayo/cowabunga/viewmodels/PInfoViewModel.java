package com.forlayo.cowabunga.viewmodels;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.graphics.drawable.Drawable;

import com.forlayo.cowabunga.fragments.ban.PInfo;
import com.forlayo.cowabunga.models.AppsModel;

import javax.inject.Inject;

public class PInfoViewModel extends ViewModel {
  private PInfo app;
  @Bindable public ObservableBoolean enabled = new ObservableBoolean();

  @Inject
  PInfoViewModel(PInfo app, AppsModel appsModel) {
    this.app = app;
    appsModel.isBanned(app).subscribe(banned -> enabled.set(!banned));

    enabled.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
      @Override
      public void onPropertyChanged(Observable observable, int i) {
        appsModel.setBanned(app, !enabled.get()).subscribe();
      }
    });
  }

  public String getName() {
    return app.appname;
  }

  public Drawable getIcon() {
    return app.icon;
  }
}
