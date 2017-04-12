package com.forlayo.cowabunga;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.forlayo.cowabunga.activities.BaseActivity;
import com.forlayo.cowabunga.di.components.ActivityComponent;
import com.forlayo.cowabunga.models.InAppModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import io.reactivex.android.schedulers.AndroidSchedulers;

@Singleton
public class AdHelper extends AdListener {
  private static final String TAG = AdHelper.class.getSimpleName();

  private AdRequest admobRequest;

  private String fbBannerId;
  private String admobBannerId;

  private com.facebook.ads.AdView facebookAdView;

  public AdHelper(Context context) {
    Log.d(TAG, "preload");

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) { // Ads are only supported on 11+.
      return;
    }

    fbBannerId                 = context.getString(R.string.facebook_banner_placement_id);
    admobBannerId              = context.getString(R.string.admob_banner_adunit_id);
    String[] admobTestDevices  = context.getResources().getStringArray(R.array.admob_test_devices);

    // Facebook
    List<String> devices = Arrays.asList(context.getResources().getStringArray(R.array.facebook_test_devices));
    AdSettings.clearTestDevices();
    AdSettings.addTestDevices(devices);

    // Admob
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) { // AdMob is only supported on 14+.
      AdRequest.Builder admobBuilder = new AdRequest.Builder();
      admobBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
      for (String deviceId : admobTestDevices) {
        admobBuilder.addTestDevice(deviceId);
      }

      admobRequest = admobBuilder.build();
    }
  }

  /**********************
   *** Public methods ***
   **********************/

  public synchronized void cleanup() {
    if (null != facebookAdView) {
      facebookAdView.destroy();
      facebookAdView = null;
    }
  }

  private void fallbackAdMob(BaseActivity activity, ViewGroup viewGroup) {
    viewGroup.removeAllViews();
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) { // AdMob is only supported on 14+.
      Log.e(TAG, "AdMob can't show ads because of low SDK:" + Build.VERSION.SDK_INT);
      return;
    }
    Log.d(TAG, "Trying AdMob ...");
    com.google.android.gms.ads.AdView admobAdView = new com.google.android.gms.ads.AdView(activity);
    admobAdView.setAdUnitId(admobBannerId);
    admobAdView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
    admobAdView.loadAd(admobRequest);
    viewGroup.addView(admobAdView);
  }

  public void showBanner(BaseActivity activity, ViewGroup viewGroup) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) { // Ads are only supported on 11+.
      Log.e(TAG, "Can't show ads because of low SDK:" + Build.VERSION.SDK_INT);
      return;
    }

    activity.getActivityComponent()
            .map(ActivityComponent::inAppModel)
            .flatMap(InAppModel::hasPurchasedRemoveAds)
            .filter(purchased -> !purchased)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(x -> realShowBanner(activity, viewGroup));
  }

  private void realShowBanner(BaseActivity activity, ViewGroup viewGroup) {
    facebookAdView = new com.facebook.ads.AdView(activity, fbBannerId, AdSize.BANNER_HEIGHT_50);
    viewGroup.addView(facebookAdView);
    facebookAdView.setAdListener(new com.facebook.ads.AdListener() {
      @Override
      public void onError(Ad ad, AdError adError) {
        Log.e(TAG, "Facebook banner onError:" + adError.getErrorMessage());
        fallbackAdMob(activity, viewGroup);
      }

      @Override
      public void onAdLoaded(Ad ad) {
        Log.d(TAG, "Facebook onAdLoaded");
      }

      @Override
      public void onAdClicked(Ad ad) {
        Log.d(TAG, "Facebook onAdClicked");
      }
    });
    facebookAdView.loadAd();
  }

  /*****************
   *** Listeners ***
   *****************/

  /* Admob */
  @Override
  public void onAdClosed() {
    super.onAdClosed();
    Log.d(TAG, "Admob onAdClosed");
  }

  @Override
  public void onAdFailedToLoad(int errorCode) {
    super.onAdFailedToLoad(errorCode);
    Log.d(TAG, "Admob onAdFailedToLoad");
  }

  @Override
  public void onAdLeftApplication() {
    super.onAdLeftApplication();
    Log.d(TAG, "Admob onAdLeftApplication");
  }

  @Override
  public void onAdOpened() {
    super.onAdOpened();
    Log.d(TAG, "Admob onAdOpened");
  }

  @Override
  public void onAdLoaded() {
    super.onAdLoaded();
    Log.d(TAG, "Admob onAdLoaded");
  }
}
