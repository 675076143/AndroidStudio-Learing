package cn.edu.fjzzit.weatherforecast;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.List;

public class FindCityActivity extends AppCompatActivity {

    private static final int FIND_SUCCESS = 2;
    private static final int FIND_FAILED = -2;
    private String cityName;
    private TextView mTextViewCityName;
    private ListView mListViewFoundCity;
    private Button mBtnFindCity;
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
                        Toast.LENGTH_LONG).show();
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_city);
        mBtnFindCity = findViewById(R.id.button_find_city);
        mListViewFoundCity = findViewById(R.id.list_view_found_city);
        mTextViewCityName = findViewById(R.id.edit_text_cityid);

        mBtnFindCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityName = mTextViewCityName.getText().toString();
                findCity();
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
