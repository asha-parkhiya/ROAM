package com.sparkle.roam.Print.view.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.printlibrary.utils.LogUtils;
import com.sparkle.roam.Print.base.AbsBaseActivity;
import com.sparkle.roam.Print.bean.BluetoothInfoBean;
import com.sparkle.roam.Print.service.Global;
import com.sparkle.roam.Print.service.StartZPService;
import com.sparkle.roam.Print.utils.ToastUtil;
import com.sparkle.roam.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.view.View.VISIBLE;

public class SettingBTActivity extends AbsBaseActivity implements AdapterView.OnItemClickListener{

    private static final String TAG = "SettingBTActivity";

    private List<BluetoothInfoBean> mInfoBeanList = new ArrayList<>();

    private ProgressBar mLoadingPb = null;
    private RadioButton mLabelRb = null;
    private RadioButton mInsertRb = null;
    private View mPageBar1 = null;
    private View mPageBar2 = null;
    private ListView mSearchLv = null;
    private SearchAdapter mAdapter = null;

    public static final String ICON = "ICON";
    public static final String PRINTERNAME = "PRINTERNAME";
    public static final String PRINTERMAC = "PRINTERMAC";
    private static List<Map<String, Object>> boundedPrinters = null;

    private ListView mPairedLv = null;
    private BroadcastReceiver broadcastReceiver = null;
    private IntentFilter intentFilter = null;
    private static Handler mHandler = null;
    private BluetoothInfoBean mInfoBean = null;
    private static String sColorSelect = "#23b4f3";
    private static String sColorUnSelect = "#444444";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_setting_bt);

        init();
    }

    @Override
    public void initView() {

        mHandler = new MHandler(this);
        StartZPService.getInstance().getPrinterManager().addHandler(mHandler);

//        mLabelRb = (RadioButton) findViewById(R.id.bt_radio_label);
//        mInsertRb = (RadioButton) findViewById(R.id.bt_radio_insert);
//        mPageBar1 = findViewById(R.id.bt_bar1);
//        mPageBar2 = findViewById(R.id.bt_bar2);
        mLabelRb.setOnClickListener(tabListener);
        mInsertRb.setOnClickListener(tabListener);

        initTitle();
        initAdapter();
        initBLU();
    }

    View.OnClickListener tabListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = view.getId();
            mLabelRb.setTextColor(Color.parseColor(sColorUnSelect));
            mInsertRb.setTextColor(Color.parseColor(sColorUnSelect));

            mLabelRb.setChecked(false);
            mInsertRb.setChecked(false);

            mPageBar1.setVisibility(View.INVISIBLE);
            mPageBar2.setVisibility(View.INVISIBLE);

