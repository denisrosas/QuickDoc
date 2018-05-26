package com.example.android.quickdoc.comparators;

import com.example.android.quickdoc.dataClasses.DoctorDetailsToUser;

import java.util.Comparator;

public class RatingComparator implements Comparator<DoctorDetailsToUser>{
    @Override
    public int compare(DoctorDetailsToUser doctor1, DoctorDetailsToUser doctor2) {
        //highest rating first
        return (int) (doctor2.getAvaregeReviews()-doctor1.getAvaregeReviews());
    }
}
