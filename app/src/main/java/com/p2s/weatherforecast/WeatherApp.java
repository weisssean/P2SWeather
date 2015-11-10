package com.p2s.weatherforecast;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.p2s.weatherforecast.interfaces.ForecastService;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by MonkeyFish on 11/9/15.
 */
public class WeatherApp extends Application {
    private static WeatherApp mInstance;
    private final static String DARK_SKY_URL = "https://api.forecast.io";
    private static ForecastService mWeatherService;

    public static ForecastService getWeatherService() {
        return mWeatherService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DARK_SKY_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mWeatherService = retrofit.create(ForecastService.class);

    }
    public static WeatherApp getInstance() {
        return mInstance;
    }

}
