package com.enterpaper.comepennymaster.util;

import android.app.Activity;

/**
 * Created by Kim on 2015-08-12.
 */
public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
        }
    }
}
