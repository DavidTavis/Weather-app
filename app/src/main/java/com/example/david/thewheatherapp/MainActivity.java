package com.example.david.thewheatherapp;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import Util.Utils;
import adapter.WeatherAdapter;
import data.CityPreference;
import data.WeatherHttpClient;
import data.WeatherRepository;
import model.WeatherModel;
import service.WeatherService;

public class MainActivity extends AppCompatActivity {

    private ListView weatherListView;
    private TextView place;
    private TextView temp;
    private ImageView iconView;
    private TextView description;
    private TextView humidity;
    private TextView pressure;
    private TextView wind;
    private TextView updated;

    WeatherAdapter mAdapter;
    private WeatherService mWeatherService;
    private Intent mIntentService;
    String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_scrolling);

        findView();

        mAdapter = new WeatherAdapter(this, new ArrayList<WeatherModel>());
        weatherListView.setAdapter(mAdapter);

        CityPreference cityPreference = new CityPreference(MainActivity.this);
        city = cityPreference.getCity();
        startingService(city);

        registeringReceiver();

    }

    private class AsyncCurrentWeather extends AsyncTask<WeatherModel,Void,WeatherModel>{

        @Override
        protected WeatherModel doInBackground(WeatherModel... params) {

            WeatherModel weatherModel = params[0];

            Bitmap icon = (new WeatherHttpClient()).getWeatherImage(weatherModel.getIcon());
            weatherModel.setPictureIcon(icon);

            return weatherModel;

        }

        @Override
        protected void onPostExecute(WeatherModel weatherModel) {
            super.onPostExecute(weatherModel);
            updateCurrentWeather(weatherModel);
        }

    }

    private class AsyncWeatherList extends AsyncTask<ArrayList<WeatherModel>,Void,ArrayList<WeatherModel>>{

        @Override
        protected ArrayList<WeatherModel> doInBackground(ArrayList<WeatherModel>... params) {

            ArrayList<WeatherModel> weatherList = params[0];
            for(WeatherModel weatherModel: weatherList){
                Bitmap icon = (new WeatherHttpClient()).getWeatherImage(weatherModel.getIcon());
                weatherModel.setPictureIcon(icon);
            }
            return weatherList;
        }

        @Override
        protected void onPostExecute(ArrayList<WeatherModel> weatherList) {
            super.onPostExecute(weatherList);
            updateForecast(weatherList);
        }
    }

    private void findView() {

        weatherListView = (ListView) findViewById(R.id.weatherList);
        place = (TextView) findViewById(R.id.cityText);
        iconView = (ImageView) findViewById(R.id.thumbnailIcon);
        temp = (TextView) findViewById(R.id.tempText);
        description = (TextView) findViewById(R.id.cloudText);
        humidity = (TextView) findViewById(R.id.humidText);
        pressure = (TextView) findViewById(R.id.pressureText);
        wind = (TextView) findViewById(R.id.windText);
        updated = (TextView) findViewById(R.id.updateText);

    }

    private void startingService(String city){

        mWeatherService = new WeatherService(getApplicationContext());
        mIntentService = new Intent(this, WeatherService.class);
        mIntentService.putExtra("city", city);

        if (!isMyServiceRunning(mWeatherService.getClass())) {
            startService(mIntentService);
        }else{
            stopService(mIntentService);
        }
    }

    private void registeringReceiver(){

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                boolean needUpdateData = intent.getBooleanExtra("DataBaseIsFull", false);

                if (needUpdateData) {

                    //current weather
                    WeatherModel weatherModel = readWeatherFromDB();
                    AsyncCurrentWeather taskWeather = new AsyncCurrentWeather();
                    taskWeather.execute(weatherModel);

                    //forecast
                    ArrayList<WeatherModel> weatherList = readDB();
                    AsyncWeatherList taskForecast = new AsyncWeatherList();
                    taskForecast.execute(weatherList);

                }

            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("myBroadcastIntent"));

    }

    private WeatherModel readWeatherFromDB(){

        return (new WeatherRepository(this)).firstRow();

    }

    private ArrayList<WeatherModel> readDB(){

        return (new WeatherRepository(this)).readDB();

    }

    private void updateForecast(ArrayList<WeatherModel> weatherList) {
        mAdapter = new WeatherAdapter(this, weatherList);
        weatherListView.setAdapter(mAdapter);
        Utils.logInfo("updateForecast");
    }

    private void updateCurrentWeather(WeatherModel weatherModel){

        place.setText(city);
        iconView.setImageBitmap(weatherModel.getPictureIcon());
        updated.setText("Updated: " + weatherModel.getDate());
        humidity.setText("Humidity: " + weatherModel.getHumidity());
        pressure.setText("Pressure: " + weatherModel.getPressure());
        wind.setText("Wind: " + weatherModel.getSpeed());

        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        String temperature = decimalFormat.format(weatherModel.getTemp() - 273.15);
        temp.setText("" + temperature + "°C");
        description.setText("Condition: " + weatherModel.getMainWeather() + " (" + weatherModel.getDescription() + ")");

        Utils.logInfo("updateWeather");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.change_cityId){
            showInputDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Change city");

        final EditText cityInput = new EditText(MainActivity.this);
        cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
        cityInput.setHint("Moscow,RU");
        builder.setView(cityInput);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CityPreference cityPreference = new CityPreference(getApplicationContext());
                cityPreference.setCity(cityInput.getText().toString());

                city = cityPreference.getCity();

                startingService(city);
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(mIntentService);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Utils.logInfo("ServiceRunning true");
                return true;
            }
        }

        Utils.logInfo("ServiceRunning false");
        return false;

    }

}
