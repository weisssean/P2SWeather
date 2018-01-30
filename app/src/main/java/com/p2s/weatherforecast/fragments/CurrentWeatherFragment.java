package com.p2s.weatherforecast.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.p2s.weatherforecast.R;
import com.p2s.weatherforecast.classes.Weather;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CurrentWeatherFragment extends Fragment {

    @BindView(R.id.txt_date)
    TextView txt_date;
    @BindView(R.id.txt_summary)
    TextView txt_summary;
    @BindView(R.id.txt_humidity)
    TextView txt_humidity;
    @BindView(R.id.txt_windSpeed)
    TextView txt_windSpeed;
    @BindView(R.id.txt_temperature)
    TextView txt_temperature;
    @BindView(R.id.txt_temperatureMin)
    TextView txt_temperatureMin;
    @BindView(R.id.txt_temperatureMax)
    TextView txt_temperatureMax;
    @BindView(R.id.ll_tempHolder)
    View ll_tempHolder;
    @BindView(R.id.ll_minMaxHolder)
    View ll_minMaxHolder;

    private Weather mWeather;


    public static CurrentWeatherFragment newInstance() {
        return new CurrentWeatherFragment();
    }

    public CurrentWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_current_weather, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    public void setWeather(Weather weather) {
        mWeather = weather;
        setTextBoxes(mWeather);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            mWeather = (Weather) savedInstanceState.getSerializable("weather");
        }
        if (mWeather != null)
            setTextBoxes(mWeather);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("weather", mWeather);
        super.onSaveInstanceState(outState);
    }

    private void setTextBoxes(Weather weather) {
        if (txt_summary!=null && weather != null) {
            txt_summary.setText(weather.getSummary());
            txt_humidity.setText(weather.getHumidity() + "");
            txt_windSpeed.setText(weather.getWindSpeed() + "");
            txt_temperature.setText(weather.getTemperature());
            txt_temperatureMin.setText(weather.getTemperatureMin());
            txt_temperatureMax.setText(weather.getTemperatureMax());

            if (TextUtils.isEmpty(weather.getTemperature())) {
                ll_tempHolder.setVisibility(View.GONE);
                ll_minMaxHolder.setVisibility(View.VISIBLE);
            } else {
                ll_tempHolder.setVisibility(View.VISIBLE);
                ll_minMaxHolder.setVisibility(View.GONE);
            }

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            String date = format.format(weather.getTime());

            txt_date.setText(date);
        }
    }


}
