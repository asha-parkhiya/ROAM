package com.sparkle.roam.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateEventMutation;
import com.amazonaws.amplify.generated.graphql.IssueCodeMutation;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.printlibrary.permission.PermissionCenter;
import com.example.printlibrary.utils.DimensUtil;
import com.example.printlibrary.utils.LogUtils;
import com.example.printlibrary.utils.SharedPreferencesUtils;
import com.sparkle.roam.Activity.HomeActivity;
import com.sparkle.roam.Adapter.NewAdapter;
import com.sparkle.roam.ContentProvider.ToDo;
import com.sparkle.roam.ContentProvider.ToDoListDBAdapter;
import com.sparkle.roam.LocalDatabase.DatabaseHelper;
import com.sparkle.roam.MyApplication;
import com.sparkle.roam.Print.app.App;
import com.sparkle.roam.Print.bean.BluetoothInfoBean;
import com.sparkle.roam.Print.bean.PrintParamsBean;
import com.sparkle.roam.Print.service.Global;
import com.sparkle.roam.Print.service.StartZPService;
import com.sparkle.roam.Print.service.ZPrinterManager;
import com.sparkle.roam.Print.utils.BluetoothManager;
import com.sparkle.roam.Print.utils.ToastUtil;
import com.sparkle.roam.Print.utils.UIHelp;
import com.sparkle.roam.Print.view.customView.AddIconImageView;
import com.sparkle.roam.Print.view.customView.DragView;
import com.sparkle.roam.Print.view.edit.EditActivity;
import com.sparkle.roam.Print.view.edit.EditorMethod;
import com.sparkle.roam.Print.view.main.MainActivity;
import com.sparkle.roam.Print.view.main.MainPrintView;
import com.sparkle.roam.R;
import com.sparkle.roam.Utils.Constants;
import com.sparkle.roam.Utils.MyPref;
import com.sparkle.roam.View.CodeErrorDialog;
import com.sparkle.roam.WebServices.WebCallType;
import com.sparkle.roam.WebServices.WebRequest;
import com.sparkle.roam.WebServices.WebResponseListener;
import com.sparkle.roam.services.APPClientService;
import com.sparkle.roam.services.MqttService;
import com.sparkle.roam.services.TestReceiver;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import type.CreateEventInput;
import type.IssueCodeInput;
import type.RequestMetaInput;

import static android.view.View.VISIBLE;
import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;
import static com.sparkle.roam.Print.view.bluetooth.SettingBTActivity.ICON;
import static com.sparkle.roam.Print.view.bluetooth.SettingBTActivity.PRINTERMAC;
import static com.sparkle.roam.Print.view.bluetooth.SettingBTActivity.PRINTERNAME;
import static com.sparkle.roam.Print.view.main.MainActivity.BT_STA_CONNECTED;
import static com.sparkle.roam.Print.view.main.MainActivity.BT_STA_CONNECTING;
import static com.sparkle.roam.Print.view.main.MainActivity.BT_STA_DISCONNECT;


public class GenerateCodeDialog extends DialogFragment implements View.OnClickListener, CodeErrorDialog.OnLogout, WebResponseListener, NewAdapter.onItemClickListener{

    private GenerateCodeDialog.OnIssuecode onIssuecode;
    private String token = null;
    private String currentTime;

    private int limit;
    private int loop = 0;
    private int position;

    private String notes = null;

    private String PPID = "";
    private String firmwareVersion = "";
    private String agentassignment = "";
    private String issuecode = null;
    private int day = 0;
    private String codevalue = null;
    private TextView tv_showcode;
    private EditText et_code, et_note;

    private ArrayList<String> codelist;
    private ArrayList<String> codehashtoplist;

    private Button btn_generatecode, btn_issuecode,btn_print_aactivationcode;
    private int payAccID,agentID;
    private MyPref myPref;
    private String codehashtop;
    private String codehasformate = "";
    private ImageButton btn_back;

    private String search;
    private boolean isfromHome = false;
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
    private ImageButton bt_close;

