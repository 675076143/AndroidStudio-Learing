package cn.edu.fjzzit.weatherforecast;
import org.litepal.LitePal;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenuItemView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 主页面
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView mForecastListView;
    private ListView mListViewLifeStyle;
    private static final int UPDATE_SUCCESS = 1;
    private static final int UPDATE_FAILED = 0;
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NavigationView mNavigationView;
    private TextView mTextViewCityTitle;
    private TextView mTextViewNowTmp;
    private TextView mTextViewNowFl;
    private TextView mTextViewNowCondTxt;
    private ImageView mImageViewNowCondCode;
    private String cid = "CN101230601";
    private String localTime;
    private WeatherNow weatherNow = new WeatherNow();
    private List<LifeStyle> lifeStyleList = new ArrayList<LifeStyle>();

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == UPDATE_SUCCESS){
                //适配器填充七日天气数据
                ForecastAdapter forecastAdapter = new ForecastAdapter(
                        MainActivity.this,
                        R.layout.layout_list_item_forecast,
                        LitePal.where("cityID=?",getCurrentCityID()).find(Weather.class));
                mForecastListView.setAdapter(forecastAdapter);
                //填充实时天气数据
                mImageViewNowCondCode.setImageResource(setImageByCondCode(weatherNow.getCondCode()));
                mTextViewNowTmp.setText(weatherNow.getTmp()+"℃");
                mTextViewNowFl.setText("体感温度："+weatherNow.getFl()+"℃");
                mTextViewNowCondTxt.setText(weatherNow.getCondTxt());
                //适配器填充生活指数
                LifeStyleAdapter lifeStyleAdapter = new LifeStyleAdapter(
                        MainActivity.this,
                        R.layout.layout_list_item_life_style,
                        LitePal.findAll(LifeStyle.class));
                mListViewLifeStyle.setAdapter(lifeStyleAdapter);

            }else if(msg.what == UPDATE_FAILED){
                Toast.makeText(MainActivity.this,
                        "Failed",
                        Toast.LENGTH_LONG).show();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onResume() {
        updateForecast();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //下拉刷新事件
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);
        //刷新进度条的颜色为colorPrimary（导航栏颜色）
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateForecast();
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this,"刷新成功！",Toast.LENGTH_SHORT).show();
            }
        });

        //使用Material控件--ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("轻天气");
        setSupportActionBar(toolbar);
        //添加按钮 点击显示滑动菜单
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
        }
        //取得nav_view实例
        mNavigationView = findViewById(R.id.nav_view);
        //设置nav_call为默认选中
        mNavigationView.setCheckedItem(R.id.nav_find_city);

        //左侧滑动菜单项选中事件的监听器
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                switch (menuItem.getItemId())
                {
                    case R.id.nav_find_city:
                        //跳转到查询城市Activity
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this,FindCityActivity.class);
                        //startActivityForResult页面间传值
                        startActivityForResult(intent,0x002);
                        break;
                    case R.id.nav_download_img:
                        //跳转到查询城市Activity
                        Intent intentDownloadImg = new Intent();
                        intentDownloadImg.setClass(MainActivity.this,downloadImgActivity.class);
                        startActivity(intentDownloadImg);
                        break;
                }
                return true;
            }
        });

        //更新天气
        //updateForecast();
        mForecastListView = findViewById(R.id.list_view_forecast);
        mListViewLifeStyle = findViewById(R.id.list_view_life_style);
        mTextViewCityTitle = findViewById(R.id.text_view_city_title);
        mTextViewNowTmp = findViewById(R.id.text_view_now_tmp);
        mImageViewNowCondCode = findViewById(R.id.image_view_now_cond_code);
        mTextViewNowFl = findViewById(R.id.text_view_now_fl);
        mTextViewNowCondTxt = findViewById(R.id.text_view_now_cond_txt);
        mTextViewCityTitle.setText("漳州");
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
            //点击显示滑动菜单
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 获取当前的城市ID
     */
    String getCurrentCityID(){
        //从配置文件中取出城市ID
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.this);
        return preferences.getString("cityID","CN101230601");
    }
    /**
     * 更新天气
     */
    private void updateForecast(){
        //获取数据的线程
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //利用Uri解析Url地址
                Uri uri = Uri.parse("https://free-api.heweather.com/s6/weather")
                        .buildUpon()
                        .appendQueryParameter("location", getCurrentCityID())
                        .appendQueryParameter("key", "c0eabd2cbf7d4920bb45ff74c85dad5d")
                        .build();
                String url = uri.toString();
                /*教室用url
                String url = "http://192.168.22.161/s6/weather/forecast?location=%E6%BC%B3%E5%B7%9E&key=123456";
                个人用url
                https://free-api.heweather.com/s6/weather/forecast?location=cityName&key=c0eabd2cbf7d4920bb45ff74c85dad5d
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
                    List<Weather> weatherList = formatJsonData(jsonStr);
                    //更新前删除原有数据
                    LitePal.deleteAll(Weather.class, "cityId=?", getCurrentCityID());
                    for(Weather weather:weatherList){
                        //利用LitePal保存数据
                        weather.save();
                    }
                    //调用解析json文件的方法，获得装有LifeStyle对象的List
                    //更新前删除原有数据
                    LitePal.deleteAll(LifeStyle.class);
                    for(LifeStyle lifeStyle:lifeStyleList){
                        //利用LitePal保存数据
                        lifeStyle.save();
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

    /**
     *  对获取到的json文件进行解析
     * @param string json数据
     * @return weather对象
     */
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
                        /*其他数据解析[备用]
                        JSONObject location = basic.getJSONObject("location");
                        JSONObject parent_city = basic.getJSONObject("parent_city");
                        JSONObject admin_area = basic.getJSONObject("admin_area");
                        JSONObject cnty = basic.getJSONObject("cnty");
                        JSONObject lat = basic.getJSONObject("lat");
                        JSONObject lon = basic.getJSONObject("lon");
                        JSONObject tz = basic.getJSONObject("tz");

                    JSONObject update = heWeather.getJSONObject("update");
                        JSONObject loc = update.getJSONObject("loc");
                        JSONObject utc = update.getJSONObject("utc");
                        */
                        Log.d(TAG,"值为："+cid);
                    //接口更新时间

                    JSONObject update = heWeather.getJSONObject("update");
                        String loc = update.getString("loc");
                        String utc = update.getString("utc");
                        Log.d(TAG,loc);
                        localTime = loc.split(" ")[1];
                        Log.d(TAG,localTime);


                    //当前天气(now)
                    JSONObject now = heWeather.getJSONObject("now");
                        String cloud = now.getString("cloud");
                        String condCode = now.getString("cond_code");
                        String condTxt = now.getString("cond_txt");
                        String fl = now.getString("fl");
                        String hum = now.getString("hum");
                        String pcpn = now.getString("pcpn");
                        String pres = now.getString("pres");
                        String tmp = now.getString("tmp");
                        String vis = now.getString("vis");
                        String windDeg = now.getString("wind_deg");
                        String windDir = now.getString("wind_dir");
                        String windSc = now.getString("wind_sc");
                        String windSpd = now.getString("wind_spd");
                        //填充数据
                        Log.d(TAG,"weatherNow.setCondCode(condCode)="+condCode);
                        weatherNow.setCondCode(condCode);
                        weatherNow.setCondTxt(condTxt);
                        weatherNow.setFl(fl);
                        weatherNow.setTmp(tmp);

                    //7日天气预报(dailyForecast)
                    JSONArray dailyForecast = heWeather.getJSONArray("daily_forecast");
                    for (int i = 0;i<dailyForecast.length();i++){
                        JSONObject dailyForecast0 = dailyForecast.getJSONObject(i);
                        String tmpMax = dailyForecast0.getString("tmp_max");
                        String tmpMin = dailyForecast0.getString("tmp_min");
                        String date = dailyForecast0.getString("date");
                        String condTxtD = dailyForecast0.getString("cond_txt_d");
                        String condTxtN = dailyForecast0.getString("cond_txt_n");
                        String condCodeD = dailyForecast0.getString("cond_code_d");
                        String condCodeN = dailyForecast0.getString("cond_code_n");
                        /*
                        String windDir = dailyForecast0.getString("wind_dir");
                        String windSc = dailyForecast0.getString("wind_sc");
                        String windSpd = dailyForecast0.getString("wind_spd");
                        Log.d(TAG,"温度"+tmpMin+"℃ ~ "+tmpMax+"℃");
                        Log.d(TAG,date+"的天气："+condTxtD+"转"+condTxtN+" 吹"+windDir+" 风力 "+windSc+" 级");
                        */

                        //实例化weather对象，保存解析过后的数据
                        Weather weather = new Weather(cid,tmpMin,tmpMax,date,
                                condTxtD,condTxtN,condCodeD,condCodeN);
                        weathers.add(weather);
                        Log.d(TAG,weather.getTmpMin()+"->"+weather.getTmpMax());
                    }
                    //生活指数预报
                    JSONArray lifesSyles = heWeather.getJSONArray("lifestyle");
                    for(int i = 0;i<lifesSyles.length();i++) {
                        JSONObject lifeStyle = lifesSyles.getJSONObject(i);
                        String type = lifeStyle.getString("type");
                        //通过生活指数类型代码，获得生活指数类型
                        type = getLifeStyleType(type);
                        String brf = lifeStyle.getString("brf");
                        String txt = lifeStyle.getString("txt");
                        //实例化LifeStyle对象，保存解析过后的数据
                        LifeStyle lifeStyleObject = new LifeStyle(type,brf,txt);
                        Log.d(TAG,type+brf+txt);
                        lifeStyleList.add(lifeStyleObject);

                    }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"format失败");
        }
        //Log.d(TAG,weathers);
        return weathers;
    }

    /**
     * 通过实况天气状况代码，判断返回的天气图片
     * @param CondCode 实况天气状况代码
     * @return 图片资源文件ID
     */
    private int setImageByCondCode(String CondCode)
    {
        Log.d(TAG,localTime);
        boolean isDay = isDay(localTime);
        //如果时间是夜间，则在实况天气状况代码(cond_code)后加一个n
        if (!isDay)
        {
            Log.d(TAG,"现在是夜间");
            CondCode = CondCode+"n";
            Log.d(TAG,CondCode);

        }
        switch (CondCode)
        {
            //白天
            case "100":
                return R.drawable.cond_icon_heweather_100;
            case "101":
                return R.drawable.cond_icon_heweather_101;
            case "102":
                return R.drawable.cond_icon_heweather_102;
            case "103":
                return R.drawable.cond_icon_heweather_103;
            case "104":
                return R.drawable.cond_icon_heweather_104;
            case "200":
                return R.drawable.cond_icon_heweather_200;
            case "201":
                return R.drawable.cond_icon_heweather_201;
            case "202":
                return R.drawable.cond_icon_heweather_202;
            case "203":
                return R.drawable.cond_icon_heweather_203;
            case "204":
                return R.drawable.cond_icon_heweather_204;
            case "205":
                return R.drawable.cond_icon_heweather_205;
            case "206":
                return R.drawable.cond_icon_heweather_206;
            case "207":
                return R.drawable.cond_icon_heweather_207;
            case "208":
                return R.drawable.cond_icon_heweather_208;
            case "209":
                return R.drawable.cond_icon_heweather_209;
            case "210":
                return R.drawable.cond_icon_heweather_210;
            case "211":
                return R.drawable.cond_icon_heweather_211;
            case "212":
                return R.drawable.cond_icon_heweather_212;
            case "213":
                return R.drawable.cond_icon_heweather_213;
            case "300":
                return R.drawable.cond_icon_heweather_300;
            case "301":
                return R.drawable.cond_icon_heweather_301;
            case "302":
                return R.drawable.cond_icon_heweather_302;
            case "303":
                return R.drawable.cond_icon_heweather_303;
            case "304":
                return R.drawable.cond_icon_heweather_304;
            case "305":
                return R.drawable.cond_icon_heweather_305;
            case "306":
                return R.drawable.cond_icon_heweather_306;
            case "307":
                return R.drawable.cond_icon_heweather_307;
            case "308":
                return R.drawable.cond_icon_heweather_308;
            case "309":
                return R.drawable.cond_icon_heweather_309;
            case "310":
                return R.drawable.cond_icon_heweather_310;
            case "311":
                return R.drawable.cond_icon_heweather_311;
            case "312":
                return R.drawable.cond_icon_heweather_312;
            case "313":
                return R.drawable.cond_icon_heweather_313;
            case "314":
                return R.drawable.cond_icon_heweather_314;
            case "315":
                return R.drawable.cond_icon_heweather_315;
            case "316":
                return R.drawable.cond_icon_heweather_316;
            case "317":
                return R.drawable.cond_icon_heweather_317;
            case "318":
                return R.drawable.cond_icon_heweather_318;
            case "399":
                return R.drawable.cond_icon_heweather_399;
            case "400":
                return R.drawable.cond_icon_heweather_400;
            case "401":
                return R.drawable.cond_icon_heweather_401;
            case "402":
                return R.drawable.cond_icon_heweather_402;
            case "403":
                return R.drawable.cond_icon_heweather_403;
            case "404":
                return R.drawable.cond_icon_heweather_404;
            case "405":
                return R.drawable.cond_icon_heweather_405;
            case "406":
                return R.drawable.cond_icon_heweather_406;
            case "407":
                return R.drawable.cond_icon_heweather_407;
            case "408":
                return R.drawable.cond_icon_heweather_408;
            case "409":
                return R.drawable.cond_icon_heweather_409;
            case "410":
                return R.drawable.cond_icon_heweather_410;
            case "499":
                return R.drawable.cond_icon_heweather_499;
            case "500":
                return R.drawable.cond_icon_heweather_500;
            case "501":
                return R.drawable.cond_icon_heweather_501;
            case "502":
                return R.drawable.cond_icon_heweather_502;
            case "503":
                return R.drawable.cond_icon_heweather_503;
            case "504":
                return R.drawable.cond_icon_heweather_504;
            case "507":
                return R.drawable.cond_icon_heweather_507;
            case "508":
                return R.drawable.cond_icon_heweather_508;
            case "509":
                return R.drawable.cond_icon_heweather_509;
            case "510":
                return R.drawable.cond_icon_heweather_510;
            case "511":
                return R.drawable.cond_icon_heweather_511;
            case "512":
                return R.drawable.cond_icon_heweather_512;
            case "513":
                return R.drawable.cond_icon_heweather_513;
            case "514":
                return R.drawable.cond_icon_heweather_514;
            case "515":
                return R.drawable.cond_icon_heweather_515;
            case "900":
                return R.drawable.cond_icon_heweather_900;
            case "901":
                return R.drawable.cond_icon_heweather_901;
            case "999":
                return R.drawable.cond_icon_heweather_999;
            //夜间
            case "100n":
                return R.drawable.cond_icon_heweather_100n;
            case "101n":
                return R.drawable.cond_icon_heweather_101;
            case "102n":
                return R.drawable.cond_icon_heweather_102;
            case "103n":
                return R.drawable.cond_icon_heweather_103n;
            case "104n":
                return R.drawable.cond_icon_heweather_104n;
            case "200n":
                return R.drawable.cond_icon_heweather_200;
            case "201n":
                return R.drawable.cond_icon_heweather_201;
            case "202n":
                return R.drawable.cond_icon_heweather_202;
            case "203n":
                return R.drawable.cond_icon_heweather_203;
            case "204n":
                return R.drawable.cond_icon_heweather_204;
            case "205n":
                return R.drawable.cond_icon_heweather_205;
            case "206n":
                return R.drawable.cond_icon_heweather_206;
            case "207n":
                return R.drawable.cond_icon_heweather_207;
            case "208n":
                return R.drawable.cond_icon_heweather_208;
            case "209n":
                return R.drawable.cond_icon_heweather_209;
            case "210n":
                return R.drawable.cond_icon_heweather_210;
            case "211n":
                return R.drawable.cond_icon_heweather_211;
            case "212n":
                return R.drawable.cond_icon_heweather_212;
            case "213n":
                return R.drawable.cond_icon_heweather_213;
            case "300n":
                return R.drawable.cond_icon_heweather_300n;
            case "301n":
                return R.drawable.cond_icon_heweather_301n;
            case "302n":
                return R.drawable.cond_icon_heweather_302;
            case "303n":
                return R.drawable.cond_icon_heweather_303;
            case "304n":
                return R.drawable.cond_icon_heweather_304;
            case "305n":
                return R.drawable.cond_icon_heweather_305;
            case "306n":
                return R.drawable.cond_icon_heweather_306;
            case "307n":
                return R.drawable.cond_icon_heweather_307;
            case "308n":
                return R.drawable.cond_icon_heweather_308;
            case "309n":
                return R.drawable.cond_icon_heweather_309;
            case "310n":
                return R.drawable.cond_icon_heweather_310;
            case "311n":
                return R.drawable.cond_icon_heweather_311;
            case "312n":
                return R.drawable.cond_icon_heweather_312;
            case "313n":
                return R.drawable.cond_icon_heweather_313;
            case "314n":
                return R.drawable.cond_icon_heweather_314;
            case "315n":
                return R.drawable.cond_icon_heweather_315;
            case "316n":
                return R.drawable.cond_icon_heweather_316;
            case "317n":
                return R.drawable.cond_icon_heweather_317;
            case "318n":
                return R.drawable.cond_icon_heweather_318;
            case "399n":
                return R.drawable.cond_icon_heweather_399;
            case "400n":
                return R.drawable.cond_icon_heweather_400;
            case "401n":
                return R.drawable.cond_icon_heweather_401;
            case "402n":
                return R.drawable.cond_icon_heweather_402;
            case "403n":
                return R.drawable.cond_icon_heweather_403;
            case "404n":
                return R.drawable.cond_icon_heweather_404;
            case "405n":
                return R.drawable.cond_icon_heweather_405;
            case "406n":
                return R.drawable.cond_icon_heweather_406n;
            case "407n":
                return R.drawable.cond_icon_heweather_407n;
            case "408n":
                return R.drawable.cond_icon_heweather_408;
            case "409n":
                return R.drawable.cond_icon_heweather_409;
            case "410n":
                return R.drawable.cond_icon_heweather_410;
            case "499n":
                return R.drawable.cond_icon_heweather_499;
            case "500n":
                return R.drawable.cond_icon_heweather_500;
            case "501n":
                return R.drawable.cond_icon_heweather_501;
            case "502n":
                return R.drawable.cond_icon_heweather_502;
            case "503n":
                return R.drawable.cond_icon_heweather_503;
            case "504n":
                return R.drawable.cond_icon_heweather_504;
            case "507n":
                return R.drawable.cond_icon_heweather_507;
            case "508n":
                return R.drawable.cond_icon_heweather_508;
            case "509n":
                return R.drawable.cond_icon_heweather_509;
            case "510n":
                return R.drawable.cond_icon_heweather_510;
            case "511n":
                return R.drawable.cond_icon_heweather_511;
            case "512n":
                return R.drawable.cond_icon_heweather_512;
            case "513n":
                return R.drawable.cond_icon_heweather_513;
            case "514n":
                return R.drawable.cond_icon_heweather_514;
            case "515n":
                return R.drawable.cond_icon_heweather_515;
            case "900n":
                return R.drawable.cond_icon_heweather_900;
            case "901n":
                return R.drawable.cond_icon_heweather_901;
            case "999n":
                return R.drawable.cond_icon_heweather_999;
            default:
                return R.drawable.cond_icon_heweather_999;

        }

    }

    /**
     * 判断是否为白天(6:00-18:00)
     * @param localTime 接口更新时间(当地时间loc)
     * @return 白天为True
     */
    private boolean isDay(String localTime)
    {
        String format = "HH:mm";
        Date nowTime = null;
        Date startTime = null;
        Date endTime = null;
        try {
            nowTime = new SimpleDateFormat(format).parse(localTime);
            startTime = new SimpleDateFormat(format).parse("06:00");
            endTime = new SimpleDateFormat(format).parse("18:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar now = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        now.setTime(nowTime);
        start.setTime(startTime);
        end.setTime(endTime);
        if (now.after(start) && now.before(end))
        {
            return true;
        }else {
            return false;
        }

    }

    /**
     * 通过生活指数类型代码(type)，返回生活指数类型
     * @param type 生活指数类型代码
     * @return 生活指数类型
     */
    private String getLifeStyleType(String type)
    {
        switch (type)
        {
            case "comf":
                return "舒适度指数";
            case "cw":
                return "洗车指数";
            case "drsg":
                return "穿衣指数";
            case "flu":
                return "感冒指数";
            case "sport":
                return "运动指数";
            case "trav":
                return "旅游指数";
            case "uv":
                return "紫外线指数";
            case "air":
                return "空气污染扩散条件指数";
            default:
                return "其他";
        }
    }

}
