package com.forlayo.cowabunga.models;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.forlayo.cowabunga.BuildConfig;
import com.forlayo.cowabunga.di.scopes.PerActivity;
import com.forlayo.cowabunga.iabutil.IabHelper;
import com.forlayo.cowabunga.iabutil.SkuDetails;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

@PerActivity
public class InAppModel {
  private static final String TAG     = InAppModel.class.getSimpleName();

  private static final int RC_REQUEST        = 1001;
  private static final String SKU_REMOVE_ADS = "sku_remove_ads";

  private AppCompatActivity activity;
  private Single<IabHelper> iabHelper;

  private String getKey() {
    return REMOVED!!
  }

  public InAppModel(AppCompatActivity activity) {
    this.activity = activity;

    iabHelper = Single.<IabHelper>create(e -> {
      IabHelper h = new IabHelper(activity, getKey());
      h.enableDebugLogging(BuildConfig.DEBUG);
      h.startSetup(r -> {
        if (r.isFailure()) {
          e.onError(new RuntimeException());
          Log.d(TAG, "Problem setting up In-app Billing: " + r);
          return;
        }

        e.onSuccess(h);
      });
    }).subscribeOn(Schedulers.computation())
            .cache();
  }

  public Single<Boolean> hasPurchasedRemoveAds() {
    return iabHelper.map(IabHelper::queryInventory)
            .map(inv -> null != inv.getPurchase(SKU_REMOVE_ADS));
  }

  public Single<String> getRemoveAdsPrice() {
    return iabHelper.map(h -> h.getDetails(SKU_REMOVE_ADS))
            .map(SkuDetails::getPrice);
  }

  public Completable purchaseRemoveAds() {
    return iabHelper.flatMapCompletable(h -> Completable.create(e ->
      h.launchPurchaseFlow(activity, SKU_REMOVE_ADS, RC_REQUEST, (result, info) -> {
        if (result.isFailure()) {
          Log.e(TAG, "Error purchasing: " + result);
          e.onError(new RuntimeException());
          return;
        }
        Log.d(TAG, "Purchase successful.");
        e.onComplete();
      })
    ));
  }

  public Completable clean() {
    return Completable.fromAction(() -> {
      if (null != iabHelper) {
        iabHelper.subscribe(IabHelper::dispose);
        iabHelper = null;
      }
    });
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    iabHelper.subscribe(h -> h.handleActivityResult(requestCode, resultCode, data));
  }
}