    APPClientService aPPClientService;
    boolean mBound = false;
    boolean mBound1 = false;
    MqttService mService;
    RelativeLayout rl;

    private static final String TAG = "MainActivity";

//    private AnimationDrawable mAnimationDrawable = null;
    // 用于处理各种通知消息，刷新界面的handler
    private final Handler mHandler = new Handler();
    private EditActivity mEditActivity;

    private StartZPService mStartZPService = null;
    private ZPrinterManager mPrinterManager = null;
    private ImageView mStateIv = null;
    private TextView mNameTv = null;
    private PermissionCenter mPermissionCenter = null;
    private DragView mDragView = null;
    private PrintParamsBean mPrintParamsBean = null;
//    private View mDragLayoutFl = null;
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
    private static EditorMethod mEditorMethod;
    private TextView text2;
    private TextView text4;
    private TextView text6;
    private TextView text8;
    String firstName;
    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver broadcastReceiver = null;
    private IntentFilter intentFilter = null;
    private BluetoothInfoBean mInfoBean = null;


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


    public interface OnIssuecode {
        void update();
    }

    public GenerateCodeDialog(GenerateCodeDialog.OnIssuecode onIssuecode, boolean isfromHome) {
        this.isfromHome = isfromHome;
        this.onIssuecode = onIssuecode;
    }
    public static GenerateCodeDialog newInstance(String AGENTASSIGNMENT, int payAccID, int limit, int position, GenerateCodeDialog.OnIssuecode onIssuecode, String search, boolean isfromHome, String firstname, String lastname, String productItemOESN, String firmwareVersion) {
        GenerateCodeDialog fragment = new GenerateCodeDialog(onIssuecode, isfromHome);
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
            System.out.println("-----------------------"+payAccID);
            limit = (int) getArguments().get(Constants.OFFSET);
            position = (int) getArguments().get(Constants.POSITION);
            firstName = getArguments().getString(Constants.FIRST_NAME);
            search = getArguments().getString(Constants.SEARCH_TEXT);
            PPID = getArguments().getString(Constants.PRODUCTOESN);
            firmwareVersion = getArguments().getString(Constants.FIRMWAREVERSION);
            agentassignment = getArguments().getString(Constants.AGENTASSIGNMENT);
            webRequest = new WebRequest(getContext());
            csvfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyCsvFile.csv");
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        System.out.println("------------------------dialog dismiss");
        EventBus.getDefault().post("Dialog Dismiss");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark_issuecode);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        tv_showcode = ((TextView)dialog.findViewById(R.id.tv_showcode));
        et_code = ((EditText)dialog.findViewById(R.id.et_code));
        et_note = ((EditText)dialog.findViewById(R.id.et_note));
        btn_issuecode = ((Button)dialog.findViewById(R.id.btn_issuecode));
        btn_print_aactivationcode = ((Button)dialog.findViewById(R.id.btn_print_aactivationcode));
        btn_generatecode = ((Button)dialog.findViewById(R.id.btn_generatecode));
        bt_close = ((ImageButton)dialog.findViewById(R.id.bt_close));

        btn_generatecode.setOnClickListener(this);
        btn_issuecode.setOnClickListener(this);
        btn_print_aactivationcode.setOnClickListener(this);
        bt_close.setOnClickListener(this);
        text2 = dialog.findViewById(R.id.text2);
        text4 = dialog.findViewById(R.id.text4);
        text6 = dialog.findViewById(R.id.text6);
        text8 = dialog.findViewById(R.id.text8);
        rl = dialog.findViewById(R.id.rl);

//        printer();
//        initData();

        mDragView = (DragView) dialog.findViewById(R.id.main_label_viewer);
        mContainerLl = dialog.findViewById(R.id.main_container_ll);
//        new MainPrintView(GenerateCodeDialog.this, mHandler);
        init();
        return dialog;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_close:
                dismiss();

