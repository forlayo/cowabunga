package com.forlayo.cowabunga;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.provider.Settings;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.v7.widget.AppCompatButton;

import com.forlayo.cowabunga.activities.MainActivity;
import com.forlayo.cowabunga.models.AccessibilityModel;
import com.forlayo.cowabunga.models.AppsModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import io.reactivex.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.assertNoUnverifiedIntents;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class NavigatorTests {
  @Rule
  public EspressoDaggerMockRule rule = new EspressoDaggerMockRule();

  @Rule
  public IntentsTestRule<MainActivity> activityRule = new IntentsTestRule<>(MainActivity.class, false, false);

  @Mock AccessibilityModel accessibility;
  @Mock AppsModel appsModel;
  @Mock AdHelper adHelper;

  private Navigator navigator;

  @Before
  public void setUp() {
    when(appsModel.getInstalled()).thenReturn(Observable.empty());
    when(accessibility.isAccessibilityServiceEnabled()).thenReturn(true);
    doNothing().when(adHelper).showBanner(any(), any());
  }

  @Test
  public void testOpenAccessibilityFromDialogButton() {
    navigator = new Navigator(activityRule.launchActivity(null));
    intending(anyIntent()).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

    navigator.openAccessibility();

    onView(allOf(withClassName(is(AppCompatButton.class.getName())), withText(R.string.dialog_accessibility_button)))
            .inRoot(isDialog())
            .check(matches(allOf(isClickable(), isCompletelyDisplayed())))
            .perform(click());

    intended(hasAction(Settings.ACTION_ACCESSIBILITY_SETTINGS));
  }

  @Test
  public void testOpenAccessibilityFromDialogImage() {
    navigator = new Navigator(activityRule.launchActivity(null));
    intending(anyIntent()).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

    navigator.openAccessibility();

    onView(withId(R.id.dialog_image))
            .inRoot(isDialog())
            .check(matches(allOf(isClickable(), isCompletelyDisplayed())))
            .perform(click());

    intended(hasAction(Settings.ACTION_ACCESSIBILITY_SETTINGS));
  }

  @Test
  public void testOpenAccessibilityAndCancel() {
    navigator = new Navigator(activityRule.launchActivity(null));
    intending(anyIntent()).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

    navigator.openAccessibility();

    Espresso.pressBack();

    assertNoUnverifiedIntents();
  }

  @Test
  public void testShareApp() {
    navigator = new Navigator(activityRule.launchActivity(null));
    intending(anyIntent()).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

    navigator.shareApp();

    intended(hasAction(Intent.ACTION_CHOOSER));
  }

  @Test
  public void testContact() {
    MainActivity activity = activityRule.launchActivity(null);
    navigator = new Navigator(activity);
    intending(anyIntent()).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

    navigator.showContact();

    intended(allOf(
            hasAction(Intent.ACTION_VIEW),
            hasData(activity.getString(R.string.config_contact_url))
    ));
  }
}
