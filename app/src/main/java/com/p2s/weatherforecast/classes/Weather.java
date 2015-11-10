package com.p2s.weatherforecast.classes;

import android.content.Context;
import android.view.View;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by MonkeyFish on 11/9/15.
 */
public class Weather implements Serializable {
    private long time;
    private double humidity;
    private double windSpeed;
    private String summary;
    private String temperature;
    private String temperatureMin;
    private String temperatureMax;
    private double cloudCover;
    private String icon;

    private String lng;
    private String lat;

    public Weather() {
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }


    public String getSummary() {
        return summary;
    }

    public String getTemperature() {
        return temperature!=null?temperature+" ℉":"";
    }

    public String getHumidity() {

        int retval = (int) (humidity*100);

        return  retval+"%";
    }

    public String getWindSpeed() {
        return windSpeed+" mph";
    }

    public Date getTime() {

        return new Date(time*1000);
    }

    public String getTemperatureMin() {
        return temperatureMin+" ℉";
    }

    public String getTemperatureMax() {
        return temperatureMax+" ℉";
    }

    public double getCloudCover() {
        return cloudCover;
    }

    public String getIcon() {
        return icon;
    }
}
