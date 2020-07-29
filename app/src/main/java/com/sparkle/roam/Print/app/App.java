package com.sparkle.roam.Print.app;

import android.app.Application;

/**
 *
 * Created by Administrator on 2017/11/14.
 */

public class App extends Application {

    private static final String TAG = "App";

    private static App context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static App getContext() {
        return context;
    }
}
