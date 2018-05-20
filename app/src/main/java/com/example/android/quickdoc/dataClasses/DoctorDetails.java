package com.example.android.quickdoc.dataClasses;

import java.io.Serializable;

public class DoctorDetails implements Serializable{

    //data used for quick search
    private String name;
    private float avaregeReviews;

    private int reviewsCount;
    private float addressLat;
    private float addressLng;

    //data used for showing details
    private String addressExtended;
    private String phoneNumber;

    private String presentationEn;
    private String presentationPt;

    //accepted health care planes
    private boolean acceptsAmil;
    private boolean acceptsBradescoSaude;
    private boolean acceptsHapVida;
    private boolean acceptsPreventSenior;
    private boolean acceptsSulamerica;
    private boolean acceptsUnimed;

    public DoctorDetails(){}

    public DoctorDetails(String name, float avaregeReviews, int reviewsCount, float addressLat, float addressLng, String addressExtended, String phoneNumber, String presentationEn, String presentationPt, boolean acceptsAmil, boolean acceptsBradescoSaude, boolean acceptsHapVida, boolean acceptsPreventSenior, boolean acceptsSulamerica, boolean acceptsUnimed) {
        this.name = name;
        this.avaregeReviews = avaregeReviews;
        this.reviewsCount = reviewsCount;
        this.addressLat = addressLat;
        this.addressLng = addressLng;
        this.addressExtended = addressExtended;
        this.phoneNumber = phoneNumber;
        this.presentationEn = presentationEn;
        this.presentationPt = presentationPt;
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

    public float getAddressLat() {
        return addressLat;
    }

    public float getAddressLng() {
        return addressLng;
    }

    public String getAddressExtended() {
        return addressExtended;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPresentationEn() {
        return presentationEn;
    }

    public String getPresentationPt() {
        return presentationPt;
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
