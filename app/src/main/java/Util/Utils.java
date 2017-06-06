package Util;

import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by TechnoA on 22.04.2017.
 */

public class Utils {

    public static final String BASE_URL_PART_CITY = "http://api.openweathermap.org/data/2.5/forecast?q=";
    public static final String BASE_URL_PART_LAT = "http://api.openweathermap.org/data/2.5/forecast?lat=";
    public static final String BASE_URL_PART_LON = "&lon=";
    public static final String BASE_URL_PART_APPID = "&appid=f8c690ef9002bb5fac771397f582ce12";

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
        appendLogToFile(info);
    }

    private static void appendLogToFile(String text){

        if(!isExternalStorageWritable()){
            return;
        }

        File logFile = new File("sdcard/MyLogLocation.file");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}

