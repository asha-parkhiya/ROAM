package com.sparkle.roam.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.ClearCacheException;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.sparkle.roam.AWS.ClientFactory;
import com.sparkle.roam.ContentProvider.ToDoListDBAdapter;
import com.sparkle.roam.Fragments.GenerateCodeDialog;
import com.sparkle.roam.Fragments.PayAccountDetailFragment;
import com.sparkle.roam.Fragments.PayAccountFragment;
import com.sparkle.roam.Fragments.PayEventHistoryDetailFragment;
import com.sparkle.roam.Fragments.PayEventHistoryFragment;
import com.sparkle.roam.Fragments.AddPaymentDialog;
import com.sparkle.roam.Fragments.UserListFragment;
import com.sparkle.roam.Fragments.UserPayAccountFragment;
import com.sparkle.roam.LocalDatabase.DatabaseHelper;
import com.sparkle.roam.LoginData;
import com.sparkle.roam.LoginRoomDatabase;
import com.sparkle.roam.Model.PayEventSync.PayEventData;
import com.sparkle.roam.Model.SyncData.PayAccountData;
import com.sparkle.roam.Model.SyncProductData.ProductData;
import com.sparkle.roam.Model.SyncUserData.UserData;
import com.sparkle.roam.MyApplication;
import com.sparkle.roam.R;
import com.sparkle.roam.Utils.ConstantMethod;
import com.sparkle.roam.Utils.Constants;
import com.sparkle.roam.Utils.MyPref;
import com.sparkle.roam.View.CodeErrorDialog;
import com.sparkle.roam.View.LogoutDialog;
import com.sparkle.roam.View.MessagePrintDialog;
import com.sparkle.roam.WebServices.WebCallType;
import com.sparkle.roam.WebServices.WebRequest;
import com.sparkle.roam.WebServices.WebResponseListener;
import com.sparkle.roam.events.BooleanData;
import com.sparkle.roam.services.APPClientService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static com.sparkle.roam.Utils.Constants.SYNC_DATA_URL;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LogoutDialog.OnOkClick, CodeErrorDialog.OnLogout, View.OnClickListener, PayAccountFragment.OnNavigation, WebResponseListener, MessagePrintDialog.OnOkClose {

    MyPref myPref;
    private AWSAppSyncClient mAWSAppSyncClient;
    StringBuilder stringBuilder;
    DrawerLayout drawer;
    TextView tv_synctiime, tv_logout,tv_sync, tv_syncStatus;
    ImageView im_logo1;
    ActionBarDrawerToggle toggle;
    ImageView btn_add;
    String token = null;
    private ProgressDialog processDialog;
    LoginData loginDataclass;
    LoginRoomDatabase mLoginDatabase;

    String[] permissionsRequired = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;

    private WebRequest webRequest;
    private DatabaseHelper mdbhelper;

    Gson gson;

    int payAccOffset = 0;
    int payEveOffset = 0;
    int userOffset = 0;
    int productOffset = 0;

    boolean payAcccall = false;
    boolean payEvecall = false;
    boolean usercall = false;
    boolean productcall = false;
    boolean onlogoutclick = false;

    boolean done = true;

    String lastSyncDatePayAccount;
    String lastSyncDatePayEvent;
    String lastSyncDateUser;
    String lastSyncDateProductItem;

    String android_id,username;

    APPClientService aPPClientService;
    boolean mBound1 = false;
    boolean display = false;
    int count = 0;
    private BluetoothAdapter mBTAdapter;

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    private ServiceConnection mConnection1 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            APPClientService.LocalBinder binder = (APPClientService.LocalBinder) service;
            aPPClientService = binder.getService();
            mBound1 = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound1 = false;
        }
    };

    public void forceCrash() {
        throw new RuntimeException("This is a crash");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        forceCrash();
        setContentView(R.layout.activity_home);
        myPref = new MyPref(this);
        processDialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        mLoginDatabase = ((MyApplication) getApplication()).getDatabase();

        stringBuilder = new StringBuilder();
        myPref.setPref(Constants.IS_FIRSTTIMERUN, false);
//        myPref.setPref(Constants.ALL_TIME_HAND, false);

        webRequest = new WebRequest(this);
        mdbhelper = new DatabaseHelper(this);

        gson = new Gson();

        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "Functionality Comming soon...", Toast.LENGTH_SHORT).show();
            }
        });

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        goToPermissionCheck();
//        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio
//        discover();
//        bluetoothOn();

    }
    private void bluetoothOn(){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
    private void discover(){
        // Check if the device is already discovering
        if(mBTAdapter.isDiscovering()){
            mBTAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(),"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) {
//                mBTArrayAdapter.clear(); // clear items
                mBTAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("BluetoothItem");
                intentFilter.addAction("BluetoothStatus");
                intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                registerReceiver(blReceiver, intentFilter);
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }
    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.EXTRA_DEVICE.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                System.out.println("----------------------"+device.getName());
                if (device.getName().equals("Galaxy J2")){
                    pairDevice(device);
                }
            }
        }
    };

    private void pairDevice(BluetoothDevice device) {
        try {
            Log.d("pairDevice()", "Start Pairing...");
            Method m = device.getClass().getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
            Log.d("pairDevice()", "Pairing finished.");
        } catch (Exception e) {
        }
    }

    public void startMainActivity(){

        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        username = myPref.getPref(Constants.USER_NAME, "");

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        tv_synctiime = navigationView.getHeaderView(0).findViewById(R.id.tv_synctiime);
        if (!myPref.getPref(Constants.DB_LAST_SYNC, "").equals("")){
            tv_synctiime.setText(myPref.getPref(Constants.DB_LAST_SYNC, ""));
        }else {
            Date date = new Date();
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            date = calendar.getTime();
            String data = new SimpleDateFormat("E dd MMM yyyy HH:mm:ss z").format(date);
            String maketime = "(Last Sync:" + data + ")";
            tv_synctiime.setText(maketime);
            myPref.setPref(Constants.DB_LAST_SYNC, maketime);
        }
        tv_sync = navigationView.getHeaderView(0).findViewById(R.id.tv_sync);
        im_logo1 = navigationView.getHeaderView(0).findViewById(R.id.im_logo1);
        tv_sync.setOnClickListener(this);
        tv_syncStatus = navigationView.getHeaderView(0).findViewById(R.id.tv_syncStatus);
        tv_syncStatus.setOnClickListener(this);
        tv_logout = navigationView.getHeaderView(0).findViewById(R.id.tv_logout);
        tv_logout.setOnClickListener(this);
        setNewPayAccountFragment(this::onNavigationClick);
        boolean iscoonected = isNetworkConnected();
        if (iscoonected) {
            tv_syncStatus.setText("All records up to date.");
        }else {
            tv_syncStatus.setText("Sync to OVES Hub when data link is available.");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                aPPClientService.checkmutation();
                System.out.println("---------------------calll");
            }
        },2000);

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean iscoonected2 = isNetworkConnected();
                if (iscoonected2) {
                    LogoutDialog logoutDialog = new LogoutDialog(HomeActivity.this,HomeActivity.this::onOkClick);
                    logoutDialog.show();
//                    syncAPICall();
//                    onlogoutclick = true;
                }else{
                    Toast.makeText(HomeActivity.this, "To logout and sync your states, you must be connected to the internet.", Toast.LENGTH_LONG).show();
                }
            }
        });

        tv_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean iscoonected = isNetworkConnected();
                if (iscoonected) {
                    syncAPICall();
                }else{
                    Toast.makeText(HomeActivity.this, "You must be connected to the internet.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_synctiime:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(BooleanData update) {
        boolean mutation = update.data;
        if (mutation){
            clearanimation();
            tv_sync.setText("Sync");
            tv_sync.setTextColor(getResources().getColor(R.color.white));
            tv_sync.setEnabled(true);
            im_logo1.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_refresh1));
            tv_logout.setEnabled(true);
            tv_logout.setTextColor(getResources().getColor(R.color.white));
        }else {
            im_logo1.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_refresh));
            tv_sync.setTextColor(getResources().getColor(R.color.dark_gray));
            tv_sync.setEnabled(false);
            tv_syncStatus.setText("Sync to OVES Hub when data link is available.");
            tv_logout.setTextColor(getResources().getColor(R.color.dark_gray));
            tv_logout.setEnabled(false);
            boolean iscoonected = isNetworkConnected();
            if (iscoonected) {
                startanimation();
                tv_sync.setText("Syncing...");
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Date date = new Date();
//                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
//                        date = calendar.getTime();
//                        String data = new SimpleDateFormat("E dd MMM yyyy HH:mm:ss z").format(date);
//                        String maketime = "(Last Sync:" + data + ")";
//                        myPref.setPref(Constants.DB_LAST_SYNC, maketime);
//                        setSyncTime(maketime);
//                    }
//                },4000);

//                tv_syncStatus.setText("Wait until data send to server.");
            }