                break;
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
                        msg = "You do not have enough credit. You remaining credit on this account is " + codelist.size() + " days";
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
                            mService.connect();
//                            if (mService.connect()) {
//                                Toast.makeText(getContext(), "MQTT connected.", Toast.LENGTH_SHORT).show();
//                            }else {
//                                Toast.makeText(getContext(), "MQTT not connected.", Toast.LENGTH_SHORT).show();
//                            }
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
//                    CreateEventMutationquery();

//                    et_code.setText("");
//                    et_code.setHint("Assignment days:");
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
//                    dismiss();
                }

//                btn_issuecode.setEnabled(false);
//                Drawable unwrappedDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.btn_back_disable);
//                btn_issuecode.setBackground(unwrappedDrawable);


                break;
            case R.id.btn_print_aactivationcode:
                if (!tv_showcode.getText().toString().equals("Issue Code")){
//                    if (!StartZPService.getInstance().getPrinterManager().workThread.isConnected()) {
                    Toast.makeText(getContext(), "Printer not connected", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                    currentPrint();
                }else {
                    Toast.makeText(getContext(), "First Generate Code.", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    public void init(){
        myPref = new MyPref(getContext());
        token = myPref.getPref(Constants.OFFLINE_TOKEN, "");


        btn_generatecode.setOnClickListener(this);
        btn_issuecode.setOnClickListener(this);
        ((HomeActivity) getActivity()).updateButtonAdd(false);

//        btn_issuecode.setEnabled(false);

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

            System.out.println("-----------------------codelist size---  "+codelist.size());

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

//        mService.connect();
        if (!mService.isConnected()) {
            if (!mBound1) return;
            if (mService.connect()) {
//                Toast.makeText(getContext(), "MQTT connected", Toast.LENGTH_SHORT).show();
                System.out.println("--------------------connect.");
            }else {
//                Toast.makeText(getContext(), "MQTT not connected", Toast.LENGTH_SHORT).show();
                System.out.println("--------------------not connect.");
            }

            mService.publish("_OV1/"+device_type+"/" + PPID, codehasformate);
        } else {
            mService.publish("_OV1/"+device_type+"/" + PPID, codehasformate);
        }
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
        Toast.makeText(getContext(), "Code successfully save.", Toast.LENGTH_SHORT).show();
//        dismiss();
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
        @SuppressLint("SimpleDateFormat") SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
        Calendar c = Calendar.getInstance();
        String formattedDate = input.format(c.getTime());

        mdbhelper.insertPayEventData(String.valueOf(str),formattedDate,"","",
                "",String.valueOf(payAccID),"codeIssue",String.valueOf(codevalue),String.valueOf(codehasformate),String.valueOf(codehashtop), String.valueOf(0));

    }

    public void IssueCodeMutationquery() {
        String android_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        String username = myPref.getPref(Constants.USER_NAME, "");

        IssueCodeInput issueCodeInput = IssueCodeInput.builder().payRecordNote(notes).assignmentDays(day).agentID(agentID).payAccountID(payAccID).build();
        RequestMetaInput requestMetaInput = RequestMetaInput.builder().mobileID(android_id).username(username).build();
        final IssueCodeMutation issueCodeMutation = IssueCodeMutation.builder().input(issueCodeInput).requestMeta(requestMetaInput).build();
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
                                        webRequest.POST_METHOD(Constants.REFRESH_TOKEN_URL, requestJson, null, GenerateCodeDialog.this, WebCallType.GET_ACCESS_TOKEN, false);
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
        Intent intent1 = new Intent(getContext(), MqttService.class);
        getActivity().bindService(intent1, mConnection1, Context.BIND_AUTO_CREATE);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBound1) {
            getActivity().unbindService(mConnection1);
            mBound1 = false;
        }
    }

//    private Handler mLoopHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.arg1 == msg.arg2) {
//                int sta = msg.arg1;
//
//                switch (sta) {
//                    case BT_STA_CONNECTED:
//                        try {
//                            if (msg.obj != null) {
//                                BluetoothInfoBean bt_bean = (BluetoothInfoBean)msg.obj;
//                                mBtNameStr = bt_bean.getName();
//                                mBtAddressStr = bt_bean.getAddress();
//                            }
//                            if (mBtNameStr != null) {
//                                mNameTv.setText(mBtNameStr);// + "-" + bean.getAddress()
//                            }
////                            stopAnimition();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        } finally {
//                            mHandler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    SharedPreferencesUtils.putBooleanPreferences(getContext(), UIHelp.PRINTOR, true);
//                                    SharedPreferencesUtils.putStringPreferences(getContext(), "BT_Address", mBtAddressStr);
//                                    sendToast("Connected");
//                                }
//                            }, 1000);
//                        }
//                        break;
//                    case BT_STA_DISCONNECT:
//                    default:
//                        mHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                SharedPreferencesUtils.putBooleanPreferences(getContext(), UIHelp.PRINTOR, false);
//                                StartZPService.getInstance().getPrinterManager().workThread.disconnectBt();
//                            }
//                        });
//                        if (mBtStatus != BT_STA_DISCONNECT) {
//                            sendToast(getString(R.string.mst_bt_connect_fail));
//                        }
//                        break;
//                }
//                mBtStatus = sta;
//            }
//        }
//    };
//
//    public void sendToast(final String cotent){
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    ToastUtil.showShortToast(getContext(), cotent);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//    }
//
//
//    private void setBTInfo(BluetoothInfoBean bean) {
//        boolean isConn = false;
//        if (bean.getResult() == 1) {
//            if (StartZPService.getInstance().getPrinterManager().workThread.isConnected()) {
//                isConn = true;
//            }
//        }
//        connectedBluetooth(isConn, bean);
//    }
//
//    // 发送更新BT状态的消息
//    private void sendBtStatusMessage(int sta, Object obj) {
//        //mLoopHandler.removeMessages(0);
//
//        Message message = new Message();
//        message.what = 0;
//        message.arg1 = sta;
//        message.arg2 = sta;
//        message.obj = obj;
//        mLoopHandler.sendMessage(message);
//
//        LogUtils.d(TAG, "--bt_connect_msg=" + sta);
//    }
//
//    // 发送更新BT状态的消息
//    private void sendBtStatusMessage(int sta) {
//        sendBtStatusMessage(sta, null);
//    }
//
//    public void connectedBluetooth(final boolean isConnected) {
//        connectedBluetooth(isConnected, null);
//    }
//
//    public void connectedBluetooth(final boolean isConnected, Object obj) {
//        if (isConnected) {
//            sendBtStatusMessage(BT_STA_CONNECTED, obj);
//        } else {
//            sendBtStatusMessage(BT_STA_DISCONNECT);
//        }
//    }

//    public void printer(){
//        mPermissionCenter = new PermissionCenter(getActivity());
//        mPermissionCenter.verifyStoragePermissions(getActivity());
//        mPermissionCenter.permissionCheck(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION});
//        mPermissionCenter.permissionCheck(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
//        //mPermissionCenter.permissionCheck(new String[]{Manifest.permission.WRITE_DATABASE});
//
//        mStartZPService = StartZPService.getInstance();
//        mPrinterManager = mStartZPService.getPrinterManager();
//        if (mPrinterManager == null && !mStartZPService.isBindService()) {
//            mStartZPService.bindingZService(MyApplication.getContext());
//        }
//
//        initFilter();
//
//
//
//
//    }
//
//
//
//    private void initFilter() {
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        mBluetoothAdapter.startDiscovery();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("BluetoothItem");
//        intentFilter.addAction("BluetoothStatus");
//        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED); //配对结束时，断开连接
//        getContext().registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
//    }
//
//    private final BroadcastReceiver blReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if(BluetoothDevice.ACTION_FOUND.equals(action)){
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                System.out.println("----------------------"+device.getName());
//                if (device.getName().equals("vivo Y51L")){
////                    pairDevice(device);
//                }
//            }
//        }
//    };


//    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent != null) {
//                String action = intent.getAction();
//                if (action.equals("BluetoothItem")) {
//                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    System.out.println("--------------"+device.getName());
//                    int blueTag = intent.getIntExtra("BlueTag", -1);
//                    BluetoothInfoBean bean = (BluetoothInfoBean) intent.getSerializableExtra("BluetoothInfo");
//                    if (bean != null) {
//                        switch (blueTag) {
//                            case Global.MSG_INFO_BEAN:
//                                setBTInfo(bean);
//                                break;
//                            default:
//                                sendBtStatusMessage(BT_STA_DISCONNECT);
//                                break;
//                        }
//                    } else {
//                        sendBtStatusMessage(BT_STA_DISCONNECT);
//                    }
//                } else if (action.equals("BluetoothStatus")) {
//                    int sta = intent.getIntExtra("BlueStatus", BT_STA_DISCONNECT);
//                    sendBtStatusMessage(sta);
//                }else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
//                    sendBtStatusMessage(BT_STA_DISCONNECT);
//                }
//            }
//        }
//    };
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (mStartZPService.getPrinterManager() != null) {
//            mPrinterManager = mStartZPService.getPrinterManager();
//        } else {
//            mStartZPService.bindingZService(MyApplication.getContext());
//        }
//    }
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//
//    }
//
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
////        mDragView = null;
//        //mHandler.removeCallbacks(runnable);
//        StartZPService.getInstance().getPrinterManager().workThread.disconnectBt();
//        getActivity().unregisterReceiver(mBroadcastReceiver);
//        mStartZPService.unBindingZService(App.getContext());
//        System.gc();
//    }
//
//
//    public void initData() {
//        mPrintParamsBean = new PrintParamsBean();
//        mPermissionCenter.verifyStoragePermissions(getActivity());
//
//        initBLU();
//    }
//
//    private void initBLU() {
//        /* 启动蓝牙 */
//        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//        if (null != adapter) {
//            if (!adapter.isEnabled()) {
//                if (!adapter.enable()) {
//
//                    return;
//                }
//            }
//        }
//    }
//
//    private void setScreenInfo() {
//        UIHelp.setWidgetRefresh(getContext(), mDragView, mPrintParamsBean);
//    }
//
//    private void initDragView(int width, int height){
//        mPrintParamsBean.setPrintWidth((int) UIHelp.mmToPointWidth(width));
//        mPrintParamsBean.setWidgetWidth(mWidgetWidth);
//        mPrintParamsBean.setCurrentWidth(width);
//
//        mPrintParamsBean.setPrintHeight((int) UIHelp.mmToPointHeight(height));
//        mPrintParamsBean.setWidgetHeight(mWidgetHeight);
//        mPrintParamsBean.setCurrentHeight(height);
//        setScreenInfo();
//    }
//
////    public void showPrint(View view) {
////        if (!StartZPService.getInstance().getPrinterManager().workThread.isConnected()) {
////            Toast.makeText(getContext(), "Printer not connected", Toast.LENGTH_SHORT).show();
////            return;
////        }
////        currentPrint();
////    }
    public void currentPrint(){
        StartZPService.getInstance().getPrinterManager().workThread.MyPrinter(mDragView, mPrintParamsBean);
        showMain();

    }
    private void showMain() {
        mContainerLl.setVisibility(View.VISIBLE);
//        findViewById(R.id.main_container_main_alert).setVisibility(View.GONE);
//        //edit->720;Height=640;DisplayWidth=720;DisplayHeight=1280
//        mDragView.post(new Runnable() {
//            @Override
//            public void run() {
//                mWidgetWidth = mDragView.getWidth();
//                mWidgetHeight = mDragView.getHeight();
//            }
//        });
//        mDragView.clearView();

        text2.setText(tv_showcode.getText().toString());
        text4.setText(et_code.getText().toString());
        text6.setText(firstName);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date());
        text8.setText(dateString);
        mDragView.addDragView(rl,mPrintParamsBean,false,false);
        // new
//        Intent intent = new Intent(getContext(), EditActivity.class);
//        intent.putExtra("isSet", false);
////        mPrintParamsBean.setBackModelPath("");
//        intent.putExtra("printParams", mPrintParamsBean);
//        startActivityForResult(intent, 1);
//        initDragView(40,30);

//        mEditorMethod = new EditorMethod(GenerateCodeDialog.this, mDragView, et_code);
//        mDragView.clearView();

//        mDragView.startPrint();

    }

}
