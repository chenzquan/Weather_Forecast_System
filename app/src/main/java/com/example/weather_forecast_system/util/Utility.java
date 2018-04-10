package com.example.weather_forecast_system.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.weather_forecast_system.db.City;
import com.example.weather_forecast_system.db.County;
import com.example.weather_forecast_system.db.Province;
import com.example.weather_forecast_system.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 权 on 2017/12/20.
 */

public class Utility {


    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray provinceAll = new JSONArray(response);
                for (int i = 0; i < provinceAll.length(); i++) {
                    JSONObject proviceObject = provinceAll.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceCode(proviceObject.getInt("id"));
                    province.setProvinceName(proviceObject.getString("name"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray cityAll = new JSONArray(response);

                for(int i=0; i<cityAll.length(); i++){
                    JSONObject cityObject = cityAll.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(cityObject.getInt("id"));
                    city.setCityName(cityObject.getString("name"));
                    city.setProvinceId(provinceId);
                    city.save();
                }

                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public static boolean handcountyResponse(String response,int CityId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray countyAll = new JSONArray(response);
                for(int i=0; i<countyAll.length(); i++){
                    JSONObject countyObject = countyAll.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setCityId(countyObject.getInt("id"));
                    county.setCityId(CityId);
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public static Weather handWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();

//            Log.d("Utj",weatherContent);
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
