package com.forlayo.cowabunga.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;

import com.forlayo.cowabunga.R;

import io.reactivex.Maybe;

public class EnableAccessibilityServiceDialog {
  public static Maybe<Boolean> show(Context context) {
    return Maybe.create(e -> {
      AlertDialog dialog = new AlertDialog.Builder(context, R.style.DialogTheme)
              .setTitle(R.string.dialog_accessibility_title)
              .setMessage(R.string.dialog_accessibility_message)
              .setPositiveButton(R.string.dialog_accessibility_button, (dlg, which) -> {
                e.onSuccess(true);
                e.onComplete();
              })
              .setCancelable(true)
              .setOnCancelListener(dlg -> e.onComplete())
              .show();
      View v = dialog.findViewById(android.R.id.message);
      if (null != v) {
        ViewParent parent = v.getParent();
        if ( (null != parent) && (parent instanceof LinearLayout) ) {
          LinearLayout layout = (LinearLayout) parent;
          View image = LayoutInflater.from(context).inflate(R.layout.dialog_image, layout, false);
          layout.addView(image);
          image.setOnClickListener(v1 -> {
            dialog.dismiss();
            e.onSuccess(true);
            e.onComplete();
          });
        }
      }
    });
  }
}
