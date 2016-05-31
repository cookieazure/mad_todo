package com.android.master.mad.todo.data;

import java.io.Serializable;

/**
 * Created by misslert on 31.05.2016.
 * Basic class for storing geolocations.
 */
public class SimpleLocation implements Serializable {

    private String name;
    private double lat;
    private double lon;

    public SimpleLocation(String name,double lat, double lon) {
        this.name = name;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
