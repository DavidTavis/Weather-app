package data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Util.Utils;
import model.Place;
import model.Weather;

/**
 * Created by TechnoA on 22.04.2017.
 */

public class JSONWeatherParser {

    public static Weather getWeather(String data){
        Weather weather = new Weather();

        //createe JSONOblect from data
        try {
            Utils.logInfo(data);
            JSONObject jsonObject = new JSONObject(data);

            Place place = new Place();

            //get the coordinate obj
            JSONObject coordObj = Utils.getObject("coord", jsonObject);
            place.setLat(Utils.getFloat("lat",coordObj));
            place.setLon(Utils.getFloat("lon",coordObj));

            //get the sys obj
            JSONObject sysObj = Utils.getObject("sys", jsonObject);
            place.setCountry(Utils.getString("country", sysObj));
            place.setLastUpdate(Utils.getLong("dt", jsonObject));
            place.setSunrise(Utils.getLong("sunrise",sysObj));
            place.setSunset(Utils.getLong("sunset",sysObj));
            place.setCity(Utils.getString("name",jsonObject));
            weather.place = place;

            // get the weather info
            JSONArray jsonArray = jsonObject.getJSONArray("weather");
            JSONObject jsonWeather = jsonArray.getJSONObject(0);
            weather.currentCondition.setWeatherId(Utils.getInt("id",jsonWeather));
            weather.currentCondition.setDescription(Utils.getString("description",jsonWeather));
            weather.currentCondition.setCondition(Utils.getString("main",jsonWeather));
            weather.currentCondition.setIcon(Utils.getString("icon",jsonWeather));

            JSONObject mainOdj = Utils.getObject("main", jsonObject);
            weather.currentCondition.setHumidity(Utils.getInt("humidity", mainOdj));
            weather.currentCondition.setPressure(Utils.getInt("pressure", mainOdj));
            weather.currentCondition.setMinTemp(Utils.getFloat("temp_min", mainOdj));
            weather.currentCondition.setMaxTemp(Utils.getFloat("temp_max", mainOdj));
            weather.currentCondition.setTemperature(Utils.getDouble("temp", mainOdj));

            JSONObject windOdj = Utils.getObject("wind", jsonObject);
            weather.wind.setDeg(Utils.getFloat("deg",windOdj));
            weather.wind.setSpeed(Utils.getFloat("speed",windOdj));

            JSONObject cloudOdj = Utils.getObject("clouds", jsonObject);
            weather.clouds.setPrecipitation(Utils.getInt("all",cloudOdj));

            return weather;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}
