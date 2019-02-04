package com.athome.alex.justweather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.athome.alex.justweather.model.ForecastUnit;

import java.text.SimpleDateFormat;
import java.util.List;

public class ForecastListAdapter extends RecyclerView.Adapter<ForecastListAdapter.ForecastHolder> {

    public ForecastListAdapter(Context context) {
        m_Inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ForecastHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = m_Inflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ForecastHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastHolder forecastHolder, int i) {
        if (m_ForecastList != null) {
            // TODO: localize
            ForecastUnit unit = m_ForecastList.get(i);
            String description = "Date: ";
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd kk:mm ");
            description += dateFormat.format(unit.getM_Date());
            description += " Min: ";
            description += unit.getM_MinTemperature().toString();
            description += " Max: ";
            description += unit.getM_MaxTemperature().toString();
            description += " Hum: ";
            description += unit.getM_Humidity().toString();
            description += " Pres: ";
            description += unit.getM_Pressure().toString();
            forecastHolder.m_TextView.setText(description);
        } else {
            forecastHolder.m_TextView.setText("No data available");
        }
    }

    @Override
    public int getItemCount() {
        if (m_ForecastList != null)
            return m_ForecastList.size();
        else
            return 0;
    }

    class ForecastHolder extends RecyclerView.ViewHolder {
        private final TextView m_TextView;

        private ForecastHolder(View itemView) {
            super(itemView);
            m_TextView = itemView.findViewById(R.id.textView);
        }
    }

    private final LayoutInflater m_Inflater;
    private List<ForecastUnit> m_ForecastList;

    void setForecastList(List<ForecastUnit> forecast) {
        m_ForecastList = forecast;
        notifyDataSetChanged();
    }

}
