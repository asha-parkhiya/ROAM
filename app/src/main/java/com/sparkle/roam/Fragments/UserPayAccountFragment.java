package com.sparkle.roam.Fragments;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.GetUserPayaccountListQuery;
import com.amazonaws.amplify.generated.graphql.GetbatchCodeGenerationMutation;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
//import com.google.common.collect.Lists;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.sparkle.roam.AWS.ClientFactory;
import com.sparkle.roam.Activity.HomeActivity;
import com.sparkle.roam.Adapter.NewAdapter;
import com.sparkle.roam.Adapter.UserPayAccountAdapter;
import com.sparkle.roam.ContentProvider.ToDo;
import com.sparkle.roam.ContentProvider.ToDoListDBAdapter;
import com.sparkle.roam.Displaymodel.DisplayPayAccount;
import com.sparkle.roam.LocalDatabase.DatabaseHelper;
import com.sparkle.roam.Model.PPID_CODE;
import com.sparkle.roam.Model.SelectPayAcc;
import com.sparkle.roam.Model.SendRequest;
import com.sparkle.roam.R;
import com.sparkle.roam.Utils.Constants;
import com.sparkle.roam.Utils.MyPref;
import com.sparkle.roam.View.CodeErrorDialog;
import com.sparkle.roam.WebServices.WebRequest;
import com.sparkle.roam.services.APPClientService;
import com.sparkle.roam.services.MqttService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import type.GetBatchCodeMutationInput;
import type.RequestMetaInput;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;


public class UserPayAccountFragment extends Fragment implements View.OnClickListener, UserPayAccountAdapter.OnMclick , NewAdapter.onItemClickListener{

    private ImageButton btn_back;
    private RecyclerView rv_list;

    private LinearLayoutManager layoutManager;

    private MyPref myPref;
    private int offset = 0;
    private Gson gson;
    private String token = null;
    private WebRequest webRequest;
    private String user_fname, user_lname, user_id,user_code;
    private EditText edit_code;
    private Button btn_generatecode;
    private ImageView btn_scan;

    private UserPayAccountAdapter userPayAccountAdapter;

    private boolean ANIMATION;

    private PPID_CODE ppid_code;
    private List<PPID_CODE> ppid_codeList;

    private int numberOfpayAccount = 0;

    private final String MQTT_DEFAULT_TOPIC_SUBSCRIBE = "_OV1/GPRS/camp/";

    private DatabaseHelper mdbhelper;
    private List<DisplayPayAccount> displayPayAccounts;

    private List<SendRequest> sendRequestList;
    private List<String> stringList;

    boolean DIALOG = false;
    private AWSAppSyncClient mAWSAppSyncClient;

    private ToDoListDBAdapter toDoListDBAdapter;
    private List<ToDo> toDos;
    private RecyclerView textViewToDos;
    private NewAdapter newAdapter;

    APPClientService aPPClientService;
    boolean mBound = false;
    private BottomSheetBehavior mBehavior;
    private AppBarLayout app_bar_layout;
    private LinearLayout container;
    int count = 0;
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

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            APPClientService.LocalBinder binder = (APPClientService.LocalBinder) service;
            aPPClientService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public UserPayAccountFragment() {
        // Required empty public constructor
    }

//    private ServiceConnection mConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName className, IBinder service) {
//            MqttService.LocalBinder binder = (MqttService.LocalBinder) service;
//            mService = binder.getService();
//            mBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            mBound = false;
//        }
//    };

//    private ServiceConnection mConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName className, IBinder service) {
//            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
//            backgroundService = binder.getService();
//            mBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            mBound = false;
//        }
//    };

