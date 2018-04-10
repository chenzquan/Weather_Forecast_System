package com.example.weather_forecast_system.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 权 on 2017/12/21.
 */

public class Forecast {

    public String date;
    @SerializedName("tmp")
    public Temperature temperature;
    @SerializedName("cond")
    public More more;
    public class Temperature{
        public String max;

        public String min;
    }
    public class More{
        @SerializedName("txt_d")  //记录天气的 晴、多云等天气
        public String info;
    }


}
