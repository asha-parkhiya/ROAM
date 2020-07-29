package com.sparkle.roam.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.IssueCodeMutation;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sparkle.roam.Activity.HomeActivity;
import com.sparkle.roam.Adapter.NewAdapter;
import com.sparkle.roam.ContentProvider.ToDo;
import com.sparkle.roam.ContentProvider.ToDoListDBAdapter;
import com.sparkle.roam.LocalDatabase.DatabaseHelper;
import com.sparkle.roam.R;
import com.sparkle.roam.Utils.Constants;
import com.sparkle.roam.Utils.MyPref;
import com.sparkle.roam.View.CodeErrorDialog;
import com.sparkle.roam.WebServices.WebCallType;
import com.sparkle.roam.WebServices.WebRequest;
import com.sparkle.roam.WebServices.WebResponseListener;
import com.sparkle.roam.services.APPClientService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;
import type.IssueCodeInput;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;


public class GenerateCodeFragment extends BottomSheetDialogFragment implements View.OnClickListener, CodeErrorDialog.OnLogout, WebResponseListener, NewAdapter.onItemClickListener {

    private OnIssuecode onIssuecode;
    private String token = null;
    private String notes = null;

    private String PPID = "";
    private String firmwareVersion = "";
    private String agentassignment = "";
    private String issuecode = null;
    private String currentTime;

    private int limit;
    private int loop = 0;
    private int position;

    private int day = 0;
    private String codevalue = null;
    private TextView tv_showcode;
    private EditText et_code, et_note;

    private ArrayList<String> codelist;
    private ArrayList<String> codehashtoplist;

    private Button btn_generatecode, btn_issuecode;
    private int payAccID,agentID;
    private MyPref myPref;
    private ImageButton btn_back;

    private String search;
    private String codehashtop;
    private boolean isfromHome = false;
    private String codehasformate = "";
    private  View myRoot;

//    private MqttService mService;
//    private boolean mBound = false;

    private final String MQTT_DEFAULT_TOPIC_SUBSCRIBE = "_OV1/GPRS/camp/";
    private WebRequest webRequest;

    private boolean fireissuecodemutation = false;
    private boolean firegeneretecodemutation = false;

    private DatabaseHelper mdbhelper;

    private File csvfile;

    private boolean repeat = false;

    private ToDoListDBAdapter toDoListDBAdapter;
    private List<ToDo> toDos;
    private RecyclerView textViewToDos;
    private NewAdapter newAdapter;

    APPClientService aPPClientService;
    boolean mBound = false;

    private BottomSheetBehavior mBehavior;
    private AppBarLayout app_bar_layout;
    private LinearLayout container;

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

    public interface OnIssuecode {
        void update();
    }

    public GenerateCodeFragment(OnIssuecode onIssuecode, boolean isfromHome) {
        this.isfromHome = isfromHome;
        this.onIssuecode = onIssuecode;
    }

    public static GenerateCodeFragment newInstance(String AGENTASSIGNMENT, int payAccID, int limit, int position, OnIssuecode onIssuecode, String search, boolean isfromHome,String firstname,String lastname,String productItemOESN,String firmwareVersion) {
        GenerateCodeFragment fragment = new GenerateCodeFragment(onIssuecode, isfromHome);
        Bundle args = new Bundle();
        args.putInt(Constants.PAYACCID, payAccID);
        args.putInt(Constants.OFFSET, limit);
        args.putInt(Constants.POSITION, position);
        args.putString(Constants.SEARCH_TEXT, search);
        args.putString(Constants.FIRST_NAME, firstname);
        args.putString(Constants.LAST_NAME, lastname);
        args.putString(Constants.PRODUCTOESN, productItemOESN);
        args.putString(Constants.FIRMWAREVERSION, firmwareVersion);
        args.putString(Constants.AGENTASSIGNMENT, AGENTASSIGNMENT);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            payAccID = (int) getArguments().get(Constants.PAYACCID);
            limit = (int) getArguments().get(Constants.OFFSET);
            position = (int) getArguments().get(Constants.POSITION);
            search = getArguments().getString(Constants.SEARCH_TEXT);
            PPID = getArguments().getString(Constants.PRODUCTOESN);
            firmwareVersion = getArguments().getString(Constants.FIRMWAREVERSION);
            agentassignment = getArguments().getString(Constants.AGENTASSIGNMENT);
            webRequest = new WebRequest(getContext());
            csvfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyCsvFile.csv");

        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        final View view = View.inflate(getContext(), R.layout.fragment_generate_code, null);

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        mBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);

