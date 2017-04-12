package com.forlayo.cowabunga;

import android.view.View;
import android.view.ViewGroup;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class NthChildOfMatcher extends TypeSafeMatcher<View> {
  private final Matcher<View> parentMatcher;
  private final int childPosition;

  public NthChildOfMatcher(Matcher<View> parentMatcher, int childPosition) {
    super();
    this.parentMatcher = parentMatcher;
    this.childPosition = childPosition;
  }

  public void describeTo(Description description) {
    description.appendText("with "+childPosition+" child view of type parentMatcher");
  }

  @Override
  public boolean matchesSafely(View view) {
    if (!(view.getParent() instanceof ViewGroup)) {
      return parentMatcher.matches(view.getParent());
    }

    ViewGroup group = (ViewGroup) view.getParent();
    return parentMatcher.matches(view.getParent()) && group.getChildAt(childPosition).equals(view);
  }
}
