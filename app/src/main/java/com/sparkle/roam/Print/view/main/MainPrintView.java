package com.sparkle.roam.Print.view.main;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import com.example.printlibrary.utils.SharedPreferencesUtils;
import com.example.printlibrary.utils.StringUtils;
import com.sparkle.roam.Fragments.GenerateCodeDialog;
import com.sparkle.roam.Print.bean.BluetoothInfoBean;
import com.sparkle.roam.Print.service.StartZPService;
import com.sparkle.roam.Print.utils.ToastUtil;

import java.util.Set;

/**
 * Created by jc on 2017/11/6.
 */

public class MainPrintView {
    private static final String TAG = "MainPrintView";

    private GenerateCodeDialog mMainActivity;
    private Handler mHandler;

    public MainPrintView(GenerateCodeDialog activity, Handler handler){
        mMainActivity = activity;
        mHandler = handler;

        init();
    }

    private void init(){
        getBoundedPrinters();
    }

    private void getBoundedPrinters() {

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            return;
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices, 自动连接到最后使用的蓝牙设备
            for (final BluetoothDevice device : pairedDevices) {
                final String address = SharedPreferencesUtils.getStringValue(mMainActivity.getContext(), "BT_Address");
                if(!StringUtils.isEmpty(address)) {
                    if (device.getAddress().equals(address)) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!StartZPService.getInstance().getPrinterManager().workThread.isConnected()){    // 连接断开
                                    BluetoothInfoBean bean = new BluetoothInfoBean();
                                    bean.setAddress(address);
                                    bean.setName(device.getName());

                                    if (StartZPService.getInstance().getPrinterManager().workThread.connectBt(bean)) {
                                        ToastUtil.showShortToast(mMainActivity.getContext(), "Connecting " + device.getName() + "...");
                                    }
                                }
                            }
                        }, 1000);
                        break;  // 退出循环
                    }
                }
            }
        }
    }
}
