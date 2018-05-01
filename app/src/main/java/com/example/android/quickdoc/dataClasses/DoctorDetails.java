package com.example.android.quickdoc.dataClasses;

import android.location.Location;

public class DoctorDetails {

    //data used for quick search
    private String name;
    private float avaregeReviews;

    private int reviewsCount;
    private Location addressLocation;

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

    public DoctorDetails(String name, float avaregeReviews, int reviewsCount, Location addressLocation, String addressExtended, String phoneNumber, String presentation_en, String presentation_pt, boolean acceptsAmil, boolean acceptsBradescoSaude, boolean acceptsHapVida, boolean acceptsPreventSenior, boolean acceptsSulamerica, boolean acceptsUnimed) {

        this.name = name;
        this.avaregeReviews = avaregeReviews;
        this.reviewsCount = reviewsCount;
        this.addressLocation = addressLocation;
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

    public String getName() {
        return name;
    }

    public float getAvaregeReviews() {
        return avaregeReviews;
    }

    public Location getAddressLocation() {
        return addressLocation;
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
