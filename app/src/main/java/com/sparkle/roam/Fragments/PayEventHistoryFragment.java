package com.sparkle.roam.Fragments;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.sparkle.roam.Activity.HomeActivity;
import com.sparkle.roam.Adapter.NewAdapter;
import com.sparkle.roam.ContentProvider.ToDo;
import com.sparkle.roam.ContentProvider.ToDoListDBAdapter;
import com.sparkle.roam.LocalDatabase.DatabaseHelper;
import com.sparkle.roam.Model.PayEventSync.PayEventData;
import com.sparkle.roam.Adapter.HistoryAdapter;
import com.sparkle.roam.R;
import com.sparkle.roam.Utils.Constants;
import com.sparkle.roam.Utils.MyPref;
import com.sparkle.roam.View.CodeErrorDialog;
import com.sparkle.roam.services.MqttService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class PayEventHistoryFragment extends Fragment implements View.OnClickListener, CodeErrorDialog.OnLogout, HistoryAdapter.OnItem, NewAdapter.onItemClickListener{

    RecyclerView rv_history_list;
    ImageButton btn_back;
    LinearLayoutManager linearLayoutManager;
    String p_id,PPID, firmwareVersion;
    private int codesize;
    public View myRoot;
    private MyPref myPref;
    HistoryAdapter newHistoryAdapter;
    private DatabaseHelper mdbhelper;
    private List<PayEventData> payEventDataList;

    private ToDoListDBAdapter toDoListDBAdapter;
    private List<ToDo> toDos;
    private NewAdapter newAdapter;

    private BottomSheetBehavior mBehavior;
    private AppBarLayout app_bar_layout;
    private LinearLayout container;

    boolean mBound1 = false;
    MqttService mService;

    private ServiceConnection mConnection1 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MqttService.LocalBinder binder = (MqttService.LocalBinder) service;
            mService = binder.getService();
            mBound1 = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound1 = false;
        }
    };


    public static PayEventHistoryFragment newInstance(String p_id,String productItemOESN,String firmwareVersion,int codesize) {
        PayEventHistoryFragment fragment = new PayEventHistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PAYACCID,p_id);
        bundle.putString(Constants.PRODUCTOESN, productItemOESN);
        bundle.putString(Constants.FIRMWAREVERSION, firmwareVersion);
        bundle.putInt(Constants.CODESIZE, codesize);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void UpdateUI() {
        newHistoryAdapter = null;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            p_id = getArguments().getString(Constants.PAYACCID);
            PPID = getArguments().getString(Constants.PRODUCTOESN);
            firmwareVersion = getArguments().getString(Constants.FIRMWAREVERSION);
            codesize = getArguments().getInt(Constants.CODESIZE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (myRoot == null) {
            myRoot = inflater.inflate(R.layout.fragment_pay_event_history, container, false);
        }
        return myRoot;
    }

    public void init(){
        payEventDataList = new ArrayList<>();
        Cursor res = mdbhelper.getPayEventData(p_id);
        while (res.moveToNext()){
            int payEventID = res.getInt(res.getColumnIndex("payEventID"));
            String payEventDate = res.getString(res.getColumnIndex("payEventDate"));
            int payDays = res.getInt(res.getColumnIndex("payDays"));
            int payRecordAmt = res.getInt(res.getColumnIndex("payRecordAmt"));
            String payRecordNotes = res.getString(res.getColumnIndex("payRecordNotes"));
            int payAccountID = res.getInt(res.getColumnIndex("payAccountID"));
            String eventType = res.getString(res.getColumnIndex("eventType"));
            String codeDays = res.getString(res.getColumnIndex("codeDays"));
            String codeIssued = res.getString(res.getColumnIndex("codeIssued"));
            String codehashTop = res.getString(res.getColumnIndex("codehashTop"));

            PayEventData payEventData = new PayEventData(payEventID,payEventDate,payDays,payRecordAmt,payRecordNotes,payAccountID,eventType,codeDays,codeIssued,codehashTop);
            payEventDataList.add(payEventData);
        }

        newHistoryAdapter.notifyList(payEventDataList);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        myPref = new MyPref(getContext());
        rv_history_list = view.findViewById(R.id.rv_history_list);
        rv_history_list.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        rv_history_list.setLayoutManager(linearLayoutManager);

        btn_back = view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this::onClick);
        ((HomeActivity) getActivity()).updateButtonAdd(false);
        payEventDataList = new ArrayList<>();

        newHistoryAdapter = new HistoryAdapter(getContext(), payEventDataList, PayEventHistoryFragment.this::onItemClick);
        rv_history_list.setAdapter(newHistoryAdapter);
        mdbhelper = new DatabaseHelper(getContext());

        init();


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBus event) {
        /* Do something */
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                ((HomeActivity)getActivity()).onBackPressed();
                break;
        }
    }

    @Override
    public void OnLogout(boolean msg) {
//        myPref.clearPref();
//        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
//        databaseHelper.deleteAlltable();
//        Intent i = new Intent(getContext(), LoginActivity.class);
//        startActivity(i);
//        getActivity().finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        Intent intent1 = new Intent(getContext(), MqttService.class);
        getActivity().bindService(intent1, mConnection1, Context.BIND_AUTO_CREATE);
//        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBound1) {
            getActivity().unbindService(mConnection1);
            mBound1 = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void hideView(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = 0;
        view.setLayoutParams(params);
    }

    private void showView(View view, int size) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = size;
        view.setLayoutParams(params);
    }

    private int getActionBarSize() {
        final TypedArray styledAttributes = getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int size = (int) styledAttributes.getDimension(0, 0);
        return size;
    }

    @Override
    public void onItemClick(int position) {

        if (position == -1) {
            position = position + 1;
        }

        System.out.println("================");
        System.out.println(position);
        PayEventData payEventData =  payEventDataList.get(position);

        if (payEventDataList.get(position).getEventType().equals("payCollect")){
            showDialogAmount(String.valueOf(payEventData.getPayDays()),
                    String.valueOf(payEventData.getPayRecordAmt()),
                    payEventData.getPayRecordNotes(),
                    payEventData.getEventType().toString(),
                    String.valueOf(payEventData.getCodeIssued()),
                    String.valueOf(payEventData.getCodeDays()),
                    String.valueOf(payEventData.getCodehashTop()),
                    String.valueOf(payEventData.getPayAccountID()),PPID,firmwareVersion,codesize);
        }else {
            showDialogCodeIssue(String.valueOf(payEventData.getPayDays()),
                    String.valueOf(payEventData.getPayRecordAmt()),
                    payEventData.getPayRecordNotes(),
                    payEventData.getEventType().toString(),
                    String.valueOf(payEventData.getCodeIssued()),
                    String.valueOf(payEventData.getCodeDays()),
                    String.valueOf(payEventData.getCodehashTop()),
                    String.valueOf(payEventData.getPayAccountID()),PPID,firmwareVersion,codesize);
        }

    }

    private void showDialogAmount(String paydays, String payRecordAmt, String payRecordNotes, String eventtype, String codeIssued, String codedays, String codehashtop, String payAccID,String productItemOESN,String firmwareVersion, int codesize) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark_amount);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        if (firmwareVersion != null){
            try {
                JSONObject obj = new JSONObject(firmwareVersion);
                String device_type = obj.getString("firmware");

                System.out.println("--------firmwareVersion---------"+firmwareVersion);
                System.out.println("--------device_type---------"+device_type);
                if (device_type.startsWith("ovCamp")){
                    myPref.setPref(Constants.DEVICE_TYPE,Constants.OVCAMP_DEVICE);
                }else {
                    myPref.setPref(Constants.DEVICE_TYPE,Constants.LUMN_DEVICE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        toDoListDBAdapter = ToDoListDBAdapter.getToDoListDBAdapterInstance(getContext());
        toDos=toDoListDBAdapter.getAllToDos();

        newAdapter = new NewAdapter(toDos,getContext(),this::changevalue);
        System.out.println("------toDos-------"+toDos.size());

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
        Calendar c = Calendar.getInstance();
        String formattedDate = input.format(c.getTime());

        ((TextView) dialog.findViewById(R.id.tv_payrecordamt)).setText(payRecordAmt);
        ((TextView) dialog.findViewById(R.id.tv_payrecordnote)).setText(payRecordNotes);
        ((TextView) dialog.findViewById(R.id.tv_eventtype)).setText(eventtype);

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((Button) dialog.findViewById(R.id.btn_print)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Comming Soon...", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    private void showDialogCodeIssue(String paydays, String payRecordAmt, String payRecordNotes, String eventtype, String codeIssued, String codedays, String codehashtop, String payAccID,String productItemOESN,String firmwareVersion, int codesize) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark_reissue_code);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        if (firmwareVersion != null){
            try {
                JSONObject obj = new JSONObject(firmwareVersion);
                String device_type = obj.getString("firmware");

                System.out.println("--------firmwareVersion---------"+firmwareVersion);
                System.out.println("--------device_type---------"+device_type);
                if (device_type.startsWith("ovCamp")){
                    myPref.setPref(Constants.DEVICE_TYPE,Constants.OVCAMP_DEVICE);
                }else {
                    myPref.setPref(Constants.DEVICE_TYPE,Constants.LUMN_DEVICE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        toDoListDBAdapter = ToDoListDBAdapter.getToDoListDBAdapterInstance(getContext());
        toDos=toDoListDBAdapter.getAllToDos();

        newAdapter = new NewAdapter(toDos,getContext(),this::changevalue);
        System.out.println("------toDos-------"+toDos.size());

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
        Calendar c = Calendar.getInstance();
        String formattedDate = input.format(c.getTime());

        ((TextView) dialog.findViewById(R.id.tv_paydays)).setText(paydays);
        ((TextView) dialog.findViewById(R.id.tv_payrecordamt)).setText(payRecordAmt);
        ((TextView) dialog.findViewById(R.id.tv_payrecordnote)).setText(payRecordNotes);
        ((TextView) dialog.findViewById(R.id.tv_eventtype)).setText(eventtype);
        ((TextView) dialog.findViewById(R.id.tv_codeissued)).setText(codeIssued);
        ((TextView) dialog.findViewById(R.id.tv_codedays)).setText(codedays);

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((Button) dialog.findViewById(R.id.btn_print)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Comming Soon...", Toast.LENGTH_SHORT).show();
            }
        });

        ((Button) dialog.findViewById(R.id.btn_issuecode)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (codesize == 0){
                    CodeErrorDialog codeErrorDialog = new CodeErrorDialog(getContext(), "You do not have enough credit. You remaining credit on this account is 0 days.", null);
                    codeErrorDialog.show();
                }else{
                    mdbhelper.updatePayEventData(codeIssued,formattedDate);
                    mService.connect();
                    if (!codeIssued.equals("")) {
                        if (myPref.getPref(Constants.DEVICE_TYPE,"").equals(Constants.OVCAMP_DEVICE)){
                            add_PPID(PPID,codeIssued,Constants.OVCAMP_DEVICE);
                        }else {
                            add_PPID(PPID,codeIssued,Constants.LUMN_DEVICE);
                        }
                    }

                    dialog.dismiss();
                    init();
                }


            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void add_PPID(String PPID, String codehasformate,String device_type){
        mService.publish("_OV1/"+device_type+"/" + PPID, codehasformate);
        newAdapter.filter(String.valueOf(PPID));
        String string = myPref.getPref("string","");
        int position = myPref.getPref("position",0);

        System.out.println("------under----------"+string+position);
        if (string.contains(String.valueOf(PPID))){
            toDos.remove(position-1);
            toDos.add(position-1,new ToDo(PPID,codehasformate,device_type));

            toDoListDBAdapter.modify(PPID,codehasformate,device_type);
            System.out.println("--------------yes--if-----------"+string);
            myPref.setPref("string","");
            myPref.setPref("position",0);

        }else {
            System.out.println("--------------yes--else-----------"+string);
            toDos.add(new ToDo(PPID,codehasformate,device_type));
            toDoListDBAdapter.insert(String.valueOf(PPID),codehasformate,device_type);
        }
//        CodeErrorDialog codeErrorDialog = new CodeErrorDialog(getContext(), "Code successfully save.",this);
//        codeErrorDialog.show();
        Toast.makeText(getContext(), "Code Successfully save.", Toast.LENGTH_SHORT).show();
        newAdapter.notifyList(toDos);
        System.out.println("---after---toDos-------"+toDos.size());
    }

    @Override
    public void changevalue(String string, int position) {
        myPref.setPref("string",string);
        myPref.setPref("position",position);
        System.out.println("---------out--------"+string+position);
    }
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
//        final View view = View.inflate(getContext(), R.layout.fragment_pay_event_history, null);
//
//        dialog.setContentView(view);
//        mBehavior = BottomSheetBehavior.from((View) view.getParent());
//        mBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
//
//        app_bar_layout = (AppBarLayout) view.findViewById(R.id.app_bar_layout);
//        container = (LinearLayout) view.findViewById(R.id.container);
//
//
//
//        hideView(app_bar_layout);
//
//        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                if (BottomSheetBehavior.STATE_EXPANDED == newState) {
//                    showView(app_bar_layout, getActionBarSize());
//                    hideView(container);
//                }
//                if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
//                    hideView(app_bar_layout);
//                    showView(container, getActionBarSize());
//                }
//
//                if (BottomSheetBehavior.STATE_HIDDEN == newState) {
//                    dismiss();
//                }
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//
//            }
//        });
//
//        ((ImageButton) view.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
//
//        return dialog;
//    }

}
