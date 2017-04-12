package com.forlayo.cowabunga.fragments.ban;

import java.util.Comparator;

public class PInfoComparator implements Comparator<PInfo> {
    @Override
    public int compare(PInfo pInfo, PInfo pInfo2) {
        return pInfo.appname.toLowerCase().compareTo(pInfo2.appname.toLowerCase());
    }
}
