package data;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Util.Utils;
import model.WeatherModel;

/**
 * Created by TechnoA on 22.04.2017.
 */

public class JSONWeatherParser {

    private Context context;

    public JSONWeatherParser(Context context) {
        this.context = context;
    }

    public WeatherModel fillDB(String data){

        WeatherModel currentWeatherModel = null;

        try {

            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("list");

            WeatherRepository repository = new WeatherRepository(context);
            repository.close();

            Utils.logInfo("count row = " + repository.count());
            for (int i = 0; i <jsonArray.length(); i++){

                WeatherModel weatherModel = new WeatherModel();
                JSONObject listEntry = jsonArray.getJSONObject(i);
                String date = Utils.getString("dt_txt",listEntry);

                JSONObject wind = Utils.getObject("wind",listEntry);
                float speed = Utils.getFloat("speed", wind);
                float deg = Utils.getFloat("deg", wind);

                JSONObject main = Utils.getObject("main",listEntry);
                float temp = Utils.getFloat("temp",main);
                float tempMin = Utils.getFloat("temp_min",main);
                float tempMax = Utils.getFloat("temp_max",main);
                float pressure = Utils.getFloat("pressure",main);
                float humidity = Utils.getFloat("humidity",main);

                JSONArray weatherArray = listEntry.getJSONArray("weather");
                JSONObject weather = weatherArray.getJSONObject(0);
                String mainWeater = Utils.getString("main",weather);
                String description = Utils.getString("description",weather);
                String icon = Utils.getString("icon", weather);

                weatherModel.setDate(date);
                weatherModel.setSpeed(speed);
                weatherModel.setDeg(deg);
                weatherModel.setTemp(temp);
                weatherModel.setTempMax(tempMax);
                weatherModel.setTempMin(tempMin);
                weatherModel.setPressure(pressure);
                weatherModel.setHumidity(humidity);
                weatherModel.setMainWeather(mainWeater);
                weatherModel.setDescription(description);
                weatherModel.setIcon(icon);

                if(i==0){
                    currentWeatherModel = weatherModel;
                }
                // write to DataBase
                repository.addDataToDB(weatherModel);

            }
            Utils.logInfo("count row = " +repository.count());

            return currentWeatherModel;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return currentWeatherModel;
    }

}
