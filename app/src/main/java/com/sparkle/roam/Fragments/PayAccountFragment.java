package com.sparkle.roam.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.sparkle.roam.Activity.HomeActivity;
import com.sparkle.roam.Displaymodel.DisplayPayAccount;
import com.sparkle.roam.LocalDatabase.DatabaseHelper;
import com.sparkle.roam.Adapter.PayAccountAdapter;
import com.sparkle.roam.R;
import com.sparkle.roam.Utils.Constants;
import com.sparkle.roam.Utils.MyPref;
import com.sparkle.roam.View.CodeErrorDialog;
import com.sparkle.roam.WebServices.WebCallType;
import com.sparkle.roam.WebServices.WebRequest;
import com.sparkle.roam.WebServices.WebResponseListener;
import com.sparkle.roam.services.MqttService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;


public class PayAccountFragment extends Fragment implements View.OnClickListener, PayAccountAdapter.OnMclick, CodeErrorDialog.OnLogout, PayAccountAdapter.OnButtonClick, TextWatcher, WebResponseListener {
    private CircleImageView profile_image;
    private EditText et_serch;
    private ImageView iv_menu;
    private PayAccountFragment.OnNavigation onNavigation;

    private int totalPayAccount = 0;
    private boolean scroll = false;

    public void updatedata() {
        newPayAccountAdapter = null;
    }

    public interface OnNavigation {

        void onNavigationClick();
    }

    private RecyclerView rv_list;
    private MyPref myPref;
    private int off = 0;
    private Gson gson;
    private String token = null;

    private View myRoot;
    private LinearLayoutManager layoutManager;
    private ImageView iv_filter;
    private PayAccountAdapter newPayAccountAdapter;
    private List<DisplayPayAccount> displayPayAccounts;
    private CircleImageView mqtt_status;


    private WebRequest webRequest;
    private ProgressDialog processDialog;
    private int loop = 0;
    private DatabaseHelper mdbhelper;

    private boolean listbool;
    Button btn;

    long assignmentID;
    String assignmentDate;
    int assignmentDays;
    JSONObject jsonObject;

