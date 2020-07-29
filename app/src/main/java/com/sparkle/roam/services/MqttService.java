//package com.sparkle.roam.services;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.Binder;
//import android.os.IBinder;
//import android.util.Log;
//import android.widget.Toast;
//
//
//import com.sparkle.roam.events.MqttStringEvent;
//
//import org.eclipse.paho.android.service.MqttAndroidClient;
//import org.eclipse.paho.client.mqttv3.IMqttActionListener;
//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.IMqttToken;
//import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
//import org.eclipse.paho.client.mqttv3.MqttCallback;
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.greenrobot.eventbus.EventBus;
//
//public class MqttService extends Service {
//    public static final String TAG = MqttService.class.getSimpleName();
//    public static final String MQTT_HOST = "tcp://mqtt.omnivoltaic.com";
//    public static final String USERNAME = "admin";
//    public static final String PASSWORD = "admin123";
//    private static final int CONNECT_TIMEOUT = 2000;
//    private final IBinder mBinder = new LocalBinder();
//    private MqttAsyncClient mMqttClient = null;
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return mBinder;
//    }
//
//    @Override
//    public void onDestroy() {
//        if (isConnected()) disconnect();
//    }
//
//    public void connect(final String uri, final String clieintId, final String username, final String password) {
//        String clientId = MqttClient.generateClientId();
////            mMqttClient = new MqttAsyncClient(MQTT_HOST, clientId, null);
//        mMqttClient.setCallback(new MyMqttCallback());
//
//        MqttConnectOptions options = new MqttConnectOptions();
////        options.setUserName(username);
////        options.setPassword(password.toCharArray());
//
//        try {
////            options = new MqttConnectOptions();
//            options.setUserName(USERNAME);
//            options.setPassword(PASSWORD.toCharArray());
//            final IMqttToken connectToken = mMqttClient.connect(options);
//            connectToken.waitForCompletion(CONNECT_TIMEOUT);
//        } catch (MqttException e) {
//            Log.d(TAG, "Connection attempt failed with reason code: " + e.getReasonCode() + ":" + e.getCause());
//            e.printStackTrace();
//        }
//    }
//
//    public boolean connect() {
//        try {
//            String clientId = MqttClient.generateClientId();
//            mMqttClient = new MqttAsyncClient(MQTT_HOST, clientId, null);
////            mMqttClient = new MqttAndroidClient(getApplicationContext(), MQTT_HOST, clientId);
//            mMqttClient.setCallback(new MyMqttCallback());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return false;
//        }
//        MqttConnectOptions options = new MqttConnectOptions();
//
//        try {
//            options.setUserName(USERNAME);
//            options.setPassword(PASSWORD.toCharArray());
//            final IMqttToken connectToken = mMqttClient.connect(options);
////            final IMqttToken connectToken = mMqttClient.connect();
////            connectToken.waitForCompletion(CONNECT_TIMEOUT);
//
//            connectToken.setActionCallback(new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    subscribe("ROAM/PPID", 0);
//                }
//
//                @Override
//                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//
//                }
//            });
//        } catch (MqttException e) {
//            Log.d(TAG, "Connection attempt failed with reason code: " + e.getReasonCode() + ":" + e.getCause());
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//
//    public void publish(final String topic, final String payload) {
//        try {
//            MqttMessage mqttMessage = new MqttMessage();
//            mqttMessage.setPayload(payload.getBytes());
//            mMqttClient.publish(topic, mqttMessage);
//            Toast.makeText(getApplicationContext(),"published message on ROAM/PPID topic",Toast.LENGTH_SHORT).show();
//        } catch (MqttException e) {
//            Log.d(TAG, "Publish failed with reason code: " + e.getReasonCode());
//            e.printStackTrace();
//        }
//    }
//
//    public void subscribe(final String topic, int qos) {
//        try {
////            Toast.makeText(getApplicationContext(),"True",Toast.LENGTH_SHORT).show();
//            mMqttClient.subscribe(topic, qos);
//        } catch (MqttException e) {
//            Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "Subscribe failed with reason code: " + e.getReasonCode());
//            e.printStackTrace();
//        }
//    }
//
//    public void disconnect() {
//        try {
//            mMqttClient.disconnect();
//        } catch (MqttException e) {
//            Log.d(TAG, "Disconnect failed with reason code: " + e.getReasonCode());
//            e.printStackTrace();
//        }
//    }
//
//    public boolean isConnected() {
//        if (mMqttClient == null) return false;
//        return mMqttClient.isConnected();
//    }
//
//    public class LocalBinder extends Binder {
//        public MqttService getService() {
//            return MqttService.this;
//        }
//    }
//
//    public class MyMqttCallback implements MqttCallback {
//
//        @Override
//        public void connectionLost(Throwable cause) {
//            Log.d(TAG, "MQTT Server connection lost: " + cause.toString());
//            cause.printStackTrace();
//        }
//
//        @Override
//        public void messageArrived(String topic, MqttMessage message) throws Exception {
//            Log.d(TAG, "Message arrived: " + topic + ":" + message.toString());
//            EventBus.getDefault().post(new MqttStringEvent(topic, message.toString()));
//            System.out.println("Data  --------"+message.toString());
//        }
//
//        @Override
//        public void deliveryComplete(IMqttDeliveryToken token) {
//            Log.d(TAG, "Delivery complete");
//        }
//
//
////        public void connectionLost(Throwable cause) {
////
////        }
////
////        public void messageArrived(String topic, MqttMessage message) {
////
////
////
////        }
////
////        public void deliveryComplete(IMqttDeliveryToken token) {
////
////        }
//    }
//
//    public void setCallback(){
//        mMqttClient.setCallback(new MyMqttCallback());
//    }
//}


