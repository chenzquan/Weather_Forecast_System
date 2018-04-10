package com.example.weather_forecast_system.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 权 on 2017/12/21.
 */

public class Weather {

    public String status;  //记录 数据是否返回成功
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

}
