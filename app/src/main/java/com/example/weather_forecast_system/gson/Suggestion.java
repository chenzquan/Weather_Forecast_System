package com.example.weather_forecast_system.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 权 on 2017/12/21.
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;
    @SerializedName("cw")
    public WashCar washcar;
    @SerializedName("sport")
    public Sport sport;
    public class Comfort{
        @SerializedName("txt")
        public String info;  //记录天气的状况
    }
    public class WashCar{
        @SerializedName("txt")
        public String info;
    }
    public class Sport{
        @SerializedName("txt")
        public String info;
    }
}
