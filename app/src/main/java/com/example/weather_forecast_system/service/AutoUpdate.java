package com.example.weather_forecast_system.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.weather_forecast_system.WeatherActvity;
import com.example.weather_forecast_system.gson.Weather;
import com.example.weather_forecast_system.util.HttpUtil;
import com.example.weather_forecast_system.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static java.lang.System.load;

public class AutoUpdate extends Service {
    public AutoUpdate() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        UpdateImage();
        UpdateWeather();

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int Update_interval = 6*60*60*1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + Update_interval;
        Intent i = new Intent(this,AutoUpdate.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        alarmManager.cancel(pi);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);

    }


    private void UpdateWeather(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String WeatherString = sharedPreferences.getString("weather",null);

        if(WeatherString!=null){
            Weather weather = Utility.handWeatherResponse(WeatherString);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId
                    + "&key=bc0418b57b2d4918819d3974ac1285d9";

            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String weatherRresponse = response.body().string();
                    Weather weather1 = Utility.handWeatherResponse(weatherRresponse);
                    if(weather1!=null && weather1.status.equals("ok")){
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdate.this).edit();
                        editor.putString("weather",weatherRresponse);
                        editor.apply();
                    }
                }
            });
        }

    }


    private void UpdateImage(){
        String requestImage = "http://guolin.tech/api/bing_pic";

        HttpUtil.sendOkHttpRequest(requestImage, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseImage = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdate.this).edit();
                editor.putString("imagecity",responseImage);
                editor.apply();

            }
        });
    }


}
