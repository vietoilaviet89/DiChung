package model;

import java.io.Serializable;

/**
 * Created by 123456789 on 4/12/2017.
 */

public class StudentLatLng implements Serializable {
    private double lat;
    private double lng;

    public StudentLatLng() {

    }

    public StudentLatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
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
