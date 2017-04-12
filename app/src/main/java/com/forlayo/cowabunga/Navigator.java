package com.forlayo.cowabunga;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

import com.forlayo.cowabunga.activities.FAQActivity;
import com.forlayo.cowabunga.di.scopes.PerActivity;
import com.forlayo.cowabunga.dialogs.EnableAccessibilityServiceDialog;

import io.reactivex.android.schedulers.AndroidSchedulers;

@PerActivity
public class Navigator {
  private AppCompatActivity activity;

  public Navigator(AppCompatActivity activity) {
    this.activity = activity;
  }

  public void openAccessibility() {
    EnableAccessibilityServiceDialog.show(activity)
            .filter(Boolean::booleanValue)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(b -> activity.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
  }

  public void shareApp() {
    Intent i = new Intent(Intent.ACTION_SEND);
    i.setType("text/plain");
    i.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.share_subject));
    i.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.share_text));
    activity.startActivity(Intent.createChooser(i, activity.getString(R.string.share_title_popup)));
  }

  public void showFAQ() {
    activity.startActivity(FAQActivity.getIntent(activity));
  }

  public void showContact() {
    try {
      activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.config_contact_url))));
    } catch (ActivityNotFoundException ignored) {
    }
  }
}
