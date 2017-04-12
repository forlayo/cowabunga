package com.forlayo.cowabunga.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableBoolean;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class BooleanFieldObservable extends BaseObservable {
  @Bindable public ObservableBoolean checked = new ObservableBoolean();

  private Subject<Boolean> observable;

  BooleanFieldObservable() {
    observable = PublishSubject.create();
    checked.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
      @Override
      public void onPropertyChanged(android.databinding.Observable o, int i) {
        observable.onNext(checked.get());
      }
    });
  }

  public void set(boolean value) {
    checked.set(value);
  }

  Observable<Boolean> toObservable() {
    return observable;
  }
}
