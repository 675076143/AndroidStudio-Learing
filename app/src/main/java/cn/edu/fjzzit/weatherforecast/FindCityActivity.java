package cn.edu.fjzzit.weatherforecast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * 查找城市页面
 */
public class FindCityActivity extends AppCompatActivity {
    //定义两个常量，记录城市查询是否成功
    private static final int FIND_SUCCESS = 2;
    private static final int FIND_FAILED = -2;
    private String cityName;
    private TextView mTextViewCityName;
    private ListView mListViewFoundCity;
    private Button mBtnBack;
    private static final String TAG = "FindCityActivity";

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == FIND_SUCCESS){
                //利用cityAdapter装配数据
                CityAdapter cityAdapter = new CityAdapter(
                        FindCityActivity.this,
                        R.layout.layout_list_city,
                        LitePal.findAll(City.class));
                mListViewFoundCity.setAdapter(cityAdapter);

            }else if(msg.what == FIND_FAILED){
                Toast.makeText(FindCityActivity.this,
                        "未找到该城市",
                        Toast.LENGTH_SHORT).show();
                //利用cityAdapter装配数据
                //实例化List<City> cityList,不存任何数据(空)
                //将实例化的cityList利用适配器装配
                List<City> cityList = new ArrayList<City>();
                CityAdapter cityAdapter = new CityAdapter(
                        FindCityActivity.this,
                        R.layout.layout_list_city,
                        cityList);
                mListViewFoundCity.setAdapter(cityAdapter);
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_city);
        mListViewFoundCity = findViewById(R.id.list_view_found_city);
        //文本输入框[城市名称]
        mTextViewCityName = findViewById(R.id.edit_text_cityid);
        //文本输入框内容变换监听
        mTextViewCityName.addTextChangedListener(new TextWatcher() {
            //当文本框里内容变化之前的时候执行该方法
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            //当输入框里文本变化的时候执行查询城市
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cityName = mTextViewCityName.getText().toString();
                findCity();
            }
            //当文本框里内容变化之后的时候执行该方法
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBtnBack = findViewById(R.id.button_back);
        //点击返回主页
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到MainActivity
                finish();

            }
        });
        //ListView的Item点击事件
        mListViewFoundCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               City city = (City) parent.getItemAtPosition(position);
                //将点击取得的城市ID保存到配置文件中
                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(FindCityActivity.this);
                preferences.edit().putString("cityID", city.getCityId());
                //结束当前Activity
               finish();
            }
        });

    }

    private void findCity(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //利用Uri解析Url地址
                Uri uri = Uri.parse("https://search.heweather.net/find")
                        .buildUpon()
                        .appendQueryParameter("location", cityName)
                        .appendQueryParameter("key", "c0eabd2cbf7d4920bb45ff74c85dad5d")
                        .build();
                String url = uri.toString();
                 /*教室用url
                String url = "http://192.168.22.161/s6/find?location=%E6%BC%B3%E5%B7%9E&key=123456";
                个人用url
                https://search.heweather.net/find?location=cityName&key=c0eabd2cbf7d4920bb45ff74c85dad5d
                */
                HttpURLConnection httpURLConnection = null;

                InputStream inputStream = null;
                BufferedReader bufferedReader = null;
                Log.d(TAG,"开始...");
                try {
                    httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                    httpURLConnection.setConnectTimeout(5000);
                    inputStream = httpURLConnection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuffer buffer = new StringBuffer();
                    String str;
                    while ((str = bufferedReader.readLine())!=null){
                        buffer.append(str);
                    }
                    String jsonStr = buffer.toString();
                    Log.d(TAG,"开始TRY...");
                    Log.d(TAG,jsonStr);

                    //调用解析json文件的方法，获得装有weather对象的List
                    if(formatJsonData(jsonStr)!=null){//如果返回值不为空，代表找到该城市
                        List<City> cityList = formatJsonData(jsonStr);
                        LitePal.deleteAll(City.class);

                        for(City city: cityList){
                            //利用LitePal保存数据
                            city.save();
                        }
                        mHandler.sendEmptyMessage(FIND_SUCCESS);
                    }else {//否则未查询到城市
                        mHandler.sendEmptyMessage(FIND_FAILED);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG,"TRY失败");
                }finally {
                    if(bufferedReader!=null){
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (inputStream!=null){
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (httpURLConnection!=null){
                        httpURLConnection.disconnect();
                    }
                }


            }
        });
        thread.start();
    }



    /**
     *  对获取到的json文件进行解析
     * @param string json数据
     * @return city对象
     */
    private List<City> formatJsonData(String string){
        Log.d(TAG,"开始Format：");
        List<City> citys = new ArrayList();

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray heWeather6 = jsonObject.getJSONArray("HeWeather6");
            JSONObject heWeather = heWeather6.getJSONObject(0);
            String status = heWeather.getString("status");
            if(!status.equals("ok")){

                Log.d(TAG,"没有查到该城市！");

                return null;
            }
            JSONArray basic = heWeather.getJSONArray("basic");
            for(int i = 0; i < basic.length(); i++){
                JSONObject city = basic.getJSONObject(i);
                String cityId = city.getString("cid");
                String location = city.getString("location");
                String parent_city = city.getString("parent_city");
                String admin_area = city.getString("admin_area");
                String cnty = city.getString("cnty");
                Log.d(TAG,"城市ID"+cityId+"  "+"城市名："+location);
                //实例化City对象，保存解析过后的数据
                City foundCity = new City(cityId,location,parent_city,admin_area,cnty);
                citys.add(foundCity);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"format失败");
        }
        return citys;
    }

}