        app_bar_layout = (AppBarLayout) view.findViewById(R.id.app_bar_layout);
        container = (LinearLayout) view.findViewById(R.id.container);

//        btn_back = view.findViewById(R.id.btn_back);
//        btn_back.setOnClickListener(this);
        tv_showcode = view.findViewById(R.id.tv_showcode);
        et_code = view.findViewById(R.id.et_code);
        et_note = view.findViewById(R.id.et_note);
        btn_issuecode = view.findViewById(R.id.btn_issuecode);
        btn_generatecode = view.findViewById(R.id.btn_generatecode);

        init();

        hideView(app_bar_layout);

        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                    showView(app_bar_layout, getActionBarSize());
                    hideView(container);
                }
                if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    hideView(app_bar_layout);
                    showView(container, getActionBarSize());
                }

                if (BottomSheetBehavior.STATE_HIDDEN == newState) {
                    dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

//        ((ImageButton) view.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });

        return dialog;
    }



//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        if (myRoot == null) {
//            myRoot = inflater.inflate(R.layout.fragment_generate_code, container, false);
//        }
//        return myRoot;
//    }

    private void UpdateOfflineCodelist(final int position, final int limit, String search) {

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_generatecode:
                et_code.requestFocus();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_code.getWindowToken(), 0);

                codevalue = et_code.getText().toString();
                if (String.valueOf(codevalue).equals("")) {
                    Toast.makeText(getContext(), "Please Enter the day", Toast.LENGTH_SHORT).show();
                } else {
                    day = Integer.parseInt(et_code.getText().toString());
                    if (day > codelist.size()) {
                        String msg = null;
                        msg = "You do not have enough credit. You remaining credit on this account is " + codelist.size() + "days";
                        CodeErrorDialog codeErrorDialog = new CodeErrorDialog(getContext(), msg, null);
                        codeErrorDialog.show();
                    } else {
                        if (day - 1 == -1) {
                            CodeErrorDialog codeErrorDialog = new CodeErrorDialog(getContext(), "You do not have enough credit.\\nYou remaining credit on this account is 0 days", null);
                            codeErrorDialog.show();
                        } else {
                            issuecode = codelist.get(day - 1);
                            codehashtop = codehashtoplist.get(day - 1);
                            System.out.println("-------gene-----------"+codelist.get(day - 1));
                            codehasformate = codelist.get(day - 1);
                            tv_showcode.setText(codelist.get(day - 1));
                            btn_issuecode.setEnabled(true);
                            btn_issuecode.setHeight(55);
                            btn_issuecode.setTextColor(getResources().getColor(R.color.black));
                            btn_issuecode.setBackgroundColor(getResources().getColor(R.color.white));
//                            Drawable unwrappedDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.btn_backgroundcolor);
//                            btn_issuecode.setBackground(unwrappedDrawable);
//                            GenerateCodequery();
                        }
                    }
                }
                break;

            case R.id.btn_issuecode:
                toDoListDBAdapter = ToDoListDBAdapter.getToDoListDBAdapterInstance(getContext());
                toDos=toDoListDBAdapter.getAllToDos();

                newAdapter = new NewAdapter(toDos,getContext(),this::changevalue);
                System.out.println("------toDos-------"+toDos.size());
                String ascii = PPID;
                System.out.println("----------ppid----------"+PPID);
                System.out.println("-------codehasformate------------"+codehasformate);


                codevalue = et_code.getText().toString();

                if (String.valueOf(codevalue).equals("")) {
                    Toast.makeText(getContext(), "Please Enter the day", Toast.LENGTH_SHORT).show();
                } else if (issuecode == null) {
                    Toast.makeText(getContext(), "Please Generate the code", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = day - 1; i >= 0; i--) {
                        codelist.remove(i);
                        codehashtoplist.remove(i);
                    }
                    notes = et_note.getText().toString().trim();
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    currentTime = sdf.format(c.getTime());
                    updateCodeDatabase(agentassignment,codelist,codehashtoplist);
                    Offline(payAccID,codevalue,codehasformate,codehashtop);
                    IssueCodeMutationquery();

                    et_code.setText("");
                    et_code.setHint("Assignment days:");
                    et_note.setText("");
                    et_note.setHint("Please Enter the note");

//                    EventBus.getDefault().postSticky(codelist.size());
                }

                if (!codehasformate.equals("")) {
                    if (myPref.getPref(Constants.DEVICE_TYPE,"").equals(Constants.OVCAMP_DEVICE)){
                        add_PPID(PPID,codehasformate,Constants.OVCAMP_DEVICE);
                    }else {
                        add_PPID(PPID,codehasformate,Constants.LUMN_DEVICE);
                    }
                }

                btn_issuecode.setEnabled(false);
                Drawable unwrappedDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.btn_back_disable);
                btn_issuecode.setBackground(unwrappedDrawable);

                break;
