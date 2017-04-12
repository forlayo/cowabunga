package com.forlayo.cowabunga.activities;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.forlayo.cowabunga.AdHelper;
import com.forlayo.cowabunga.BR;
import com.forlayo.cowabunga.BuildConfig;
import com.forlayo.cowabunga.Navigator;
import com.forlayo.cowabunga.R;
import com.forlayo.cowabunga.databinding.ActivityMainBinding;
import com.forlayo.cowabunga.di.components.ActivityComponent;
import com.forlayo.cowabunga.models.AccessibilityModel;
import com.forlayo.cowabunga.viewmodels.MainViewModel;
import com.forlayo.cowabunga.viewmodels.PInfoViewModel;
import com.forlayo.cowabunga.viewmodels.ViewModel;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass;

public class MainActivity extends BaseActivity {
  private Single<Boolean> serviceEnabled;

  @Inject MainViewModel mainViewModel;
  @Inject Navigator navigator;
  @Inject AdHelper adHelper;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    binding.setItemBinding(new OnItemBindClass<ViewModel>()
            .map(MainViewModel.class, BR.vm, R.layout.ban_list_header)
            .map(PInfoViewModel.class, BR.app, R.layout.ban_list_row));

    setSupportActionBar(binding.toolbar);

    serviceEnabled = getActivityComponent()
            .map(ActivityComponent::accessibilityModel)
            .cache()
            .map(AccessibilityModel::isAccessibilityServiceEnabled)
            .observeOn(AndroidSchedulers.mainThread());

    serviceEnabled.filter(enabled -> !enabled)
            .subscribe(b -> navigator.openAccessibility());

    getActivityComponent()
            .map(activityComponent -> {
              activityComponent.inject(this);
              binding.setVm(mainViewModel);

              mainViewModel.removeAdsViewService = () -> binding.adContainer.post(binding.adContainer::removeAllViews);

              mainViewModel.initialize();
              return activityComponent;
            })
            .blockingGet();
    adHelper.showBanner(this, binding.adContainer);

    binding.executePendingBindings();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    adHelper.cleanup();
  }

  @Override
  protected ViewModel getViewModel() {
    return mainViewModel;
  }

  @Override
  protected void onResume() {
    super.onResume();

    supportInvalidateOptionsMenu();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);

    MenuItem alertItem= menu.findItem(R.id.menu_main_alert);

    MenuItem switchItem = menu.findItem(R.id.menu_main_status);
    SwitchCompat switchView = ((SwitchCompat)MenuItemCompat.getActionView(switchItem));
    switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
      switchView.setChecked(false);
      navigator.openAccessibility();
    });

    serviceEnabled.subscribe(isServiceEnabled -> {
      if (isServiceEnabled) {
        switchItem.setVisible(false);
        alertItem.setVisible(false);
      } else {
        switchItem.setChecked(false);
        if (!BuildConfig.DEBUG) {
          Drawable d = alertItem.getIcon();
          if (d instanceof Animatable) {
            ((Animatable) d).start();
          }
        }
      }
    });

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_main_alert:
        navigator.openAccessibility();
        break;
      case R.id.menu_main_share:
        navigator.shareApp();
        break;
      case R.id.menu_main_faq:
        navigator.showFAQ();
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}
