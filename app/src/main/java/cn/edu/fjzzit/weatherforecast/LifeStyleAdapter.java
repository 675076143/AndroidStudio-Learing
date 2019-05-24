package cn.edu.fjzzit.weatherforecast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class LifeStyleAdapter extends ArrayAdapter<LifeStyle> {

    private int resourceID;
    public LifeStyleAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        resourceID = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LifeStyle lifeStyle = getItem(position);
        //获取控件
        View view = LayoutInflater.from(getContext()).inflate(resourceID, parent, false);
        TextView type = view.findViewById(R.id.text_view_type);
        TextView brf = view.findViewById(R.id.text_view_brf);
        TextView txt = view.findViewById(R.id.text_view_txt);
        //设置控件Text的值
        type.setText(lifeStyle.getType());
        brf.setText(lifeStyle.getBrf());
        txt.setText(lifeStyle.getTxt());
        return view;
    }
}