//            case R.id.btn_back:
//                ((HomeActivity) getActivity()).onBackPressed();
//                break;
        }
    }

    public void init(){
        myPref = new MyPref(getContext());
        token = myPref.getPref(Constants.OFFLINE_TOKEN, "");


        btn_generatecode.setOnClickListener(this);
        btn_issuecode.setOnClickListener(this);
        ((HomeActivity) getActivity()).updateButtonAdd(false);

        btn_issuecode.setEnabled(false);

        agentID = myPref.getPref(Constants.AGENTID,0);
        mdbhelper = new DatabaseHelper(getContext());

        codelist = new ArrayList<>();
        codehashtoplist = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(agentassignment);
            JSONArray jsonArray = jsonObject.getJSONArray("assignedCodes");
            for (int i = 0; i < jsonArray.length(); i++) {
                codehashtoplist.add(jsonArray.getJSONObject(i).getString("hashTop"));
                codelist.add(jsonArray.getJSONObject(i).getString("otpHashFormatted"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

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
    }

    public void add_PPID(String PPID, String codehasformate,String device_type){
        newAdapter.filter(String.valueOf(PPID));
        String string = myPref.getPref("string","");
        int position = myPref.getPref("position",0);
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
        CodeErrorDialog codeErrorDialog = new CodeErrorDialog(getContext(), "Code successfully save.",this::OnLogout);
        codeErrorDialog.show();
        newAdapter.notifyList(toDos);
        System.out.println("---after---toDos-------"+toDos.size());
    }

    @Override
    public void changevalue(String string, int position) {
        myPref.setPref("string",string);
        myPref.setPref("position",position);
    }

    private void updateCodeDatabase(String agentassignment, ArrayList<String> codelist, ArrayList<String> codehashtoplist) {
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
                "",String.valueOf(payAccID),"codeIssue",String.valueOf(codevalue),String.valueOf(codehasformate),String.valueOf(codehashtop), String.valueOf(0));

    }

    public void IssueCodeMutationquery() {

        IssueCodeInput issueCodeInput = IssueCodeInput.builder().payRecordNote(notes).assignmentDays(day).agentID(agentID).payAccountID(payAccID).build();
        final IssueCodeMutation issueCodeMutation = IssueCodeMutation.builder().input(issueCodeInput).build();
        aPPClientService.AWSAppSyncClient().mutate(issueCodeMutation).enqueue(new GraphQLCall.Callback<IssueCodeMutation.Data>() {
            @Override
            public void onResponse(@Nonnull final Response<IssueCodeMutation.Data> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.errors().size() != 0) {
                            if (response.errors().get(0).message().equals("Unauthorized.")) {

                                boolean isConnected = isNetworkConnected();
                                if (isConnected) {
                                    JSONObject requestJson = new JSONObject();
                                    String refreshToken = myPref.getPref(Constants.REFRESH_TOKEN,"");
                                    String username = myPref.getPref(Constants.USERNAME,"");
                                    try {
                                        requestJson.put("query", "mutation renewToken{ RenewToken(refreshToken:\""+refreshToken+"\",username:\""+username+"\"){ message}}");
                                        webRequest.POST_METHOD(Constants.REFRESH_TOKEN_URL, requestJson, null, GenerateCodeFragment.this, WebCallType.GET_ACCESS_TOKEN, false);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    fireissuecodemutation = true;
                                    firegeneretecodemutation = false;

                                }
                            }
                        }

                        System.out.println("---------mutation-----------"+response.data().IssueCodeMutation());
                        System.out.println("---------mutation error-----------"+response.errors());
                    }
                });

            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {

            }
        });

    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onResponse(Object response, WebCallType webCallType) throws JSONException {
        if (response != null) {
            switch (webCallType){
                case GET_ACCESS_TOKEN:
                    JSONObject jsonObject = (JSONObject) response;
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONObject RenewToken = data.getJSONObject("RenewToken");
                    String access_token = RenewToken.getString("message");
                    myPref.setPref(Constants.OFFLINE_TOKEN,access_token);
                    token = access_token;

                    if(firegeneretecodemutation){
//                        GenerateCodequery();
                        firegeneretecodemutation = false;
                    }

                    if (fireissuecodemutation){
                        IssueCodeMutationquery();
                        fireissuecodemutation = false;
                    }
                    EventBus.getDefault().post(access_token);
                    break;
            }
        }
    }


    @Override
    public void OnLogout(boolean msg) {
        if (msg) {
//            ((HomeActivity) getActivity()).onBackPressed();
            dismiss();
        } else
            ((HomeActivity) getActivity()).setLogout();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    //    public void add_PPID_CODE(String PPID, String codehasformate,String device_type){
//
//        if (csvfile.exists()){
//            itemArrayAdapter.filter(PPID);
//
//            String string = myPref.getPref("string","");
//            int position = myPref.getPref("position",0);
//            if (string.contains(PPID)){
//                scoreList.remove(position-1);
//                scoreList.add(position-1,new String[]{device_type,PPID,codehasformate});
//                System.out.println("--------------yes--if-----------"+string+"--"+scoreList.size());
//                myPref.setPref("string","");
//                myPref.setPref("position",0);
//                CodeErrorDialog codeErrorDialog = new CodeErrorDialog(getContext(), "Code successfully save.",null);
//                codeErrorDialog.show();
//            }else {
//                scoreList.add(new String[]{device_type,PPID,codehasformate});
//                System.out.println("--------------yes--else-----------"+scoreList.size());
//                CodeErrorDialog codeErrorDialog = new CodeErrorDialog(getContext(), "Code successfully added.", null);
//                codeErrorDialog.show();
//            }
//        }else {
//            scoreList.add(new String[]{device_type,PPID,codehasformate});
//            System.out.println("--------------no-------------"+scoreList.size());
//            CodeErrorDialog codeErrorDialog = new CodeErrorDialog(getContext(), "Code successfully saved.", null);
//            codeErrorDialog.show();
//        }
//        itemArrayAdapter.notifyList(scoreList);
//
//    }

    //                    FileWriter sw  = null;
//                    try {
//                        sw = new FileWriter(csvfile.getAbsolutePath());
//                        CSVWriter writer = new CSVWriter(sw);
//                        writer.writeAll(scoreList);
//                        writer.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
    //    public String testmethod() {
//
//        JSONArray jsArray = new JSONArray();
//        for (int i = 0; i < codelist.size(); i++) {
//            JSONObject hashtopjson = new JSONObject();
//            try {
//                hashtopjson.put("hashTop", codehashtoplist.get(i));
//                hashtopjson.put("otpHashFormatted", codelist.get(i));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            jsArray.put(hashtopjson);
//        }
//
//
//        JSONObject jo = new JSONObject();
//        try {
//            jo.put("assignedCodes", jsArray);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return jo.toString();
//
//
//    }
//
//    public void GenerateCodequery() {
//        mAWSAppSyncClient = ClientFactory.getInstance(getContext());
//
//        MyInput myInput = MyInput.builder().assignmentDays(day).authToken(token).payAccountID(payAccID).build();
//        System.out.println("payAccID: " + payAccID);
//        final GenerateIssueCodeMutation generateIssueCodeMutation = GenerateIssueCodeMutation.builder().input(myInput).build();
//        mAWSAppSyncClient.mutate(generateIssueCodeMutation).enqueue(new GraphQLCall.Callback<GenerateIssueCodeMutation.Data>() {
//            @Override
//            public void onResponse(@Nonnull final Response<GenerateIssueCodeMutation.Data> response) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (response.errors().size() != 0) {
//                            if (response.errors().get(0).message().equals("Unauthorized.")) {
//
//                                boolean isConnected = isNetworkConnected();
//                                if (isConnected) {
//                                    JSONObject requestJson = new JSONObject();
//                                    String refreshToken = myPref.getPref(Constants.REFRESH_TOKEN,"");
//                                    String username = myPref.getPref(Constants.USERNAME,"");
//                                    try {
//                                        requestJson.put("query", "mutation renewToken{ RenewToken(refreshToken:\""+refreshToken+"\",username:\""+username+"\"){ message}}");
//                                        webRequest.POST_METHOD(Constants.REFRESH_TOKEN_URL, requestJson, null, GenerateCodeFragment.this, WebCallType.GET_ACCESS_TOKEN, false);
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    firegeneretecodemutation = true;
//                                    fireissuecodemutation = false;
//                                }
//                            }
//                        }
//                        System.out.println(response.errors().toString());
//                        System.out.println(response.data());
//                        if (response.data() != null) {
//                            System.out.println("------response-----");
//                            System.out.println(response.data().toString());
//                            if (response.data().generateIssueCodeMutation() == null) {
////                                btn_generatecode.setEnabled(false);
////                                btn_issuecode.setEnabled(false);
////                                CodeErrorDialog codeErrorDialog = new CodeErrorDialog(getContext(), "Your card is not belongs from this account please write data first into card.", GenerateCodeFragment.this);
////                                codeErrorDialog.show();
//                            } else {
//                                if (response.data().generateIssueCodeMutation().codeArr().get(0).hashTop() != null) {
//                                    hashtop = response.data().generateIssueCodeMutation().codeArr().get(0).hashTop();
//                                    PPID = response.data().generateIssueCodeMutation().accountProduct().get(0).productItemPAYG_SN();
//                                    codehasformate = response.data().generateIssueCodeMutation().codeArr().get(0).otpHashFormatted();
//                                    System.out.println("-------------api online---------------"+response.data().generateIssueCodeMutation().codeArr().get(0).otpHashFormatted());
//                                }
//                            }
//
//                        }
//                    }
//                });
//
//            }
//
//            @Override
//            public void onFailure(@Nonnull ApolloException e) {
//
//            }
//        });
//
//    }


//    private String makeHastop(String hashtop) {
//        List<String> list = new ArrayList<>();
//        String newHashtop = "";
//        for (int i = 0; i < hashtop.length(); i = i + 2) {
//            String firsttwochar = hashtop.substring(i, i + 2);
//            list.add(firsttwochar);
//        }
//
//        for (int i = list.size(); i > 0; i--) {
//            newHashtop = newHashtop + list.get(i - 1);
//        }
//
//        System.out.println("------------");
//        System.out.println(newHashtop);
//        System.out.println("------------");
//
//        return newHashtop;
//    }
//
//    public String FindLength(String data) {
//        int len = data.length();
//        len = len / 2;
//        len = len + 2;
//        return ConstantMethod.toHex(len);
//    }
//
//    private String CheckCRC_8_MAXIM(AlgoParams[] params, byte[] TestBytes) {
//        for (int i = 0; i < params.length; i++) {
//            CrcCalculator calculator = new CrcCalculator(params[i]);
//            long result = calculator.Calc(TestBytes, 0, TestBytes.length);
//            if (result != calculator.Parameters.Check) {
//                System.out.println(calculator.Parameters.Name + " - BAD ALGO!!! " + Long.toHexString(result).toUpperCase());
//                return Long.toHexString(result).toUpperCase();
//            }
//        }
//        return null;
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBus event) {
        /* Do something */
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        Intent intent = new Intent(getContext(), APPClientService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
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
}
