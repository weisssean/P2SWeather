package com.p2s.weatherforecast.interfaces;

import com.p2s.weatherforecast.classes.ForecastResult;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by TacoEater on 11/9/15.
 */
public interface ForecastService {
     final static String API_KEY = "203bf0976335ed98863b556ed9f61f79";

    @GET("/forecast/"+API_KEY+"/{LATITUDE},{LONGITUDE}")
    Call<ForecastResult> forecast(@Path("LATITUDE")double lat,@Path("LONGITUDE") double lng);

}
