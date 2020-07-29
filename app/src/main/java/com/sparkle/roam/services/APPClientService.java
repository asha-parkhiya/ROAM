package com.sparkle.roam.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.sparkle.roam.AWS.ClientFactory;
import com.sparkle.roam.events.BooleanData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

public class APPClientService extends Service {

    private AWSAppSyncClient mAWSAppSyncClient;

    private final IBinder mBinder = new LocalBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    public class LocalBinder extends Binder {
        public APPClientService getService() {

            mAWSAppSyncClient = ClientFactory.getInstance(getApplicationContext());
            return APPClientService.this;
        }
    }

    public AWSAppSyncClient AWSAppSyncClient(){
        return mAWSAppSyncClient;
    }
    public void checkmutation(){
        handEveryTime();
    }

    public boolean isMutationQueue(){
        return mAWSAppSyncClient.isMutationQueueEmpty();
    }
    public void handEveryTime(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                EventBus.getDefault().post(new BooleanData(mAWSAppSyncClient.isMutationQueueEmpty()));
                handEveryTime();
            }
        }, 3000);
    }

}
