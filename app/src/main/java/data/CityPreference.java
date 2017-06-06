package data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by TechnoA on 23.04.2017.
 */

public class CityPreference {

    SharedPreferences prefs;

    public CityPreference(Context context){
        prefs = context.getSharedPreferences("myPrefs",Context.MODE_PRIVATE);
    }


    // City
    public String getCity(){
        return prefs.getString("city","Kev,UA");
    }
    public void setCity(String city){
        prefs.edit().putString("city",city).commit();
    }


    // Latitude
    public float getLatitude(){
        return prefs.getFloat("latitude",0);
    }
    public void setLatitude(float latitude){
        prefs.edit().putFloat("latitude", latitude).commit();
    }


    // Longitude
    public float getLongitude(){
        return prefs.getFloat("longitude",0);
    }
    public void setLongitude(float longitude){
        prefs.edit().putFloat("longitude", longitude).commit();
    }

}
