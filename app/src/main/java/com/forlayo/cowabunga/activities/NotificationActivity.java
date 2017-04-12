package com.forlayo.cowabunga.activities;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.forlayo.cowabunga.AdHelper;
import com.forlayo.cowabunga.NotificationService;
import com.forlayo.cowabunga.R;
import com.forlayo.cowabunga.databinding.ActivityNotificationBinding;
import com.forlayo.cowabunga.models.NotificationsModel;
import com.forlayo.cowabunga.models.SettingsModel;
import com.forlayo.cowabunga.viewmodels.ViewModel;

import javax.inject.Inject;

public class NotificationActivity extends BaseActivity {

  private static final String TAG = NotificationActivity.class.getSimpleName();

  TextView tickerTextView;
  TextView appNameTextView;
  ImageView icon;
  LinearLayout notificationContainer;

  String appNameSaved;
  Drawable appIconSaved;
  String tickerTextSaved;
  String packageSaved;


  boolean configChanges = false;

  @Inject AdHelper adHelper;
  @Inject SettingsModel settingsModel;
  @Inject NotificationsModel notificationsModel;

  private BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      intent.putExtra("fromReceiver", true);
      onNewIntent(intent);
    }
  };

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ActivityNotificationBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);

    getActivityComponent()
            .map(activityComponent -> {
              activityComponent.inject(this);
              return activityComponent;
            })
            .blockingGet();
    adHelper.showBanner(this, binding.adContainer);


    Window win = getWindow();
    win.addFlags( WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


    tickerTextView = binding.notificationText;
    appNameTextView = binding.notificationAppname;
    icon = binding.notificationIcon;
    notificationContainer = binding.notificationContainer;

        /*
            To reduce brightness
        */
    WindowManager.LayoutParams lp = getWindow().getAttributes();
    if(settingsModel.getPrefEnergySaving().blockingGet())
    {
      lp.screenBrightness = 0.005f;
    }
    else
    {
      lp.screenBrightness = 0.75f;
    }

    getWindow().setAttributes(lp);


    if(Build.VERSION.SDK_INT>14)
      binding.rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


    onNewIntent(getIntent());

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    adHelper.cleanup();
  }

  @Override
  protected ViewModel getViewModel() {
    return null;
  }

  @Override

  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    configChanges=true;
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Log.d(TAG, "onNewIntent");

    Bundle b = intent.getExtras();
    if(b!=null&&b.containsKey("packageName"))
    {
      showNotification(b.getString("tickerText"),b.getString("packageName"),!b.containsKey("fromReceiver"));
      //showNotification((Notification) b.getParcelable("notification"),!b.containsKey("fromReceiver"));
    }
  }

  private void showNotification(String tickerText, final String packageName, boolean newNotification)
  {
    if(packageName==null)return;

    String tempPackage;
    String tempTickerText;

    //tempPackage = notification.contentIntent.getTargetPackage();
    //tempTickerText = String.valueOf(notification.tickerText);
    tempPackage = packageName;
    tempTickerText = tickerText;

    Log.d(TAG, ""+tempPackage+" "+tempTickerText);

    // Google talk receive messages from android.gsf instead talk, so when you try to get label and icon fails
    if(tempPackage.equals("com.google.android.gsf")) tempPackage="com.google.android.talk";

    if(tempPackage.equals(packageSaved)&&tempTickerText.equals(tickerTextSaved))return;
    packageSaved=tempPackage;
    tickerTextSaved=tempTickerText;

    final PackageManager pm = getPackageManager();
    try{
      ApplicationInfo ai = pm.getApplicationInfo(packageSaved,0);
      appNameSaved = String.valueOf(pm.getApplicationLabel(ai));
      appNameTextView.setText(appNameSaved);
    }catch (PackageManager.NameNotFoundException e) {
      appNameTextView.setVisibility(View.GONE);
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }

    try {
      appIconSaved = pm.getApplicationIcon(packageSaved);
      icon.setImageDrawable(appIconSaved);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }

    tickerTextView.setText(tickerTextSaved);

    final String finalTempPackage = tempPackage;
    notificationContainer.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        try {

          Notification savedNotification = null;
          if(NotificationService.received_notifications!=null) savedNotification = NotificationService.received_notifications.get(finalTempPackage);

          if(savedNotification!=null)
          {
            PendingIntent pendingIntent = savedNotification.contentIntent;
            pendingIntent.send();
          }
          else
          {
            Intent appStartIntent = pm.getLaunchIntentForPackage(finalTempPackage);
            if (null != appStartIntent)
            {
              startActivity(appStartIntent);
            }
          }
          NotificationService.received_notifications=null;
          notificationsModel.clearAll().subscribe();
          finish();
        } catch (PendingIntent.CanceledException e) {
          e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
      }
    });

    if(!configChanges)
    {
      // Funny animation to get text coming to screen
      Animation a = AnimationUtils.loadAnimation(this,(newNotification)? R.anim.coming:R.anim.fadein);
      a.reset();
      notificationContainer.clearAnimation();
      notificationContainer.startAnimation(a);
    }
    if(settingsModel.getPrefNotificationList().blockingGet())
      notificationsModel.save(appNameSaved, tickerTextSaved, packageSaved).subscribe();

  }

  @Override
  public void onBackPressed() {
    notificationsModel.clearAll().subscribe();
    super.onBackPressed();
  }

  @Override
  protected void onResume() {
    super.onResume();
    // Receiver to manage new notifications with this activity open. ( refreshing notifications )
    IntentFilter filter = new IntentFilter();
    filter.addAction(NotificationService.ACTION_NOTIFY_ONSCREENOPEN);
    filter.setPriority(999);
    registerReceiver(receiver, filter);

  }

  @Override
  protected void onPause() {
    super.onPause();
    try{
      unregisterReceiver(receiver);
    } catch (Exception ignored){}
  }
}
