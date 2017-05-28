package com.example.david.thewheatherapp;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import Util.Utils;
import data.JSONWeatherParser;
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

        String city = intent.getStringExtra("city");
        Utils.logInfo("CITY = " + city);

        // getting JSON response
        String responseJSON = (new WeatherHttpClient()).getWeatherData(city);

//        while(true) {
//
//            //parsing data and filling DB
//            (new JSONWeatherParser(this)).fillDB(responseJSON);
//
//            try {
//                //sleep 3 hours
//                TimeUnit.SECONDS.sleep(3600*3);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        //parsing data and filling DB
        (new JSONWeatherParser(this)).fillDB(responseJSON);

    }


}
