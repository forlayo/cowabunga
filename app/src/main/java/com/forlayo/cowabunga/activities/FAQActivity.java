package com.forlayo.cowabunga.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.forlayo.cowabunga.AdHelper;
import com.forlayo.cowabunga.R;
import com.forlayo.cowabunga.databinding.ActivityFaqBinding;
import com.forlayo.cowabunga.viewmodels.FAQViewModel;
import com.forlayo.cowabunga.viewmodels.ViewModel;

import javax.inject.Inject;

public class FAQActivity extends BaseActivity {
  public static Intent getIntent(Context context) {
    return new Intent(context, FAQActivity.class);
  }

  @Inject FAQViewModel viewModel;
  @Inject AdHelper adHelper;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ActivityFaqBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_faq);

    getActivityComponent()
            .map(activityComponent -> {
              activityComponent.inject(this);
              binding.setVm(viewModel);
              viewModel.initialize();
              return activityComponent;
            })
            .blockingGet();

    setSupportActionBar(binding.toolbar);

    ActionBar actionBar = getSupportActionBar();
    if (null != actionBar) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    adHelper.showBanner(this, binding.adContainer);
  }

  @Override
  protected ViewModel getViewModel() {
    return viewModel;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    adHelper.cleanup();
  }
}