    public static UserPayAccountFragment newInstance(String userid) {
        UserPayAccountFragment fragment = new UserPayAccountFragment();
        Bundle args = new Bundle();
        args.putString(Constants.USERID, userid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user_id = getArguments().getString(Constants.USERID);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_pay_account, container, false);

        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_back = view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_generatecode = view.findViewById(R.id.btn_generatecode);
        edit_code = view.findViewById(R.id.edit_code);
        btn_generatecode.setOnClickListener(this);
        btn_scan = view.findViewById(R.id.btn_scan);

        myPref = new MyPref(getContext());
        myPref.setPref("string","");
        myPref.setPref("position",0);
        token = myPref.getPref(Constants.OFFLINE_TOKEN, "");
        rv_list = view.findViewById(R.id.rv_list);
        ppid_code = new PPID_CODE();
        ppid_codeList = new ArrayList<>();

        numberOfpayAccount = 0;
        rv_list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_list.setLayoutManager(layoutManager);

        webRequest = new WebRequest(getContext());
        UpdateUI();

        sendRequestList = new ArrayList<>();
        stringList = new ArrayList<>();

        displayPayAccounts = new ArrayList<>();
        userPayAccountAdapter = new UserPayAccountAdapter(getContext(),displayPayAccounts,this);
        rv_list.setAdapter(userPayAccountAdapter);

        mdbhelper = new DatabaseHelper(getContext());

        mAWSAppSyncClient = ClientFactory.getInstance(getContext());
//        textViewToDos.setAdapter(newAdapter);


        Cursor res = mdbhelper.checkAssignmentType(user_id);//limited by days
        while (res.moveToNext()){
            System.out.println("---lmited by days-----"+res.getInt(0));
            if(res.getInt(0) > 0) {count++;}
        }
//
        if(count == 0){
            Cursor res1 = mdbhelper.checkAssignmentTypeCode(user_id);//limited by codes
            while (res1.moveToNext()){
                try {
                    String string = res1.getString(0);
                    JSONObject jsonObject1 = new JSONObject(string);
                    String str = jsonObject1.getString("assignedCodes");
                    JSONArray jsonArray = new JSONArray(str);
                    int y = jsonArray.length();
                    System.out.println("---lmited by codes-----"+y);
                    if(y == 0) {count++;}
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if(count == 0){
//            btn_generatecode.setEnabled(true);
//            btn_generatecode.setBackgroundColor(getResources().getColor(R.color.white));
//            btn_generatecode.setText(Constants.GENERATE_CODE);
//            btn_generatecode.setHeight(55);
//            btn_generatecode.setTextColor(getResources().getColor(R.color.black));
//                        btn_generatecode.setBackgroundColor(getResources().getColor(R.color.underline_color));
//                        btn_generatecode = new Button(new ContextThemeWrapper(getContext(),R.style.Button_Primary));
        }else {
//            btn_generatecode.setEnabled(false);
//            btn_generatecode.setText(Constants.GENERATE_CODE);
//            btn_generatecode.setTextColor(getResources().getColor(R.color.white));
//            btn_generatecode.setBackgroundColor(getResources().getColor(R.color.btn_disabled));
            CodeErrorDialog codeErrorDialog = new CodeErrorDialog(getContext(), "Can't generate Batch Code, some of the payAccounts do not have enough credit.", null);
            codeErrorDialog.show();
        }

        updatedata();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mService.connect();
            }
        },1000);

    }

