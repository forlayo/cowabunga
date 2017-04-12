package com.forlayo.cowabunga.viewmodels;

import com.forlayo.cowabunga.Navigator;

import javax.inject.Inject;

public class FAQViewModel extends ViewModel {
  private Navigator navigator;

  @Inject
  FAQViewModel(Navigator navigator) {
    this.navigator = navigator;
  }

  public void contact() {
    navigator.showContact();
  }
}
