package com.sparkle.roam.Print.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.printlibrary.utils.LogUtils;

/**
 * Created by Administrator on 2017/11/15.
 */

public class ZService extends Service {

    private static final String TAG = "ZService";
    private ZBinder mZBinder = new ZBinder();
    private ZPrinterManager mPrinterManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.d(TAG,"-----onBind---1-->");
        mPrinterManager = ZPrinterManager.getInsetance(this);
        LogUtils.d(TAG,"-----onBind--2--->");
        return mZBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mPrinterManager.onStartCommand();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d(TAG,"-----onCreate----->");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPrinterManager.onDestroy();
        LogUtils.d(TAG,"-----onDestroy----->");
    }

    class ZBinder extends Binder{
        public ZPrinterManager getPrinterManager(){
            LogUtils.d(TAG,"-----ZBinder----->");
            return mPrinterManager;
        }
    }
}
