package service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import Util.Utils;
import data.JSONWeatherParser;
import data.WeatherHttpClient;

/**
 * Created by TechnoA on 13.05.2017.
 */

public class WeatherService extends Service {

    private Timer timer;
    private TaskCity taskCity;

    public WeatherService(Context context) {
        super();
    }

    public WeatherService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        String city = intent.getStringExtra("city");
        stopTimer();
        startTimer(city);
        return START_STICKY;

    }


    public void startTimer(String city) {

        timer = new Timer();
        taskCity = new TaskCity(city);
        timer.schedule(taskCity, 0, 600000);

    }


    private class TaskCity extends TimerTask{

        private String city;

        public TaskCity(String city) {
            this.city = city;
        }

        @Override
        public void run() {
            Thread myThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    String responseJSON = (new WeatherHttpClient()).getWeatherData(city);
                    (new JSONWeatherParser(getApplicationContext())).fillDB(responseJSON);

                }
            });

            myThread.start();

        }
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if(taskCity != null){
            taskCity.cancel();
            taskCity = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.logInfo("onDestroy service");

        stopTimer();
        sendBroadcast(new Intent("com.animation.david.thewheatherapp.RestartService"));

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}