package com.sparkle.roam.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVWriter;
import com.sparkle.roam.Adapter.ItemArrayAdapter;
import com.sparkle.roam.Utils.Constants;
import com.sparkle.roam.Utils.MyPref;
import com.sparkle.roam.WebServices.WebCallType;
import com.sparkle.roam.WebServices.WebRequest;
import com.sparkle.roam.WebServices.WebResponseListener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MqttService extends Service {
    public static final String TAG = MqttService.class.getSimpleName();
    private static final int CONNECT_TIMEOUT = 2000;
    private final IBinder mBinder = new LocalBinder();
    private MqttAsyncClient mMqttClient = null;

//    public static final String MQTT_HOST = "tcp://mqtt.omnivoltaic.com";
    public static final String MQTT_HOST = "tcp://localhost:1883";
    public static final String USERNAME = "admin";
    public static final String PASSWORD = "admin123";
    private MyPref myPref;
    private WebRequest webRequest;
    private final String MQTT_DEFAULT_TOPIC_SUBSCRIBE = "_OV1/GPRS/camp/";
    private ItemArrayAdapter itemArrayAdapter;
    private List<String[]> scoreList;
    final File csvfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyCsvFile.csv");

    public class LocalBinder extends Binder {
        public MqttService getService() {
            myPref = new MyPref(getApplicationContext());
            webRequest = new WebRequest(getApplicationContext());

            return MqttService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        if (isConnected()) disconnect();
    }


    public boolean connect() {
        try {
            String clientId = MqttClient.generateClientId();
            mMqttClient = new MqttAsyncClient(MQTT_HOST, clientId, null);

        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        MqttConnectOptions options = new MqttConnectOptions();
        options.setMaxInflight(1000);

        try {
            options.setUserName(USERNAME);
            options.setPassword(PASSWORD.toCharArray());
            final IMqttToken connectToken = mMqttClient.connect(options);
            connectToken.waitForCompletion(CONNECT_TIMEOUT);

//            Toast.makeText(this, "connection: "+mMqttClient.isConnected(), Toast.LENGTH_SHORT).show();
        } catch (MqttException e) {
            Log.d(TAG, "Connection attempt failed with reason code: " + e.getReasonCode() + ":" + e.getCause());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void publish(final String topic, final String payload) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(payload.getBytes());
//            mMqttClient.publish(topic, mqttMessage);
            mMqttClient.publish(topic,payload.getBytes(),2,true);
//            mMqttClient.publish(topic, payload.getBytes(),2,false);
//            Toast.makeText(this, "Publish successfully. ", Toast.LENGTH_SHORT).show();
        }
        catch (MqttException e) {
            Log.d(TAG, "Publish failed with reason code: " + e.getReasonCode());
            e.printStackTrace();
        }
    }

    public void subscribe(final String topic, int qos) {
        try {
            mMqttClient.subscribe(topic, qos);
            System.out.println("------------topic----------"+topic);
//            Toast.makeText(this, "subscribe: "+topic, Toast.LENGTH_SHORT).show();
            mMqttClient.setCallback(new MyMqttCallback());
        }
        catch (MqttException e) {
            Log.d(TAG, "Subscribe failed with reason code: " + e.getReasonCode());
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            mMqttClient.disconnect();
        } catch (MqttException e) {
            Log.d(TAG, "Disconnect failed with reason code: " + e.getReasonCode());
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        if (mMqttClient == null) return false;
        return mMqttClient.isConnected();
    }

    public class MyMqttCallback implements MqttCallback {
        public void connectionLost(Throwable cause) {
            System.out.println("---------------------"+"MQTT Server connection lost: " + cause.toString());
            cause.printStackTrace();
        }

        public void messageArrived(String topic, MqttMessage message) {
            System.out.println("---------------------"+"Message arrived: " + topic + ":" + message.toString());
//            EventBus.getDefault().post(new MqttStringEvent(topic, message.toString()));
//            myPref.setPref(Constants.MESSAGE, message.toString());

//            myPref.setPref(Constants.MESSAGE,Constants.REQUEST_COMPLETED);
//            EventBus.getDefault().post(Constants.REQUEST_COMPLETED);
//                JSONObject requestJson = new JSONObject();
//                try {
//                    requestJson.put("authToken",myPref.getPref(Constants.OFFLINE_TOKEN, ""));
//                    requestJson.put("userID",myPref.getPref(Constants.SELECETD_USER_ID,""));
//                    requestJson.put("offset","0");
//                    requestJson.put("limit","100");
//                    webRequest.POST_METHOD(Constants.GET_CODE_ARRAY_URL, requestJson, null, MqttService.this, WebCallType.GET_CODE_ARRAY, false);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

        }

        public void deliveryComplete(IMqttDeliveryToken token) {
            System.out.println("---------------------"+"Delivery complete "+ Arrays.toString(token.getTopics()));
        }
    }

//    @Override
//    public void onError(String message) {
//
//    }
//
//    @Override
//    public void onResponse(Object response, WebCallType webCallType) throws JSONException {
//        if (response != null) {
//            switch (webCallType){
//                case GET_CODE_ARRAY:
//                    boolean DIALOG = false;
//                    System.out.println("-------------response---------------"+response);
//
//                    JSONObject jsonObject2 = (JSONObject) response;
//                    String data1 = jsonObject2.getString("data");
//
////                    System.out.println("-------------data1---------------"+data1);
//
//                    JSONArray jsonArray = new JSONArray(data1);
//                    for (int i = 0; i < jsonArray.length(); i++){
//                        JSONObject jsonObject3 = jsonArray.getJSONObject(i);
//
//                        String str = String.valueOf(jsonObject3);
//                        String[] p = str.split(",");
//                        String final1 = p[0];
//                        final1 = final1.replace("{","");
//                        final1 = final1.replace("\"","");
//                        String[] parts = final1.split(":");
//
////                        System.out.println("-------------final1---------------"+parts[0]+" ---- "+parts[1]);
//                        String string = jsonObject3.getString("firmwareVersion");
//                        JSONObject object = new JSONObject(string);
//                        String device = object.getString("firmware");
//                        if (device.contains("ovCamp")){
//                            add_PPID_CODE(parts[0],parts[1],Constants.OVCAMP_DEVICE);
//                            System.out.println("--------ov");
//                        }else {
//                            add_PPID_CODE(parts[0],parts[1],Constants.LUMN_DEVICE);
//                            System.out.println("--------lumn");
//                        }
//                        if (!mMqttClient.isConnected()) {
////                            if (mBound == false) return;
//                            if (connect()) {
//                                Toast.makeText(getApplicationContext(), "Successfully connected", Toast.LENGTH_SHORT).show();
//                            }
//
//                            publish(MQTT_DEFAULT_TOPIC_SUBSCRIBE + parts[0], parts[1]);
//                        } else {
//                            publish(MQTT_DEFAULT_TOPIC_SUBSCRIBE + parts[0], parts[1]);
//                        }
//
//                        if (i == jsonArray.length()-1){
//                            DIALOG = true;
//                            try {
//                                FileWriter sw  = new FileWriter(csvfile.getAbsolutePath());
//                                CSVWriter writer = new CSVWriter(sw);
//                                writer.writeAll(scoreList);
//                                writer.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }
//
//                    if (DIALOG){
//                        Toast.makeText(getApplicationContext(), myPref.getPref(Constants.SELECETD_USER_NAME,"")+" user's Code successfully published to MQTT server and saved in your Device.", Toast.LENGTH_SHORT).show();
//                        myPref.setPref(Constants.MESSAGE,Constants.GENERATE_CODE);
//                        EventBus.getDefault().post(Constants.GENERATE_CODE);
//
//                    }
//                    break;
//            }
//        }
//    }

    public void add_PPID_CODE(String PPID, String codehasformate,String device_type){
//        try {
//        FileWriter sw ;
        if (csvfile.exists()){
            itemArrayAdapter.filter(PPID);

            String string = myPref.getPref("string","");
            int position = myPref.getPref("position",0);
            if (string.contains(PPID)){
                scoreList.remove(position-1);
                scoreList.add(position-1,new String[]{device_type,PPID,codehasformate});
//                sw = new FileWriter(csvfile.getAbsolutePath());
                System.out.println("--------------yes--if-----------"+string+"--"+scoreList.size());
                myPref.setPref("string","");
                myPref.setPref("position",0);
            }else {
                scoreList.add(new String[]{device_type,PPID,codehasformate});
//                sw = new FileWriter(csvfile.getAbsolutePath(),true);
                System.out.println("--------------yes--else-----------"+scoreList.size());
            }
        }else {
            scoreList.add(new String[]{device_type,PPID,codehasformate});
//            sw = new FileWriter(csvfile.getAbsolutePath());
            System.out.println("--------------no-------------"+scoreList.size());
        }
        itemArrayAdapter.notifyList(scoreList);


//        } catch (IOException e) {
//            e.printStackTrace();
//        }




    }
}
