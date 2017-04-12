package com.forlayo.cowabunga.activities;

import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.SwitchCompat;

import com.forlayo.cowabunga.AdHelper;
import com.forlayo.cowabunga.EspressoDaggerMockRule;
import com.forlayo.cowabunga.NthChildOfMatcher;
import com.forlayo.cowabunga.R;
import com.forlayo.cowabunga.RecyclerViewItemCountAssertion;
import com.forlayo.cowabunga.RecyclerViewMatcher;
import com.forlayo.cowabunga.models.AccessibilityModel;
import com.forlayo.cowabunga.models.AppsModel;
import com.forlayo.cowabunga.models.SettingsModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class SettingsTests {
  @Rule
  public EspressoDaggerMockRule rule = new EspressoDaggerMockRule();

  @Rule
  public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class, false, false);

  @Mock AccessibilityModel accessibility;
  @Mock AppsModel appsModel;
  @Mock SettingsModel settingsModel;
  @Mock AdHelper adHelper;

  @Before
  public void setUp() {
    when(accessibility.isAccessibilityServiceEnabled()).thenReturn(true);
    when(appsModel.getInstalled()).thenReturn(Observable.empty());
    doNothing().when(adHelper).showBanner(any(), any());
  }

  private void testSettings(boolean notificationList, boolean energySaving) {
    when(settingsModel.getPrefNotificationList()).thenReturn(Single.just(notificationList));
    when(settingsModel.getPrefEnergySaving()).thenReturn(Single.just(energySaving));

    activityRule.launchActivity(null);

    verify(settingsModel, times(1)).getPrefNotificationList();
    verify(settingsModel, times(1)).getPrefEnergySaving();

    onView(withId(R.id.recyclerView)).check(new RecyclerViewItemCountAssertion(1));
    onView(allOf(
            isDescendantOfA(new RecyclerViewMatcher(R.id.recyclerView).atPosition(0)),
            isDescendantOfA(new NthChildOfMatcher(withId(R.id.options_container), 1)),
            withClassName(is(SwitchCompat.class.getName()))
    ))
            .check(matches(allOf(
                    isCompletelyDisplayed(),
                    notificationList ? isChecked() : not(isChecked()),
                    isClickable()
            )));

    onView(withId(R.id.recyclerView)).check(new RecyclerViewItemCountAssertion(1));
    onView(allOf(
            isDescendantOfA(new RecyclerViewMatcher(R.id.recyclerView).atPosition(0)),
            isDescendantOfA(new NthChildOfMatcher(withId(R.id.options_container), 2)),
            withClassName(is(SwitchCompat.class.getName()))
    ))
            .check(matches(allOf(
                    isCompletelyDisplayed(),
                    energySaving ? isChecked() : not(isChecked()),
                    isClickable()
            )));
  }

  private void testModifySetting(int index, boolean activate, boolean bySwipe) {
    when(settingsModel.getPrefNotificationList()).thenReturn(Single.just((1 == index)&&(!activate)));
    when(settingsModel.getPrefEnergySaving()).thenReturn(Single.just((2 == index)&&(!activate)));
    when(settingsModel.setPrefNotificationList(anyBoolean())).thenReturn(Completable.complete());
    when(settingsModel.setPrefEnergySaving(anyBoolean())).thenReturn(Completable.complete());

    activityRule.launchActivity(null);

    verify(settingsModel, times(1)).getPrefNotificationList();
    verify(settingsModel, times(1)).getPrefEnergySaving();

    onView(withId(R.id.recyclerView)).check(new RecyclerViewItemCountAssertion(1));
    onView(allOf(
            isDescendantOfA(new RecyclerViewMatcher(R.id.recyclerView).atPosition(0)),
            isDescendantOfA(new NthChildOfMatcher(withId(R.id.options_container), index)),
            withClassName(is(SwitchCompat.class.getName()))
    ))
            .check(matches(allOf(
                    isCompletelyDisplayed(),
                    activate ? not(isChecked()) : isChecked(),
                    isClickable()
            )))
            .perform(bySwipe ? (activate ? swipeLeft() : swipeRight()) : click())
            .check(matches(activate ? isChecked() : not(isChecked())));

    switch(index) {
      case 1:
        verify(settingsModel, times(1)).setPrefNotificationList(activate);
        verify(settingsModel, never()).setPrefEnergySaving(activate);
        break;
      case 2:
        verify(settingsModel, never()).setPrefNotificationList(activate);
        verify(settingsModel, times(1)).setPrefEnergySaving(activate);
        break;
    }
  }

  @Test
  public void testShowingSettingsAllFalse() {
    testSettings(false, false);
  }

  @Test
  public void testShowingSettingsAllTrue() {
    testSettings(true, true);
  }

  @Test
  public void testShowingSettingsTrueFalse() {
    testSettings(true, false);
  }

  @Test
  public void testShowingSettingsFalseTrue() {
    testSettings(false, true);
  }

  @Test
  public void testActivateNotificationListWithClick() {
    testModifySetting(1, true, false);
  }

  @Test
  public void testDeactivateNotificationListWithClick() {
    testModifySetting(1, false, false);
  }

  @Test
  public void testActivateNotificationListWithSwipe() {
    testModifySetting(1, true, true);
  }

  @Test
  public void testDeactivateNotificationListWithSwipe() {
    testModifySetting(1, false, true);
  }

  @Test
  public void testActivateEnergySavingWithClick() {
    testModifySetting(2, true, false);
  }

  @Test
  public void testDeactivateEnergySavingWithClick() {
    testModifySetting(2, false, false);
  }

  @Test
  public void testActivateEnergySavingWithSwipe() {
    testModifySetting(2, true, true);
  }

  @Test
  public void testDeactivateEnergySavingWithSwipe() {
    testModifySetting(1, false, true);
  }
}
