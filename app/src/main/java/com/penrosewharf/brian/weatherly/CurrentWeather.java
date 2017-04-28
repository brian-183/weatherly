package com.penrosewharf.brian.weatherly;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Brian on 27/04/2017.
 */

public class CurrentWeather {
    private String mIcon;
    private long mTime;
    private double mTemp;
    private double mHumidity;
    private double mPrecipChance;
    private String mSummary;
    private String mTimezone;
    private String mLocation;

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public int getIconId(){
        int iconId = R.mipmap.clear_day;

        if (mIcon.equals("clear-day")) {
            iconId = R.mipmap.clear_day;
        }
        else if (mIcon.equals("clear-night")) {
            iconId = R.mipmap.clear_night;
        }
        else if (mIcon.equals("rain")) {
            iconId = R.mipmap.rain;
        }
        else if (mIcon.equals("snow")) {
            iconId = R.mipmap.snow;
        }
        else if (mIcon.equals("sleet")) {
            iconId = R.mipmap.sleet;
        }
        else if (mIcon.equals("wind")) {
            iconId = R.mipmap.wind;
        }
        else if (mIcon.equals("fog")) {
            iconId = R.mipmap.fog;
        }
        else if (mIcon.equals("cloudy")) {
            iconId = R.mipmap.cloudy;
        }
        else if (mIcon.equals("partly-cloudy-day")) {
            iconId = R.mipmap.partly_cloudy;
        }
        else if (mIcon.equals("partly-cloudy-night")) {
            iconId = R.mipmap.cloudy_night;
        }
        return iconId;
    }

    public long getTime() {
        return mTime;
    }

    public String getFormattedTime(){
        //Couldn't come up with a method for checking gmt + 1 hence the 3600 addition for the extra hour
        //Hope you're not using this on holidays outside gmt + 1!!
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimezone()));
        Date dateTime = new Date((getTime() + 3600) * 1000);
        String timeString = formatter.format(dateTime);

        return timeString;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getTemp() {
        return (int) Math.round(mTemp);
    }

    public void setTemp(double temp) {
        //Math to convert Fahrenheight to Celsius
        mTemp = ((temp - 32) * 5 / 9);
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public int getPrecipChance() {
        double precipPercentage = mPrecipChance * 100;
        return (int)Math.round(precipPercentage);
    }

    public void setPrecipChance(double precipChance) {
        mPrecipChance = precipChance;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }


    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }


}
