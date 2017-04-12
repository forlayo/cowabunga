package com.forlayo.cowabunga.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.forlayo.cowabunga.AndroidApplication;
import com.forlayo.cowabunga.di.components.ActivityComponent;
import com.forlayo.cowabunga.di.modules.ActivityModule;
import com.forlayo.cowabunga.viewmodels.ViewModel;
import com.google.firebase.analytics.FirebaseAnalytics;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseActivity extends AppCompatActivity {
  protected Single<ActivityComponent> activityComponent;
  private FirebaseAnalytics mFirebaseAnalytics;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    activityComponent = Single.just(getApplicationContext())
            .subscribeOn(Schedulers.computation())
            .cast(AndroidApplication.class)
            .flatMap(AndroidApplication::getAppComponent)
            .map(app -> app.activityComponent(new ActivityModule(this)))
            .cache();
  }

  public Single<ActivityComponent> getActivityComponent() {
    return activityComponent;
  }

  protected abstract ViewModel getViewModel();

  @Override
  protected void onPause() {
    super.onPause();
    ViewModel vm = getViewModel();
    if (null != vm) {
      vm.pause();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    ViewModel vm = getViewModel();
    if (null != vm) {
      vm.resume();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    ViewModel vm = getViewModel();
    if (null != vm) {
      vm.destroy();
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    ViewModel vm = getViewModel();
    if (null != vm) {
      vm.saveState(outState);
    }
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    ViewModel vm = getViewModel();
    if (null != vm) {
      vm.restoreState(savedInstanceState);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    ViewModel vm = getViewModel();
    if (null != vm) {
      vm.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        onBackPressed();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
