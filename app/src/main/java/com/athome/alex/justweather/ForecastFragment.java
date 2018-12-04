package com.athome.alex.justweather;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.athome.alex.justweather.model.ForecastUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ForecastFragment extends Fragment {
    Handler handler = new Handler();
    ForecastListAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateForecastData(new CityPreference(getActivity()).getCity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        adapter = new ForecastListAdapter(this.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        return rootView;
    }

    private void updateForecastData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON_Forecast(getActivity(), city);
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
                            renderForecast(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderForecast(JSONObject json) {
        try {
            List<ForecastUnit> forecastList = new ArrayList<>();
            JSONArray array = json.getJSONArray("list");

            for (int i = 0; i < array.length(); i++) {
                JSONObject record = (JSONObject) array.get(i);
                ForecastUnit unit = new ForecastUnit();
                unit.setM_Date(new Date(record.getLong("dt")*1000));
                unit.setM_MinTemperature((float) record.getJSONObject("main").getDouble("temp_min"));
                unit.setM_MaxTemperature((float) record.getJSONObject("main").getDouble("temp_max"));
                unit.setM_Pressure((float) record.getJSONObject("main").getDouble("pressure"));
                unit.setM_Humidity(record.getJSONObject("main").getInt("humidity"));
                unit.setM_WeatherID(record.getJSONArray("weather").getJSONObject(0).getInt("id"));
                forecastList.add(unit);
            }

            adapter.setForecastList(forecastList);
        } catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }
}
