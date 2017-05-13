package com.example.david.thewheatherapp;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import data.JSONWeatherParser;
import model.Weather;
import data.WeatherHttpClient;

/**
 * Created by TechnoA on 13.05.2017.
 */

public class WeatherService extends IntentService {

    public WeatherService() {
        super("WeatherService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        JSONWeatherParser parser = new JSONWeatherParser(this);
        WeatherHttpClient httpClient = new WeatherHttpClient();
        String responseJSON = httpClient.getWeatherData("");

        parser.fillDB(responseJSON);

    }
}
