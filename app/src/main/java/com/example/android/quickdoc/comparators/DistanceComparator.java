package com.example.android.quickdoc.comparators;

import com.example.android.quickdoc.dataClasses.DoctorDetailsToUser;

import java.util.Comparator;

public class DistanceComparator implements Comparator<DoctorDetailsToUser>{
    @Override
    public int compare(DoctorDetailsToUser doctor1, DoctorDetailsToUser doctor2) {
        //lowest distance first
        return (int) (doctor1.getDistanceToDoctor()-doctor2.getDistanceToDoctor());
    }
}
