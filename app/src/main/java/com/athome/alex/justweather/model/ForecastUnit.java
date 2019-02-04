package com.athome.alex.justweather.model;

import java.util.Date;

import static java.lang.Math.round;

public class ForecastUnit {
    private Date m_Date;
    private Float m_MinTemperature;
    private Float m_MaxTemperature;
    private Float m_Pressure;
    private Integer m_Humidity;
    private Integer m_WeatherID;

    public Date getM_Date() {
        return m_Date;
    }

    public void setM_Date(Date m_Date) {
        this.m_Date = m_Date;
    }

    public Float getM_MinTemperature() {
        return m_MinTemperature;
    }

    public Integer getI_MinTemperature() { return round(m_MinTemperature);}

    public void setM_MinTemperature(Float m_MinTemperature) {
        this.m_MinTemperature = m_MinTemperature;
    }

    public Float getM_MaxTemperature() {
        return m_MaxTemperature;
    }

    public Integer getI_MaxTemperature() {
        return round(m_MaxTemperature);
    }

    public void setM_MaxTemperature(Float m_MaxTemperature) {
        this.m_MaxTemperature = m_MaxTemperature;
    }

    public Float getM_Pressure() {
        return m_Pressure;
    }

    public void setM_Pressure(Float m_Pressure) {
        this.m_Pressure = m_Pressure;
    }

    public Integer getM_Humidity() {
        return m_Humidity;
    }

    public void setM_Humidity(Integer m_Humidity) {
        this.m_Humidity = m_Humidity;
    }

    public Integer getM_WeatherID() {
        return m_WeatherID;
    }

    public void setM_WeatherID(Integer m_WeatherID) {
        this.m_WeatherID = m_WeatherID;
    }
}
