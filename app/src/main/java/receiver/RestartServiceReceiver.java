package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import Util.Utils;
import data.CityPreference;
import service.WeatherService;

/**
 * Created by TechnoA on 28.05.2017.
 */

public class RestartServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String city = (new CityPreference(context)).getCity();
        Intent intentWeather = new Intent(context, WeatherService.class);
        intentWeather.putExtra("city", city);
        context.startService(intentWeather);
        Utils.logInfo("RestartServiceReceiver start service");
    }
}
