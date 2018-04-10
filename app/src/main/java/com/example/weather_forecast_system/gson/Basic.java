package com.example.weather_forecast_system.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 权 on 2017/12/21.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updataTime;
    }
}
