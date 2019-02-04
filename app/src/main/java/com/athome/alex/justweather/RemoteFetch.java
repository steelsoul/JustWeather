package com.athome.alex.justweather;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class RemoteFetch {
    private static final String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";
    private static final String OPEN_WEATHER_FORECAST_API =
            "http://api.openweathermap.org/data/2.5/forecast?q=%s&units=metric";
    private static String OPEN_WEATHER_API_KEY = "";
    private static final int SUCCESS_RESPONSE_CODE = 200;

    public static JSONObject getJSON_Map(String city){
        try {
            URLConnection connection = getUrlConnection(OPEN_WEATHER_MAP_API, city);
            return getJsonObject(connection);
        }catch(Exception e){
            return null;
        }
    }

    public static JSONObject getJSON_Forecast(String city) {
        try {
            URLConnection connection = getUrlConnection(OPEN_WEATHER_FORECAST_API, city);
            return getJsonObject(connection);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static void SetAPIKey(String apiKey) {
        OPEN_WEATHER_API_KEY = apiKey;
    }

    private static JSONObject getJsonObject(URLConnection connection) throws JSONException {
        StringBuffer json = getJSON(connection);
        JSONObject data = new JSONObject(json.toString());
        return validateJsonObject(data);
    }

    @Nullable
    private static JSONObject validateJsonObject(JSONObject data) throws JSONException {
        if (data.getInt("cod") != SUCCESS_RESPONSE_CODE) {
            return null;
        }
        return data;
    }

    private static StringBuffer getJSON(URLConnection connection) {
        BufferedReader reader;
        reader = getBufferedReader(connection);
        if (reader == null) return null;

        StringBuffer json = new StringBuffer(1024);
        readLines(reader, json);

        closeReader(reader);
        return json;
    }

    @NonNull
    private static URLConnection getUrlConnection(String api, String city) throws IOException {
        URL url = new URL(String.format(api, city));
        URLConnection connection = url.openConnection();
        connection.addRequestProperty("x-api-key", OPEN_WEATHER_API_KEY);
        return connection;
    }

    private static void closeReader(BufferedReader reader) {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readLines(BufferedReader reader, StringBuffer json) {
        String line;

        try {
            do {
                line = reader.readLine();
                json.append(line).append('\n');
            } while (line != null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private static BufferedReader getBufferedReader(URLConnection connection) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return reader;
    }
}
