package com.p2s.weatherforecast.classes;

import android.util.Log;

/**
 * Created by MonkeyFish on 11/9/15.
 */
public class ForecastResult {
    private double longitude;
    private double latitude;
    private String timezone;
    private Weather currently;
    private DailyWeather daily;


    public ForecastResult(Object body) {
        Log.d("",body.toString());
    }

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
