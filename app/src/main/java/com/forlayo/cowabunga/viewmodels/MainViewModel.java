package com.forlayo.cowabunga.viewmodels;

import android.content.Intent;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.util.Log;

import com.forlayo.cowabunga.di.components.ActivityComponent;
import com.forlayo.cowabunga.di.components.PInfoComponent;
import com.forlayo.cowabunga.di.modules.PInfoModule;
import com.forlayo.cowabunga.models.AppsModel;
import com.forlayo.cowabunga.models.InAppModel;
import com.forlayo.cowabunga.models.SettingsModel;
import com.forlayo.cowabunga.viewservices.ViewService;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList;

public class MainViewModel extends ViewModel {
  private static final String TAG = MainViewModel.class.getSimpleName();

  private ActivityComponent activityComponent;
  private AppsModel appsModel;
  private InAppModel inAppModel;

  @Bindable public ObservableList<ViewModel> apps       = new ObservableArrayList<>();
  @Bindable public MergeObservableList<ViewModel> cells = new MergeObservableList<ViewModel>()
          .insertItem(this)
          .insertList(apps);

  private BooleanFieldObservable recentNotifications = new BooleanFieldObservable();
  private BooleanFieldObservable energySave          = new BooleanFieldObservable();
  @Bindable public ObservableBoolean showRemoveAds   = new ObservableBoolean();
  @Bindable public ObservableField<String> adFreeCost = new ObservableField<>();

  public ViewService removeAdsViewService;

  @Inject
  MainViewModel(AppsModel appsModel, SettingsModel settingsModel, InAppModel inAppModel, ActivityComponent activityComponent) {
    this.inAppModel        = inAppModel;
    this.appsModel         = appsModel;
    this.activityComponent = activityComponent;

    settingsModel.getPrefNotificationList().subscribe(recentNotifications::set);
    settingsModel.getPrefEnergySaving().subscribe(energySave::set);

    recentNotifications.toObservable()
            .flatMap(b -> settingsModel.setPrefNotificationList(b).toObservable())
            .subscribe();
    energySave.toObservable()
            .flatMap(b -> settingsModel.setPrefEnergySaving(b).toObservable())
            .subscribe();
  }

  public BooleanFieldObservable getRecentNotifications() {
    return recentNotifications;
  }

  public BooleanFieldObservable getEnergySave() {
    return energySave;
  }

  @Override
  public void initialize() {
    resetPurchase();

    appsModel.getInstalled()
            .subscribeOn(Schedulers.io())
            .map(PInfoModule::new)
            .map(activityComponent::pInfoComponent)
            .map(PInfoComponent::pInfoViewModel)
            .map(vm -> {
              vm.initialize();
              return vm;
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(apps::add);
  }

  private void resetPurchase() {
    inAppModel.hasPurchasedRemoveAds()
            .subscribe(purchased -> {
              showRemoveAds.set(!purchased);

              if (purchased) {
                removeAdsViewService.execute();
              } else {
                inAppModel.getRemoveAdsPrice()
                        .subscribeOn(Schedulers.computation())
                        .subscribe(adFreeCost::set, Throwable::printStackTrace);
              }
            }, e -> {
              Log.e(TAG, "Error getting purchase status.", e);
              showRemoveAds.set(false);
            });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    inAppModel.onActivityResult(requestCode, resultCode, data);
    resetPurchase();
  }

  @Override
  public void destroy() {
    super.destroy();
    inAppModel.clean().subscribe();
  }

  public void click() {
    inAppModel.purchaseRemoveAds()
            .toObservable()
            .subscribe(o -> {}, e -> {});
  }
}
