package com.p2s.weatherforecast.classes;

import java.io.Serializable;
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
    private int lng;
    private String lat;

    public Weather() {
    }

    public String getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }


    public String getSummary() {
        return summary;
    }

    public String getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public Date getTime() {

        return new Date(time*1000);
    }
}
