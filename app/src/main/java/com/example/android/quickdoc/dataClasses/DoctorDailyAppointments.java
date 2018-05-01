package com.example.android.quickdoc.dataClasses;


import java.util.ArrayList;

public class DoctorDailyAppointments {

    private static final int APPOINTMENTS_PER_DAY = 16;

    //for every position in the daily agenda, if the String is null, means it's not reserved
    public ArrayList<String> reservationArray = new ArrayList<>();

}
