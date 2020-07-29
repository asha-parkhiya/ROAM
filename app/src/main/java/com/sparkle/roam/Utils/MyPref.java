package com.sparkle.roam.Utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.amazonaws.amplify.generated.graphql.GetPayAccountListQuery;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sparkle.roam.Model.SendRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Admin on 01-05-2018.
 */

public class MyPref {

    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context mContext;
    private static final String PREF_NAME = "sparkle.midlal.midlal.preference";

    public MyPref(Context context) {
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setPref(String flagid, int fid) {
        editor.putInt(flagid, fid);
        editor.commit();
    }

    public void setPref(String flagid, List<SendRequest> fid) {
        Gson gson = new Gson();
        String json = gson.toJson(fid);
        editor.putString(flagid, json);
        editor.commit();
    }

    public List<SendRequest> getsendRequestArrayList(String key){
        Gson gson = new Gson();
        String json = pref.getString(key, null);
        Type type = new TypeToken<List<SendRequest>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void setPref(String flagid, GetPayAccountListQuery.GetPayAccountListQuery1 fid) {
        Gson gson = new Gson();
        String json = gson.toJson(fid);
        editor.putString(flagid, json);
        editor.commit();
    }

    public GetPayAccountListQuery.GetPayAccountListQuery1 getQuery(String key){
        Gson gson = new Gson();
        String json = pref.getString(key, null);
        Type type = new TypeToken<GetPayAccountListQuery.GetPayAccountListQuery1>() {}.getType();
        return gson.fromJson(json, type);
    }


    public List<GetPayAccountListQuery.GetPayAccountListQuery1> getArrayList(String key){
        Gson gson = new Gson();
        String json = pref.getString(key, null);
        Type type = new TypeToken<List<GetPayAccountListQuery.GetPayAccountListQuery1>>() {}.getType();
        return gson.fromJson(json, type);
    }


    public Integer getPref(String flagid, int fid) {
        return pref.getInt(flagid, fid);
    }

    public void setPref(String key, String val) {
        editor.putString(key, val);
        editor.commit();
    }

    public String getPref(String key, String val) {
        return pref.getString(key, val);
    }


    public void setPref(String pref, boolean val) {
        editor.putBoolean(pref, val);
        editor.commit();
    }

    public int getUnreadCount() {
        String unread = getPref(Constants.UNREAD_COMMENTS, "");
        int count = 0;
        if (unread.isEmpty())
            return 0;

        try {
            JSONObject unreadJson = new JSONObject(unread);
            Iterator<String> iter = unreadJson.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                count += unreadJson.getInt(key);
            }
            return count;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addUnreadCount(String chatId) {
        String unread = getPref(Constants.UNREAD_COMMENTS, "");
        try {
            JSONObject unreadJson;
            if (unread.isEmpty())
                unreadJson = new JSONObject();
            else
                unreadJson = new JSONObject(unread);

            if (unreadJson.has(chatId)) {
                int current = unreadJson.getInt(chatId);
                unreadJson.put(chatId, current + 1);
            } else {
                unreadJson.put(chatId, 1);
            }
            setPref(Constants.UNREAD_COMMENTS, unreadJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void removeFromUnread(String chatId) {
        String unread = getPref(Constants.UNREAD_COMMENTS, "");
        try {
            if (!unread.isEmpty()) {
                JSONObject unreadJson = new JSONObject(unread);
                if (unreadJson.has(chatId)) {
                    unreadJson.remove(chatId);
                    setPref(Constants.UNREAD_COMMENTS, unreadJson.toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public boolean getPref(String key, boolean val) {
        return pref.getBoolean(key, val);
    }

    public void clearPref() {
        editor.clear();
        editor.commit();
    }
}
