package com.forlayo.cowabunga.models;

import android.content.ContentResolver;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.forlayo.cowabunga.BuildConfig;
import com.forlayo.cowabunga.NotificationService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AccessibilityModel {
  private static final String TAG = AccessibilityModel.class.getSimpleName();
  private ContentResolver contentResolver;

  public AccessibilityModel(ContentResolver contentResolver) {
    this.contentResolver = contentResolver;
  }

  public boolean isAccessibilityServiceEnabled(){
    int accessibilityEnabled = 0;
    final String ACCESSIBILITY_SERVICE = BuildConfig.APPLICATION_ID + "/" + NotificationService.class.getName();
    boolean accessibilityFound = false;
    try {
      accessibilityEnabled = Settings.Secure.getInt(contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED);
      Log.d(TAG, "ACCESSIBILITY: " + accessibilityEnabled);
    } catch (Settings.SettingNotFoundException e) {
      Log.d(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
    }

    TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

    if (accessibilityEnabled==1){
      Log.d(TAG, "***ACCESSIBILIY IS ENABLED***: ");

      String settingValue = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
      Log.d(TAG, "Setting: " + settingValue);
      if (settingValue != null) {
        TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
        splitter.setString(settingValue);
        while (splitter.hasNext()) {
          String accessabilityService = splitter.next();
          Log.d(TAG, "Setting: " + accessabilityService);
          if (accessabilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICE)){
            Log.d(TAG, "We've found the correct setting - accessibility is switched on!");
            return true;
          }
        }
      }

      Log.d(TAG, "***END***");
    }
    else{
      Log.d(TAG, "***ACCESSIBILIY IS DISABLED***");
    }
    return accessibilityFound;
  }
}
