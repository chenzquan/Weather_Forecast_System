package com.example.weather_forecast_system;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather_forecast_system.db.City;
import com.example.weather_forecast_system.db.County;
import com.example.weather_forecast_system.db.Province;
import com.example.weather_forecast_system.util.HttpUtil;
import com.example.weather_forecast_system.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 权 on 2017/12/20.
 */

public class ChooseAreaFragment extends Fragment {

    public static final int levelProvince = 0;
    public static final int levelCity = 1;
    public static final int levelCounty = 2;
    private ProgressDialog progressDialog;
    private TextView titleTextView;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectProvince;
    private City selectCity;
    private int levelCurrent;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.choose_area,container,false);

        titleTextView = (TextView) root.findViewById(R.id.title_text);
        backButton = (Button) root.findViewById(R.id.back_button);
        listView = (ListView) root.findViewById(R.id.list_view);

        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        queryProvince();//先从省份查起

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(levelCurrent == levelProvince){
                    selectProvince = provinceList.get(position);
                    queryCity();
                }else if(levelCurrent == levelCity){
                    selectCity = cityList.get(position);
                    queryCounty();
                }else if(levelCurrent == levelCounty){
                    String weatherId = countyList.get(position).getWeatherId();

                    if(getActivity() instanceof MainActivity){
                        Intent intent = new Intent(getActivity(),WeatherActvity.class);
                        intent.putExtra("weatherId",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof WeatherActvity){
                        WeatherActvity weatherActvity = (WeatherActvity) getActivity();
                        weatherActvity.drawerLayout.closeDrawers();
                        weatherActvity.swipeRefreshLayout.setRefreshing(true);
                        weatherActvity.requestWeather(weatherId);
                    }

                }
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(levelCurrent == levelCounty){
                    queryCity();
                }else if(levelCurrent == levelCity){
                    queryProvince();
                }
            }
        });
    }


    //先从本地数据库查询 若没有 就从服务器上查询
    private void queryProvince(){
        titleTextView.setText("中国");
        backButton.setVisibility(View.GONE);  //设置按钮隐藏
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
            dataList.clear();

            for(Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            levelCurrent = levelProvince;
        }else{
       //     String address = "http://192.168.1.100/China/getProvince.php";
            String address = "http://guolin.tech/api/china";   //域名
            queryFromServer(address,"province");
        }
    }

    private void queryCity(){
        titleTextView.setText(selectProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceId=?",String.valueOf(selectProvince.getId())).find(City.class);
        if(cityList.size()>0){
            dataList.clear();

            for(City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            levelCurrent = levelCity;

        }else {
            int provinceCode = selectProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
    //        String address = "http://192.168.1.100/China/getCity.php?id=" + provinceCode;
            queryFromServer(address,"city");
        }
    }


    private void queryCounty(){
        titleTextView.setText(selectCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
//        countyList = DataSupport.findAll(County.class);
        countyList = DataSupport.where("cityId=?",String.valueOf(selectCity.getId())).find(County.class);
        if(countyList.size()>0){
            dataList.clear();

            for(County county : countyList){
                dataList.add(county.getCountyName());
            }

            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            levelCurrent = levelCounty;

        }else{
            int provinceCode = selectProvince.getProvinceCode();
            int cityCode = selectCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
  //          String address = "http://192.168.1.100/China/getCounty.php?id=" + cityCode;
            queryFromServer(address,"county");
        }
    }

    private void queryFromServer(String address,final String type){

        showProgressDialog();

        HttpUtil.sendOkHttpRequest(address, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败!!",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String responceText = response.body().string();
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvinceResponse(responceText);
                }else if ("city".equals(type)){
                    result = Utility.handleCityResponse(responceText,selectProvince.getId());

                }else if("county".equals(type)){
                    result = Utility.handcountyResponse(responceText,selectCity.getId());
                }

                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvince();
                            }else if("city".equals(type)) {
                                queryCity();
                            }else if("county".equals(type)){
                                queryCounty();
                            }
                        }
                    });
                }

            }
        });
    }


    private void showProgressDialog(){
        if(this.progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("加载中……");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if(this.progressDialog != null){
            this.progressDialog.dismiss();
        }
    }

}
