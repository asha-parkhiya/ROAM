package com.sparkle.roam.WebServices;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Window;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.sparkle.roam.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 27-04-2018.
 */

public class WebRequest {

    private Context mContext;
    private ProgressDialog processDialog;


    public WebRequest(Context mContext) {
        this.mContext = mContext;
        processDialog = new ProgressDialog(mContext);

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

    public void GET_METHOD(String url, final WebResponseListener listener, final WebCallType webCallType, boolean isShowProgress) {
        if (mContext != null) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        listener.onResponse(response, webCallType);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    listener.onError(error.getMessage());
                }
            });


            stringRequest.setTag(mContext);
            MySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
        } else
            Log.d(WebRequest.class.getSimpleName(), "Something went wrong!");
    }


    public void GET_METHOD(String url, final WebResponseListener listener, final WebCallType webCallType, boolean isShowProgress, final String token) {
        if (mContext != null) {
            if (isShowProgress)
                showProcess();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    cancelProcess();
                    try {
                        listener.onResponse(response, webCallType);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    cancelProcess();
                    listener.onError(error.getMessage());
                }
            }){

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", token);

                    return params;
                }


            };
            stringRequest.setTag(mContext);
            MySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } else
            Log.d(WebRequest.class.getSimpleName(), "Something went wrong!");
    }


    public void POST_METHOD(String url, JSONObject requestJson, final Map<String, String> hashMap,
                            final WebResponseListener listener, final WebCallType webCallType,
                            final boolean isShowProgress) {
        if (mContext != null) {
            if (isShowProgress)
                showProcess();
            if (hashMap != null) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        cancelProcess();
                        try {
                            listener.onResponse(response, webCallType);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cancelProcess();
                        listener.onError(error.toString());
                    }
                }) {
                    @Override
                    public Map getHeaders() throws AuthFailureError {
                        HashMap headers = new HashMap();
                        headers.put("Content-Type", "application/json");
//                        headers.put("x-api-key", "da2-zbfaebxsdbbqlaj5omqklpbzwy");
                        return headers;
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return hashMap;
                    }
                };

                stringRequest.setTag(mContext);

                MySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
            } else {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                        requestJson, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        cancelProcess();
                        try {
                            listener.onResponse(response, webCallType);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cancelProcess();
                        listener.onError(error.toString());
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
//                        headers.put("x-api-key", "da2-zbfaebxsdbbqlaj5omqklpbzwy");
                        return headers;
                    }

                };
                // Access the RequestQueue through singleton class.
                jsonObjectRequest.setTag(mContext);
                MySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        60000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }
        } else
            Log.d(WebRequest.class.getSimpleName(), "Something went wrong!");
    }

    public void DELETE_METHOD(String url, JSONObject requestJson, final Map<String, String> hashMap,
                            final WebResponseListener listener, final WebCallType webCallType,
                            final boolean isShowProgress) {
        if (mContext != null) {
            if (isShowProgress)
                showProcess();
            if (hashMap != null) {
                StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        cancelProcess();
                        try {
                            listener.onResponse(response, webCallType);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cancelProcess();
                        listener.onError(error.toString());
                    }
                }) {
                    @Override
                    public Map getHeaders() throws AuthFailureError {
                        HashMap headers = new HashMap();
                        headers.put("Content-Type", "application/json");
//                        headers.put("x-api-key", "da2-zbfaebxsdbbqlaj5omqklpbzwy");
                        return headers;
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return hashMap;
                    }
                };

                stringRequest.setTag(mContext);

                MySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
            } else {
//                System.out.println("URL ---"+url+"requestjson:-------------"+requestJson);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, requestJson, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        cancelProcess();
                        try {
                            listener.onResponse(response, webCallType);
                        } catch (JSONException e) {
                            System.out.println("----------e------"+e);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cancelProcess();
                        listener.onError(error.toString());
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
//                        headers.put("x-api-key", "da2-zbfaebxsdbbqlaj5omqklpbzwy");
                        return headers;
                    }

                    @Override
                    protected VolleyError parseNetworkError(VolleyError volleyError) {
                        String json;
                        if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                            try {
                                json = new String(volleyError.networkResponse.data,
                                        HttpHeaderParser.parseCharset(volleyError.networkResponse.headers));
                                System.out.println("---------json-----------"+json);
                            } catch (UnsupportedEncodingException e) {
                                return new VolleyError(e.getMessage());
                            }
                            return new VolleyError(json);
                        }
                        return volleyError;
                    }

                };
                // Access the RequestQueue through singleton class.
                jsonObjectRequest.setTag(mContext);
                MySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        60000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }
        } else
            Log.d(WebRequest.class.getSimpleName(), "Something went wrong!");
    }

    public void POST_MULTIPART(String url, final Map<String, String> params, final Map<String, DataPart> multipartParams,
                               final WebResponseListener listener, final WebCallType webCallType,
                               final boolean isShowProgress) {
        if (mContext != null) {
            if (isShowProgress)
                showProcess();
            //our custom volley request
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            cancelProcess();
                            try {
                                listener.onResponse(new String(response.data), webCallType);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            cancelProcess();
                            listener.onError(error.toString());
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    return multipartParams;
                }
            };
            volleyMultipartRequest.setTag(mContext);
            MySingleton.getInstance(mContext).addToRequestQueue(volleyMultipartRequest);
            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } else
            Log.d(WebRequest.class.getSimpleName(), "Something went wrong!");
    }

    public void CancelRequest() {
        if (mContext != null) {
            RequestQueue requestQueue = MySingleton.getInstance(mContext).getRequestQueue();
            if (requestQueue != null)
                requestQueue.cancelAll(mContext);
        }
    }


    public void GROUP_GET_METHOD(String url, final WebResponseListener listener, final Map<String, String> hashMap1, JSONObject jsonObject1, final WebCallType webCallType1, boolean isShowProgress) {
        if (isShowProgress)
            showProcess();

        if (mContext != null) {
            if (isShowProgress)
                showProcess();
            if (hashMap1 != null) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        cancelProcess();

                        try {
                            listener.onResponse(response, webCallType1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cancelProcess();
                        listener.onError(error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return hashMap1;
                    }
                };

                stringRequest.setTag(mContext);

                MySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
            } else {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                        jsonObject1, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        cancelProcess();

                        try {
                            listener.onResponse(response, webCallType1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cancelProcess();
                        listener.onError(error.toString());
                    }
                });
                // Access the RequestQueue through singleton class.
                jsonObjectRequest.setTag(mContext);
                MySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        60000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }
        } else
            Log.d(WebRequest.class.getSimpleName(), "Something went wrong!");
    }

//    public void sendRequest(String Url, Map<String, Object> params, JSONObject jsonObject,
//                            final webcallid callId) {
//        if (IshowProgress)
//            process.show();
//
//        cb = new AjaxCallback<JSONObject>() {
//
//            @Override
//            public void callback(String url, JSONObject result, AjaxStatus status) {
//                if (process != null && process.isShowing())
//                    process.dismiss();
//                if (listener != null)
//                    listener.getResponse(url, result, status, callId);
//            }
//        };
//        cb.method(AQuery.METHOD_POST);
//        cb.expire(150*1000);
//        if (jsonObject != null)
//            aq.post(Url, jsonObject, JSONObject.class, cb);
//
//        else {
//            cb.params(params);
//            aq.ajax(Url, params, JSONObject.class, cb);
//        }

//    }


}
