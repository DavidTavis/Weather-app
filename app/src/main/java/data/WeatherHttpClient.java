package data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import Util.Utils;

/**
 * Created by TechnoA on 22.04.2017.
 */

public class WeatherHttpClient {

    public String getWeatherData(String place){
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {

            StringBuilder ref = new StringBuilder();
            ref.append(Utils.BASE_URL_PART_ONE).append(place).append(Utils.BASE_URL_PART_TWO);
            Utils.logInfo("REF = " + ref);

            URL url = new URL(ref.toString());

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            int responseCode = connection.getResponseCode();

//            Utils.logInfo("" + responseCode);

            if(responseCode == HttpURLConnection.HTTP_OK){
                //read the response
                StringBuffer stringBuffer = new StringBuffer();

                inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line = null;

                while ((line = bufferedReader.readLine()) != null){
                    stringBuffer.append(line + "\r\n");
                }

                inputStream.close();
                connection.disconnect();

                return stringBuffer.toString();
            }


        } catch (IOException e) {
            e.printStackTrace();
            Utils.logInfo("No HttpURLConnection!!!!");
        }finally {
            try { inputStream.close(); } catch(Throwable t) {}
            try { connection.disconnect(); } catch(Throwable t) {}
        }

        return null;
    }

    public Bitmap getWeatherImage(String code){
        HttpURLConnection connection = null ;
        InputStream inputStream = null;

        StringBuilder str = new StringBuilder(Utils.ICON_URL);
        String path = str.append(code).append(".png").toString();

        try {
            URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
            inputStream = connection.getInputStream();
            Bitmap img = BitmapFactory.decodeStream(inputStream);
            return img;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

            try {
                inputStream.close(); }
            catch(Throwable t) {}

            try {
                connection.disconnect();
            } catch(Throwable t) {}

        }

        return null;

    }


}