//            if (position == R.id.bt_radio_label) {
//                mLabelRb.setTextColor(Color.parseColor(sColorSelect));
//                mLabelRb.setChecked(true);
//                mPageBar1.setVisibility(VISIBLE);
//
//                mSearchLv.setVisibility(VISIBLE);
//                mPairedLv.setVisibility(View.GONE);
//
//            } else if (position == R.id.bt_radio_insert) {
//                mPageBar2.setVisibility(VISIBLE);
//                mInsertRb.setTextColor(Color.parseColor(sColorSelect));
//                mInsertRb.setChecked(true);
//
//                mSearchLv.setVisibility(View.GONE);
//                mPairedLv.setVisibility(VISIBLE);
//            }
        }
    };

    private void initTitle() {
//        findViewById(R.id.title_main_left).setOnClickListener(this);
//        TextView titleOptText = (TextView) findViewById(R.id.title_opt_text);
//        titleOptText.setVisibility(View.VISIBLE);
//        titleOptText.setOnClickListener(this);
//        titleOptText.setText(R.string.str_search);
//        TextView nameTv = (TextView) findViewById(R.id.title_main_name);
//        mLoadingPb = (ProgressBar) findViewById(R.id.pb_loading);
//        nameTv.setText(R.string.msg_btlist_str);
    }

    private void initBLU() {
        //mPrintUtil.searchBluetooth(this);
        searchBlutooth();
    }

    private void initAdapter() {
//        mSearchLv = (ListView) findViewById(R.id.search_lv);
//        mPairedLv = (ListView) findViewById(R.id.paired_lv);
//        mSearchLv.setVisibility(VISIBLE);
//        mPairedLv.setVisibility(View.GONE);
//
//        boundedPrinters = getBoundedPrinters();
//        mPairedLv.setAdapter(new SimpleAdapter(this, boundedPrinters,
//                R.layout.list_item_printernameandmac, new String[] { ICON,
//                PRINTERNAME, PRINTERMAC }, new int[] {
//                R.id.btListItemPrinterIcon, R.id.tvListItemPrinterName,
//                R.id.tvListItemPrinterMac }));

        mPairedLv.setOnItemClickListener(this);

        mAdapter = new SearchAdapter();
        mSearchLv.setAdapter(mAdapter);
        mSearchLv.setOnItemClickListener(mOnItemClickListener);
    }

    private List<Map<String, Object>> getBoundedPrinters() {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            return list;
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a
                // ListView
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(ICON, android.R.drawable.stat_sys_data_bluetooth);
                // Toast.makeText(this,
                // ""+device.getBluetoothClass().getMajorDeviceClass(),
                // Toast.LENGTH_LONG).show();
                map.put(PRINTERNAME, device.getName());
                map.put(PRINTERMAC, device.getAddress());
                list.add(map);
            }
        }
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        final String address = (String) boundedPrinters.get(position).get(PRINTERMAC);
        String addname = (String) boundedPrinters.get(position).get(PRINTERNAME);

            mInfoBean = new BluetoothInfoBean();
            mInfoBean.setAddress(address);
            mInfoBean.setName(addname);
            //ToastUtil.showShortToast(SettingBTActivity.this, "Connecting...");
            startBtConnect();

    }

    @Override
    public void initData() {
        initBroadcast();
    }

    private void initBroadcast() {
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String action = intent.getAction();
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice device = intent
                                .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (device != null) {
                            final String address = device.getAddress();
                            String name = device.getName();
                            if (name == null) {
                                name = "Unnamed";
                            }
                            else if (name.equals(address)) {
                                name = "BlueTooth";
                            }

                            BluetoothInfoBean bean = new BluetoothInfoBean();
                            bean.setAddress(address);
                            bean.setName(name);

                            for (int i = 0; i < mInfoBeanList.size(); ++i) {
                                BluetoothInfoBean bluetoothInfoBean = mInfoBeanList.get(i);
                                if (address.equals(bluetoothInfoBean.getAddress())) {
                                    return;
                                }
                            }
                            showLoading();
                            mInfoBeanList.add(bean);
                            mAdapter.notifyDataSetChanged();
                        }

                    } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                        showLoading();
                    } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        mLoadingPb.post(new Runnable() {
                            @Override
                            public void run() {
                                mLoadingPb.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }

        };
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    class MHandler extends Handler {

        WeakReference<SettingBTActivity> mActivity;

        MHandler(SettingBTActivity activity) {
            mActivity = new WeakReference<SettingBTActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**
                 * DrawerService 的 onStartCommand会发送这个消息
                 */
                case Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT: {
                    try {
                        int result = msg.arg1;
                        Bundle bundle = msg.getData();
                        if (bundle == null)  return ;
                        BluetoothInfoBean infoBean = (BluetoothInfoBean) bundle.getSerializable("infoBeans");
                        if (infoBean == null)  return ;
                        //LogUtils.v(TAG, "Connect Result: " + result);
                        if (1 == result) {
                            finish();
                        } else {  // 恢复默认设置
//                            TextView nameTv = (TextView) findViewById(R.id.title_main_name);
//                            nameTv.setText(R.string.msg_btlist_str);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

    }

    private void searchBlutooth(){
        mInfoBeanList.clear();
        mAdapter.notifyDataSetChanged();
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (null == adapter) {
            finish();
        }
        if (!adapter.isEnabled()) {
            if (adapter.enable()) {
                int timeout = 0;
                while (!adapter.isEnabled()) {
                    try {
                        Thread.sleep(100);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (++timeout > 20){    // 超时退出
                        break;
                    }
                }
                LogUtils.v(TAG, "Enable BluetoothAdapter");
            } else {
                finish();
            }
        }
        adapter.cancelDiscovery();
        //PrintUtil.WaitMs(10);
        try {
            Thread.sleep(10);
        }catch (Exception e){
            e.printStackTrace();
        }
        adapter.startDiscovery();
    }

    private void startBtConnect(){
//        TextView nameTv = (TextView) findViewById(R.id.title_main_name);
//        nameTv.setText(R.string.str_connecting);

        if (mHandler == null) return ;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                StartZPService.getInstance().getPrinterManager().workThread.disconnectBt();

                // 只有没有连接且没有在用，这个才能改变状态
                if (StartZPService.getInstance().getPrinterManager().workThread.connectBt(mInfoBean)) {
                    ToastUtil.showShortToast(SettingBTActivity.this, "Connecting " + mInfoBean.getName() + "...");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mInfoBeanList != null){
            mInfoBeanList.clear();
        }
        mInfoBean = null;

        StartZPService.getInstance().getPrinterManager().delHandler(mHandler);
        mHandler = null;
        uninitBroadcast();
    }

    private void uninitBroadcast() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    private class SearchAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mInfoBeanList.size();
        }

        @Override
        public BluetoothInfoBean getItem(int position) {
            return mInfoBeanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            BluetoothInfoBean infoBean = getItem(position);

//            if (view == null){
//                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_blutooth, null);
//            }
//            TextView contentTv = com.wisdom.tian.utils.ViewHolder.get(view, R.id.item_content_tv);
//            contentTv.setText(infoBean.getName());
//            TextView addressTv = com.wisdom.tian.utils.ViewHolder.get(view, R.id.item_address_tv);
//            addressTv.setText(infoBean.getAddress());
            return view;
        }
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(mAdapter != null){
                mInfoBean = mAdapter.getItem(i);
                if (mInfoBean != null) {
                    startBtConnect();
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.title_main_left:
//                finish();
//                break;
//            case R.id.title_opt_text://搜索蓝牙
//                searchBlutooth();
//                break;
        }
    }

    private void showLoading() {
        if (mLoadingPb.getVisibility() == View.VISIBLE){
            return ;
        }
        mLoadingPb.post(new Runnable() {
            @Override
            public void run() {
                mLoadingPb.setVisibility(View.VISIBLE);
            }
        });
    }

}
