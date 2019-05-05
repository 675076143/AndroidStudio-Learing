package cn.edu.fjzzit.weatherforecast;
import org.litepal.LitePal;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView mForecastListView;
    private static final int UPDATE_SUCCESS = 1;
    private static final int UPDATE_FAILED = 0;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == UPDATE_SUCCESS){
                ForecastAdapter forecastAdapter = new ForecastAdapter(
                        MainActivity.this,
                        R.layout.layout_list_item_forecast,
                        LitePal.findAll(Weather.class));
                mForecastListView.setAdapter(forecastAdapter);

            }else if(msg.what == UPDATE_FAILED){
                Toast.makeText(MainActivity.this,
                        "Failed",
                        Toast.LENGTH_LONG).show();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //使用Material控件--ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        updateForecast();
        mForecastListView = findViewById(R.id.list_view_forecast);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*菜单栏点击事件*/
        switch (item.getItemId()){
            //点击刷新
            case R.id.menu_item_refresh:
                updateForecast();
                Toast.makeText(MainActivity.this,"刷新成功！",Toast.LENGTH_SHORT).show();
                break;
            //点击跳转到设置页面
            case R.id.menu_item_setting:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
                break;
            //点击跳转到下载图片页面
            case R.id.menu_item_switch_download:
                Intent intent2 = new Intent();
                intent2.setClass(MainActivity.this,downloadImgActivity.class);
                startActivity(intent2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateForecast(){
        //获取数据的线程
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //从配置文件中读取信息
                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(MainActivity.this);
                String cityName = preferences.getString("location", "漳州");
                //利用Uri解析Url地址
                Uri uri = Uri.parse("https://free-api.heweather.com/s6/weather/forecast")
                        .buildUpon()
                        .appendQueryParameter("location", cityName)
                        .appendQueryParameter("key", "c0eabd2cbf7d4920bb45ff74c85dad5d")
                        .build();
                String url = uri.toString();
                //String url = "http://192.168.22.161/s6/weather/forecast?location=%E6%BC%B3%E5%B7%9E&key=123456";
                //String url = "https://free-api.heweather.com/s6/weather/forecast?location=%E6%BC%B3%E5%B7%9E&key=c0eabd2cbf7d4920bb45ff74c85dad5d";
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
                    List<Weather> weatherList = formatJsonData(jsonStr);
                    LitePal.deleteAll(Weather.class);
                    for(Weather weather:weatherList){
                        weather.save();
                    }
                    mHandler.sendEmptyMessage(UPDATE_SUCCESS);

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

    private List<Weather> formatJsonData(String string){
        Log.d(TAG,"开始Format：");
        List<Weather> weathers = new ArrayList();

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray heWeather6 = jsonObject.getJSONArray("HeWeather6");
                JSONObject heWeather = heWeather6.getJSONObject(0);
                    String status = heWeather.getString("status");
                    if(!status.equals("ok")){
                        return null;
                    }
                    JSONObject basic = heWeather.getJSONObject("basic");
                        String cid =  basic.getString("cid");
//                        JSONObject location = basic.getJSONObject("location");
//                        JSONObject parent_city = basic.getJSONObject("parent_city");
//                        JSONObject admin_area = basic.getJSONObject("admin_area");
//                        JSONObject cnty = basic.getJSONObject("cnty");
//                        JSONObject lat = basic.getJSONObject("lat");
//                        JSONObject lon = basic.getJSONObject("lon");
//                        JSONObject tz = basic.getJSONObject("tz");
                    JSONObject update = heWeather.getJSONObject("update");
//                        JSONObject loc = update.getJSONObject("loc");
//                        JSONObject utc = update.getJSONObject("utc");
                        Log.d(TAG,"值为："+cid);
                    JSONArray dailyForecast = heWeather.getJSONArray("daily_forecast");
                    for (int i = 0;i<dailyForecast.length();i++){
                        JSONObject dailyForecast0 = dailyForecast.getJSONObject(i);
                        String tmpMax = dailyForecast0.getString("tmp_max");
                        String tmpMin = dailyForecast0.getString("tmp_min");
                        String date = dailyForecast0.getString("date");
                        String condTxtD = dailyForecast0.getString("cond_txt_d");
                        String condTxtN = dailyForecast0.getString("cond_txt_n");
                        String windDir = dailyForecast0.getString("wind_dir");
                        String windSc = dailyForecast0.getString("wind_sc");
                        String windSpd = dailyForecast0.getString("wind_spd");
                        Log.d(TAG,"温度"+tmpMin+"℃ ~ "+tmpMax+"℃");
                        Log.d(TAG,date+"的天气："+condTxtD+"转"+condTxtN+" 吹"+windDir+" 风力 "+windSc+" 级");
                        Weather weather = new Weather(cid,tmpMin,tmpMax,date,condTxtD,condTxtN);
                        weathers.add(weather);
                        Log.d(TAG,"面向对象："+weathers.get(0).getDate());
                    }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"format失败");
        }
        //Log.d(TAG,weathers);
        return weathers;
    }


}
