package com.lisn.signingplugin;

import android.app.Application;

/**
 * Created by admin on 2017/5/6.
 */

public class app extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(getApplicationContext());
    }
}
