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
import java.util.List;

public class CityAdapter extends ArrayAdapter<City> {

    private static final String TAG = "CityAdapter";
    private int resourceID;
    public CityAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        resourceID = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        City city = getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(resourceID, parent, false);
        TextView location = view.findViewById(R.id.text_view_location);
        TextView parent_city = view.findViewById(R.id.text_view_parent_city);
        TextView admin_area = view.findViewById(R.id.text_view_admin_area);
        TextView cnty = view.findViewById(R.id.text_view_cnty);

        location.setText(city.getLocation());
        parent_city.setText(city.getParent_city());
        admin_area.setText(city.getAdmin_area());
        cnty.setText(city.getCnty());

        return view;
    }
}
