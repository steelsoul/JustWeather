package com.athome.alex.justweather;

import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.athome.alex.justweather.model.ForecastUnit;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FragmentForecastGraph extends Fragment {
    private LineGraphSeries<DataPoint> m_DataSeries;
    private LineGraphSeries<DataPoint> m_LowDataSeries;
    Handler m_Handler = new Handler();
    GraphView m_Graph = null;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        updateForecastData(new CityPreference(getActivity()).getCity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_graphforecast, container, false);
        m_Graph = rootView.findViewById(R.id.graph);
        return rootView;
    }

    private void updateForecastData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON_Forecast(city);
                if(json == null){
                    m_Handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    m_Handler.post(new Runnable(){
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
            List<DataPoint> points1 = new ArrayList<>();
            List<DataPoint> points2 = new ArrayList<>();
            for (ForecastUnit unit: forecastList) {
                points1.add(new DataPoint(unit.getM_Date().getTime(), unit.getI_MaxTemperature()));
                points2.add(new DataPoint(unit.getM_Date().getTime(), unit.getI_MinTemperature()));
            }
            DataPoint[] datapoints1 = new DataPoint[points1.size()];
            DataPoint[] datapoints2 = new DataPoint[points2.size()];
            datapoints1 = points1.toArray(datapoints1);
            datapoints2 = points2.toArray(datapoints2);
            m_DataSeries = new LineGraphSeries<>(datapoints1);
            m_DataSeries.setColor(Color.RED);
            m_LowDataSeries = new LineGraphSeries<>(datapoints2);
            m_LowDataSeries.setColor(Color.BLUE);
            renderGraph();

        } catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    private void renderGraph() {
        m_Graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        m_Graph.addSeries(m_DataSeries);
        m_Graph.addSeries(m_LowDataSeries);

        // set manual x bounds to have nice steps
        m_Graph.getViewport().setMinX(m_DataSeries.getLowestValueX());
        m_Graph.getViewport().setMaxX(m_DataSeries.getHighestValueX());
        m_Graph.getViewport().setXAxisBoundsManual(true);
        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        m_Graph.getGridLabelRenderer().setHumanRounding(false);
        m_Graph.getGridLabelRenderer().setHorizontalLabelsAngle(50);
    }

}
