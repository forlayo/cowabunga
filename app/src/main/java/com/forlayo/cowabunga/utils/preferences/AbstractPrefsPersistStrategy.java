package com.forlayo.cowabunga.utils.preferences;

import android.content.SharedPreferences;
import android.os.Build;

abstract public class AbstractPrefsPersistStrategy {
    abstract void persistAsync(SharedPreferences.Editor editor);

    private static final AbstractPrefsPersistStrategy INSTANCE=initImpl();

    public static void persist(SharedPreferences.Editor editor) {
        INSTANCE.persistAsync(editor);
    }

    private static AbstractPrefsPersistStrategy initImpl() {

        if (Build.VERSION.SDK_INT< Build.VERSION_CODES.HONEYCOMB) {
            return(new CommitAsyncStrategy());
        }

        return(new ApplyStrategy());
    }

    static class CommitAsyncStrategy extends AbstractPrefsPersistStrategy {
        @Override
        void persistAsync(final SharedPreferences.Editor editor) {
            (new Thread() {
                @Override
                public void run() {
                    editor.commit();
                }
            }).start();
        }
    }
}
