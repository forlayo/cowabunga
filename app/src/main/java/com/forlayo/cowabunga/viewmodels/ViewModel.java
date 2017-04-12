package com.forlayo.cowabunga.viewmodels;

import android.content.Intent;
import android.databinding.BaseObservable;
import android.os.Bundle;

@SuppressWarnings("UnusedParameters")
public abstract class ViewModel extends BaseObservable {
  public void initialize() {}
  public void pause() {}
  public void resume() {}
  public void destroy() {}
  public void saveState(Bundle state) {}
  public void restoreState(Bundle state) {}
  public void onActivityResult(int requestCode, int resultCode, Intent data) {}
}
