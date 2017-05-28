package data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by TechnoA on 23.04.2017.
 */

public class CityPreference {

    SharedPreferences prefs;

//    public CityPreference(Activity activity) {
//        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
//    }
    public CityPreference(Context context){
        prefs = context.getSharedPreferences("myPrefs",Context.MODE_PRIVATE);
    }

    public String getCity(){
        return prefs.getString("city","Kev,UA");
    }

    public void setCity(String city){
        prefs.edit().putString("city",city).commit();
    }

}
