package model;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by 123456789 on 4/3/2017.
 */

public class MarkerStudent {
    Student student;
    Marker marker;

    public MarkerStudent(){

    }

    public MarkerStudent(Student student, Marker marker) {
        this.student = student;
        this.marker = marker;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
