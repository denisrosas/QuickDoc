package com.example.android.quickdoc.dataClasses;

import java.io.Serializable;

/**
 * Created by Denis on 01/05/2018.
 */

public class UserAppointment implements Serializable {

    private String date;
    private int time;
    private String specialty;
    private int doctorId;
    private boolean reviewed;
    private boolean wontReview;

    public UserAppointment(){}

    public UserAppointment(String date, int time, String specialty, int doctorId,
                           boolean reviewed, boolean wontReview) {
        this.date = date;
        this.time = time;
        this.specialty = specialty;
        this.doctorId = doctorId;
        this.reviewed = reviewed;
        this.wontReview = wontReview;
    }


    public String getDate() {
        return date;
    }

    public int getTime() {
        return time;
    }

    public String getSpecialty() {
        return specialty;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public boolean isWontReview() {
        return wontReview;
    }
}
