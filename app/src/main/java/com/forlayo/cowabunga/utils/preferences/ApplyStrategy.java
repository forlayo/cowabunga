package com.forlayo.cowabunga.utils.preferences;

import android.content.SharedPreferences.Editor;

public class ApplyStrategy extends AbstractPrefsPersistStrategy {

    @Override
    void persistAsync(Editor editor) {
        editor.apply();
    }
}
