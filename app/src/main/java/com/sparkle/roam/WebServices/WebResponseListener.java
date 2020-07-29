package com.sparkle.roam.WebServices;

import org.json.JSONException;

/**
 * Created by Admin on 27-04-2018.
 */

public interface WebResponseListener {

    void onError(String message);

    void onResponse(Object response, WebCallType webCallType) throws JSONException;
}
