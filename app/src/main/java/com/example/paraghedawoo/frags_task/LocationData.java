package com.example.paraghedawoo.frags_task;

/**
 * Created by Parag Hedawoo on 3/30/2017.
 */

public class LocationData {
    public String name;
    public double lat;
    public double lng;

    public LocationData() {
    }

    public LocationData(String name, double lat, double lng) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
