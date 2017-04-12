package com.forlayo.cowabunga;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.widget.ImageView;

public class Bindings {
  @BindingAdapter({"animateOnChangeOf", "animateOnChangeImage"})
  public static void animateOnChange(ImageView imageView, boolean value, String image) {
    Context context = imageView.getContext();
    Resources resources = context.getResources();

    Drawable d = null;
    if (0 == "power".compareTo(image)) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        d = AnimatedVectorDrawableCompat.create(context, value ? R.drawable.avd_power_off_to_on : R.drawable.avd_power_on_to_off);
      } else {
        d = resources.getDrawable(value ? R.drawable.ic_power_on : R.drawable.ic_power_off);
      }
    } else if (0 == "list_bulleted".compareTo(image)) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        d = AnimatedVectorDrawableCompat.create(context, value ? R.drawable.avd_list_bulleted_single_to_many : R.drawable.avd_list_bulleted_many_to_single);
      } else {
        d = resources.getDrawable(value ? R.drawable.ic_list_bulleted_many : R.drawable.ic_list_bulleted_single);
      }
    } else if (0 == "ad_remove".compareTo(image)) {
      d = VectorDrawableCompat.create(resources, R.drawable.ic_ad_remove, context.getTheme());
    }

    imageView.setImageDrawable(d);
    if ( (null != d) && (d instanceof Animatable) ) {
     ((Animatable) d).start();
    }
  }
}
