package com.p2s.weatherforecast.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.p2s.weatherforecast.R;
import com.p2s.weatherforecast.classes.Weather;

import java.text.SimpleDateFormat;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentWeatherFragment extends Fragment {


    private TextView txt_lat;
    private TextView txt_lng;
    private TextView txt_summary;
    private TextView txt_temperature;
    private TextView txt_humidity;
    private TextView txt_windSpeed;
    private TextView txt_date;

     private Weather mWeather;
    private View ll_tempHolder;
    private View ll_minMaxHolder;
    private TextView txt_temperatureMin;
    private TextView txt_temperatureMax;


    public static CurrentWeatherFragment newInstance() {
        CurrentWeatherFragment frag = new CurrentWeatherFragment();


        return frag;
    }


    public CurrentWeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_current_weather, container, false);
        txt_lat = (TextView) v.findViewById(R.id.txt_lat);
        txt_lng = (TextView) v.findViewById(R.id.txt_lng);
        txt_summary = (TextView) v.findViewById(R.id.txt_summary);
        txt_temperature = (TextView) v.findViewById(R.id.txt_temperature);
        txt_temperatureMin = (TextView) v.findViewById(R.id.txt_temperatureMin);
        txt_temperatureMax = (TextView) v.findViewById(R.id.txt_temperatureMax);
        txt_humidity =(TextView) v.findViewById(R.id.txt_humidity);
        txt_windSpeed =(TextView) v.findViewById(R.id.txt_windSpeed);
        txt_date =(TextView) v.findViewById(R.id.txt_date);
        ll_tempHolder = v.findViewById(R.id.ll_tempHolder);
        ll_minMaxHolder = v.findViewById(R.id.ll_minMaxHolder);

        return v;
    }

    public void setWeather(Weather weather){
        mWeather = weather;
        setTextBoxes(mWeather);

     }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState!=null){
            mWeather = (Weather) savedInstanceState.getSerializable("weather");
        }
        if (mWeather!=null)
            setTextBoxes(mWeather);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("weather", mWeather);
        super.onSaveInstanceState(outState);
    }


    private void setTextBoxes(Weather weather) {
        if (txt_lat!=null&&weather!=null) {
            txt_lat.setText(weather.getLat() + "");
            txt_lng.setText(weather.getLng() + "");
            txt_summary.setText(weather.getSummary());
            txt_temperature.setText(weather.getTemperature());
            txt_temperatureMin.setText(weather.getTemperatureMin());
            txt_temperatureMax.setText(weather.getTemperatureMax());

            if (TextUtils.isEmpty(weather.getTemperature())){
                ll_tempHolder.setVisibility(View.GONE);
                ll_minMaxHolder.setVisibility(View.VISIBLE);
            }else {
                ll_tempHolder.setVisibility(View.VISIBLE);
                ll_minMaxHolder.setVisibility(View.GONE);
            }

            txt_humidity.setText(weather.getHumidity() + "");
            txt_windSpeed.setText(weather.getWindSpeed() + "");


            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            String date = format.format(weather.getTime());


            txt_date.setText(date);
        }
    }


}
