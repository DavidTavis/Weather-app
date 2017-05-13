package model;

import android.graphics.Bitmap;

import java.net.PortUnreachableException;

/**
 * Created by TechnoA on 22.04.2017.
 */

public class Weather {
    public  Place place;
    public Bitmap iconData;
    public CurrentCondition currentCondition;
//    public CurrentCondition currentCondition = new CurrentCondition();
    public Temperature temperature = new Temperature();
    public Wind wind = new Wind();
    public Snow snow = new Snow();
    public Clouds clouds = new Clouds();
}
