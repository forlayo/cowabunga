package com.forlayo.cowabunga.activities;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.SwitchCompat;

import com.forlayo.cowabunga.AdHelper;
import com.forlayo.cowabunga.EspressoDaggerMockRule;
import com.forlayo.cowabunga.R;
import com.forlayo.cowabunga.RecyclerViewItemCountAssertion;
import com.forlayo.cowabunga.RecyclerViewMatcher;
import com.forlayo.cowabunga.fragments.ban.PInfo;
import com.forlayo.cowabunga.models.AccessibilityModel;
import com.forlayo.cowabunga.models.AppsModel;

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
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AppListTests {
  @Rule
  public EspressoDaggerMockRule rule = new EspressoDaggerMockRule();

  @Rule
  public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class, false, false);

  @Mock AccessibilityModel accessibility;
  @Mock AppsModel appsModel;
  @Mock AdHelper adHelper;

  private PInfo app1;
  private PInfo app2;

  @Before
  public void setUp() {
    doNothing().when(adHelper).showBanner(any(), any());
    when(accessibility.isAccessibilityServiceEnabled()).thenReturn(true);
    app1 = new PInfo();
    app1.appname = "My super app";

    app2 = new PInfo();
    app2.appname = "Another app";
  }

  @Test
  public void testEmptyAppListOk() {
    when(appsModel.getInstalled()).thenReturn(Observable.empty());

    activityRule.launchActivity(null);

    verify(appsModel, times(1)).getInstalled();

    onView(ViewMatchers.withId(R.id.recyclerView)).check(new RecyclerViewItemCountAssertion(1));
  }

  @Test
  public void testShowingAppsOk() {
    when(appsModel.getInstalled()).thenReturn(Observable.just(app1, app2));
    when(appsModel.isBanned(app1)).thenReturn(Single.just(false));
    when(appsModel.isBanned(app2)).thenReturn(Single.just(true));

    activityRule.launchActivity(null);

    verify(appsModel, times(1)).isBanned(app1);
    verify(appsModel, times(1)).isBanned(app2);

    onView(withId(R.id.recyclerView)).check(new RecyclerViewItemCountAssertion(3));
    onView(new RecyclerViewMatcher(R.id.recyclerView).atPosition(1))
            .check(matches(hasDescendant(allOf(
                    withClassName(is(SwitchCompat.class.getName())),
                    isChecked()
            ))))
            .check(matches(hasDescendant(withText(app1.pname))));

    onView(new RecyclerViewMatcher(R.id.recyclerView).atPosition(2))
            .check(matches(hasDescendant(allOf(
                    withClassName(is(SwitchCompat.class.getName())),
                    not(isChecked())
            ))))
            .check(matches(hasDescendant(withText(app2.pname))));
  }

  @Test
  public void testUnCheckCheckedAppSavesOk() {
    when(appsModel.getInstalled()).thenReturn(Observable.just(app1, app2));
    when(appsModel.isBanned(app1)).thenReturn(Single.just(false));
    when(appsModel.isBanned(app2)).thenReturn(Single.just(true));
    when(appsModel.setBanned(app1, true)).thenReturn(Completable.complete());

    activityRule.launchActivity(null);

    onView(withId(R.id.recyclerView)).check(new RecyclerViewItemCountAssertion(3));
    onView(allOf(
            isDescendantOfA(new RecyclerViewMatcher(R.id.recyclerView).atPosition(1)),
            withClassName(is(SwitchCompat.class.getName()))
    )).check(matches(isChecked()))
            .perform(click())
            .check(matches(not(isChecked())));

    verify(appsModel, times(1)).setBanned(app1, true);
    verify(appsModel, never()).setBanned(eq(app2), anyBoolean());
  }

  @Test
  public void testCheckUncheckedAppSavesOk() {
    when(appsModel.getInstalled()).thenReturn(Observable.just(app1, app2));
    when(appsModel.isBanned(app1)).thenReturn(Single.just(false));
    when(appsModel.isBanned(app2)).thenReturn(Single.just(true));
    when(appsModel.setBanned(app2, false)).thenReturn(Completable.complete());

    activityRule.launchActivity(null);

    onView(withId(R.id.recyclerView)).check(new RecyclerViewItemCountAssertion(3));
    onView(allOf(
            isDescendantOfA(new RecyclerViewMatcher(R.id.recyclerView).atPosition(2)),
            withClassName(is(SwitchCompat.class.getName()))
    )).check(matches(not(isChecked())))
            .perform(click())
            .check(matches(isChecked()));

    verify(appsModel, times(1)).setBanned(app2, false);
    verify(appsModel, never()).setBanned(eq(app1), anyBoolean());
  }

  @Test
  public void testSwitchButtonCanSwipe() {
    when(appsModel.getInstalled()).thenReturn(Observable.just(app1, app2));
    when(appsModel.isBanned(app1)).thenReturn(Single.just(false));
    when(appsModel.isBanned(app2)).thenReturn(Single.just(true));
    when(appsModel.setBanned(app1, true)).thenReturn(Completable.complete());
    when(appsModel.setBanned(app2, false)).thenReturn(Completable.complete());

    activityRule.launchActivity(null);

    onView(withId(R.id.recyclerView)).check(new RecyclerViewItemCountAssertion(3));
    onView(allOf(
            isDescendantOfA(new RecyclerViewMatcher(R.id.recyclerView).atPosition(1)),
            withClassName(is(SwitchCompat.class.getName()))
    )).check(matches(isChecked()))
            .perform(swipeLeft())
            .check(matches(not(isChecked())));
    onView(allOf(
            isDescendantOfA(new RecyclerViewMatcher(R.id.recyclerView).atPosition(2)),
            withClassName(is(SwitchCompat.class.getName()))
    )).check(matches(not(isChecked())))
            .perform(swipeRight())
            .check(matches(isChecked()));

    verify(appsModel, times(1)).setBanned(app1, true);
    verify(appsModel, times(1)).setBanned(app2, false);
  }
}
