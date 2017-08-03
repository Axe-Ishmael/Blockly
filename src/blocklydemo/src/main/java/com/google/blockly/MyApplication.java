package com.google.blockly;

import android.app.Application;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by admin on 2017/7/24.
 */

public  class  MyApplication extends Application {

    private static RequestQueue requestQueue;
    private static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        sharedPreferences = getSharedPreferences("userInfo",0);
    }

    public static RequestQueue getRequestQueue(){
        return  requestQueue;
    }

    public static SharedPreferences getSharedPreferences(){
        return sharedPreferences;
    }
}
