package com.example.weather_forecast_system;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

/**
 * Created by 权 on 2017/12/20.
 */

public class MyApplication extends Application{
    private static Context context;

    @Override
    public void onCreate() {
        this.context = getApplicationContext();
        LitePal.initialize(context);
    }

    public static Context getContext(){
        return context;
    }
}














