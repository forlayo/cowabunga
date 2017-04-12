package com.forlayo.cowabunga.activities;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import com.forlayo.cowabunga.AdHelper;
import com.forlayo.cowabunga.EspressoDaggerMockRule;
import com.forlayo.cowabunga.R;
import com.forlayo.cowabunga.models.NotificationsModel;
import com.forlayo.cowabunga.models.SettingsModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import io.reactivex.Single;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class NotificationActivityTests {
  @Rule
  public EspressoDaggerMockRule rule = new EspressoDaggerMockRule();

  @Rule
  public ActivityTestRule<NotificationActivity> activityRule = new ActivityTestRule<>(NotificationActivity.class, false, false);

  @Mock AdHelper adHelper;
  @Mock NotificationsModel notificationsModel;
  @Mock SettingsModel settingsModel;

  private Intent intent;

  @Before
  public void setUp() {
    doNothing().when(adHelper).showBanner(any(), any());
    intent = new Intent();
    intent.putExtra("tickerText", "Hey buddy!");
    intent.putExtra("packageName", "com.forlayo.cowabunga");
    intent.putExtra("fromReceiver", true);
  }

  @Test
  public void testShowNotification() {
    when(settingsModel.getPrefEnergySaving()).thenReturn(Single.just(false));
    when(settingsModel.getPrefNotificationList()).thenReturn(Single.just(false));

    activityRule.launchActivity(intent);

    onView(withId(R.id.notification_icon))
            .check(matches(isCompletelyDisplayed()));
    onView(withId(R.id.notification_appname))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    onView(withText("Hey buddy!")).check(matches(isCompletelyDisplayed()));
  }

  @Test
  public void testShowNotificationWithEnergySavingOn() {
    when(settingsModel.getPrefEnergySaving()).thenReturn(Single.just(true));
    when(settingsModel.getPrefNotificationList()).thenReturn(Single.just(false));

    NotificationActivity activity = activityRule.launchActivity(intent);

    onView(withText("Hey buddy!")).check(matches(isCompletelyDisplayed()));
    assertEquals(0.005f, activity.getWindow().getAttributes().screenBrightness);
  }

  @Test
  public void testShowNotificationWithEnergySavingOff() {
    when(settingsModel.getPrefEnergySaving()).thenReturn(Single.just(false));
    when(settingsModel.getPrefNotificationList()).thenReturn(Single.just(false));

    NotificationActivity activity = activityRule.launchActivity(intent);

    onView(withText("Hey buddy!")).check(matches(isCompletelyDisplayed()));
    assertEquals(0.75f, activity.getWindow().getAttributes().screenBrightness);
  }
}
