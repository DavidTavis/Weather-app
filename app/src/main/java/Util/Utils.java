package Util;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by TechnoA on 22.04.2017.
 */

public class Utils {

    public static final String BASE_URL_PART_ONE = "http://api.openweathermap.org/data/2.5/forecast?q=";
    public static final String BASE_URL_PART_TWO = "&appid=f8c690ef9002bb5fac771397f582ce12";

    public static final String ICON_URL = "http://openweathermap.org/img/w/";

    public static JSONObject getObject(String tagName, JSONObject jsonObject) throws JSONException{
        JSONObject jOb = jsonObject.getJSONObject(tagName);
        return jOb;
    }

    public static String getString(String tagName, JSONObject jsonObject) throws JSONException {
        try{
            return jsonObject.getString(tagName);
        }catch (JSONException e){
            return "";
        }
    }

    public static float getFloat(String tagName, JSONObject jsonObject) throws JSONException {
        try{
            return (float) jsonObject.getDouble(tagName);
        }catch (JSONException e){
            return 0;
        }

    }

    public static long getLong(String tagName, JSONObject jsonObject) throws JSONException {
        try{
            return  jsonObject.getLong(tagName);
        }catch (JSONException e){
            return 0;
        }
    }

    public static double getDouble(String tagName, JSONObject jsonObject) throws JSONException {
        try{
            return jsonObject.getDouble(tagName);
        }catch (JSONException e){
            return 0;
        }
    }

    public static int getInt(String tagName, JSONObject jsonObject) throws JSONException {
        try{
            return  jsonObject.getInt(tagName);
        }catch (JSONException e){
            return 0;
        }
    }

    public static void logInfo(String info){
        Log.d("LogData",info);
    }
}
