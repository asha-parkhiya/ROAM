package com.sparkle.roam.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.sparkle.roam.LocalDatabase.DatabaseHelper;
import com.sparkle.roam.Model.SendRequest;
import com.sparkle.roam.Utils.Constants;
import com.sparkle.roam.Utils.MyPref;
import com.sparkle.roam.WebServices.WebCallType;
import com.sparkle.roam.WebServices.WebRequest;
import com.sparkle.roam.WebServices.WebResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("Registered")
public class BackgroundService extends Service implements WebResponseListener {

    private final IBinder mBinder = new LocalBinder();
    private MyPref myPref;
    private WebRequest webRequest;
    String token = null;
    private DatabaseHelper mdbhelper;
    private List<SendRequest> sendRequestList;

    public class LocalBinder extends Binder {
        public BackgroundService getService() {


            return BackgroundService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myPref = new MyPref(getApplicationContext());
        webRequest = new WebRequest(getApplicationContext());
        mdbhelper = new DatabaseHelper(getApplicationContext());
        sendRequestList = new ArrayList<>();

        Cursor res = mdbhelper.getBatchCodeData();
        while (res.moveToNext()){
            String userID = res.getString(res.getColumnIndex("userID"));
            String days = res.getString(res.getColumnIndex("days"));
            SendRequest sendRequest = new SendRequest(userID,days);
            sendRequestList.add(sendRequest);
        }
        System.out.println("-----------list---------------"+sendRequestList.size()+sendRequestList);

        System.out.println("--------------network------------------"+isNetworkConnected());
        boolean isConnected = isNetworkConnected();
        if (isConnected) {
            JSONObject requestJson = new JSONObject();
            String refreshToken = myPref.getPref(Constants.REFRESH_TOKEN,"");
            String username = myPref.getPref(Constants.USERNAME,"");
            try {
                requestJson.put("query", "mutation renewToken{ RenewToken(refreshToken:\""+refreshToken+"\",username:\""+username+"\"){ message}}");
                webRequest.POST_METHOD(Constants.REFRESH_TOKEN_URL, requestJson, null, BackgroundService.this, WebCallType.GET_ACCESS_TOKEN, false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                    JSONObject jsonObject = (JSONObject) response;

                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONObject RenewToken = data.getJSONObject("RenewToken");
                    String access_token = RenewToken.getString("message");
                    System.out.println("------------------------"+access_token);
                    myPref.setPref(Constants.OFFLINE_TOKEN,access_token);
                    token = access_token;

                    if (token != null){

                    }
                    break;
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
