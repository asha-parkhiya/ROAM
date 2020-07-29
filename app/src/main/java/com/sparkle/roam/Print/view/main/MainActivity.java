package com.sparkle.roam.Print.view.main;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.printlibrary.permission.PermissionCenter;
import com.example.printlibrary.utils.DimensUtil;
import com.example.printlibrary.utils.LogUtils;
import com.example.printlibrary.utils.SharedPreferencesUtils;
import com.sparkle.roam.MyApplication;
import com.sparkle.roam.Print.app.App;
import com.sparkle.roam.Print.base.AbsBaseActivity;
import com.sparkle.roam.Print.bean.BluetoothInfoBean;
import com.sparkle.roam.Print.bean.PrintParamsBean;
import com.sparkle.roam.Print.service.Global;
import com.sparkle.roam.Print.service.StartZPService;
import com.sparkle.roam.Print.service.ZPrinterManager;
import com.sparkle.roam.Print.utils.BluetoothManager;
import com.sparkle.roam.Print.utils.ToastUtil;
import com.sparkle.roam.Print.utils.UIHelp;
import com.sparkle.roam.Print.view.bluetooth.SettingBTActivity;
import com.sparkle.roam.Print.view.customView.DragView;
import com.sparkle.roam.Print.view.edit.EditActivity;
import com.sparkle.roam.Print.view.settings.SettingPrinterActivity;
import com.sparkle.roam.Print.view.settings.SettingView;
import com.sparkle.roam.R;

import java.util.Locale;

public class MainActivity extends AbsBaseActivity {

    private static final String TAG = "MainActivity";

    private AnimationDrawable mAnimationDrawable = null;
    // 用于处理各种通知消息，刷新界面的handler
    private final Handler mHandler = new Handler();

    private StartZPService mStartZPService = null;
    private ZPrinterManager mPrinterManager = null;
    private ImageView mStateIv = null;
    private TextView mNameTv = null;
    private PermissionCenter mPermissionCenter = null;
    private DragView mDragView = null;
    private PrintParamsBean mPrintParamsBean = null;
    private View mDragLayoutFl = null;
    private View mContainerLl = null;
    private TextView mShowTv = null;
    private String mBtNameStr = null;
    private String mBtAddressStr = null;
    private int mWidgetWidth;
    private int mWidgetHeight;
    private int mBtStatus = BT_STA_DISCONNECT;      // 0=等待连接，1=正在连接，2=连接ok

    public static final int BT_STA_DISCONNECT = 0;
    public static final int BT_STA_CONNECTING = 1;
    public static final int BT_STA_CONNECTED = 2;