//            else{
//                tv_syncStatus.setText("Sync to OVES Hub when data link is available.");
//            }
        }
    }

    public void startanimation(){
        Animation animation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(-1);
        animation.setDuration(1000);
        im_logo1.clearAnimation();
        im_logo1.setAnimation(animation);
    }

    public void clearanimation(){
        im_logo1.clearAnimation();
    }

    public void setLogout() {
        onlogoutclick = true;

        MessagePrintDialog messagePrintDialog = new MessagePrintDialog(this, "Stay connected while syncing with OVES Roam Hub. You credits and user data can be lost if you do not sync with the Hub completely.", this);
        messagePrintDialog.show();
    }

    public void syncAPICall(){
        System.out.println("----------------------sync api....");
            showProcess();
            tv_syncStatus.setText("Syncing in Progress.");
            payAccOffset = 0;payEveOffset = 0;userOffset = 0;productOffset = 0;
            payAcccall = false;payEvecall = false;usercall = false;productcall = false;done = true;

            Cursor res = mdbhelper.getLastSyncData();
            while (res.moveToNext()){
                lastSyncDatePayAccount = res.getString(res.getColumnIndex("payAccount"));
                lastSyncDatePayEvent = res.getString(res.getColumnIndex("payEvent"));
                lastSyncDateUser = res.getString(res.getColumnIndex("user"));
                lastSyncDateProductItem = res.getString(res.getColumnIndex("productItem"));
                System.out.println( "-------acc-----"+res.getString(res.getColumnIndex("payAccount")));
                System.out.println( "-------eve-----"+res.getString(res.getColumnIndex("payEvent")));
                System.out.println( "-------user-----"+res.getString(res.getColumnIndex("user")));
                System.out.println( "-------pro-----"+res.getString(res.getColumnIndex("productItem")));
            }
            mdbhelper.deleteSyncPayEvent(String.valueOf(0));

            JSONObject requestJson = new JSONObject();
            String refreshToken = myPref.getPref(Constants.REFRESH_TOKEN,"");
            String username = myPref.getPref(Constants.USERNAME,"");
            try {
                requestJson.put("query", "mutation renewToken{ RenewToken(refreshToken:\""+refreshToken+"\",username:\""+username+"\"){ message}}");
                webRequest.POST_METHOD(Constants.REFRESH_TOKEN_URL, requestJson, null, HomeActivity.this, WebCallType.GET_ACCESS_TOKEN, false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//        drawer.closeDrawers();
//        Date date = new Date();
//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
//        date = calendar.getTime();
//        String data = new SimpleDateFormat("E dd MMM yyyy HH:mm:ss z").format(date);
//        String maketime = "(Last Sync:" + data + ")";
//        myPref.setPref(Constants.DB_LAST_SYNC, maketime);
//        setSyncTime(maketime);
    }

    @Override
    public void OnLogout(boolean msg) {
        if (msg) {
            onBackPressed();
        } else
            setLogout();
    }

    @Override
    public void onOkClick() {
        setLogout();
    }

    public void setSyncTime(String syncTime) {
        tv_synctiime.setText(syncTime);
        tv_syncStatus.setText("All records up to date.");
    }

    @Override
    public void onResponse(Object response, WebCallType webCallType) throws JSONException {
        if (response != null) {

            if (response.toString().equals("{\"message\":\"User does not Exists\"}")){
                cancelProcess();
                tv_syncStatus.setText("Sync not completed.");
                count++;
                if(count == 5 || count == 9 || count == 13 || count == 17 || count == 21){display = false;}
                if (!display){
                    CodeErrorDialog codeErrorDialog = new CodeErrorDialog(this, "User does not Exists.", this);
                    codeErrorDialog.show();
                    display = true;
                }

            }
            System.out.println("-----------response------------"+response.toString());
            switch (webCallType){
                case LOGOUT_DATA:
                    System.out.println("----------delete response---------------"+response);
                    myPref.clearPref();
                    mAWSAppSyncClient = ClientFactory.getInstance(this);
                    try {
                        mAWSAppSyncClient.clearCaches();
                    } catch (ClearCacheException e) {
                        e.printStackTrace();
                    }
                    DatabaseHelper databaseHelper = new DatabaseHelper(this);
                    databaseHelper.deleteAlltable();
                    deleteDatabase("b2b_dev.db");
                    ToDoListDBAdapter toDoListDBAdapter =  ToDoListDBAdapter.getToDoListDBAdapterInstance(getApplicationContext());
                    toDoListDBAdapter.delete();
                    Intent i1 = new Intent(this, LoginActivity.class);
                    startActivity(i1);
                    finish();
//                    MessagePrintDialog messagePrintDialog = new MessagePrintDialog(this, "Stay connected while syncing with OVES Roam Hub. You credits and user data can be lost if you do not sync with the Hub completely.", this);
//                    messagePrintDialog.show();
                    break;
                case GET_ACCESS_TOKEN:
                    JSONObject jsonObject = (JSONObject) response;

                    JSONObject d = jsonObject.getJSONObject("data");
                    JSONObject RenewToken = d.getJSONObject("RenewToken");
                    String access_token = RenewToken.getString("message");
                    System.out.println("------------------------"+access_token);
                    myPref.setPref(Constants.OFFLINE_TOKEN,access_token);
                    token = access_token;



                    webRequest.GET_METHOD(SYNC_DATA_URL+"0/10/"+username+"/"+android_id+"/"+lastSyncDatePayAccount, HomeActivity.this,  WebCallType.GET_PAYACCOUNT_DATA, false, token);
                    webRequest.GET_METHOD(Constants.PAYEVENT_SYNC_DATA_URL+"0/10/"+username+"/"+android_id+"/"+lastSyncDatePayEvent, HomeActivity.this,  WebCallType.GET_PAYEVENT_DATA, false, token);
                    webRequest.GET_METHOD(Constants.USER_SYNC_DATA_URL+"0/10/"+username+"/"+android_id+"/"+lastSyncDateUser, HomeActivity.this,  WebCallType.GET_USER_DATA, false, token);
                    webRequest.GET_METHOD(Constants.PRODUCT_SYNC_DATA_URL+"0/10/"+username+"/"+android_id+"/"+lastSyncDateProductItem, HomeActivity.this,  WebCallType.GET_PRODUCT_DATA, false, token);

                    System.out.println("---------------------------"+SYNC_DATA_URL+"0/10/"+username+"/"+android_id+"/"+lastSyncDatePayAccount);
                    break;

                case GET_PAYACCOUNT_DATA:

                    try {
                        JSONObject json =  new JSONObject(String.valueOf(response));
                        System.out.println(json);
                        String data = json.getString("data");
                        System.out.println("--------data---pay acc------"+data);
                        int total = json.getInt("total");
                        int limit = json.getInt("limit");
                        lastSyncDatePayAccount = json.getString("lastSyncDate");

                        int loopcount = total/limit;

                        JSONArray jsonArray = new JSONArray(data);

                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            PayAccountData payAccountData = gson.fromJson(jsonObject1.toString(),PayAccountData.class);
                            mdbhelper.insertPayAccountData(String.valueOf(payAccountData.getPayAccountID()),String.valueOf(payAccountData.getStartDate()),String.valueOf(payAccountData.getDepositDays()),String.valueOf(payAccountData.getPayoffAmt()),String.valueOf(payAccountData.getMinPayDays()),
                                    String.valueOf(payAccountData.getMaxPayDays()),String.valueOf(payAccountData.getSchPayDays()),String.valueOf(payAccountData.getUserID()),String.valueOf(payAccountData.getDistributorID()),String.valueOf(payAccountData.getAssignedItemsID()),String.valueOf(payAccountData.getAgentID()),
                                    String.valueOf(payAccountData.getAgentAssignmentStatus()),String.valueOf(payAccountData.getAgentAssignment()),String.valueOf(payAccountData.getInitialCreditDays()),String.valueOf(payAccountData.getReceivedPayAmt()),String.valueOf(payAccountData.getDurationDays()),
                                    String.valueOf(payAccountData.getCreditDaysIssued()),String.valueOf(payAccountData.getPayDaysReceived()));
                        }

                        if (loopcount == 0){
                            payAcccall = true;
                        }else {
                            if (!payAcccall){
                                for (int i = 0; i < loopcount; i++){
                                    payAccOffset = payAccOffset + limit;
                                    webRequest.GET_METHOD(SYNC_DATA_URL + payAccOffset + "/10/"+username+"/"+android_id+"/"+lastSyncDatePayAccount,this,  WebCallType.GET_PAYACCOUNT_DATA, false, token);
                                    if (loopcount == i+1){
                                        payAcccall = true;
                                    }
                                }
                            }
                        }

                        if (productcall && usercall && payAcccall && payEvecall){

                            updatDatabase();
                            System.out.println("-------------------hello here ---------acc---==========");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                case GET_PAYEVENT_DATA:

                    try {
                        JSONObject json = new JSONObject(String.valueOf(response));
                        System.out.println(json);
                        String data = json.getString("data");
                        System.out.println("--------data----pay event-----"+data);
                        int total = json.getInt("total");
                        int limit = json.getInt("limit");
                        lastSyncDatePayEvent = json.getString("lastSyncDate");

                        int loopcount = total/limit;
                        JSONArray jsonArray = new JSONArray(data);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            PayEventData payEventData = gson.fromJson(jsonObject1.toString(), PayEventData.class);
                            mdbhelper.insertPayEventData(String.valueOf(payEventData.getPayEventID()),String.valueOf(payEventData.getPayEventDate()),String.valueOf(payEventData.getPayDays()),String.valueOf(payEventData.getPayRecordAmt()),
                                    String.valueOf(payEventData.getPayRecordNotes()),String.valueOf(payEventData.getPayAccountID()),String.valueOf(payEventData.getEventType()),String.valueOf(payEventData.getCodeDays()),String.valueOf(payEventData.getCodeIssued()),String.valueOf(payEventData.getCodehashTop()),"");
                        }
                        if (loopcount == 0){
                            payEvecall = true;
                        }else {
                            if (!payEvecall){
                                for (int i = 0; i < loopcount; i++){
                                    payEveOffset = payEveOffset + limit;
                                    webRequest.GET_METHOD(Constants.PAYEVENT_SYNC_DATA_URL + payEveOffset + "/10/"+username+"/"+android_id+"/"+lastSyncDatePayEvent,this,  WebCallType.GET_PAYEVENT_DATA, false, token);
                                    if (loopcount == i+1){
                                        payEvecall = true;
                                    }
                                }
                            }
                        }

                        if (productcall && usercall && payAcccall && payEvecall){

                            updatDatabase();
                            System.out.println("-------------------hello here ---------event---==========");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case GET_USER_DATA:
                    try {
                        JSONObject json = new JSONObject(String.valueOf(response));
                        System.out.println(json);
                        String data = json.getString("data");
                        System.out.println("--------data--user-------"+data);
                        int total = json.getInt("total");
                        int limit = json.getInt("limit");
                        lastSyncDateUser = json.getString("lastSyncDate");

                        int loopcount = total/limit;
                        JSONArray jsonArray = new JSONArray(data);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            UserData userData = gson.fromJson(jsonObject1.toString(), UserData.class);
                            mdbhelper.insertUserData(String.valueOf(userData.getUserID()),String.valueOf(userData.getDistributorID()),String.valueOf(userData.getUserCode()),String.valueOf(userData.getAgentID()),String.valueOf(userData.getLastName()),String.valueOf(userData.getFirstName()),
                                    String.valueOf(userData.getPhoneNumber()),String.valueOf(userData.getEmail()),String.valueOf(userData.getLocationGPS()),String.valueOf(userData.getAddress1()),String.valueOf(userData.getAddress2()),String.valueOf(userData.getCity()),String.valueOf(userData.getState()),
                                    String.valueOf(userData.getCountry()),String.valueOf(userData.getPostCode()));
                        }


                        if (loopcount == 0){
                            usercall = true;
                        }else {
                            if (!usercall){
                                for (int i = 0; i < loopcount; i++){
                                    userOffset = userOffset + limit;
                                    webRequest.GET_METHOD(Constants.USER_SYNC_DATA_URL + userOffset + "/10/"+username+"/"+android_id+"/"+lastSyncDateUser,this,  WebCallType.GET_USER_DATA, false, token);
                                    if (loopcount == i+1){
                                        usercall = true;
                                    }
                                }
                            }
                        }

                        if (productcall && usercall && payAcccall && payEvecall){

                            updatDatabase();
                            System.out.println("-------------------hello here ---------user---==========");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case GET_PRODUCT_DATA:
                    try {
                        JSONObject json = new JSONObject(String.valueOf(response));
                        System.out.println(json);
                        String data = json.getString("data");
                        System.out.println("--------data--product-------"+data);
                        int total = json.getInt("total");
                        int limit = json.getInt("limit");
                        lastSyncDateProductItem = json.getString("lastSyncDate");

                        int loopcount = total/limit;
                        JSONArray jsonArray = new JSONArray(data);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            ProductData productData = gson.fromJson(jsonObject1.toString(), ProductData.class);
                            mdbhelper.insertProductItemData(String.valueOf(productData.getProductItemID()),String.valueOf(productData.getProductModelID()),String.valueOf(productData.getProductBatchID()),String.valueOf(productData.getProductItemOEMSN()),
                                    String.valueOf(productData.getProductItemPAYGSN()),String.valueOf(productData.getLifeCycleStatus()),String.valueOf(productData.getFirmwareVersion()),String.valueOf(productData.getAssignedItemsID()));
                        }
                        if (loopcount == 0){
                            productcall = true;
                        }else {
                            if (!productcall){
                                for (int i = 0; i < loopcount; i++){
                                    productOffset = productOffset + limit;
                                    webRequest.GET_METHOD(Constants.PRODUCT_SYNC_DATA_URL + productOffset + "/10/"+username+"/"+android_id+"/"+lastSyncDateProductItem,this,  WebCallType.GET_PRODUCT_DATA, false, token);
                                    if (loopcount == i+1){
                                        productcall = true;
                                    }
                                }
                            }
                        }

                        if (productcall && usercall && payAcccall && payEvecall){

                            updatDatabase();
                            System.out.println("-------------------hello here ---------product---==========");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    public void updatDatabase(){
        if (done){
            Date date = new Date();
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            date = calendar.getTime();
            String data = new SimpleDateFormat("E dd MMM yyyy HH:mm:ss z").format(date);
            String maketime = "(Last Sync:" + data + ")";
            myPref.setPref(Constants.DB_LAST_SYNC, maketime);
            setSyncTime(maketime);
            done = false;
            mdbhelper.updateLastSyncData(String.valueOf(1),lastSyncDatePayAccount,lastSyncDatePayEvent,lastSyncDateUser,lastSyncDateProductItem);
            drawer.closeDrawers();
            cancelProcess();
            tv_syncStatus.setText("All records up to date.");
            if (onlogoutclick){
                MessagePrintDialog messagePrintDialog = new MessagePrintDialog(this, "You have completed sync with server and you can logout safely. You can also login again on this phone, or another phone.", this);
                messagePrintDialog.show();
                System.out.println("--------------onlogout");
            }else {
                setNewPayAccountFragment(this::onNavigationClick);
                System.out.println("--------------only sync");
            }
//            open();
        }
    }

    @Override
    public void OnOkClose(boolean msg) {
        if (msg){
            JSONObject requestJson = new JSONObject();
            try {
                requestJson.put("MobileID", myPref.getPref(Constants.DEVICE_ID,""));
                requestJson.put("username", myPref.getPref(Constants.USER_NAME,""));
//            showProcess();
                webRequest.POST_METHOD(Constants.LOGOUT_URL, requestJson, null, HomeActivity.this, WebCallType.LOGOUT_DATA, false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            myPref.clearPref();
//            mAWSAppSyncClient = ClientFactory.getInstance(this);
//            try {
//                mAWSAppSyncClient.clearCaches();
//            } catch (ClearCacheException e) {
//                e.printStackTrace();
//            }
//            DatabaseHelper databaseHelper = new DatabaseHelper(this);
//            databaseHelper.deleteAlltable();
//            deleteDatabase("b2b_dev.db");
//            ToDoListDBAdapter toDoListDBAdapter =  ToDoListDBAdapter.getToDoListDBAdapterInstance(getApplicationContext());
//            toDoListDBAdapter.delete();
////            deleteDatabase("ppid_code.db");
//            Intent i = new Intent(this, LoginActivity.class);
//            startActivity(i);
//            finish();
            System.out.println("------------------logout done");
        }else {
//            if (tv_sync.isEnabled()){
//                System.out.println("----------------sync enable----------");
//            }else {
//                System.out.println("----------------sync disable----------");
//            }
            System.out.println("------------------false----");
            syncAPICall();
        }
    }

    @Override
    public void onError(String message) {
        System.out.println("----------delete error---------------"+message);
        cancelProcess();
        drawer.closeDrawers();
        Toast.makeText(HomeActivity.this, ""+message, Toast.LENGTH_SHORT).show();
    }

    public void setUserListFragment() {
        Bundle bundle = new Bundle();
        UserListFragment userListFragment = UserListFragment.newInstance();
//        frag.show(getSupportFragmentManager(),frag.getTag());
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(userListFragment.getClass().getName());
        if (fragment != null) {
            getSupportFragmentManager().popBackStack(userListFragment.getClass().getName(), 0);
            ((UserListFragment) fragment).updatedata();
        } else {
            ConstantMethod.replaceFragment(getSupportFragmentManager(), userListFragment, R.id.flContent, true);
        }

    }

    public void setUserPayAccountFragment(String userid) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.USERID, userid);

        UserPayAccountFragment userPayAccountFragment = UserPayAccountFragment.newInstance(userid);
//        frag.show(getSupportFragmentManager(),frag.getTag());
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(userPayAccountFragment.getClass().getName());
        if (fragment != null) {
            getSupportFragmentManager().popBackStack(userPayAccountFragment.getClass().getName(), 0);
            ((UserPayAccountFragment) fragment).UpdateUI();
        } else {
            ConstantMethod.replaceFragment(getSupportFragmentManager(), userPayAccountFragment, R.id.flContent, true);
        }
    }


    public void setNewPayAccountFragment(PayAccountFragment.OnNavigation onNavigation) {
        Bundle bundle = new Bundle();
        PayAccountFragment payAccountFragment = PayAccountFragment.newInstance(onNavigation);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(payAccountFragment.getClass().getName());
        if (fragment != null) {
            getSupportFragmentManager().popBackStack(payAccountFragment.getClass().getName(), 0);
            ((PayAccountFragment) fragment).UpdateUI("");
        } else {
            ConstantMethod.replaceFragment(getSupportFragmentManager(), payAccountFragment, R.id.flContent, true);
        }

    }

    public void setPayAccountDetailFragment(String p_id,int position, String fname, String lname, String productItemOEM_SN,PayAccountDetailFragment.OnButtonClick onButtonClick) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.POSITION, position);
        bundle.putString(Constants.PAYACCID, p_id);
        bundle.putString(Constants.FNAME, fname);
        bundle.putString(Constants.LNAME, lname);
        bundle.putString(Constants.PRODUCTOESN, productItemOEM_SN);
        PayAccountDetailFragment payAccountDetailFragment = PayAccountDetailFragment.newInstance(p_id,position, fname, lname, productItemOEM_SN, onButtonClick);
        getSupportFragmentManager().popBackStack(payAccountDetailFragment.getClass().getName(), 0);
        payAccountDetailFragment.show(getSupportFragmentManager(), payAccountDetailFragment.getTag());
        //        Fragment fragment = getSupportFragmentManager().findFragmentByTag(payAccountDetailFragment.getClass().getName());
//        ((PayAccountDetailFragment) fragment).UpdateUI();
//        if (fragment != null) {
//
//            System.out.println("-------------"+"not null");
//        } else {
//            ConstantMethod.replaceFragment(getSupportFragmentManager(), payAccountDetailFragment, R.id.flContent, true);
//            System.out.println("-------------"+"null");
//        }
    }

    public void setGenerateCodeFragment(String getAgentAssignment, int payAccId, int limit, int position, GenerateCodeDialog.OnIssuecode onIssuecode, String search, boolean isfromHome,String firstname, String lastname, String productItemOESN,String firmwareVersion) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.AGENTASSIGNMENT,getAgentAssignment);
        bundle.putInt(Constants.PAYACCID, payAccId);
        bundle.putInt(Constants.OFFSET, limit);
        bundle.putInt(Constants.POSITION, position);
        bundle.putString(Constants.SEARCH_TEXT, search);
        bundle.putString(Constants.FIRST_NAME, firstname);
        bundle.putString(Constants.LAST_NAME, lastname);
        bundle.putString(Constants.FIRMWAREVERSION, firmwareVersion);
        bundle.putString(Constants.PRODUCTOESN, productItemOESN);
        GenerateCodeDialog frag = GenerateCodeDialog.newInstance(getAgentAssignment, payAccId, limit, position, onIssuecode, search, isfromHome,firstname,lastname,productItemOESN,firmwareVersion);
        getSupportFragmentManager().popBackStack(frag.getClass().getName(), 0);
        frag.show(getSupportFragmentManager(),frag.getTag());
    }

    public void setPayMentDialog(String getAgentAssignment, int payAccId, int limit, int position, AddPaymentDialog.OnIssuecode onIssuecode, String search, boolean isfromHome, String firstname, String lastname, String productItemOESN, String firmwareVersion) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.AGENTASSIGNMENT,getAgentAssignment);
        bundle.putInt(Constants.PAYACCID, payAccId);
        bundle.putInt(Constants.OFFSET, limit);
        bundle.putInt(Constants.POSITION, position);
        bundle.putString(Constants.SEARCH_TEXT, search);
        bundle.putString(Constants.FIRST_NAME, firstname);
        bundle.putString(Constants.LAST_NAME, lastname);
        bundle.putString(Constants.FIRMWAREVERSION, firmwareVersion);
        bundle.putString(Constants.PRODUCTOESN, productItemOESN);
        AddPaymentDialog frag = AddPaymentDialog.newInstance(getAgentAssignment, payAccId, limit, position, onIssuecode, search, isfromHome,firstname,lastname,productItemOESN,firmwareVersion);
        getSupportFragmentManager().popBackStack(frag.getClass().getName(), 0);
        frag.show(getSupportFragmentManager(),frag.getTag());
    }

    public void setNewPayEventHistoryFragment(String p_id,String productItemOESN,String firmwareVersion,int codesize) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PAYACCID, p_id);
        bundle.putString(Constants.PRODUCTOESN, productItemOESN);
        bundle.putString(Constants.FIRMWAREVERSION, firmwareVersion);
        bundle.putInt(Constants.CODESIZE, codesize);
        PayEventHistoryFragment frag = PayEventHistoryFragment.newInstance(p_id, productItemOESN,firmwareVersion,codesize);
//        frag.show(getSupportFragmentManager(),frag.getTag());
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(frag.getClass().getName());
        if (fragment != null) {
            getSupportFragmentManager().popBackStack(frag.getClass().getName(), 0);
            ((PayEventHistoryFragment) fragment).UpdateUI();
        } else {
            ConstantMethod.replaceFragment(getSupportFragmentManager(), frag, R.id.flContent, true);
        }

    }

    public void setPayEventHistoryDetailFragment(String paydays, String payRecordAmt, String payRecordNotes, String eventtype, String codeIssued, String codedays, String codehashtop, String payAccID,String productItemOESN,String firmwareVersion, int codesize) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PAY_DAYS, paydays);
        bundle.putString(Constants.PAY_RECORD_AMT, payRecordAmt);
        bundle.putString(Constants.PAY_RECORD_NOTES, payRecordNotes);
        bundle.putString(Constants.EVENT_TYPE, eventtype);
        bundle.putString(Constants.CODE_ISSUED, codeIssued);
        bundle.putString(Constants.CODE_DAYS, codedays);
        bundle.putString(Constants.CODE_HASHTOP, codehashtop);
        bundle.putString(Constants.PAYACCID, payAccID);
        bundle.putString(Constants.PRODUCTOESN, productItemOESN);
        bundle.putString(Constants.FIRMWAREVERSION, firmwareVersion);
        bundle.putInt(Constants.CODESIZE, codesize);
        PayEventHistoryDetailFragment frag = PayEventHistoryDetailFragment.newInstance(paydays, payRecordAmt, payRecordNotes, eventtype, codeIssued, codedays,codehashtop, payAccID, productItemOESN,firmwareVersion,codesize);
        getSupportFragmentManager().popBackStack(frag.getClass().getName(), 0);
