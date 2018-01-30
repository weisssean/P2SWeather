package com.p2s.weatherforecast.classes;

import android.util.Log;

/**
 * Created by TacoEater on 11/9/15.
 */
public class ForecastResult {
    private double longitude;
    private double latitude;
    private String timezone;
    private Weather currently;
    private DailyWeather daily;

    public Weather getCurrently() {
        return currently;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public DailyWeather getDaily() {
        return daily;
    }
}
