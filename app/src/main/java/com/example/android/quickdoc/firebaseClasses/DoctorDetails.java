package com.example.android.quickdoc.firebaseClasses;

import android.location.Location;

public class DoctorDetails {

    //data used for quick search
    private int id;
    private String name;
    private int speciality;
    private float avaregeReviews;

    private int reviewsCount;
    private Location addressLatLng;

    //data used for showing details
    private String addressExtended;
    private String phoneNumber;

    private String presentation_en;
    private String presentation_pt;

    //accepted health care planes
    private boolean acceptsAmil;
    private boolean acceptsBradescoSaude;
    private boolean acceptsHapVida;
    private boolean acceptsPreventSenior;
    private boolean acceptsSulamerica;
    private boolean acceptsUnimed;

    public DoctorDetails(int id, String name, int speciality, float avaregeReviews, int reviewsCount, Location addressLatLng, String addressExtended, String phoneNumber, String presentation_en, String presentation_pt, boolean acceptsAmil, boolean acceptsBradescoSaude, boolean acceptsHapVida, boolean acceptsPreventSenior, boolean acceptsSulamerica, boolean acceptsUnimed) {
        this.id = id;
        this.name = name;
        this.speciality = speciality;
        this.avaregeReviews = avaregeReviews;
        this.reviewsCount = reviewsCount;
        this.addressLatLng = addressLatLng;
        this.addressExtended = addressExtended;
        this.phoneNumber = phoneNumber;
        this.presentation_en = presentation_en;
        this.presentation_pt = presentation_pt;
        this.acceptsAmil = acceptsAmil;
        this.acceptsBradescoSaude = acceptsBradescoSaude;
        this.acceptsHapVida = acceptsHapVida;
        this.acceptsPreventSenior = acceptsPreventSenior;
        this.acceptsSulamerica = acceptsSulamerica;
        this.acceptsUnimed = acceptsUnimed;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSpeciality() {
        return speciality;
    }

    public float getAvaregeReviews() {
        return avaregeReviews;
    }

    public Location getAddressLatLng() {
        return addressLatLng;
    }

    public String getAddressExtended() {
        return addressExtended;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPresentationEn() {
        return presentation_en;
    }

    public String getPresentationPt() {
        return presentation_pt;
    }

    public int getReviewsCount() {
        return reviewsCount;
    }

    public boolean isAcceptsSulamerica() {
        return acceptsSulamerica;
    }

    public boolean isAcceptsAmil() {
        return acceptsAmil;
    }

    public boolean isAcceptsPreventSenior() {
        return acceptsPreventSenior;
    }

    public boolean isAcceptsBradescoSaude() {
        return acceptsBradescoSaude;
    }

    public boolean isAcceptsHapVida() {
        return acceptsHapVida;
    }

    public boolean isAcceptsUnimed() {
        return acceptsUnimed;
    }
}