//        frag.show(getSupportFragmentManager(),frag.getTag());
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag(frag.getClass().getName());
//        if (fragment != null) {
//        } else {
//            ConstantMethod.replaceFragment(getSupportFragmentManager(), frag, R.id.flContent, true);
//        }

    }

    private void showProcess() {
        if (processDialog != null) {
//            processDialog = new ProgressDialog(getApplicationContext(),R.style.AppCompatAlertDialogStyle);
            processDialog.show();
            processDialog.setCancelable(false);
            Window window = processDialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
            processDialog.setContentView(R.layout.process_dialog);
        }
    }

    private void cancelProcess() {
        if (processDialog != null) {
            processDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                int index = getSupportFragmentManager().getBackStackEntryCount() - 2;
                FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(index);
                String tag = backEntry.getName();
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment != null) {
                    Class name = fragment.getClass();
                    if (name == PayAccountFragment.class) {
                        setNewPayAccountFragment(this::onNavigationClick);
                    } else if (name == PayAccountDetailFragment.class) {
                        super.onBackPressed();
                    } else if (name == PayEventHistoryFragment.class) {
                        super.onBackPressed();
                    } else if (name == UserListFragment.class) {
                        setUserListFragment();
                    }else if (name == UserPayAccountFragment.class) {
                        super.onBackPressed();
                    }else if (name == PayEventHistoryDetailFragment.class) {
                        super.onBackPressed();
                    } else {
                        super.onBackPressed();
                    }
                } else {
                    finish();
                }
            } else {
                finish();
            }
        }
    }

    @Override
    public void onNavigationClick() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }

    }

    public void updateButtonAdd(boolean isshow) {
        if (!isshow)
            btn_add.setVisibility(View.GONE);
        else
            btn_add.setVisibility(View.VISIBLE);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_newuser) {
            // Handle the camera action
        } else if (id == R.id.nav_newpayacc) {

        } else if (id == R.id.nav_user_list){
//            setUserListFragment();
        } else if (id == R.id.nav_cal) {

        } else if (id == R.id.nav_map) {

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
//        Intent intent = new Intent(this, MqttService.class);
//        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Intent intent1 = new Intent(this, APPClientService.class);
        bindService(intent1, mConnection1, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBound1) {
            unbindService(mConnection1);
            mBound1 = false;
        }
        EventBus.getDefault().unregister(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    private class Insert extends AsyncTask<LoginData, Void, Void> {
        LoginRoomDatabase mLoginDatabase;
        Handler handler;

        public Insert(LoginRoomDatabase mLoginDatabase) {
            this.mLoginDatabase = mLoginDatabase;
            this.handler = new Handler();
        }


        @Override
        protected Void doInBackground(LoginData... loginData) {
            mLoginDatabase.loginDataDao().update(loginData[0]);
            return null;
        }
    }

    public class getTask extends AsyncTask<Void, Void, List<LoginData>> {
        LoginRoomDatabase mLoginDatabase;
        String update_token;

        public getTask(LoginRoomDatabase mLoginDatabase,String update_token) {
            this.mLoginDatabase = mLoginDatabase;
            this.update_token= update_token;
        }

        @Override
        protected List<LoginData> doInBackground(Void... voids) {
            List<LoginData> loginData = mLoginDatabase.loginDataDao().getAlldata();
            return loginData;
        }

        @Override
        protected void onPostExecute(List<LoginData> loginData) {
            super.onPostExecute(loginData);
            boolean usrname = false;
            boolean pass = false;
            int position = 0;
            for (int i = 0; i < loginData.size(); i++) {
                LoginData mloginData = loginData.get(i);
                if (mloginData.getUsername().equals(myPref.getPref(Constants.USERNAME,""))) {
                    loginDataclass = mloginData;
                    break;
                }
            }

            loginDataclass.setToken(update_token);
            new Insert(mLoginDatabase).execute(loginDataclass);

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String update_token) {
        new getTask(mLoginDatabase,update_token).execute();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void goToPermissionCheck() {
        if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])) {

                //If the user has denied the permission previously your code will come to this block
                //Here you can explain why you need this permission
                //Explain here why you need this permission

                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Permissions");
                builder.setMessage("Needs storage permissions. Please allow permissions to work application properly.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(HomeActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
                builder.setCancelable(false);
                builder.show();
            }
            else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Permissions");
                builder.setMessage("Needs storage permissions. Please allow permissions to work application properly.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions and grant permission to let App works properly.", Toast.LENGTH_LONG).show();
                    }
                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
                builder.setCancelable(false);
                builder.show();
            }
            else {
                //just request the permission
                ActivityCompat.requestPermissions(this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            startMainActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                startMainActivity();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Permissions");
                builder.setMessage("Needs storage permissions. Please allow permissions to work application properly.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(HomeActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                /*builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });*/
                builder.setCancelable(false);
                builder.show();
            } else {
                this.finish();
                Toast.makeText(getBaseContext(), "Sorry, you have denied permissions.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                startMainActivity();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                startMainActivity();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        myPref.setPref(Constants.ALL_TIME_HAND,true);
    }
}
