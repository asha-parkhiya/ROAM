package com.sparkle.roam.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.amplify.generated.graphql.CreateEventMutation;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
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

import javax.annotation.Nonnull;

import type.CreateEventInput;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;


public class AddPaymentDialog extends DialogFragment implements View.OnClickListener, CodeErrorDialog.OnLogout, WebResponseListener{

    private AddPaymentDialog.OnIssuecode onIssuecode;
    private String token = null;
    private String currentTime;

    private int limit;
    private int loop = 0;
    private int position;

    private String notes = null;

    private String PPID = "";
    private String firmwareVersion = "";
    private String agentassignment = "";
    private EditText et_code, et_amount,et_note;

    private ArrayList<String> codelist;
    private ArrayList<String> codehashtoplist;

    private Button btn_addpayment;
    private int payAccID,agentID;
    private MyPref myPref;


    private String search;
    private boolean isfromHome = false;

    private WebRequest webRequest;

    private boolean fireissuecodemutation = false;
    private boolean firegeneretecodemutation = false;

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
    String firstName;
    private DatabaseHelper mdbhelper;


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

    public AddPaymentDialog(AddPaymentDialog.OnIssuecode onIssuecode, boolean isfromHome) {
        this.isfromHome = isfromHome;
        this.onIssuecode = onIssuecode;
    }
    public static AddPaymentDialog newInstance(String AGENTASSIGNMENT, int payAccID, int limit, int position, AddPaymentDialog.OnIssuecode onIssuecode, String search, boolean isfromHome, String firstname, String lastname, String productItemOESN, String firmwareVersion) {
        AddPaymentDialog fragment = new AddPaymentDialog(onIssuecode, isfromHome);
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
            firstName = getArguments().getString(Constants.FIRST_NAME);
            search = getArguments().getString(Constants.SEARCH_TEXT);
            PPID = getArguments().getString(Constants.PRODUCTOESN);
            firmwareVersion = getArguments().getString(Constants.FIRMWAREVERSION);
            agentassignment = getArguments().getString(Constants.AGENTASSIGNMENT);
            webRequest = new WebRequest(getContext());
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
        dialog.setContentView(R.layout.dialog_dark_payment);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        et_code = dialog.findViewById(R.id.et_code);
        et_amount = dialog.findViewById(R.id.et_amount);
        et_note = dialog.findViewById(R.id.et_note);

        btn_addpayment = dialog.findViewById(R.id.btn_addpayment);
        bt_close = dialog.findViewById(R.id.bt_close);

        init();
        String locale = getContext().getResources().getConfiguration().locale.getCountry();
        System.out.println("------------------"+locale);

        mdbhelper = new DatabaseHelper(getContext());
        return dialog;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_close:
                dismiss();
                break;

            case R.id.btn_addpayment:

                btn_addpayment.requestFocus();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btn_addpayment.getWindowToken(), 0);

                if (et_code.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Please Enter the day", Toast.LENGTH_SHORT).show();
                }else if(et_amount.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Please Enter the Amount", Toast.LENGTH_SHORT).show();
                }else {
                    Offline(payAccID,et_amount.getText().toString(),notes);
                    CreateEventMutationquery();
                    Toast.makeText(getContext(), "Payment successfully.", Toast.LENGTH_SHORT).show();
                    et_code.setText("");
                    et_code.setHint("Assignment days");
                    et_amount.setText("");
                    et_amount.setHint("Please Enter Amount");
                    et_note.setText("");
                    et_note.setHint("Please Enter note");
                }
                break;
        }
    }

    public void init(){
        myPref = new MyPref(getContext());
        token = myPref.getPref(Constants.OFFLINE_TOKEN, "");

        btn_addpayment.setOnClickListener(this);
        bt_close.setOnClickListener(this);

        ((HomeActivity) getActivity()).updateButtonAdd(false);

        agentID = myPref.getPref(Constants.AGENTID,0);

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

    private void Offline(int payAccID, String amount, String notes) {

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

        mdbhelper.insertPayEventData(String.valueOf(str),formattedDate,"", amount,
                notes,String.valueOf(payAccID),"payCollect","","","", String.valueOf(0));

    }

    public void CreateEventMutationquery() {
        int day = Integer.parseInt(et_code.getText().toString());
        int amount = Integer.parseInt(et_amount.getText().toString());
        notes = et_note.getText().toString();
        if (notes.equals("")){
            notes = "paymentMutation";
        }
        CreateEventInput createEventInput = CreateEventInput.builder().eventType("payCollect").agentID(agentID).payAccountID(payAccID).payDays(day).payRecordAmt(amount).payRecordNotes(notes).build();
        final CreateEventMutation issueCodeMutation = CreateEventMutation.builder().input(createEventInput).build();
        aPPClientService.AWSAppSyncClient().mutate(issueCodeMutation).enqueue(new GraphQLCall.Callback<CreateEventMutation.Data>() {
            @Override
            public void onResponse(@Nonnull final Response<CreateEventMutation.Data> response) {
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
                                        webRequest.POST_METHOD(Constants.REFRESH_TOKEN_URL, requestJson, null, AddPaymentDialog.this, WebCallType.GET_ACCESS_TOKEN, false);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    fireissuecodemutation = true;
                                    firegeneretecodemutation = false;

                                }
                            }
                        }

                        System.out.println("---------mutation-----------"+response.data().createEventMutation());
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
                        CreateEventMutationquery();
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



}
