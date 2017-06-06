package com.example.david.thewheatherapp;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Util.Utils;
import adapter.WeatherAdapter;
import constants.Constants;
import data.CityPreference;
import data.WeatherHttpClient;
import data.WeatherRepository;
import model.WeatherModel;
import service.FetchAddressIntentService;
import service.WeatherService;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
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
    private LocationManager locationManager;

    private AddressResultReceiver mResultReceiver;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected String mAddressOutput;

    private String currentCity = null;
    String city = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_scrolling);
        findView();

        registeringReceiver();

        mResultReceiver = new AddressResultReceiver(new Handler());
        mAddressOutput = "";

        buildGoogleApiClient();

        mAdapter = new WeatherAdapter(this, new ArrayList<WeatherModel>());
        weatherListView.setAdapter(mAdapter);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Utils.logInfo("Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    public void onDisconnected() {
        Utils.logInfo("Disconnected");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Utils.logInfo("Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            // Determine whether a Geocoder is available.
            Utils.logInfo("onConnected");
            if(city == null) {
                startIntentService();
            }
        }

    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            city = resultData.getString(Constants.RESULT_DATA_KEY);

            CityPreference pref = new CityPreference(getApplicationContext());
            pref.setCity(city);

            Utils.logInfo("Found location - " + city);
            startingService(pref.getCity());

        }
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

        startService(mIntentService);

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

    public void showInputDialog() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                city = place.getName().toString();
                Utils.logInfo("Place: " + city);

                CityPreference cityPreference = new CityPreference(getApplicationContext());
                cityPreference.setCity(city);

                startingService(city);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Utils.logInfo(status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
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
                return true;
            }
        }

        return false;

    }

}
