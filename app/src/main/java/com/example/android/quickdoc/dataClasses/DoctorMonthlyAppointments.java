package com.example.android.quickdoc.dataClasses;


public class DoctorMonthlyAppointments {

    private static final int APPOINTMENTS_PER_DAY = 16;
    appointment[] dailyAppointments = new appointment[APPOINTMENTS_PER_DAY];

    class appointment {
        String userId;
        boolean scheduled;
    }
}