    public void UpdateUI() {
        ((HomeActivity) getActivity()).updateButtonAdd(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                ((HomeActivity) getActivity()).onBackPressed();
                break;

            case R.id.btn_generatecode:
                if (count == 0){
                    edit_code.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit_code.getWindowToken(), 0);

                    toDoListDBAdapter = ToDoListDBAdapter.getToDoListDBAdapterInstance(getContext());
                    toDos = toDoListDBAdapter.getAllToDos();
                    newAdapter = new NewAdapter(toDos,getContext(),this);
                    System.out.println("------toDos-------"+toDos.size());

                    String codevalue = edit_code.getText().toString();


                    if (String.valueOf(codevalue).equals("")) {
                        Toast.makeText(getContext(), "Please Enter the day", Toast.LENGTH_SHORT).show();
                    } else {
                        if(Integer.parseInt(codevalue) == 0){
                            CodeErrorDialog codeErrorDialog = new CodeErrorDialog(getContext(), "You do not have enough credit.\\nYou remaining credit on this account is 0 days", null);
                            codeErrorDialog.show();
                        }else {
                            int day = Integer.parseInt(codevalue);
                            startanimation();
//                    mdbhelper.insertBatchCodeData(user_id,codevalue);
//                    myPref.setPref(Constants.ANIMATION,false);
                            btn_generatecode.setEnabled(false);
//                            btn_generatecode.setBackgroundColor(getResources().getColor(R.color.btn_disabled));
//                            btn_generatecode.setText(Constants.CODE_REQUEST_PENDING);

                            List<List<String>> smallerLists = Lists.partition(stringList, 10);
                            System.out.println("----------------"+smallerLists.size());
                            for (int i = 0; i < smallerLists.size(); i++){
                                System.out.println("----------------"+smallerLists.get(i));
                                IssueCodeMutationquery(codevalue,smallerLists.get(i));
                            }
                            for (int i = 0; i < displayPayAccounts.size(); i++){
                                String firmwareVersion = displayPayAccounts.get(i).getFirmwareVersion();
                                ArrayList<String> codelist = new ArrayList<>();
                                ArrayList<String> codehashtoplist = new ArrayList<>();
                                try {
                                    JSONObject jsonObject = new JSONObject(displayPayAccounts.get(i).getAgentAssignment());
                                    JSONArray jsonArray = jsonObject.getJSONArray("assignedCodes");
                                    for (int i1 = 0; i1 < jsonArray.length(); i1++) {
                                        codehashtoplist.add(jsonArray.getJSONObject(i1).getString("hashTop"));
                                        codelist.add(jsonArray.getJSONObject(i1).getString("otpHashFormatted"));
                                    }
                                    if (codelist.size() != 0){
                                        String codehasformate = codelist.get(day-1);
                                        String codehashtop = codehashtoplist.get(day-1);
                                        System.out.println("--------firmwareVersion----------"+firmwareVersion);
                                        if (firmwareVersion != null){
                                            JSONObject obj = new JSONObject(firmwareVersion);
                                            String device_type = obj.getString("firmware");

                                            if (device_type.startsWith("ovCamp")){
                                                add_PPID(displayPayAccounts.get(i).getProductItemPAYGSN(),codehasformate,Constants.OVCAMP_DEVICE);
                                            }else {
                                                add_PPID(displayPayAccounts.get(i).getProductItemPAYGSN(),codehasformate,Constants.LUMN_DEVICE);
                                            }
                                        }
                                        System.out.println("---------------------"+day);
                                        for (int i2 = day - 1; i2 >= 0; i2--) {
                                            codelist.remove(i2);
                                            codehashtoplist.remove(i2);
                                        }
//                                codelist.remove(0);
//                                codehashtoplist.remove(0);
                                        updateCodeDatabase(displayPayAccounts.get(i).getPayAccountID(),displayPayAccounts.get(i).getAgentAssignment(),codelist,codehashtoplist);
                                        Offline(displayPayAccounts.get(i).getPayAccountID(),codevalue,codehasformate,codehashtop);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (i == displayPayAccounts.size()-1){
                                    DIALOG = true;
                                }
                            }

                            if (DIALOG){
                                CodeErrorDialog codeErrorDialog = new CodeErrorDialog(getContext(), "Code successfully saved.", null);
                                codeErrorDialog.show();
                                clearanimation();
                                btn_generatecode.setEnabled(true);
//                                btn_generatecode.setBackgroundColor(getResources().getColor(R.color.white));
//                                btn_generatecode.setText(Constants.GENERATE_CODE);
//                                btn_generatecode.setTextColor(getResources().getColor(R.color.black));
                                updatedata();
                            }
                        }

                    }
                }else {
                    CodeErrorDialog codeErrorDialog = new CodeErrorDialog(getContext(), "Can't generate Batch Code, some of the payAccounts do not have enough credit.", null);
                    codeErrorDialog.show();
                }

                break;
        }
    }

    public void IssueCodeMutationquery(String days,List<String> stringList) {
        String android_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        String username = myPref.getPref(Constants.USER_NAME, "");
        RequestMetaInput requestMetaInput = RequestMetaInput.builder().mobileID(android_id).username(username).build();
        GetBatchCodeMutationInput getBatchCodeMutationInput = GetBatchCodeMutationInput.builder().days(Integer.parseInt(days)).productItems(stringList).userID(Integer.parseInt(user_id)).build();
        final GetbatchCodeGenerationMutation getbatchCodeGenerationMutation = GetbatchCodeGenerationMutation.builder().input(getBatchCodeMutationInput).requestMeta(requestMetaInput).build();
        aPPClientService.AWSAppSyncClient().mutate(getbatchCodeGenerationMutation).enqueue(new GraphQLCall.Callback<GetbatchCodeGenerationMutation.Data>() {
            @Override
            public void onResponse(@Nonnull final Response<GetbatchCodeGenerationMutation.Data> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("---------mutation-----------"+response.data().getBatchCodeMutation());
                        System.out.println("---------mutation error-----------"+response.errors());
                    }
                });
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                System.out.println("-----------fail------------"+e);
            }
        });
    }

    public void add_PPID(String PPID, String codehasformate,String device_type){
        newAdapter.filter(String.valueOf(PPID));

        if (!mService.isConnected()) {
            if (!mBound1) return;
//            if (mService.connect()) {
//                Toast.makeText(getContext(), "MQTT connected", Toast.LENGTH_SHORT).show();
//                System.out.println("--------------------connect.");
//            }else {
//                Toast.makeText(getContext(), "MQTT not connected", Toast.LENGTH_SHORT).show();
//                System.out.println("--------------------not connect.");
//            }

            mService.publish("_OV1/"+device_type+"/" + PPID, codehasformate);
        } else {
            mService.publish("_OV1/"+device_type+"/" + PPID, codehasformate);
        }
        String string = myPref.getPref("string","");
        int position = myPref.getPref("position",0);
        if (string.contains(PPID)){
            toDos.remove(position-1);
            toDos.add(position-1,new ToDo(PPID,codehasformate,device_type));
            System.out.println("--------------yes--if-----------"+PPID+"--------"+codehasformate+"---------"+device_type);
            toDoListDBAdapter.modify(PPID,codehasformate,device_type);
            myPref.setPref("string","");
            myPref.setPref("position",0);

        }else {
            System.out.println("--------------yes--else-----------"+PPID+"--------"+codehasformate+"---------"+device_type);
            toDos.add(new ToDo(PPID,codehasformate,device_type));
            toDoListDBAdapter.insert(PPID,codehasformate,device_type);
        }
        newAdapter.notifyList(toDos);
        System.out.println("---after---toDos-------"+toDos.size());
    }

    @Override
    public void changevalue(String toDo, int position) {
        myPref.setPref("string",toDo);
        myPref.setPref("position",position);
    }

    private void updateCodeDatabase(int payAccID, String agentassignment, ArrayList<String> codelist, ArrayList<String> codehashtoplist) {
        try {
            JSONObject jsonObject = new JSONObject(agentassignment);
            String assignmentDate = jsonObject.optString("assignmentDate");
            if (!assignmentDate.equals("")){
                assignmentDate = assignmentDate.replace("/","-");
                jsonObject.put("assignmentDate",assignmentDate);
            }
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < codelist.size(); i++) {
                JSONObject json = new JSONObject();
                json.put("hashTop",codehashtoplist.get(i));
                json.put("otpHashFormatted",codelist.get(i));
                jsonArray.put(json);
            }
            jsonObject.put("assignedCodes",jsonArray);
            System.out.println("-------------jsonArray-------------"+jsonArray);
            mdbhelper.updateAssignCodeArrayData(String.valueOf(payAccID), String.valueOf(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Offline(int payAccID, String codevalue,String codehasformate, String codehashtop) {

        int str = 0;
        Cursor cursor = mdbhelper.getLastEventData();
        if(cursor.moveToLast()){
            str  = Integer.parseInt(cursor.getString( cursor.getColumnIndex("payEventID") ));
            str = str + 1;
        }
//        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
        Calendar c = Calendar.getInstance();
        String formattedDate = input.format(c.getTime());

        mdbhelper.insertPayEventData(String.valueOf(str),formattedDate,"","",
                "",String.valueOf(payAccID),"codeIssue",String.valueOf(codevalue),
                String.valueOf(codehasformate),String.valueOf(codehashtop), String.valueOf(0));
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String  event) {

//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }
//
//        if (event.equals(Constants.GENERATE_CODE)){
//            btn_generatecode.setEnabled(true);
//            btn_generatecode.setText(Constants.GENERATE_CODE);
//            btn_generatecode.setBackgroundColor(getResources().getColor(R.color.underline_color));
////            if (!edit_code.getText().toString().equals("")){
////                adapter.updateCode(edit_code.getText().toString());
////            }
//            userPayAccountAdapter = null;
//            UpdateUI();
//            clearanimation();
//            myPref.setPref(Constants.ANIMATION,true);
//            edit_code.setText("");
//        }else if (event.equals(Constants.REQUEST_COMPLETED)){
//            btn_generatecode.setEnabled(false);
//            btn_generatecode.setText(Constants.REQUEST_COMPLETED);
//            btn_generatecode.setBackgroundColor(getResources().getColor(R.color.btn_disabled));
//        }else if (event.equals(Constants.DOWNLOAD_CODE_ARRAY)){
//            btn_generatecode.setEnabled(false);
//            btn_generatecode.setText(Constants.DOWNLOAD_CODE_ARRAY);
//            btn_generatecode.setBackgroundColor(getResources().getColor(R.color.btn_disabled));
//        }
    }


    public void updatedata() {
        displayPayAccounts = new ArrayList<>();
        stringList = new ArrayList<>();
        Cursor res = mdbhelper.getUserPayAccountData(user_id);
        while (res.moveToNext()){
//            String firstname = res.getString(res.getColumnIndex("firstName"));
//            String lastname = res.getString(res.getColumnIndex("lastName"));
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

            DisplayPayAccount displayPayAccount = new DisplayPayAccount("","",productItemPAYG_SN,payAccountID,startDate,depositDays,payoffAmt,minPayDays,maxPayDays,schPayDays,userID,distributorID,assignedItemsID,
                    agentID,agentAssignmentStatus,agentAssignment,initialCreditDays,receivedPayAmt,durationDays,creditDaysIssued,payDaysReceived,firmwareVersion);
            displayPayAccounts.add(displayPayAccount);
            stringList.add(String.valueOf(payAccountID));
//            stringList.add("\""+payAccountID+"\"");
        }

        userPayAccountAdapter.notifyList(displayPayAccounts);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        Intent intent = new Intent(getContext(), APPClientService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        Intent intent1 = new Intent(getContext(), MqttService.class);
        getActivity().bindService(intent1, mConnection1, Context.BIND_AUTO_CREATE);
//        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }

        if (mBound1) {
            getActivity().unbindService(mConnection1);
            mBound1 = false;
        }
        super.onStop();
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
    public void onMclick(int position) {

    }

    @Override
    public void OnItemClick(SelectPayAcc selectPayAcc,GetUserPayaccountListQuery.GetUserPayAccountListQuery getUserPayAccountListQuery, int position) {
//        if (selectPayAcc.getSelection()){
//            btn_code_generate.setVisibility(View.VISIBLE);
//            selectionList.add(getUserPayAccountListQuery);
////            adapter.updateList();
//        }else {
//            selectionList.remove(getUserPayAccountListQuery);
//        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void startanimation(){
        Animation animation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(-1);
        animation.setDuration(1000);
        btn_scan.clearAnimation();
        btn_scan.setAnimation(animation);
    }

    public void clearanimation(){
        btn_scan.clearAnimation();
    }


//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
//        final View view = View.inflate(getContext(), R.layout.fragment_user_pay_account, null);
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
//
//                    if(count == 0){
//                        btn_generatecode.setEnabled(true);
//                        btn_generatecode.setBackgroundColor(getResources().getColor(R.color.white));
//                        btn_generatecode.setText(Constants.GENERATE_CODE);
//                        btn_generatecode.setHeight(55);
//                        btn_generatecode.setTextColor(getResources().getColor(R.color.black));
////                        btn_generatecode.setBackgroundColor(getResources().getColor(R.color.underline_color));
////                        btn_generatecode = new Button(new ContextThemeWrapper(getContext(),R.style.Button_Primary));
//                    }else {
//                        btn_generatecode.setEnabled(false);
//                        btn_generatecode.setText(Constants.GENERATE_CODE);
//                        btn_generatecode.setTextColor(getResources().getColor(R.color.white));
//                        btn_generatecode.setBackgroundColor(getResources().getColor(R.color.btn_disabled));
//                        CodeErrorDialog codeErrorDialog = new CodeErrorDialog(getContext(), "Can't generate Batch Code, some of the payAccounts do not have enough credit.", null);
//                        codeErrorDialog.show();
//                    }
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

    //comment code

    //            case R.id.btn_generatecode:
//
//                String codevalue = edit_code.getText().toString();
//                myPref.setPref(Constants.SELECETD_USER_ID,user_id);
//                myPref.setPref(Constants.SELECETD_USER_NAME,user_fname);
////                if (btn_generatecode.getText().equals("Generate Code")){
//                    if (String.valueOf(codevalue).equals("")) {
//                        Toast.makeText(getContext(), "Please Enter the day", Toast.LENGTH_SHORT).show();
//                    } else {
//                        boolean isConnected = isNetworkConnected();
//                        if (isConnected) {
//                            startanimation();
//                            myPref.setPref(Constants.ANIMATION,false);
//                            btn_generatecode.setEnabled(false);
//                            btn_generatecode.setBackgroundColor(getResources().getColor(R.color.btn_disabled));
//                            btn_generatecode.setText(Constants.CODE_REQUEST_PENDING);
//                            myPref.setPref(Constants.MESSAGE,Constants.CODE_REQUEST_PENDING);
////                            myPref.setPref(Constants.REMAINING_CODE,codevalue);
//                            JSONObject requestJson = new JSONObject();
//                            try {
//                                requestJson.put("authToken",token);
//                                requestJson.put("userID",user_id);
//                                requestJson.put("days",codevalue);
//                                webRequest.POST_METHOD(Constants.REQUEST_CODE_URL, requestJson, null, UserPayAccountFragment.this, WebCallType.GET_REQUEST_CODE, true);
////                                myPref.setPref(Constants.MESSAGE,"GENERATE_CODE");
//                                CodeErrorDialog codeErrorDialog = new CodeErrorDialog(getContext(),"Request for "+codevalue+" days codes for "+numberOfpayAccount+" number of accounts have been received. Codes will be ready for download or automatically assigned to your accounts.",null);
//                                codeErrorDialog.show();
//                                edit_code.setText("");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }else {
//                            CodeErrorDialog codeErrorDialog = new CodeErrorDialog(getContext(),"Please check your Internet connection.",null);
//                            codeErrorDialog.show();
//                        }
//                    }

    //            case R.id.btn_code_generate:
//                List<String> list = new ArrayList<>();
//                for (int i = 0; i < selectionList.size(); i++){
//                    list.add(""+selectionList.get(i).payAccountID()+"");
//                }
//                myPref.setPref("hello",list);
//                System.out.println("-----------pay id---------------"+myPref.getPref("hello",""));
//                break;


    //
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(MqttStringEvent event) {
//        if (event.data.equals("{\"message\":\"completed\"}")){
////            btn_generatecode.setEnabled(true);
////            btn_generatecode.setText("Download");
////            btn_generatecode.setBackgroundColor(getResources().getColor(R.color.underline_color));
//
//            myPref.setPref(Constants.MESSAGE,Constants.REQUEST_COMPLETED);
//            btn_generatecode.setText(Constants.REQUEST_COMPLETED);
//            boolean isConnected = isNetworkConnected();
//            if (isConnected) {
//                JSONObject requestJson = new JSONObject();
//                try {
//                    requestJson.put("authToken",token);
//                    requestJson.put("userID",user_id);
//                    requestJson.put("offset","0");
//                    requestJson.put("limit","100");
//                    webRequest.POST_METHOD(Constants.GET_CODE_ARRAY_URL, requestJson, null, UserPayAccountFragment.this, WebCallType.GET_CODE_ARRAY, true);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }else {
//                CodeErrorDialog codeErrorDialog = new CodeErrorDialog(getContext(),"Please check your Internet connection.",null);
//                codeErrorDialog.show();
//            }
//        }
//    }

//    @Override
//    public void onError(String message) {
//
//    }
//
//    @Override
//    public void onResponse(Object response, WebCallType webCallType) throws JSONException {
//        if (response != null) {
//            switch (webCallType){
//                case GET_REQUEST_CODE:
////                    JSONObject jsonObject1 = (JSONObject) response;//{"message":"Request#1581308885796 has been acceped","requestID":1581308885796}
////                    String message = jsonObject1.getString("message");
////                    String requestID = jsonObject1.getString("requestID");
////                    if (!requestID.equals("")){
//////                        myPref.setPref(Constants.MESSAGE,Constants.REQUEST_ACCEPTED);
//////                        btn_generatecode.setText(Constants.REQUEST_ACCEPTED);
////                        if (!mService.isConnected()) {
////                            if (mBound == false) return;
////                            if (mService.connect()) {
////                                Toast.makeText(getContext(), "Successfully connected", Toast.LENGTH_SHORT).show();
////                            }
////                            System.out.println("-----------requestID-------"+requestID);
////                            mService.subscribe("JOB/"+requestID+"/completed" , 2);
////                        } else {
////                            mService.subscribe("JOB/"+requestID+"/completed" , 2);
////                        }
////                    }
//
//                    break;
//
//                case GET_CODE_ARRAY:
//                    boolean DIALOG = false;
//                    System.out.println("-------------response---------------"+response);
//
//                    JSONObject jsonObject2 = (JSONObject) response;
//                    String data1 = jsonObject2.getString("data");
//
//                    System.out.println("-------------data1---------------"+data1);
//
//                    JSONArray jsonArray = new JSONArray(data1);
//                    for (int i = 0; i < jsonArray.length(); i++){
//                        JSONObject jsonObject3 = jsonArray.getJSONObject(i);
//
//                        String str = String.valueOf(jsonObject3);
//                        String[] parts = str.split(",");
//                        String final1 = parts[0];
//                        final1 = final1.replace("{","");
//                        final1 = final1.replace("\"","");
//                        String[] parts1 = final1.split(":");
//
//                        System.out.println("-------------final1---------------"+parts1[0]+" ---- "+parts1[1]);
//                        String string = jsonObject3.getString("firmwareVersion");
//                        JSONObject object = new JSONObject(string);
//                        String device = object.getString("firmware");
//                        if (device.contains("ovCamp")){
//                            System.out.println("-----------OVCAMP");
//                        }else {
//                            System.out.println("-----------LUMN");
//                        }
//
//                    }
////                    myPref.setPref(Constants.MESSAGE,Constants.DOWNLOAD_CODE_ARRAY);
////                    btn_generatecode.setText(Constants.DOWNLOAD_CODE_ARRAY);
////                    JSONObject jsonObject2 = (JSONObject) response;
////                    String data1 = jsonObject2.getString("data");
////                    JSONArray jsonArray = new JSONArray(data1);
////                    for (int i = 0; i < jsonArray.length(); i++){
////                        JSONObject jsonObject3 = jsonArray.getJSONObject(i);
////
////                        String object = String.valueOf(jsonObject3);
////                        object = object.replace("{","");
////                        object = object.replace("}","");
////                        object = object.replace("\"","");
////                        String[] parts = object.split(":");
////                        if (!mService.isConnected()) {
////                            if (mBound == false) return;
////                            if (mService.connect()) {
////                                Toast.makeText(getContext(), "Successfully connected", Toast.LENGTH_SHORT).show();
////                            }
////                            mService.publish(MQTT_DEFAULT_TOPIC_SUBSCRIBE + parts[0], parts[1]);
////                        } else {
////                            mService.publish(MQTT_DEFAULT_TOPIC_SUBSCRIBE + parts[0], parts[1]);
////                        }
////
////                        if (i == jsonArray.length()-1){
////                            DIALOG = true;
////                        }
//////                        ppid_code = new PPID_CODE(parts[0],parts[1]);
//////                        ppid_codeList.add(ppid_code);
////                    }
//////                    for (int i = 0; i < ppid_codeList.size(); i++){
//////                        if (!mService.isConnected()) {
//////                            if (mBound == false) return;
//////                            if (mService.connect()) {
//////                                Toast.makeText(getContext(), "Successfully connected", Toast.LENGTH_SHORT).show();
//////                            }
//////                            mService.publish(MQTT_DEFAULT_TOPIC_SUBSCRIBE + ppid_codeList.get(i).getPPID(), ppid_codeList.get(i).getCODE());
//////                        } else {
//////                            mService.publish(MQTT_DEFAULT_TOPIC_SUBSCRIBE + ppid_codeList.get(i).getPPID(), ppid_codeList.get(i).getCODE());
//////                        }
//////                    }
////                    if (DIALOG){
////                        CodeErrorDialog codeErrorDialog = new CodeErrorDialog(getContext(), "Code successfully published to MQTT server.", null);
////                        codeErrorDialog.show();
//////                        myPref.setPref(Constants.MESSAGE,"");
////                        myPref.setPref(Constants.MESSAGE,Constants.GENERATE_CODE);
////                        btn_generatecode.setEnabled(true);
////                        btn_generatecode.setBackgroundColor(getResources().getColor(R.color.underline_color));
////                        btn_generatecode.setText(Constants.GENERATE_CODE);
////                    }
//                    break;
//            }
//        }
//
//    }
}
