package cn.edu.fjzzit.weatherforecast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;

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
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

public class WeatherFragment extends Fragment {
    private static final String PARAM = "LOCATION";
    private static final String TAG = "WeatherFragment";
    private ListView mForecastListView;
    //private ListView mListViewLifeStyle;
    private static final int UPDATE_SUCCESS = 101;
    private static final int UPDATE_FAILED = 102;
    private TextView mTextViewCityTitle;
    private TextView mTextViewNowTmp;
    private TextView mTextViewNowFl;
    private TextView mTextViewNowCondTxt;
    private ImageView mImageViewNowCondCode;
    private String cid = "CN101230601";
    private String localTime;
    private WeatherNow weatherNow = new WeatherNow();
    private List<LifeStyle> lifeStyleList = new ArrayList();
    private List<Weather> weathers = new ArrayList();
    private RecyclerView mRecyclerView;
    private MyUtils myUtils = new MyUtils();    //工具类，包含根据id获取图片，转换生活指数类型等
    private String mLocaion;
    //折线图
    private LineChartView mLineChartView24H;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //private ArrayList<Entry> pointValues = new ArrayList<>();


    String[] hourly_times = {"","","","","","","",""};//X轴的标注，24小时预报的时间段
    float[] hourly_tmp = {0,0,0,0,0,0,0,0};//图表的数据
    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisValues = new ArrayList<AxisValue>();
    Context context = MyApplication.getContext();

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == UPDATE_SUCCESS){
                //适配器填充七日天气数据

                ForecastAdapter forecastAdapter = new ForecastAdapter(
                        context,
                        R.layout.layout_list_item_forecast,
                        LitePal.where("cityID=?",myUtils.getCurrentCityID()).find(Weather.class));
                mForecastListView.setAdapter(forecastAdapter);
                //填充实时天气数据
                mImageViewNowCondCode.setImageResource(myUtils.setImageByCondCode(weatherNow.getCondCode()));
                mTextViewNowTmp.setText(weatherNow.getTmp()+"℃");
                mTextViewNowFl.setText("体感温度："+weatherNow.getFl()+"℃");
                mTextViewNowCondTxt.setText(weatherNow.getCondTxt());
                mTextViewCityTitle.setText(mLocaion);
                //适配器填充生活指数（CardView）
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
                mRecyclerView.setLayoutManager(gridLayoutManager);
                LifeStyleCardViewAdapter lifeStyleCardViewAdapter = new LifeStyleCardViewAdapter(LitePal.findAll(LifeStyle.class));
                mRecyclerView.setAdapter(lifeStyleCardViewAdapter);
                //Toast显示成功
                Toast.makeText(context,
                        "更新成功",
                        Toast.LENGTH_SHORT).show();

            }
            else if(msg.what == UPDATE_FAILED){
                /*
                //Toast显示失败
                Toast.makeText(MainActivity.this,
                        "更新失败",
                        Toast.LENGTH_SHORT).show();
                */
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        super.onCreate(savedInstanceState);
        //取得ID
        mForecastListView = view.findViewById(R.id.list_view_forecast);
        mTextViewCityTitle = view.findViewById(R.id.text_view_city_title);
        mTextViewNowTmp = view.findViewById(R.id.text_view_now_tmp);
        mImageViewNowCondCode = view.findViewById(R.id.image_view_now_cond_code);
        mTextViewNowFl = view.findViewById(R.id.text_view_now_fl);
        mTextViewNowCondTxt = view.findViewById(R.id.text_view_now_cond_txt);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLineChartView24H = (LineChartView)view.findViewById(R.id.line_chart_view_24h);

        //下拉刷新事件
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        //刷新进度条的颜色为colorPrimary（导航栏颜色）
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG,"刷新测试");
                updateForecast();
                mSwipeRefreshLayout.setRefreshing(false);
                //Toast.makeText(MainActivity.this,"刷新成功！",Toast.LENGTH_SHORT).show();
            }
        });
        //更新天气
        //updateForecast();
        return view;
    }

    public static WeatherFragment newInstance(String cityId) {
        Bundle args = new Bundle();
        args.putString(PARAM, cityId);
        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        Log.d("主碎片onResume","123");
        super.onResume();
        updateForecast();
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
                        .appendQueryParameter("location", "CN101230601")
                        .appendQueryParameter("key", "c0eabd2cbf7d4920bb45ff74c85dad5d")
                        .build();
                String url = uri.toString();
                Log.d("切换城市后返回主界面：", "CN101230601");
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

                    //调用解析json文件的方法 true则为成功取得数据
                    //FORMAT_JSON_DATA_LIFE_STYLES_FAILED则为没有取得生活指数数据
                    if(formatJsonData(jsonStr))
                    {
                        //更新前删除原有数据
                        LitePal.deleteAll(Weather.class, "cityId=?", "CN101230601");
                        LitePal.deleteAll(LifeStyle.class, "cityId=?", "CN101230601");
                        for(Weather weather:weathers){
                            //利用LitePal保存数据
                            weather.save();
                        }
                        //更新前删除原有数据
                        LitePal.deleteAll(LifeStyle.class);
                        for(LifeStyle lifeStyle:lifeStyleList){
                            //利用LitePal保存数据
                            lifeStyle.save();
                        }
                        mHandler.sendEmptyMessage(UPDATE_SUCCESS);

                    }
                    else
                    {
                        mHandler.sendEmptyMessage(UPDATE_FAILED);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG,"TRY失败");
                    mHandler.sendEmptyMessage(UPDATE_FAILED);
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
    private boolean formatJsonData(String string){
        Log.d(TAG,"开始Format：");
        try {

            JSONObject jsonObject = new JSONObject(string);
            JSONArray heWeather6 = jsonObject.getJSONArray("HeWeather6");
            JSONObject heWeather = heWeather6.getJSONObject(0);
            String status = heWeather.getString("status");
            if(!status.equals("ok")){
                return false;
            }
            JSONObject basic = heWeather.getJSONObject("basic");
            String cid =  basic.getString("cid");
            String locaion = basic.getString("location");
            mLocaion = locaion;
            Log.d("城市Loaction = ",mLocaion);
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
            myUtils.setLocalTime(loc.split(" ")[1]);
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
            //因部分地区无生活指数参数，所以使用try catch
            try
            {
                JSONArray lifesSyles = heWeather.getJSONArray("lifestyle");
                for(int i = 0;i<lifesSyles.length();i++) {
                    JSONObject lifeStyle = lifesSyles.getJSONObject(i);
                    String type = lifeStyle.getString("type");
                    //通过生活指数类型代码，获得生活指数类型
                    int typeImg = myUtils.setLifeStyleTypeImg(type);
                    type = myUtils.getLifeStyleType(type);
                    String brf = lifeStyle.getString("brf");
                    String txt = lifeStyle.getString("txt");
                    //实例化LifeStyle对象，保存解析过后的数据
                    LifeStyle lifeStyleObject = new LifeStyle(cid,typeImg,type,brf,txt);
                    Log.d(TAG,String.valueOf(myUtils.setLifeStyleTypeImg(type)));
                    lifeStyleList.add(lifeStyleObject);
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            //逐小时预报
            JSONArray hourlys = heWeather.getJSONArray("hourly");
            for(int i = 0; i < hourlys.length(); i++){
                JSONObject hourly = hourlys.getJSONObject(i);
                String hourlyTmp = hourly.getString("tmp");
                String time = hourly.getString("time");
                String cond_code = hourly.getString("cond_code");
                //只显示时间，不显示日期
                time = time.split(" ")[1];
                Log.d(TAG,time);
                hourly_times[i] = time;
                hourly_tmp[i] = Integer.parseInt(hourlyTmp);
                Log.d(TAG,hourlyTmp);
            }

            getAxisLables();//获取x轴的标注
            getAxisPoints();//获取坐标点
            initLineChart();//初始化


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"format失败");
            return false;
        }
        //Log.d(TAG,weathers);
        return true;
    }

    /**
     * 初始化LineChart的一些设置
     */
    private void initLineChart(){
        Line line = new Line(mPointValues).setColor(Color.WHITE).setCubic(false);  //折线的颜色
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.SQUARE）
        line.setCubic(true);//曲线是否平滑
        line.setFilled(true);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
        line.setPointColor(R.color.colorBackground);
        //line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);
        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false); //坐标轴是否斜着显示
        axisX.setTextColor(Color.WHITE);  //设置字体颜色
        axisX.setName("24小时预报");  //表格名称
        axisX.setTextSize(7);//设置字体大小
        axisX.setMaxLabelChars(8);  //最多几个X轴坐标
        axisX.setValues(mAxisValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
//	    data.setAxisXTop(axisX);  //x 轴在顶部
        Axis axisY = new Axis();  //Y轴
        axisY.setMaxLabelChars(3); //默认是3，只能看最后三个数字
        axisY.setName("温度");//y轴标注
        axisY.setTextSize(7);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
//	    data.setAxisYRight(axisY);  //y轴设置在右边

        //设置行为属性，支持缩放、滑动以及平移

        mLineChartView24H.setInteractive(true);
        mLineChartView24H.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        mLineChartView24H.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        mLineChartView24H.setLineChartData(data);
        mLineChartView24H.setVisibility(View.VISIBLE);

    }

    /**
     * X 轴的显示
     */
    private void getAxisLables(){
        //填充前先清空数据
        mAxisValues.clear();
        for (int i = 0; i < hourly_times.length; i++) {
            mAxisValues.add(new AxisValue(i).setLabel(hourly_times[i]));
            Log.d(TAG, hourly_times[i]);
        }
    }



    /**
     * 图表的每个点的显示
     */
    private void getAxisPoints(){
        //填充前先清空数据
        mPointValues.clear();
        for (int i = 0; i < hourly_tmp.length; i++) {
            mPointValues.add(new PointValue(i, hourly_tmp[i]));
        }
    }


}