    private Handler mLoopHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == msg.arg2) {
                int sta = msg.arg1;

                switch (sta) {
                    case BT_STA_CONNECTING:
//                        mNameTv.setText( R.string.str_connecting);
//                        mShowTv.setText(R.string.msg_btunconnect_str);
                        try {
//                            mStateIv.setImageResource(R.anim.printer_state_animation);
                            mAnimationDrawable = (AnimationDrawable) mStateIv.getDrawable();
                            mAnimationDrawable.start();  //开始动画

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    SharedPreferencesUtils.putBooleanPreferences(MainActivity.this, UIHelp.PRINTOR, false);
                                }
                            });
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mBtStatus < BT_STA_CONNECTED && mBtStatus != BT_STA_DISCONNECT) {
                                        sendBtStatusMessage(BT_STA_DISCONNECT);  // 提示连接成功
                                    }
                                }
                            }, 8000);  // 连接超时8s
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case BT_STA_CONNECTED:
                        try {
                            if (msg.obj != null) {
                                BluetoothInfoBean bt_bean = (BluetoothInfoBean)msg.obj;
                                mBtNameStr = bt_bean.getName();
                                mBtAddressStr = bt_bean.getAddress();
                            }
//                            mShowTv.setText(R.string.msg_bt_connected);
//                            mStateIv.setImageResource(R.drawable.bluetooth_connected);
                            if (mBtNameStr != null) {
                                mNameTv.setText(mBtNameStr);// + "-" + bean.getAddress()
                            }
                            stopAnimition();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    SharedPreferencesUtils.putBooleanPreferences(MainActivity.this, UIHelp.PRINTOR, true);
                                    SharedPreferencesUtils.putStringPreferences(MainActivity.this, "BT_Address", mBtAddressStr);
                                    sendToast("Connected");
                                }
                            }, 1000);
                        }
                        break;
                    case BT_STA_DISCONNECT:
                    default:
                        stopShow();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferencesUtils.putBooleanPreferences(MainActivity.this, UIHelp.PRINTOR, false);
                                StartZPService.getInstance().getPrinterManager().workThread.disconnectBt();
                            }
                        });
                        if (mBtStatus != BT_STA_DISCONNECT) {
                            sendToast(getString(R.string.mst_bt_connect_fail));
                        }
                        break;
                }
                mBtStatus = sta;
            }
        }
    };

    // change language
    private void changeAppLanguage() {
        Locale local = null;
        int lang = SharedPreferencesUtils.getIntPreferences(this,"appLanguage", SettingView.LANGUAGE_SYS);
        switch (lang){
        case SettingView.LANGUAGE_CN:
            local = Locale.SIMPLIFIED_CHINESE;
            break;
        case SettingView.LANGUAGE_EN:
            local = Locale.ENGLISH;
            break;
        case SettingView.LANGUAGE_TW:
            local = Locale.TRADITIONAL_CHINESE;
            break;
        default:
            local = Locale.getDefault();
            break;
        }
        // 本地语言设置
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = local;
        res.updateConfiguration(conf, dm);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        changeAppLanguage();   // 更改APP语言

        setContentView(R.layout.activity_main);
        mPermissionCenter = new PermissionCenter(this);
        mPermissionCenter.verifyStoragePermissions(this);
        mPermissionCenter.permissionCheck(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION});
        mPermissionCenter.permissionCheck(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
        //mPermissionCenter.permissionCheck(new String[]{Manifest.permission.WRITE_DATABASE});

        mStartZPService = StartZPService.getInstance();
        mPrinterManager = mStartZPService.getPrinterManager();
        if (mPrinterManager == null && !mStartZPService.isBindService()) {
            mStartZPService.bindingZService(MyApplication.getContext());
        }

        initFilter();
        init();
    }

    private void initFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("BluetoothItem");
        intentFilter.addAction("BluetoothStatus");
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED); //配对结束时，断开连接
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (action.equals("BluetoothItem")) {
                    int blueTag = intent.getIntExtra("BlueTag", -1);
                    BluetoothInfoBean bean = (BluetoothInfoBean) intent.getSerializableExtra("BluetoothInfo");
                    if (bean != null) {
                        switch (blueTag) {
                            case Global.MSG_INFO_BEAN:
                                setBTInfo(bean);
                                break;
                            default:
                                sendBtStatusMessage(BT_STA_DISCONNECT);
                                break;
                        }
                    } else {
                        sendBtStatusMessage(BT_STA_DISCONNECT);
                    }
                } else if (action.equals("BluetoothStatus")) {
                    int sta = intent.getIntExtra("BlueStatus", BT_STA_DISCONNECT);
                    sendBtStatusMessage(sta);
                }else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                    sendBtStatusMessage(BT_STA_DISCONNECT);
                }
            }
        }
    };

    public void sendToast(final String cotent){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ToastUtil.showShortToast(MainActivity.this, cotent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void sendToast(final String cotent, int delay){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    ToastUtil.showShortToast(MainActivity.this, cotent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, delay);

    }

    // 保存连接OK蓝牙信息
    private void setBTInfo(BluetoothInfoBean bean) {
        boolean isConn = false;
        if (bean.getResult() == 1) {
            if (StartZPService.getInstance().getPrinterManager().workThread.isConnected()) {
                isConn = true;
            }
        }
        connectedBluetooth(isConn, bean);
    }

    // 发送更新BT状态的消息
    private void sendBtStatusMessage(int sta, Object obj) {
        //mLoopHandler.removeMessages(0);

        Message message = new Message();
        message.what = 0;
        message.arg1 = sta;
        message.arg2 = sta;
        message.obj = obj;
        mLoopHandler.sendMessage(message);

        LogUtils.d(TAG, "--bt_connect_msg=" + sta);
    }

    // 发送更新BT状态的消息
    private void sendBtStatusMessage(int sta) {
        sendBtStatusMessage(sta, null);
    }

    public void connectedBluetooth(final boolean isConnected) {
        connectedBluetooth(isConnected, null);
    }

    public void connectedBluetooth(final boolean isConnected, Object obj) {
        if (isConnected) {
            sendBtStatusMessage(BT_STA_CONNECTED, obj);
        } else {
            sendBtStatusMessage(BT_STA_DISCONNECT);
        }
    }



    @Override
    public void initView() {
//        findViewById(R.id.main_container_main_alert).setOnClickListener(this);

        findViewById(R.id.title_backicon).setVisibility(View.GONE);
        findViewById(R.id.title_backtext).setVisibility(View.GONE);

//        findViewById(R.id.title_mainicon).setVisibility(View.VISIBLE);
//        findViewById(R.id.title_mainicon).setOnClickListener(this);
//        findViewById(R.id.title_opticon).setVisibility(View.VISIBLE);
//        findViewById(R.id.title_opticon).setOnClickListener(this);
//        findViewById(R.id.main_func_newlabel).setOnClickListener(this);

        mDragLayoutFl = findViewById(R.id.main_container_label_viewer);
        mDragView = (DragView) findViewById(R.id.main_label_viewer);
        ViewGroup.LayoutParams layoutParams = mDragLayoutFl.getLayoutParams();
        layoutParams.width = DimensUtil.getDisplayWidth(this);
        layoutParams.height = (DimensUtil.getDisplayWidth(this) / 4) * 3;
        mDragLayoutFl.setLayoutParams(layoutParams);
        mDragView.setEditor(3);
        mContainerLl = findViewById(R.id.main_container_ll);
//        RelativeLayout mMainDragFl = (RelativeLayout) findViewById(R.id.main_drag_rl);
//        mMainDragFl.setOnClickListener(this);
//        mShowTv = (TextView) findViewById(R.id.tv_printlabel_name); // 连接状态

        initBluetoothUi();
//        new MainPrintView(this, mHandler);
    }

    private void hidePrint() {
//        findViewById(R.id.main_container_main_alert).setVisibility(View.VISIBLE);
        mContainerLl.setVisibility(View.GONE);
    }

    private void initBluetoothUi() {
//        mStateIv = (ImageView) findViewById(R.id.iv_printer_state);
//        mNameTv = (TextView) findViewById(R.id.tv_printer_name);
        mAnimationDrawable = (AnimationDrawable) mStateIv.getDrawable();
        //开始动画
        mAnimationDrawable.start();

//        findViewById(R.id.vg_printer_name).setOnClickListener(this);

        if ((BluetoothManager.isBluetoothSupported()) && (!BluetoothManager.isBluetoothEnabled())) {
            mAnimationDrawable.stop();
//            mStateIv.setImageResource(R.drawable.bluetooth_disconnected);
//            mNameTv.setText(R.string.printer_not_connected);
        }
    }

    public void stopAnimition() {
        mAnimationDrawable.stop();
    }

    public void stopShow() {
        SharedPreferencesUtils.putBooleanPreferences(this, UIHelp.PRINTOR, false);
        stopAnimition();
//        mStateIv.setImageResource(R.drawable.bluetooth_disconnected);
//        mShowTv.setText(R.string.msg_btunconnect_str);
//        mNameTv.setText(R.string.msg_select_btdev_str);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mStartZPService.getPrinterManager() != null) {
            mPrinterManager = mStartZPService.getPrinterManager();
        } else {
            mStartZPService.bindingZService(MyApplication.getContext());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mStartZPService.getPrinterManager() != null) {
            mPrinterManager = mStartZPService.getPrinterManager();
        } else {
            mStartZPService.bindingZService(MyApplication.getContext());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDragView = null;
        //mHandler.removeCallbacks(runnable);
        StartZPService.getInstance().getPrinterManager().workThread.disconnectBt();
        unregisterReceiver(mBroadcastReceiver);
        mStartZPService.unBindingZService(MyApplication.getContext());
        System.gc();
    }

    @Override
    public void initData() {
        mPrintParamsBean = new PrintParamsBean();
        mPermissionCenter.verifyStoragePermissions(this);

        initBLU();
    }

    private void initBLU() {
        /* 启动蓝牙 */
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (null != adapter) {
            if (!adapter.isEnabled()) {
                if (!adapter.enable()) {
                    finish();
                    return;
                }
            }
        }
    }

    private void setScreenInfo() {
        UIHelp.setWidgetRefresh(this, mDragLayoutFl, mPrintParamsBean);
    }

    private void initDragView(int width, int height){
        mPrintParamsBean.setPrintWidth((int) UIHelp.mmToPointWidth(width));
        mPrintParamsBean.setWidgetWidth(mWidgetWidth);
        mPrintParamsBean.setCurrentWidth(width);

        mPrintParamsBean.setPrintHeight((int) UIHelp.mmToPointHeight(height));
        mPrintParamsBean.setWidgetHeight(mWidgetHeight);
        mPrintParamsBean.setCurrentHeight(height);
        setScreenInfo();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.main_container_main_alert://点击创建标签
//            case R.id.main_func_newlabel://点击创建标签
//            case R.id.main_drag_rl://drawView
//                showMain();
//                break;
//            case R.id.title_opticon://设置
//                LogUtils.d(TAG, "--mprinter--设置" + mPrinterManager);
//                startActivity(new Intent(this, SettingPrinterActivity.class));
//                break;
//
//            case R.id.vg_printer_name://蓝牙配对
//                startActivityForResult(new Intent(this, SettingBTActivity.class), 0);
//                break;
        }
    }

    public void showPrint(View view) {
        if (!StartZPService.getInstance().getPrinterManager().workThread.isConnected()) {
//            ToastUtil.showShortToast(this, getString(R.string.printer_not_connected));
            return;
        }
        currentPrint();
    }

    private void showMain() {
        mContainerLl.setVisibility(View.VISIBLE);
//        findViewById(R.id.main_container_main_alert).setVisibility(View.GONE);
        //edit->720;Height=640;DisplayWidth=720;DisplayHeight=1280
        mDragView.post(new Runnable() {
            @Override
            public void run() {
                mWidgetWidth = mDragView.getWidth();
                mWidgetHeight = mDragView.getHeight();
            }
        });
        mDragView.clearView();

        // new
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("isSet", false);
        mPrintParamsBean.setBackModelPath("");
        intent.putExtra("printParams", mPrintParamsBean);
        startActivityForResult(intent, 1);
        initDragView(40,30);
    }

    public void currentPrint(){
        StartZPService.getInstance().getPrinterManager().workThread.MyPrinter(mDragView, mPrintParamsBean);

    }
}
