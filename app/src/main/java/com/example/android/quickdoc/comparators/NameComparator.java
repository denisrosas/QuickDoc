package com.example.android.quickdoc.comparators;

import com.example.android.quickdoc.dataClasses.DoctorDetailsToUser;

import java.util.Comparator;

public class NameComparator implements Comparator<DoctorDetailsToUser> {
    @Override
    public int compare(DoctorDetailsToUser doctor1, DoctorDetailsToUser doctor2) {
        //order alphabetically
        return doctor1.getName().toUpperCase().compareTo(doctor2.getName().toUpperCase());
    }
}
