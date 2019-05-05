package cn.edu.fjzzit.weatherforecast;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ForecastAdapter extends ArrayAdapter<Weather> {

    private int resourceID;
    public ForecastAdapter( Context context, int resource, List objects) {
        super(context, resource, objects);
        resourceID = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Weather weather = getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(resourceID, parent, false);
        TextView dateTextView = view.findViewById(R.id.text_view_date);
        TextView forecastTextView = view.findViewById(R.id.text_view_forecast);
        TextView tempTextView = view.findViewById(R.id.text_view_temp);

        dateTextView.setText(weather.getDate());
        forecastTextView.setText(weather.getCondTxtD()+"转"+weather.getCondTxtN());

        //通过配置文件判断使用华氏度还是摄氏度
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String temp_unit = preferences.getString("temp_unit", "摄氏度");

        if (temp_unit.equals("华氏度")){
            Double TmpMin = 32 + (Double.parseDouble(weather.getTmpMin())*1.8);
            Double TmpMax = 32 + (Double.parseDouble(weather.getTmpMax())*1.8);
            tempTextView.setText(TmpMin+"℉ -> "+TmpMax+"℉");
        }
        else {
            tempTextView.setText(weather.getTmpMin()+"℃ -> "+weather.getTmpMax()+"℃");
        }

        return view;
    }
}