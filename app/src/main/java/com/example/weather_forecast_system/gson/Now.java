package com.example.weather_forecast_system.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 权 on 2017/12/21.
 */


public class Now {

    @SerializedName("tmp")
    public String Temperature;//记录温度
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt")
        public String info;
    }

}




