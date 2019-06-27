package cn.edu.fjzzit.weatherforecast;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 天气数据适配器
 * 三列数据，分别为：日期、天气、温度
 */
public class ForecastAdapter extends ArrayAdapter<Weather> {

    MyUtils myUtils = new MyUtils();
    private static final String TAG = "ForecastAdapter";
    private int resourceID;
    public ForecastAdapter( Context context, int resource, List objects) {
        super(context, resource, objects);
        resourceID = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Weather weather = getItem(position);
        //获取控件
        View view = LayoutInflater.from(getContext()).inflate(resourceID, parent, false);
        TextView dateTextView = view.findViewById(R.id.text_view_date);
        TextView forecastTextView = view.findViewById(R.id.text_view_forecast);
        TextView tempTextView = view.findViewById(R.id.text_view_temp);
        //设置控件Text的值
        switch (position){
            case 0:
                dateTextView.setText("今天");
                break;
            case 1:
                dateTextView.setText("明天");
                break;
            case 2:
                dateTextView.setText("后天");
                break;
            default:
                dateTextView.setText(myUtils.dateToWeek(weather.getDate()));
                break;
        }

        forecastTextView.setText(weather.getCondTxtD()+"转"+weather.getCondTxtN());
        tempTextView.setText(myUtils.temp_unit(weather.getTmpMin()) + "->" + myUtils.temp_unit(weather.getTmpMax()));
        return view;
    }
}
