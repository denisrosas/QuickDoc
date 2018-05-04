package com.example.android.quickdoc.dataClasses;


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

    private int doctorId;

    //constructor calling super

    public DoctorDetailsToUser(DoctorDetails doctorDetails) {
        super(doctorDetails.getName(), doctorDetails.getAvaregeReviews(), doctorDetails.getReviewsCount(), doctorDetails.getAddressLat(), doctorDetails.getAddressLng(), doctorDetails.getAddressExtended(), doctorDetails.getPhoneNumber(), doctorDetails.getPresentationEn(), doctorDetails.getPresentationPt(), doctorDetails.isAcceptsAmil(), doctorDetails.isAcceptsBradescoSaude(), doctorDetails.isAcceptsHapVida(), doctorDetails.isAcceptsPreventSenior(), doctorDetails.isAcceptsSulamerica(), doctorDetails.isAcceptsUnimed());
    }

    //Getters
    public float getDistanceToDoctor() {
        return distanceToDoctor;
    }

    public int getWaitingDays() {
        return waitingDays;
    }

    public int getDoctorId() {
        return doctorId;
    }

    //setters
    public void setDistanceToDoctor(float distanceToDoctor) {
        this.distanceToDoctor = distanceToDoctor;
    }

    public void setWaitingDays(int waitingDays) {
        this.waitingDays = waitingDays;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
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
