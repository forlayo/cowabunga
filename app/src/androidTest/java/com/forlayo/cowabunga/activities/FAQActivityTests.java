package com.forlayo.cowabunga.activities;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import com.forlayo.cowabunga.AdHelper;
import com.forlayo.cowabunga.EspressoDaggerMockRule;
import com.forlayo.cowabunga.Navigator;
import com.forlayo.cowabunga.NestedScrollViewScrollToAction;
import com.forlayo.cowabunga.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class FAQActivityTests {
  @Rule
  public EspressoDaggerMockRule rule = new EspressoDaggerMockRule();

  @Rule
  public ActivityTestRule<FAQActivity> activityRule = new ActivityTestRule<>(FAQActivity.class, false, false);

  @Mock Navigator navigator;
  @Mock AdHelper adHelper;

  @Before
  public void setUp() {
    doNothing().when(adHelper).showBanner(any(), any());
  }

  @Test
  public void testClickContactTriggersContact() {
    activityRule.launchActivity(null);

    onView(ViewMatchers.withText(R.string.faq_contact))
            .perform(new NestedScrollViewScrollToAction())
            .check(matches(isCompletelyDisplayed()))
            .perform(click());

    verify(navigator, times(1)).showContact();
  }
}
