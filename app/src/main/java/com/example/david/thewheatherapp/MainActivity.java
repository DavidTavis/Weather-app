package com.example.david.thewheatherapp;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import Util.Utils;
import data.CityPreference;
import data.JSONWeatherParser;
import data.WeatherHttpClient;
import model.Weather;

public class MainActivity extends AppCompatActivity {

    private TextView cityName;
    private TextView temp;
    private ImageView iconView;
    private TextView description;
    private TextView humidity;
    private TextView pressure;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private TextView updated;
    private TextView precipitation;

    Weather weather = new Weather();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (TextView) findViewById(R.id.cityText);
        iconView = (ImageView) findViewById(R.id.thumbnailIcon);
        temp = (TextView) findViewById(R.id.tempText);
        description = (TextView) findViewById(R.id.cloudText);
        humidity = (TextView) findViewById(R.id.humidText);
        pressure = (TextView) findViewById(R.id.pressureText);
        wind = (TextView) findViewById(R.id.windText);
        sunrise = (TextView) findViewById(R.id.riseText);
        sunset = (TextView) findViewById(R.id.setText);
        updated = (TextView) findViewById(R.id.updateText);
        precipitation = (TextView) findViewById(R.id.precipitationText);

        CityPreference cityPreference = new CityPreference(MainActivity.this);
        renderWeatherData(cityPreference.getCity());
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
                CityPreference cityPreference = new CityPreference(MainActivity.this);
                cityPreference.setCity(cityInput.getText().toString());

                String newCity = cityPreference.getCity();

                renderWeatherData(newCity);
            }
        });
        builder.show();
    }
    public  void renderWeatherData(String city){

        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(new String[]{city});
    }

    private class WeatherTask extends AsyncTask<String, Void, Weather>{

        @Override
        protected Weather doInBackground(String... params) {

            Utils.logInfo(params[0]);
            String data = (new WeatherHttpClient()).getWeatherData(params[0]);
            if(data==null){
                return null;
            }
            weather = JSONWeatherParser.getWeather(data);

            weather.iconData = (new WeatherHttpClient()).getWeatherImage(weather.currentCondition.getIcon());

            return weather;

        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);
            if(weather==null) return;
            updateUI();
        }
    }

    public void updateUI(){

        // set time format for sunset and sunrise
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+3"));

        String sunsetDate = sdf.format(new Date(weather.place.getSunset()));
        String sunriseDate = sdf.format(new Date(weather.place.getSunrise()));

        // set time format for last update
        sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+3"));

        String updateDate = sdf.format(new Date(weather.place.getLastUpdate()));

        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        String tempFormat = decimalFormat.format(weather.currentCondition.getTemperature() - 273.15);

        cityName.setText("" + weather.place.getCity() + "," + weather.place.getCountry());
        temp.setText("" + tempFormat + "°C");
        description.setText("Condition: " + weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescription() + ")");
        humidity.setText("Humidity: " + weather.currentCondition.getHumidity() + "%");
        pressure.setText("Pressure: " + weather.currentCondition.getPressure() + " hPa") ;
        wind.setText("Wind: " + weather.wind.getSpeed() + " mps");
        sunrise.setText("Sunrise: " + sunriseDate);
        sunset.setText("SunSet: " + sunsetDate);
        updated.setText("Last Updagted: " + updateDate);
        precipitation.setText("Precipitation: " + weather.clouds.getPrecipitation());
        if (weather.iconData != null) {
            iconView.setImageBitmap(weather.iconData);
        }
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

}
