package com.example.weather_forecast_system;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weather_forecast_system.gson.Forecast;
import com.example.weather_forecast_system.gson.Weather;
import com.example.weather_forecast_system.service.AutoUpdate;
import com.example.weather_forecast_system.util.HttpUtil;
import com.example.weather_forecast_system.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.os.Build.VERSION.SDK;

public class WeatherActvity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView city_title;
    private TextView UpdateTimeText;
    private TextView degressText;
    private TextView weather_info_text;
    private TextView apiText;
    private TextView pm2Text;
    private LinearLayout forecastLayout;
    private TextView ComfortableText;
    private TextView clothesAdviceText;
    private TextView sportAdviceText;

    private ImageView image;

    public SwipeRefreshLayout swipeRefreshLayout;
    private String mWeatherId;

    public DrawerLayout drawerLayout;
    private Button home;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_actvity);


        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        city_title = (TextView) findViewById(R.id.city_title);
        UpdateTimeText = (TextView) findViewById(R.id.title_update_time);
        degressText = (TextView) findViewById(R.id.degree_text);
        weather_info_text = (TextView) findViewById(R.id.weather_info_text);
        apiText = (TextView) findViewById(R.id.api_text);
        pm2Text = (TextView) findViewById(R.id.PM2_5_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        ComfortableText = (TextView) findViewById(R.id.comfortable);
        clothesAdviceText = (TextView) findViewById(R.id.clothes_advice);
        sportAdviceText = (TextView) findViewById(R.id.sport_advice);

        image = (ImageView) findViewById(R.id.image_city);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        home = (Button) findViewById(R.id.home_button);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);//从SharedPreferences中读取数据
        String weatherString = prefs.getString("weather",null);  //取键值为“weather” 的字符串 若没有就返回 null

        String imageCity = prefs.getString("imagecity",null);

        if(imageCity!=null){
            Glide.with(this).load(imageCity).into(image);
        }else{
            loadImage();
        }


        if(weatherString!=null){
            Weather weather = Utility.handWeatherResponse(weatherString);

            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);

        }else {
            mWeatherId = getIntent().getStringExtra("weatherId");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){//更新的控件
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


    }



    private void showWeatherInfo(Weather weather){  //输出布局界面

        String cityName = weather.basic.cityName;
        String UpdateTime = weather.basic.update.updataTime;
        String degree = weather.now.Temperature + "℃";
        String weatherInfo = weather.now.more.info;


        city_title.setText(cityName);
        UpdateTimeText.setText(UpdateTime.split(" ")[0]);
        degressText.setText(degree);
        weather_info_text.setText(weatherInfo);

        forecastLayout.removeAllViews();
        for(Forecast forecast : weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_weather);
            TextView minTemperatureText = (TextView) view.findViewById(R.id.min_text);
            TextView maxTemperatureText = (TextView) view.findViewById(R.id.max_text);

            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            minTemperatureText.setText(forecast.temperature.min + "℃");
            maxTemperatureText.setText(forecast.temperature.max + "℃");
            forecastLayout.addView(view);
        }


        if (weather.aqi!=null){
            apiText.setText(weather.aqi.city.aqi);
            pm2Text.setText(weather.aqi.city.pm25);
        }


        String comfortable = "舒适度：" + weather.suggestion.comfort.info;
        String clothesAdvice = "洗车建议：" + weather.suggestion.washcar.info;
        String sportAdvice = "运动建议：" + weather.suggestion.sport.info;
        ComfortableText.setText(comfortable);
        clothesAdviceText.setText(clothesAdvice);
        sportAdviceText.setText(sportAdvice);
        weatherLayout.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this,AutoUpdate.class);
        startService(intent);

    }



    public void requestWeather(final String weatherId){

   //     String weatherUrl = "http://192.168.1.100/China/getWeather.php?weather_id=" + weatherId ;
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId
                + "&key=bc0418b57b2d4918819d3974ac1285d9";
//                + "&key=af8b13eee1174a189f674663a8cd4f6e";

        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                        public void run() {
                        Toast.makeText(WeatherActvity.this,"加载天气信息失败!!",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
            //    Log.d("WeatherActivity",responseText);
                final Weather weather = Utility.handWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null && weather.status.equals("ok")){
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActvity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);

                        }else{
                            Toast.makeText(WeatherActvity.this,"加载天气信息失败!!",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }


    private void loadImage(){
        final String UrlImage = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(UrlImage, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseImage = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActvity.this).edit();
                editor.putString("imagecity",responseImage);
                editor.apply();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActvity.this).load(responseImage).into(image);
                    }
                });
            }
        });
    }

}























