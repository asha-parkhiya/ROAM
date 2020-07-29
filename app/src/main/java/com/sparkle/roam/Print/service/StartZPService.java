package com.sparkle.roam.Print.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.example.printlibrary.utils.LogUtils;

/**
 * Created by Administrator on 2017/11/15.
 */

public class StartZPService {

    private static StartZPService sStartZPService = new StartZPService();

    private static final String TAG = "StartZPService";
    private boolean isBindService;
    private PinterServiceListener mServiceListener;
    private ZPrinterManager mPrinterManager;

    public static StartZPService getInstance(){
        return sStartZPService;
    }

    public void bindingZService(Context context){
        Intent mIntent = new Intent(context, ZService.class);
        context.bindService(mIntent, conn, Service.BIND_AUTO_CREATE);
    }

    public void unBindingZService(Context context){
        if(isBindService) {
            context.unbindService(conn);
        }
        isBindService = false;
    }

    // 启动Service
    public void startZService(Context context){
        Intent mIntent = new Intent(context, ZService.class);
        context.startService(mIntent);
    }

    // 停止Service
    public void stopZService(Context context){
        Intent mIntent = new Intent(context, ZService.class);
        context.stopService(mIntent);
    }



    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.e(TAG, "-----连接成功--");
            if(mServiceListener != null){
                mServiceListener.onConnected();
                isBindService = true;
            }
            mPrinterManager = ((ZService.ZBinder) service).getPrinterManager();
            LogUtils.e(TAG, "-------mPrinterManager-->"+mPrinterManager);
            //mPrinterManager.startInit();
        }


        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "-------断开连接--");
        }
    };

    public ZPrinterManager getPrinterManager(){
        return mPrinterManager;
    }

    //是否绑定
    public boolean isBindService(){
        return isBindService;
    }

    public void setPinterServiceListener(PinterServiceListener serviceListener){
        mServiceListener = serviceListener;
    }

    public interface PinterServiceListener{
        void onConnected();
    }

}
