package com.forlayo.cowabunga;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;

import com.forlayo.cowabunga.activities.NotificationActivity;
import com.forlayo.cowabunga.utils.preferences.ObjectSerializer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Miguel
 * Date: 23/04/13
 * Time: 22:32
 * To change this template use File | Settings | File Templates.
 */
public class NotificationService extends AccessibilityService implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOGTAG = "NotificationService";
    public static HashMap<String,Notification> received_notifications;
    ArrayList<String> mBannedPackages;

    public static final String ACTION_NOTIFY_ONSCREENOPEN = "com.forlayo.NotificationsService.OnScreenOpen";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        if(accessibilityEvent.getEventType()==AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED)
        {
            Log.d(LOGTAG, " type " + accessibilityEvent.getEventType()
                    + " package " + accessibilityEvent.getPackageName()
                    + " text " + accessibilityEvent.getText()
                    + " classname " + accessibilityEvent.getClassName());

            Notification notification = (Notification)accessibilityEvent.getParcelableData();

            if(notification!=null&&notification.tickerText!=null&&notification.contentIntent!=null)
            {
                if(mBannedPackages.contains(notification.contentIntent.getTargetPackage())) return;
                if(notification.contentIntent.getTargetPackage().equals("com.google.android.gsf")&&mBannedPackages.contains("com.google.android.talk")) return;

                if(received_notifications==null)received_notifications = new HashMap<String, Notification>();
                received_notifications.put(notification.contentIntent.getTargetPackage(), notification);

                //prueba(notification);

                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

                if(!pm.isScreenOn())
                {
                    Log.d(LOGTAG, "launching notification, screen is off ");
                    Intent i = new Intent(this,NotificationActivity.class);
                   // i.putExtra("notification",accessibilityEvent.getParcelableData());
                    i.putExtra("packageName",notification.contentIntent.getTargetPackage());
                    i.putExtra("tickerText",String.valueOf(notification.tickerText));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                else
                {
                    Log.d(LOGTAG, "launching broadcast, screen is on ");
                    Intent iBroad = new Intent();
                    iBroad.setAction(ACTION_NOTIFY_ONSCREENOPEN);
                    iBroad.putExtra("packageName",notification.contentIntent.getTargetPackage());
                    iBroad.putExtra("tickerText",String.valueOf(notification.tickerText));
                   // iBroad.putExtra("notification",accessibilityEvent.getParcelableData());
                    sendBroadcast(iBroad);
                }
            }
        }

    }


    private void prueba(Notification notification)
    {
        RemoteViews views = notification.contentView;
        Class secretClass = views.getClass();

        try {
            Map<Integer, String> text = new HashMap<Integer, String>();

            Field outerFields[] = secretClass.getDeclaredFields();
            for (int i = 0; i < outerFields.length; i++) {
                if (!outerFields[i].getName().equals("mActions")) continue;

                outerFields[i].setAccessible(true);

                ArrayList<Object> actions = (ArrayList<Object>) outerFields[i].get(views);
                for (Object action : actions) {
                    Field innerFields[] = action.getClass().getDeclaredFields();

                    Object value = null;
                    Integer type = null;
                    Integer viewId = null;
                    for (Field field : innerFields) {

                        field.setAccessible(true);


                        //System.out.println("name -> "+field.get(action)+" int "+field.getInt(action));
                        if (field.getName().equals("value")) {
                            value = field.get(action);
                        } else if (field.getName().equals("type")) {
                            type = field.getInt(action);
                        } else if (field.getName().equals("viewId")) {
                            viewId = field.getInt(action);
                        }


                    }

                    if (type == 9 || type == 10) {
                        text.put(viewId, value.toString());

                    }
                }

                System.out.println("title is: " + text.get(16908310));
                System.out.println("info is: " + text.get(16908294));
                System.out.println("text is: " + text.get(16908352));
            }
            for(Map.Entry<Integer,String> s:text.entrySet())
            {
               System.out.println("K: "+s.getKey()+" V:"+s.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onInterrupt() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_VISUAL;
        info.notificationTimeout = 100;
        setServiceInfo(info);
        loadBannedPackages(true);
    }

    private void loadBannedPackages(boolean registerReceiver)
    {
        String PREFERENCES_FILE = getApplicationContext().getString(R.string.preferences_file);
        String BANNED_ARRAY = getApplicationContext().getString(R.string.banned_packages);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

        try {
            mBannedPackages = (ArrayList<String>) ObjectSerializer.deserialize(
                    prefs.getString(BANNED_ARRAY, ObjectSerializer.serialize(new ArrayList<String>()))
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(registerReceiver)
        {
            prefs.registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        loadBannedPackages(false);
    }
}
