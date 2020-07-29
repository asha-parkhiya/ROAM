package com.sparkle.roam;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import com.sparkle.roam.Print.app.App;

import io.fabric.sdk.android.Fabric;


public class MyApplication extends Application {

    private static final String TAG = "App";

    private static MyApplication context;



    public static MyApplication getContext() {
        return context;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        context = this;
//        HermesEventBus.getDefault().init(this);
        //The following is to check whether the potential bug caused by a dead lock
        //in the main thread has been fixed.
//        HermesEventBus.getDefault().post("Event posted before connection from sub-process");

//        EventBus.getDefault().register(this);
    }

    public LoginRoomDatabase getDatabase() {
        return LoginRoomDatabase.getDatabase(this);
    }


}
