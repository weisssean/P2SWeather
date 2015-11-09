package com.p2s.weatherforecast.interfaces;

import com.p2s.weatherforecast.classes.ForecastResult;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by MonkeyFish on 11/9/15.
 */
public interface ForecastService {
    @GET("/forecast/{APIKEY}/{LATITUDE},{LONGITUDE}")
    Call<ForecastResult> forecast(@Path("APIKEY")String apiKey,@Path("LATITUDE")double lat,@Path("LONGITUDE") double lng);

}
