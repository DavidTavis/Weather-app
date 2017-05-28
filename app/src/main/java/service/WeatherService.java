package service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

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
    private MyServiceTask myServiceTask;

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
        startTimer(city);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.logInfo("onDestroy service");
        Intent broadcastIntent = new Intent("com.animation.david.thewheatherapp.RestartService");
        sendBroadcast(broadcastIntent);
        stopTimerTask();
    }

    public void startTimer(String city) {

        timer = new Timer();
        timer.schedule(new MyServiceTask(city), 0, 600000);

    }

    private class MyServiceTask extends TimerTask{

        private String city;

        public MyServiceTask(String city) {
            this.city = city;
        }

        @Override
        public void run() {
            Thread myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String responseJSON = (new WeatherHttpClient()).getWeatherData(city);

                    (new JSONWeatherParser(getApplicationContext())).fillDB(responseJSON);

                    Utils.logInfo("Starting the MyServiceTask. Parsing data. Filling DB");
                }
            });
            myThread.start();

        }
    }

    public void stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}