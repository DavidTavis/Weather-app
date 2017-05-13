package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import Util.Utils;

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

    public void addDataToDB(String date, float speed, float deg, float temp, float tempMax, float tempMin, float pressure, float humidity, String mainWeather, String description, String icon){

        SQLiteDatabase db = sqlite.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_SPEED, speed);
        values.put(COLUMN_DEG, deg);
        values.put(COLUMN_TEMP, temp);
        values.put(COLUMN_TEMPMAX, tempMax);
        values.put(COLUMN_TEMPMIN, tempMin);
        values.put(COLUMN_PRESSURE, pressure);
        values.put(COLUMN_HUMIDITY, humidity);
        values.put(COLUMN_MAINWEATHER, mainWeather);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_ICON, icon);

        long id = db.insert(TABLE_NAME, null, values);

        Utils.logInfo("Row id = " + id +  " date = " + date + " speed = " + speed + " deg = " + deg + " temp = " + temp + " tempMax = " + tempMax + " tempMin = " + tempMin + " pressure = " + pressure + " humidity = " + humidity + " mainWeather = " + mainWeather + " description = " + description + " icon = " + icon);
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
