package com.example.weather_forecast_system.gson;

/**
 * Created by 权 on 2017/12/21.
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
