package com.example.android.quickdoc.firebaseClasses;


import android.location.Location;

import java.util.Comparator;

/** This Class has all the Doctor's details, but has information specific to the user
 * DoctorDetails class is used to retrieve data from Firebase.
 * DoctorDetailsToUser will also store user specific information, like:
 *
 * distanceToDoctor -> calculated distance between the user and the Doctor's office
 * ReviewComparator -> sort the doctors who has most reviews
 * WaitingDaysComparator -> sort the doctors who will take less time to schedule an appointment
 * DistanceComparator -> sort the closer doctors first
 */

public class DoctorDetailsToUser extends DoctorDetails {

    private float distanceToDoctor;

    private int waitingDays;

    public DoctorDetailsToUser(int id, String name, int speciality, float avaregeReviews, int reviewsCount, Location addressLatLng, String addressExtended, String phoneNumber, String presentation_en, String presentation_pt, boolean acceptsAmil, boolean acceptsBradescoSaude, boolean acceptsHapVida, boolean acceptsPreventSenior, boolean acceptsSulamerica, boolean acceptsUnimed) {
        super(id, name, speciality, avaregeReviews, reviewsCount, addressLatLng, addressExtended, phoneNumber, presentation_en, presentation_pt, acceptsAmil, acceptsBradescoSaude, acceptsHapVida, acceptsPreventSenior, acceptsSulamerica, acceptsUnimed);
    }

    //constructor calling super

    //Getters
    public float getDistanceToDoctor() {
        return distanceToDoctor;
    }

    public int getWaitingDays() {
        return waitingDays;
    }

    //setters
    public void setDistanceToDoctor(float distanceToDoctor) {
        this.distanceToDoctor = distanceToDoctor;
    }

    public void setWaitingDays(int waitingDays) {
        this.waitingDays = waitingDays;
    }

    //sort by Reviews
    public static Comparator<DoctorDetailsToUser> DoctorsBestReviewsComparator = new Comparator<DoctorDetailsToUser>() {
        @Override
        public int compare(DoctorDetailsToUser doctor1, DoctorDetailsToUser doctor2) {
            return (int) (doctor2.getAvaregeReviews()-doctor1.getAvaregeReviews());
        }
    };

    //sort by distance
    public static Comparator<DoctorDetailsToUser> DoctorMinorDistanceComparator = new Comparator<DoctorDetailsToUser>() {
        @Override
        public int compare(DoctorDetailsToUser doctor1, DoctorDetailsToUser doctor2) {
            return (int) (doctor1.distanceToDoctor-doctor2.distanceToDoctor);
        }
    };

    //sort by waiting time in days
    public static Comparator<DoctorDetailsToUser> DoctorLessWaitingDaysComparator = new Comparator<DoctorDetailsToUser>() {
        @Override
        public int compare(DoctorDetailsToUser doctor1, DoctorDetailsToUser doctor2) {
            return doctor1.waitingDays-doctor2.waitingDays;
        }
    };

    //sort by name
    public static Comparator<DoctorDetailsToUser> DoctorNameComparator = new Comparator<DoctorDetailsToUser>() {
        @Override
        public int compare(DoctorDetailsToUser doctor1, DoctorDetailsToUser doctor2) {
            return doctor1.getName().toUpperCase().compareTo(doctor2.getName().toUpperCase());
        }
    };

}
