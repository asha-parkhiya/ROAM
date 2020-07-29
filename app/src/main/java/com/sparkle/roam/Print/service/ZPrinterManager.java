package com.sparkle.roam.Print.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.example.printlibrary.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/15.
 */

public class ZPrinterManager{

    private static final String TAG = "ZPrinterManager";

    private static ZPrinterManager sManager = new ZPrinterManager();
    // Service和workThread通信用mHandler
    public static WorkThread workThread = null;
    private static Handler mHandler = null;
    private static List<Handler> targetsHandler = new ArrayList<Handler>(5);

    private static Context mContext;

    private ZPrinterManager(){
    }

    public static ZPrinterManager getInsetance(Context context){
        mContext = context;
        onCreate();
        return sManager;
    }

    private static void onCreate() {
        mHandler = new MHandler(sManager);
        workThread = new WorkThread(mContext, mHandler);
        //workThread.start();
        LogUtils.v("WorkService", "-----onCreate");
    }

    public void onStartCommand() {
        LogUtils.v("WorkService", "-----onStartCommand");
        Message msg = Message.obtain();
        msg.what = Global.MSG_ALLTHREAD_READY;
        notifyHandlers(msg);
    }

    public void onDestroy() {
        workThread.disconnectBt();
        //workThread.disconnectBle();
        //workThread.disconnectNet();
        //workThread.disconnectUsb();
        workThread.quit();
        workThread = null;
        LogUtils.v("DrawerService", "-----onDestroy");
    }

    public static class MHandler extends Handler {

        WeakReference<ZPrinterManager> mService;

        MHandler(ZPrinterManager service) {
            mService = new WeakReference<ZPrinterManager>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            notifyHandlers(msg);
        }
    }

    /**
     *
     * @param handler
     */
    public static void addHandler(Handler handler) {
        if (!targetsHandler.contains(handler)) {
            targetsHandler.add(handler);
        }
    }

    /**
     *
     * @param handler
     */
    public static void delHandler(Handler handler) {
        if (targetsHandler.contains(handler)) {
            targetsHandler.remove(handler);
        }
    }

    /**
     *
     * @param msg
     */
    public static void notifyHandlers(Message msg) {
        for (int i = 0; i < targetsHandler.size(); i++) {
            Message message = Message.obtain(msg);
            targetsHandler.get(i).sendMessage(message);
        }
    }
}
