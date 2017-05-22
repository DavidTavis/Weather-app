package com.example.david.thewheatherapp;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import data.JSONWeatherParser;
import data.WeatherRepository;
import model.Weather;
import data.WeatherHttpClient;
import model.WeatherModel;

/**
 * Created by TechnoA on 13.05.2017.
 */

public class WeatherService extends IntentService {

    public WeatherService() {
        super("WeatherService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        // getting JSON response
        String responseJSON = (new WeatherHttpClient()).getWeatherData("");

        WeatherModel weatherModel = (new JSONWeatherParser(this)).fillDB(responseJSON);

    }


}
