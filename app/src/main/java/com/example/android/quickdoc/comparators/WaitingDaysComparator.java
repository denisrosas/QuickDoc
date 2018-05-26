package com.example.android.quickdoc.comparators;

import com.example.android.quickdoc.dataClasses.DoctorDetailsToUser;

import java.util.Comparator;

public class WaitingDaysComparator implements Comparator<DoctorDetailsToUser> {
    @Override
    public int compare(DoctorDetailsToUser doctor1, DoctorDetailsToUser doctor2) {
        return doctor1.getWaitingDays()-doctor2.getWaitingDays();
    }
}
