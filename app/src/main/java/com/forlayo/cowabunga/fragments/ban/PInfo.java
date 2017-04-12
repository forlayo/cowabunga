package com.forlayo.cowabunga.fragments.ban;

import android.graphics.drawable.Drawable;

public class PInfo {
  public String appname = "";
  public String pname = "";
  public String versionName = "";
  public int versionCode = 0;
  public Drawable icon;
  public boolean enabled = true;
  public String prettyPrint() {
    return  appname + "\t" + pname + "\t" + versionName + "\t" + versionCode;
  }

  @Override
  public String toString() {
    return "PInfo{" +
            "appname='" + appname + '\'' +
            ", pname='" + pname + '\'' +
            ", versionName='" + versionName + '\'' +
            ", versionCode=" + versionCode +
            ", icon=" + icon +
            ", enabled=" + enabled +
            '}';
  }
}
