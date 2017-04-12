package com.forlayo.cowabunga.activities;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;

import com.forlayo.cowabunga.AdHelper;
import com.forlayo.cowabunga.EspressoDaggerMockRule;
import com.forlayo.cowabunga.Navigator;
import com.forlayo.cowabunga.R;
import com.forlayo.cowabunga.models.AccessibilityModel;
import com.forlayo.cowabunga.models.AppsModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import io.reactivex.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class MainActivityTests {
  @Rule
  public EspressoDaggerMockRule rule = new EspressoDaggerMockRule();

  @Rule
  public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class, false, false);

  @Mock AccessibilityModel accessibility;
  @Mock AppsModel appsModel;
  @Mock Navigator navigator;
  @Mock AdHelper adHelper;

  @Before
  public void setUp() {
    when(appsModel.getInstalled()).thenReturn(Observable.empty());
    doNothing().when(adHelper).showBanner(any(), any());
  }

  static class VariableBooleanAnswer implements Answer<Boolean> {
    private boolean answer;

    VariableBooleanAnswer(boolean answer) {
      this.answer = answer;
    }

    void setAnswer(boolean answer) {
      this.answer = answer;
    }

    @Override
    public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
      return answer;
    }
  }

  @Test
  public void testAccessibilityDisabledLaunchesDialog() {
    when(accessibility.isAccessibilityServiceEnabled()).thenReturn(false);

    activityRule.launchActivity(null);

    verify(navigator, times(1)).openAccessibility();
  }

  @Test
  public void testAccessibilityEnabledDoesNotLaunchesDialog() {
    when(accessibility.isAccessibilityServiceEnabled()).thenReturn(true);

    activityRule.launchActivity(null);

    verify(navigator, never()).openAccessibility();
  }

  @Test
  public void testClickOnActionBarAlertLaunchesAccessibilityScreen() {
    when(accessibility.isAccessibilityServiceEnabled()).thenReturn(false);

    activityRule.launchActivity(null);

    onView(withId(R.id.menu_main_alert))
            .check(matches(allOf(isEnabled(), isClickable(), isCompletelyDisplayed())))
            .perform(click());
    onView(allOf(isDescendantOfA(withClassName(is(Toolbar.class.getName()))), withClassName(is(SwitchCompat.class.getName()))))
            .check(matches(allOf(not(isChecked()), isEnabled(), isClickable(), isCompletelyDisplayed())));

    verify(navigator, times(2)).openAccessibility();
    verify(accessibility, atLeastOnce()).isAccessibilityServiceEnabled();
  }

  @Test
  public void testClickOnActionBarButtonLaunchesAccessibilityScreen() {
    when(accessibility.isAccessibilityServiceEnabled()).thenReturn(false);

    activityRule.launchActivity(null);

    onView(withId(R.id.menu_main_alert))
            .check(matches(allOf(isEnabled(), isClickable(), isCompletelyDisplayed())));
    onView(allOf(isDescendantOfA(withClassName(is(Toolbar.class.getName()))), withClassName(is(SwitchCompat.class.getName()))))
            .check(matches(allOf(not(isChecked()), isEnabled(), isClickable(), isCompletelyDisplayed())))
            .perform(click());

    verify(navigator, times(2)).openAccessibility();
    verify(accessibility, atLeastOnce()).isAccessibilityServiceEnabled();
  }

  @Test
  public void testSwipeOnActionBarButtonLaunchesAccessibilityScreen() {
    when(accessibility.isAccessibilityServiceEnabled()).thenReturn(false);

    activityRule.launchActivity(null);

    onView(withId(R.id.menu_main_alert))
            .check(matches(allOf(isEnabled(), isClickable(), isCompletelyDisplayed())));
    onView(allOf(isDescendantOfA(withClassName(is(Toolbar.class.getName()))), withClassName(is(SwitchCompat.class.getName()))))
            .check(matches(allOf(not(isChecked()), isEnabled(), isClickable(), isCompletelyDisplayed())))
            .perform(swipeRight());

    verify(navigator, times(2)).openAccessibility();
    verify(accessibility, atLeastOnce()).isAccessibilityServiceEnabled();
  }

  @Test
  public void testClickOnActionBarButtonAndEnableAccessibility() throws InterruptedException {
    VariableBooleanAnswer answer = new VariableBooleanAnswer(false);
    doAnswer(answer).when(accessibility).isAccessibilityServiceEnabled();

    MainActivity activity = activityRule.launchActivity(null);

    onView(withId(R.id.menu_main_alert))
            .check(matches(allOf(isEnabled(), isClickable(), isCompletelyDisplayed())));
    onView(allOf(isDescendantOfA(withClassName(is(Toolbar.class.getName()))), withClassName(is(SwitchCompat.class.getName()))))
            .check(matches(isCompletelyDisplayed()));
    answer.setAnswer(true);

    onView(allOf(isDescendantOfA(withClassName(is(Toolbar.class.getName()))), withClassName(is(SwitchCompat.class.getName()))))
            .check(matches(allOf(not(isChecked()), isEnabled(), isClickable(), isCompletelyDisplayed())))
            .perform(click());

    verify(navigator, times(2)).openAccessibility();

    InstrumentationRegistry.getInstrumentation().runOnMainSync(activity::onPause);
    InstrumentationRegistry.getInstrumentation().runOnMainSync(activity::onResume);

    Thread.sleep(1000); // Wait for activity to update after onPause/onResume.

    onView(allOf(isDescendantOfA(withClassName(is(Toolbar.class.getName()))), withClassName(is(SwitchCompat.class.getName()))))
            .check(doesNotExist());
    onView(withId(R.id.menu_main_alert)).check(doesNotExist());

    verify(accessibility, atLeast(2)).isAccessibilityServiceEnabled();
  }

  @Test
  public void testSwitchNotExistsIfEnabledAccessibility() {
    when(accessibility.isAccessibilityServiceEnabled()).thenReturn(true);

    activityRule.launchActivity(null);

    onView(allOf(isDescendantOfA(withClassName(is(Toolbar.class.getName()))), withClassName(is(SwitchCompat.class.getName())))).check(doesNotExist());
    onView(withId(R.id.menu_main_alert)).check(doesNotExist());

    verify(accessibility, atLeastOnce()).isAccessibilityServiceEnabled();
  }

  @Test
  public void testClickShareAppLaunchesIntent() {
    activityRule.launchActivity(null);

    Context context = InstrumentationRegistry.getTargetContext();
    Espresso.openActionBarOverflowOrOptionsMenu(context);

    onView(ViewMatchers.withText(R.string.main_menu_share))
            .check(matches(isCompletelyDisplayed()))
            .perform(click());

    verify(navigator, times(1)).shareApp();
  }

  @Test
  public void testClickFAQLaunchesActivity() {
    activityRule.launchActivity(null);

    Context context = InstrumentationRegistry.getTargetContext();
    Espresso.openActionBarOverflowOrOptionsMenu(context);

    onView(ViewMatchers.withText(R.string.main_menu_faq))
            .check(matches(isCompletelyDisplayed()))
            .perform(click());

    verify(navigator, times(1)).showFAQ();
  }
}
