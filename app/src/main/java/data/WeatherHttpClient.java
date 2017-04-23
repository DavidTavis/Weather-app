package data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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

            URL url = new URL(Utils.BASE_URL + place);
            Utils.logInfo(url.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

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

        } catch (IOException e) {
            e.printStackTrace();
            Utils.logInfo("No HttpURLConnection");
        }finally {
            try { inputStream.close(); } catch(Throwable t) {}
            try { connection.disconnect(); } catch(Throwable t) {}
        }

        return null;
    }

    public Bitmap getWeatherImage(String code){
        HttpURLConnection con = null ;
        InputStream is = null;

        StringBuilder str = new StringBuilder(Utils.ICON_URL);
        String path = str.append(code).append(".png").toString();

        try {
            URL url = new URL(path);
            con = (HttpURLConnection) url.openConnection();
            is = con.getInputStream();
            Bitmap img = BitmapFactory.decodeStream(is);
            return img;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }

        return null;

    }


}