    MqttService mService;
    boolean mBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MqttService.LocalBinder binder = (MqttService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    public PayAccountFragment(PayAccountFragment.OnNavigation onNavigation) {
        this.onNavigation = onNavigation;
        // Required empty public constructor
    }

    public static PayAccountFragment newInstance(PayAccountFragment.OnNavigation onNavigation) {
        PayAccountFragment fragment = new PayAccountFragment(onNavigation);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (myRoot == null) {
            myRoot = inflater.inflate(R.layout.fragment_pay_account_detail, container, false);
        }
        return myRoot;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myPref = new MyPref(getContext());
        gson = new Gson();
        myPref.setPref(Constants.IS_FIRSTTIMERUN, false);
        webRequest = new WebRequest(getContext());

        token = myPref.getPref(Constants.OFFLINE_TOKEN, "");
        profile_image = view.findViewById(R.id.profile_image);
        profile_image.setOnClickListener(this);
        et_serch = view.findViewById(R.id.et_serch);
        et_serch.clearFocus();
        et_serch.addTextChangedListener(this);

        iv_menu = view.findViewById(R.id.iv_menu);
        iv_menu.setOnClickListener(this);
        iv_filter = view.findViewById(R.id.iv_filter);
        iv_filter.setOnClickListener(this);
        rv_list = view.findViewById(R.id.rv_list);
        rv_list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_list.setLayoutManager(layoutManager);
        btn = view.findViewById(R.id.btn);

        displayPayAccounts = new ArrayList<>();
        newPayAccountAdapter = new PayAccountAdapter(getContext(),displayPayAccounts,this::onbclick);
        rv_list.setAdapter(newPayAccountAdapter);

        mdbhelper = new DatabaseHelper(getContext());
        mqtt_status = view.findViewById(R.id.mqtt_status);

        UpdateUI("");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mService != null){
                    mService.connect();
                    if (mService.isConnected()){
                        mqtt_status.setImageResource(R.drawable.green_icon);
                        Toast.makeText(getContext(), "MQTT connected", Toast.LENGTH_SHORT).show();
                    }else {
                        mqtt_status.setImageResource(R.drawable.gray1);
                        Toast.makeText(getContext(), "MQTT not connected", Toast.LENGTH_SHORT).show();
                    }
                    System.out.println("------connection---------"+mService.isConnected());

                    if (!mService.isConnected()) {
                        if (!mBound) return;
                        if (mService.connect()) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getContext(), "MQTT connected", Toast.LENGTH_SHORT).show();
                                    mqtt_status.setImageResource(R.drawable.green_icon);
                                }
                            });
                        }
                    }
                }else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getContext(), "MQTT not connected", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        }, 1000);
    }



    public void UpdateUI(String s) {
        displayPayAccounts = new ArrayList<>();
        Cursor res = mdbhelper.getPayAccountData(s);
        while (res.moveToNext()){
            String firstname = res.getString(res.getColumnIndex("firstName"));
            String lastname = res.getString(res.getColumnIndex("lastName"));
            String productItemPAYG_SN = res.getString(res.getColumnIndex("productItemPAYG_SN"));
            int payAccountID = res.getInt(res.getColumnIndex("payAccountID"));
            String startDate = res.getString(res.getColumnIndex("startDate"));
            int depositDays = res.getInt(res.getColumnIndex("depositDays"));
            int payoffAmt = res.getInt(res.getColumnIndex("payoffAmt"));
            int minPayDays = res.getInt(res.getColumnIndex("minPayDays"));
            int maxPayDays = res.getInt(res.getColumnIndex("maxPayDays"));
            int schPayDays = res.getInt(res.getColumnIndex("schPayDays"));
            int userID = res.getInt(res.getColumnIndex("userID"));
            int distributorID = res.getInt(res.getColumnIndex("distributorID"));
            int assignedItemsID = res.getInt(res.getColumnIndex( "assignedItemsID"));
            int agentID = res.getInt(res.getColumnIndex( "agentID"));
            String agentAssignmentStatus = res.getString(res.getColumnIndex( "agentAssignmentStatus"));
            String agentAssignment = res.getString(res.getColumnIndex( "agentAssignment"));
            int initialCreditDays = res.getInt(res.getColumnIndex( "initialCreditDays"));
            int receivedPayAmt = res.getInt(res.getColumnIndex( "receivedPayAmt"));
            int durationDays = res.getInt(res.getColumnIndex( "durationDays"));
            int creditDaysIssued = res.getInt(res.getColumnIndex( "creditDaysIssued"));
            int payDaysReceived = res.getInt(res.getColumnIndex( "payDaysReceived"));
            String firmwareVersion = res.getString(res.getColumnIndex("firmwareVersion"));

            DisplayPayAccount displayPayAccount = new DisplayPayAccount(firstname,lastname,productItemPAYG_SN,payAccountID,startDate,depositDays,payoffAmt,minPayDays,maxPayDays,schPayDays,userID,distributorID,assignedItemsID,
                    agentID,agentAssignmentStatus,agentAssignment,initialCreditDays,receivedPayAmt,durationDays,creditDaysIssued,payDaysReceived,firmwareVersion);
            displayPayAccounts.add(displayPayAccount);
        }//search time error generate
        newPayAccountAdapter.notifyList(displayPayAccounts);
        ((HomeActivity) getActivity()).updateButtonAdd(true);
    }

    @Override
    public void onMclick(int position) {

        ((HomeActivity) getActivity()).setPayAccountDetailFragment(String.valueOf(displayPayAccounts.get(position).getPayAccountID()),position
                , displayPayAccounts.get(position).getFirstName()
                , displayPayAccounts.get(position).getLastName()
                , String.valueOf(displayPayAccounts.get(position).getProductItemPAYGSN())
                , this::onbclick);
    }


    @Override
    public void onbclick(int btn_number, int position, String agentAssignment) {

        if (position == -1) {
            position = position + 1;
        }

        if (btn_number == 100) {
            String assignAgent = "";
        ArrayList<String> codes = new ArrayList<>();
        ArrayList<String> codesHashtop = new ArrayList<>();
        if (displayPayAccounts.get(position).getAgentAssignment() != null)
            assignAgent = displayPayAccounts.get(position).getAgentAssignment();
        try {
            JSONObject jsonObject = new JSONObject(assignAgent);
            JSONArray jsonArray = jsonObject.getJSONArray("assignedCodes");
            for (int i = 0; i < jsonArray.length(); i++) {
                codesHashtop.add(jsonArray.getJSONObject(i).getString("hashTop"));
                codes.add(jsonArray.getJSONObject(i).getString("otpHashFormatted"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int payAccId = displayPayAccounts.get(position).getPayAccountID();
        DisplayPayAccount displayPayAccount = displayPayAccounts.get(position);
        ((HomeActivity) getActivity()).setNewPayEventHistoryFragment(String.valueOf(payAccId),displayPayAccount.getProductItemPAYGSN(),displayPayAccount.getFirmwareVersion(),codes.size());
        }

        if (btn_number == 200) {
            DisplayPayAccount displayPayAccount = displayPayAccounts.get(position);
            ((HomeActivity) getActivity()).setGenerateCodeFragment(displayPayAccount.getAgentAssignment(), displayPayAccount.getPayAccountID(), displayPayAccounts.size(), position, PayAccountFragment.this::update, et_serch.getText().toString().trim(), false,displayPayAccount.getFirstName(),displayPayAccount.getLastName(),displayPayAccount.getProductItemPAYGSN(),displayPayAccount.getFirmwareVersion());
        }

        if (btn_number == 300) {
            DisplayPayAccount displayPayAccount = displayPayAccounts.get(position);
            ((HomeActivity) getActivity()).setPayMentDialog(displayPayAccount.getAgentAssignment(), displayPayAccount.getPayAccountID(), displayPayAccounts.size(), position, PayAccountFragment.this::update, et_serch.getText().toString().trim(), false,displayPayAccount.getFirstName(),displayPayAccount.getLastName(),displayPayAccount.getProductItemPAYGSN(),displayPayAccount.getFirmwareVersion());
        }
    }

    public void update(){

    }


    @Override
    public void OnLogout(boolean msg) {

    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent2 = new Intent(getContext(), MqttService.class);
        getContext().bindService(intent2, mConnection, Context.BIND_AUTO_CREATE);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBound) {
            getContext().unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                if (onNavigation != null)
                    onNavigation.onNavigationClick();

                break;
            case R.id.profile_image:
//                getContext().startService(new Intent(getContext(), MyService.class));
//                Intent intent = new Intent(getContext(), HelloService.class);
//                getContext().startService(intent);
                break;

            case R.id.iv_filter:
                ((HomeActivity) getActivity()).setUserListFragment();
                break;
        }
    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onResponse(Object response, WebCallType webCallType) throws JSONException {
        if (response != null) {
            switch (webCallType){
                case GET_ACCESS_TOKEN:
//                    JSONObject jsonObject = (JSONObject) response;
//
//                    JSONObject data = jsonObject.getJSONObject("data");
//                    JSONObject RenewToken = data.getJSONObject("RenewToken");
//                    String access_token = RenewToken.getString("message");
//                    System.out.println("------------------------"+access_token);
//                    myPref.setPref(Constants.OFFLINE_TOKEN,access_token);
//                    token = access_token;
//                    newPayAccountAdapter = null;
//                    UpdateUI();
//                    EventBus.getDefault().post(access_token);
                    break;
            }
        }

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {
        System.out.println("------event str--------"+event);
        if (event.equals("Dialog Dismiss")){
            UpdateUI("");
        }
        /* Do something */
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("---------pay acc frag-------------resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("===============pause");
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        UpdateUI(et_serch.getText().toString().trim());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
