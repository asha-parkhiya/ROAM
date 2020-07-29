package com.sparkle.roam.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sparkle.roam.LocalDatabase.DatabaseHelper;
import com.sparkle.roam.LoginData;
import com.sparkle.roam.LoginRoomDatabase;
import com.sparkle.roam.Model.PayEventSync.PayEventData;
import com.sparkle.roam.Model.SyncData.PayAccountData;
import com.sparkle.roam.Model.SyncProductData.ProductData;
import com.sparkle.roam.Model.SyncUserData.UserData;
import com.sparkle.roam.MyApplication;
import com.sparkle.roam.R;
import com.sparkle.roam.Utils.Constants;
import com.sparkle.roam.Utils.MyPref;
import com.sparkle.roam.View.MessagePrintDialog;
import com.sparkle.roam.WebServices.WebCallType;
import com.sparkle.roam.WebServices.WebRequest;
import com.sparkle.roam.WebServices.WebResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, WebResponseListener, MessagePrintDialog.OnOkClose {
    MyPref myPref;
    WebRequest webRequest;

    TextInputEditText et_username, et_password;
    Button btn_login;

    String token;
    String usertype;
    String refreshToken;
    int flag;

    LoginRoomDatabase mLoginDatabase;
    boolean logincredantial = false;

    DatabaseHelper myDb;

    Gson gson;

    int payAccOffset = 0;
    int payEveOffset = 0;
    int userOffset = 0;
    int productOffset = 0;

    boolean payAcccall = false;
    boolean payEvecall = false;
    boolean usercall = false;
    boolean productcall = false;

    boolean one = false;
    boolean two = false;
    boolean three = false;
    boolean four = false;
    boolean done = true;

    String lastSyncDatePayAccount;
    String lastSyncDatePayEvent;
    String lastSyncDateUser;
    String lastSyncDateProductItem;

    private ProgressDialog processDialog;
    private String android_id, username;

//    Thread thread1,thread2,thread3,thread4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        webRequest = new WebRequest(this);
        mLoginDatabase = ((MyApplication) getApplication()).getDatabase();

        btn_login = findViewById(R.id.btn_login);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_login.setOnClickListener(this);
        myPref = new MyPref(this);

        myDb = new DatabaseHelper(this);


        gson = new Gson();

        processDialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);

        boolean isfirsttimerun = myPref.getPref(Constants.IS_FIRSTTIMERUN, true);
        if (!isfirsttimerun) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("key", myPref.getPref(Constants.OFFLINE_TOKEN, ""));
            startActivity(intent);
            finish();
        }
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        System.out.println("-------android-id--------"+android_id);
    }

    private boolean isValidEmailId(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    private boolean passValidation() {
        if (et_password.getText().toString().trim().equals("")) {
            et_password.setError("Please Enter password");
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                boolean pass = passValidation();
                boolean email = isValidEmailId(et_username.getText().toString().trim());

                boolean isConnected = isNetworkConnected();

                if (pass && email) {
                    if (isConnected) {
                        JSONObject requestJson = new JSONObject();
                        try {
                            requestJson.put("MobileID", android_id);
                            requestJson.put("username", et_username.getText().toString().trim());
                            requestJson.put("password", et_password.getText().toString().trim());
                            myPref.setPref(Constants.DEVICE_ID, android_id);
                            myPref.setPref(Constants.USER_NAME, et_username.getText().toString().trim());
                            android_id = myPref.getPref(Constants.DEVICE_ID,"");
                            username = myPref.getPref(Constants.USER_NAME,"");
                            showProcess();
                            webRequest.POST_METHOD(Constants.LOGIN_URL, requestJson, null, this, WebCallType.GET_LOGIN, false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        new getTask(mLoginDatabase).execute();
                    }
                } else {
                    et_username.setError("please Enter valid Email");
                }
                break;

        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onError(String message) {
        Toast.makeText(LoginActivity.this, ""+message, Toast.LENGTH_SHORT).show();
        System.out.println("----------error-------------"+message);
        cancelProcess();
    }

    public void openHomeActivity(){
        if (done){
            if (one){
                one = false; two = false; three = false; four = false; done = false; open();
            }if (two){
                one = false; two = false; three = false; four = false; done = false; open();
            }if (three){
                one = false; two = false; three = false; four = false; done = false; open();
            }if (four){
                two = false; three = false; one = false; four = false; done = false; open();
            }
        }

    }

    public void open(){
        myDb.updateLastSyncData(String.valueOf(1),lastSyncDatePayAccount,lastSyncDatePayEvent,lastSyncDateUser,lastSyncDateProductItem);
        System.out.println("-----------------------updatelastsync");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cancelProcess();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("key", token);
                myPref.setPref(Constants.OFFLINE_TOKEN, token);
                myPref.setPref(Constants.REFRESH_TOKEN, refreshToken);
                myPref.setPref(Constants.USERNAME,et_username.getText().toString().trim());
                startActivity(intent);
                finish();
            }
        }, 3000);
    }

    public void callSyncApi(){
        webRequest.GET_METHOD(Constants.SYNC_DATA_URL+"0/10/"+username+"/"+android_id, LoginActivity.this,  WebCallType.GET_PAYACCOUNT_DATA, false, token);
        webRequest.GET_METHOD(Constants.PAYEVENT_SYNC_DATA_URL+"0/10/"+username+"/"+android_id, LoginActivity.this,  WebCallType.GET_PAYEVENT_DATA, false, token);
        webRequest.GET_METHOD(Constants.USER_SYNC_DATA_URL+"0/10/"+username+"/"+android_id, LoginActivity.this,  WebCallType.GET_USER_DATA, false, token);
        webRequest.GET_METHOD(Constants.PRODUCT_SYNC_DATA_URL+"0/10/"+username+"/"+android_id, LoginActivity.this,  WebCallType.GET_PRODUCT_DATA, false, token);
    }

    @Override
    public void OnOkClose(boolean msg) {
        callSyncApi();
    }

    @Override
    public void onResponse(Object response, WebCallType webCallType) {
        if (response != null) {
            switch (webCallType) {
                case GET_LOGIN:
                    System.out.println("-------------login------------------"+response);
                    if (response.toString().equals("{\"message\":\"User already loggedIn in some device\"}")){
                        Toast.makeText(LoginActivity.this, "User already loggedIn in some device.", Toast.LENGTH_SHORT).show();
                        cancelProcess();
                    }else if (response.toString().equals("{\"message\":\"User does not exists\"}")){
                        Toast.makeText(LoginActivity.this, "User does not exists.", Toast.LENGTH_SHORT).show();
                        cancelProcess();
                    }else {
                        JSONObject jsonObject = (JSONObject) response;
                        try {
                            token = jsonObject.getString("accessToken");
                            usertype = jsonObject.getString("user_role");
                            refreshToken = jsonObject.getString("refreshToken");
                            flag = jsonObject.getInt("flag");
                            myPref.setPref(Constants.REFRESH_TOKEN, refreshToken);
//                        insert(token, usertype);
                            new getTask(mLoginDatabase).execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
                        Calendar c = Calendar.getInstance();
                        String formattedDate = input.format(c.getTime());
                        myDb.insertLastSyncData(String.valueOf(1),formattedDate,formattedDate,formattedDate,formattedDate);
                        System.out.println("-----------------------insertlastsync");
                        if (flag == 0){
                            MessagePrintDialog messagePrintDialog = new MessagePrintDialog(this, "Welcome to ROAM. Please stay online and sync with ROAM Hub to get your assignments. You assignments are all synced. Please always sync with OVES Hub when you are online.", this);
                            messagePrintDialog.show();
                        }else {
                            callSyncApi();
                        }
                    }

                    break;

                case GET_PAYACCOUNT_DATA:

                    try {
                        JSONObject json =  new JSONObject(String.valueOf(response));
                        System.out.println(json);
                        String data = json.getString("data");
                        System.out.println("--------data---------"+data);
                        int total = json.getInt("total");
                        int limit = json.getInt("limit");
                        lastSyncDatePayAccount = json.getString("lastSyncDate");

                        int loopcount = total/limit;

                        JSONArray jsonArray = new JSONArray(data);

                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            PayAccountData payAccountData = gson.fromJson(jsonObject1.toString(),PayAccountData.class);
                            myDb.insertPayAccountData(String.valueOf(payAccountData.getPayAccountID()),String.valueOf(payAccountData.getStartDate()),String.valueOf(payAccountData.getDepositDays()),String.valueOf(payAccountData.getPayoffAmt()),String.valueOf(payAccountData.getMinPayDays()),
                                    String.valueOf(payAccountData.getMaxPayDays()),String.valueOf(payAccountData.getSchPayDays()),String.valueOf(payAccountData.getUserID()),String.valueOf(payAccountData.getDistributorID()),String.valueOf(payAccountData.getAssignedItemsID()),String.valueOf(payAccountData.getAgentID()),
                                    String.valueOf(payAccountData.getAgentAssignmentStatus()),String.valueOf(payAccountData.getAgentAssignment()),String.valueOf(payAccountData.getInitialCreditDays()),String.valueOf(payAccountData.getReceivedPayAmt()),String.valueOf(payAccountData.getDurationDays()),
                                    String.valueOf(payAccountData.getCreditDaysIssued()),String.valueOf(payAccountData.getPayDaysReceived()));
                            myPref.setPref(Constants.AGENTID,payAccountData.getAgentID());
                        }

                        if (loopcount == 0){
                            payAcccall = true;
                        }else {
                            if (!payAcccall){
                                for (int i = 0; i < loopcount; i++){
                                    payAccOffset = payAccOffset + limit;
                                    webRequest.GET_METHOD(Constants.SYNC_DATA_URL + payAccOffset + "/10/"+username+"/"+android_id,this,  WebCallType.GET_PAYACCOUNT_DATA, false, token);
                                    if (loopcount == i+1){
                                        payAcccall = true;
                                    }
                                }
                            }
                        }

                        if (productcall && usercall && payAcccall && payEvecall){
                            one = true;
                            openHomeActivity();
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
                        System.out.println("--------data---------"+data);
                        int total = json.getInt("total");
                        int limit = json.getInt("limit");
                        lastSyncDatePayEvent = json.getString("lastSyncDate");

                        int loopcount = total/limit;
                        JSONArray jsonArray = new JSONArray(data);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            PayEventData payEventData = gson.fromJson(jsonObject1.toString(), PayEventData.class);
                            myDb.insertPayEventData(String.valueOf(payEventData.getPayEventID()),String.valueOf(payEventData.getPayEventDate()),String.valueOf(payEventData.getPayDays()),String.valueOf(payEventData.getPayRecordAmt()),
                                    String.valueOf(payEventData.getPayRecordNotes()),String.valueOf(payEventData.getPayAccountID()),String.valueOf(payEventData.getEventType()),String.valueOf(payEventData.getCodeDays()),String.valueOf(payEventData.getCodeIssued()),String.valueOf(payEventData.getCodehashTop()),"");
                        }
                        if (loopcount == 0){
                            payEvecall = true;
                        }else {
                            if (!payEvecall){
                                for (int i = 0; i < loopcount; i++){
                                    payEveOffset = payEveOffset + limit;
                                    webRequest.GET_METHOD(Constants.PAYEVENT_SYNC_DATA_URL + payEveOffset + "/10/"+username+"/"+android_id,this,  WebCallType.GET_PAYEVENT_DATA, false, token);
                                    if (loopcount == i+1){
                                        payEvecall = true;
                                    }
                                }
                            }
                        }

                        if (productcall && usercall && payAcccall && payEvecall){
                            two = true;
                            openHomeActivity();
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
                        System.out.println("--------data---------"+data);
                        int total = json.getInt("total");
                        int limit = json.getInt("limit");
                        lastSyncDateUser = json.getString("lastSyncDate");

                        int loopcount = total/limit;
                        JSONArray jsonArray = new JSONArray(data);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            UserData userData = gson.fromJson(jsonObject1.toString(), UserData.class);
                            myDb.insertUserData(String.valueOf(userData.getUserID()),String.valueOf(userData.getDistributorID()),String.valueOf(userData.getUserCode()),String.valueOf(userData.getAgentID()),String.valueOf(userData.getLastName()),String.valueOf(userData.getFirstName()),
                                    String.valueOf(userData.getPhoneNumber()),String.valueOf(userData.getEmail()),String.valueOf(userData.getLocationGPS()),String.valueOf(userData.getAddress1()),String.valueOf(userData.getAddress2()),String.valueOf(userData.getCity()),String.valueOf(userData.getState()),
                                    String.valueOf(userData.getCountry()),String.valueOf(userData.getPostCode()));
                        }


                        if (loopcount == 0){
                            usercall = true;
                        }else {
                            if (!usercall){
                                for (int i = 0; i < loopcount; i++){
                                    userOffset = userOffset + limit;
                                    webRequest.GET_METHOD(Constants.USER_SYNC_DATA_URL + userOffset + "/10/"+username+"/"+android_id,this,  WebCallType.GET_USER_DATA, false, token);
                                    if (loopcount == i+1){
                                        usercall = true;
                                    }
                                }
                            }
                        }

                        if (productcall && usercall && payAcccall && payEvecall){
                            three = true;
                            openHomeActivity();
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
                        System.out.println("--------data---------"+data);
                        int total = json.getInt("total");
                        int limit = json.getInt("limit");
                        lastSyncDateProductItem = json.getString("lastSyncDate");

                        int loopcount = total/limit;
                        JSONArray jsonArray = new JSONArray(data);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            ProductData productData = gson.fromJson(jsonObject1.toString(), ProductData.class);
                            myDb.insertProductItemData(String.valueOf(productData.getProductItemID()),String.valueOf(productData.getProductModelID()),String.valueOf(productData.getProductBatchID()),String.valueOf(productData.getProductItemOEMSN()),
                                    String.valueOf(productData.getProductItemPAYGSN()),String.valueOf(productData.getLifeCycleStatus()),String.valueOf(productData.getFirmwareVersion()),String.valueOf(productData.getAssignedItemsID()));
                        }
                        if (loopcount == 0){
                            productcall = true;
                        }else {
                            if (!productcall){
                                for (int i = 0; i < loopcount; i++){
                                    productOffset = productOffset + limit;
                                    webRequest.GET_METHOD(Constants.PRODUCT_SYNC_DATA_URL + productOffset + "/10/"+username+"/"+android_id,this,  WebCallType.GET_PRODUCT_DATA, false, token);
                                    if (loopcount == i+1){
                                        productcall = true;
                                    }
                                }
                            }
                        }

                        if (productcall && usercall && payAcccall && payEvecall){
                            four = true;
                            openHomeActivity();
                            System.out.println("-------------------hello here ---------product---==========");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    public void insert(String token, String usertype, String uname, String passrd, String refreshToken) {
        LoginData loginData = new LoginData();
        loginData.setUsername(uname);
        loginData.setPassword(passrd);
        loginData.setToken(token);
        loginData.setUsertype(usertype);
        loginData.setRefreshToken(refreshToken);

        new Insert(mLoginDatabase).execute(loginData);
    }

    private void showProcess() {
        if (processDialog != null) {
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

    private class Insert extends AsyncTask<LoginData, Void, Void> {
        LoginRoomDatabase mLoginDatabase;
        Handler handler;

        public Insert(LoginRoomDatabase mLoginDatabase) {
            this.mLoginDatabase = mLoginDatabase;
            this.handler = new Handler();
        }


        @Override
        protected Void doInBackground(LoginData... loginData) {
            mLoginDatabase.loginDataDao().insert(loginData[0]);
            return null;
        }
    }


    public class getTask extends AsyncTask<Void, Void, List<LoginData>> {
        LoginRoomDatabase mLoginDatabase;

        public getTask(LoginRoomDatabase mLoginDatabase) {
            this.mLoginDatabase = mLoginDatabase;
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

                if (mloginData.getUsername().equals(et_username.getText().toString().trim())) {
                    usrname = true;
                }

                if (mloginData.getPassword().equals(et_password.getText().toString().trim())) {
                    pass = true;
                    position = i;
                    break;

                } else {
                    usrname = false;
                    pass = false;
                }
            }

            if (usrname && pass) {
                logincredantial = true;
            } else {
                logincredantial = false;
            }

            if (isNetworkConnected()) {
                if (!logincredantial) {
                    insert(token, usertype, et_username.getText().toString().trim(), et_password.getText().toString().trim(),refreshToken);
                    myPref.setPref(Constants.OFFLINE_TOKEN, token);
                    myPref.setPref(Constants.REFRESH_TOKEN, refreshToken);
                    myPref.setPref(Constants.USERNAME, et_username.getText().toString().trim());
                }else {
                    myPref.setPref(Constants.OFFLINE_TOKEN, loginData.get(position).getToken());
                    myPref.setPref(Constants.REFRESH_TOKEN, loginData.get(position).getRefreshToken());
                    myPref.setPref(Constants.USERNAME, loginData.get(position).getUsername());
                }
            } else {
                if (logincredantial) {
//                    Toast.makeText(LoginActivity.this, "LoginFrom Offline", Toast.LENGTH_SHORT).show();
                    myPref.setPref(Constants.OFFLINE_TOKEN, loginData.get(position).getToken());
                    myPref.setPref(Constants.REFRESH_TOKEN, loginData.get(position).getRefreshToken());
                    myPref.setPref(Constants.USERNAME, loginData.get(position).getUsername());
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    i.putExtra("key", loginData.get(position).getToken());
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Please Connect to Internet or \n Check your Email id or password", Toast.LENGTH_SHORT).show();
                }

            }

        }
    }

}
