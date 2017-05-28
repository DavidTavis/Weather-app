package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;

import Util.Utils;
import model.WeatherModel;

public class WeatherRepository {

    //table
    private static final String TABLE_NAME = "WeatherTable";
    //columns
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_SPEED = "speed";
    private static final String COLUMN_DEG = "deg";
    private static final String COLUMN_ID = BaseColumns._ID;
    private static final String COLUMN_TEMP = "temp";
    private static final String COLUMN_TEMPMAX = "tempMax";
    private static final String COLUMN_TEMPMIN = "tempMin";
    private static final String COLUMN_PRESSURE = "pressure";
    private static final String COLUMN_HUMIDITY = "humidity";
    private static final String COLUMN_MAINWEATHER = "mainWeater";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_ICON = "icon";

    private SQLite sqlite;

    public WeatherRepository(Context context) {

        sqlite = new SQLite(context);

    }

    private class SQLite extends SQLiteOpenHelper {

        // data base and table
        private static final String DATABASE_NAME = "WeatherDataBase.db";
        private static final int DATABASE_VERSION = 3;


        public SQLite(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            createDatabase(sqLiteDatabase);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            createDatabase(sqLiteDatabase);
        }

        private void createDatabase(SQLiteDatabase db) {

            db.execSQL(String.format("DROP TABLE IF EXISTS %s;", TABLE_NAME));

            String query = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s REAL, %s REAL, %s REAL, %s REAL, %s REAL, %s REAL, %s REAL, %s TEXT, %s TEXT, %s TEXT);", TABLE_NAME, COLUMN_ID, COLUMN_DATE, COLUMN_SPEED, COLUMN_DEG, COLUMN_TEMP, COLUMN_TEMPMAX, COLUMN_TEMPMIN, COLUMN_PRESSURE, COLUMN_HUMIDITY, COLUMN_MAINWEATHER, COLUMN_DESCRIPTION, COLUMN_ICON);
            db.execSQL(query);

        }

    }

    public void addDataToDB(WeatherModel weatherModel){

        SQLiteDatabase db = sqlite.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, weatherModel.getDate());
        values.put(COLUMN_SPEED, weatherModel.getSpeed());
        values.put(COLUMN_DEG, weatherModel.getDeg());
        values.put(COLUMN_TEMP, weatherModel.getTemp());
        values.put(COLUMN_TEMPMAX, weatherModel.getTempMax());
        values.put(COLUMN_TEMPMIN, weatherModel.getTempMin());
        values.put(COLUMN_PRESSURE, weatherModel.getPressure());
        values.put(COLUMN_HUMIDITY, weatherModel.getHumidity());
        values.put(COLUMN_MAINWEATHER, weatherModel.getMainWeather());
        values.put(COLUMN_DESCRIPTION, weatherModel.getDescription());
        values.put(COLUMN_ICON, weatherModel.getIcon());

        long id = db.insert(TABLE_NAME, null, values);

        Utils.logInfo("Row id = " + id +  " date = " + weatherModel.getDate() + " speed = " + weatherModel.getSpeed()
                + " deg = " +  weatherModel.getDeg() + " temp = " + weatherModel.getTemp()
                + " tempMax = " + weatherModel.getTempMax() + " tempMin = " + weatherModel.getTempMin()
                + " pressure = " + weatherModel.getPressure() + " humidity = " + weatherModel.getHumidity()
                + " mainWeather = " + weatherModel.getMainWeather() + " description = " + weatherModel.getDescription()
                + " icon = " +  weatherModel.getIcon());
    }

    public WeatherModel firstRow(){

        SQLiteDatabase db = sqlite.getReadableDatabase();
        String query = String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, MIN(%s) as _id  FROM %s", COLUMN_DATE, COLUMN_SPEED,
                COLUMN_DEG, COLUMN_TEMP, COLUMN_TEMPMAX, COLUMN_TEMPMIN, COLUMN_PRESSURE, COLUMN_HUMIDITY,
                COLUMN_MAINWEATHER, COLUMN_DESCRIPTION, COLUMN_ICON, COLUMN_ID,  TABLE_NAME);
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();

        WeatherModel weatherModel= new WeatherModel();
        weatherModel.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
        weatherModel.setSpeed(cursor.getFloat(cursor.getColumnIndex(COLUMN_SPEED)));
        weatherModel.setDeg(cursor.getFloat(cursor.getColumnIndex(COLUMN_DEG)));
        weatherModel.setTemp(cursor.getFloat(cursor.getColumnIndex(COLUMN_TEMP)));
        weatherModel.setTempMax(cursor.getFloat(cursor.getColumnIndex(COLUMN_TEMPMAX)));
        weatherModel.setTempMin(cursor.getFloat(cursor.getColumnIndex(COLUMN_TEMPMIN)));
        weatherModel.setPressure(cursor.getFloat(cursor.getColumnIndex(COLUMN_PRESSURE)));
        weatherModel.setHumidity(cursor.getFloat(cursor.getColumnIndex(COLUMN_HUMIDITY)));
        weatherModel.setMainWeather(cursor.getString(cursor.getColumnIndex(COLUMN_MAINWEATHER)));
        weatherModel.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
        weatherModel.setIcon(cursor.getString(cursor.getColumnIndex(COLUMN_ICON)));

        return weatherModel;
    }

    public ArrayList<WeatherModel> readDB(){

        SQLiteDatabase db = sqlite.getReadableDatabase();
        String query = String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s", COLUMN_DATE, COLUMN_SPEED,
                COLUMN_DEG, COLUMN_TEMP, COLUMN_TEMPMAX, COLUMN_TEMPMIN, COLUMN_PRESSURE, COLUMN_HUMIDITY,
                COLUMN_MAINWEATHER, COLUMN_DESCRIPTION, COLUMN_ICON, COLUMN_ID,  TABLE_NAME);
        Cursor cursor = db.rawQuery(query,null);

        ArrayList<WeatherModel> weatherList = new ArrayList<>();

        String currentDay = "";
        int currentMinDayTemp = Integer.MAX_VALUE;
        int currentMaxDayTemp = Integer.MIN_VALUE;

        while(cursor.moveToNext()){

            String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));

            if (!currentDay.equals(date.substring(8, 10))){
                currentDay = date.substring(8, 10);
                currentMinDayTemp = Integer.MAX_VALUE;
                currentMaxDayTemp = Integer.MIN_VALUE;
            }

            int minDayTemp = (int) cursor.getFloat(cursor.getColumnIndex(COLUMN_TEMPMIN));
            int maxDayTemp = (int) cursor.getFloat(cursor.getColumnIndex(COLUMN_TEMPMAX));

            if(minDayTemp < currentMinDayTemp) currentMinDayTemp = minDayTemp;
            if(maxDayTemp > currentMaxDayTemp) currentMaxDayTemp = maxDayTemp;

            if(!date.substring(11, 13).equals("15")){
                continue;
            }

            WeatherModel weatherModel= new WeatherModel();

            weatherModel.setDate(date);
            weatherModel.setSpeed(cursor.getFloat(cursor.getColumnIndex(COLUMN_SPEED)));
            weatherModel.setDeg(cursor.getFloat(cursor.getColumnIndex(COLUMN_DEG)));
            weatherModel.setTemp(cursor.getFloat(cursor.getColumnIndex(COLUMN_TEMP)));
            weatherModel.setTempMax(currentMaxDayTemp);
            weatherModel.setTempMin(currentMinDayTemp);
            weatherModel.setPressure(cursor.getFloat(cursor.getColumnIndex(COLUMN_PRESSURE)));
            weatherModel.setHumidity(cursor.getFloat(cursor.getColumnIndex(COLUMN_HUMIDITY)));
            weatherModel.setMainWeather(cursor.getString(cursor.getColumnIndex(COLUMN_MAINWEATHER)));
            weatherModel.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
            weatherModel.setIcon(cursor.getString(cursor.getColumnIndex(COLUMN_ICON)));

            weatherList.add(weatherModel);
        }

        return weatherList;

    }

    public void close(){
        SQLiteDatabase db = sqlite.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        sqlite.close();
    }

    public int count(){
        SQLiteDatabase db = sqlite.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}
