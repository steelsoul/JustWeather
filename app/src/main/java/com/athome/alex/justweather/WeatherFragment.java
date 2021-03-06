package com.athome.alex.justweather;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WeatherFragment extends Fragment {
    Typeface weatherFont;
    Handler handler = new Handler();
    ArrayList<WeatherData> weather_data = new ArrayList<>();
    MySpecAdapter adapter;
    SwipeRefreshLayout swipe_refresh_layout;

    public class WeatherData
    {
        public WeatherData(String city_field, String humidity, String pressure_kpa, double temp, long updated_time, int icon_id, long icon_sunrize, long icon_sunset) {
            this.city_field = city_field;
            this.humidity = humidity;
            this.pressure_kpa = pressure_kpa;
            this.temperature = temp;
            this.updated_time = updated_time;
            this.icon_id = icon_id;
            this.icon_sunrize = icon_sunrize;
            this.icon_sunset = icon_sunset;
        }

        String city_field;
        String humidity;
        String pressure_kpa;
        double temperature;
        long  updated_time;
        int icon_id;
        long icon_sunrize;
        long icon_sunset;
    }

    class MySpecAdapter extends BaseAdapter {
        Context ctx;
        LayoutInflater inflater;
        ArrayList<WeatherData> objects;
        Typeface specFont;

        // TODO: place adapter code outside.
        MySpecAdapter(Context context, ArrayList<WeatherData> data, Typeface font) {
            ctx = context;
            objects = data;
            specFont = font;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public Object getItem(int position) {
            return objects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.my_list_item, parent, false);
            }
            WeatherData w = getWeatherData(position);

            TextView city = view.findViewById(R.id.city_field);
            city.setText(w.city_field);

            TextView updated_time = view.findViewById(R.id.updated_field);
            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(w.updated_time));
            updated_time.setText(updatedOn);

            TextView weatherIcon = view.findViewById(R.id.weather_icon);
            weatherIcon.setTypeface(specFont);
            weatherIcon.setText(getWeatherIconText(w.icon_id, w.icon_sunrize, w.icon_sunset));

            TextView current_temp = view.findViewById(R.id.current_temperature_field);
            current_temp.setText(String.format("%.1f", w.temperature));

            TextView humidity = view.findViewById(R.id.humidity);
            humidity.setText(w.humidity);

            TextView pressure = view.findViewById(R.id.pressure);
            pressure.setText(w.pressure_kpa);

            return view;
        }

        private WeatherData getWeatherData(int position)
        {
            return objects.get(position);
        }

        private String getWeatherIconText(int actualId, long sunrise, long sunset){
            int id = actualId / 100;
            String icon = "";
            if(actualId == 800){
                long currentTime = new Date().getTime() / 1000;
                if(currentTime>=sunrise && currentTime<sunset) {
                    icon = getActivity().getString(R.string.weather_sunny);
                } else {
                    icon = getActivity().getString(R.string.weather_clear_night);
                }
            } else {
                switch(id) {
                    case 2 : icon = getActivity().getString(R.string.weather_thunder);
                        break;
                    case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                        break;
                    case 7 : icon = getActivity().getString(R.string.weather_foggy);
                        break;
                    case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                        break;
                    case 6 : icon = getActivity().getString(R.string.weather_snowy);
                        break;
                    case 5 : icon = getActivity().getString(R.string.weather_rainy);
                        break;
                }
            }
            return icon;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.change_city){
            showInputDialog();
        }
        return false;
    }

    private void showInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Change city");
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeCity(input.getText().toString());
            }
        });
        builder.show();
    }

    public void changeCity(String city){
        updateWeatherData(city);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        updateWeatherData(new CityPreference(getActivity()).getCity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        adapter = new MySpecAdapter(this.getActivity(), weather_data, weatherFont);
        ListView lv_main = rootView.findViewById(R.id.list_view);
        lv_main.setAdapter(adapter);
        swipe_refresh_layout = rootView.findViewById(R.id.swipe_refresh);
        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateWeatherData(new CityPreference(getActivity()).getCity());
            }
        });
        return rootView;
    }

    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON_Map(city);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json){
        try {
            weather_data.clear();
            adapter.notifyDataSetInvalidated();
            String city_field = json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country");

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");

            /*detailsField*/
            String humidity = main.getString("humidity");
            String pressure = main.getString("pressure");
            double temperature = main.getDouble("temp");
            long dateinfo = json.getLong("dt")*1000;

            int details_id = details.getInt("id");
            long sunrize = json.getJSONObject("sys").getLong("sunrise");
            long sunset = json.getJSONObject("sys").getLong("sunset");

            WeatherData w = new WeatherData(city_field, humidity, pressure, temperature,
                    dateinfo, details_id, sunrize, sunset);
            weather_data.add(w);
            adapter.notifyDataSetChanged();
        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
        swipe_refresh_layout.setRefreshing(false);
    }


}
