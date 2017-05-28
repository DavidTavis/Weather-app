package adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.david.thewheatherapp.R;

import java.util.List;

import model.WeatherModel;


/**
 * Created by TechnoA on 26.05.2017.
 */

public class WeatherAdapter extends ArrayAdapter<WeatherModel> {

    public WeatherAdapter(@NonNull Context context, List<WeatherModel> weatherModelList) {
        super(context, 0, weatherModelList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        WeatherModel currentWeather = getItem(position);

        //date
        TextView date = (TextView) listItemView.findViewById(R.id.date);
        String dateString = currentWeather.getDate();
        dateString = dateString.substring(5,10).replace("-","/");
        date.setText(dateString);

        ImageView icon = (ImageView) listItemView.findViewById(R.id.thumbnailIcon);
        icon.setImageBitmap(currentWeather.getPictureIcon());

        //temperature
        TextView temperature = (TextView) listItemView.findViewById(R.id.temperature);
        int tempMax = (int)(currentWeather.getTempMax() - 273.15);
        int tempMin = (int)(currentWeather.getTempMin() - 273.15);
        temperature.setText("" + tempMin + " - " +  tempMax + " °C");
        return listItemView;
    }
}
