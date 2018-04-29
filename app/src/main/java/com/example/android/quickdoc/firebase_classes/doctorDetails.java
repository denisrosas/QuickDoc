package com.example.android.quickdoc.firebase_classes;

import android.location.Location;

public class doctorDetails {

    //data used for quick search
    private String id;
    private String name;
    private String speciality;
    private float avaregeReviews;
    private Location addressLatLng;

    //data used for showing details
    private String addressExtended;
    private String phoneNumber;
    private String presentation;

    //accepted health care planes
    private boolean acceptsAmil;
    private boolean acceptsBradescoSaude;
    private boolean acceptsHapVida;
    private boolean acceptsPreventSenior;
    private boolean acceptsSulamerica;
    private boolean acceptsUnimed;

    public doctorDetails(String id, String name, String speciality, float avaregeReviews, Location addressLatLng, String addressExtended, String phoneNumber, String presentation, boolean acceptsSulamerica, boolean acceptsAmil, boolean acceptsPreventSenior, boolean acceptsBradescoSaude, boolean acceptsHapVida, boolean acceptsUnimed) {
        this.id = id;
        this.name = name;
        this.speciality = speciality;
        this.avaregeReviews = avaregeReviews;
        this.addressLatLng = addressLatLng;
        this.addressExtended = addressExtended;
        this.phoneNumber = phoneNumber;
        this.presentation = presentation;
        this.acceptsSulamerica = acceptsSulamerica;
        this.acceptsAmil = acceptsAmil;
        this.acceptsPreventSenior = acceptsPreventSenior;
        this.acceptsBradescoSaude = acceptsBradescoSaude;
        this.acceptsHapVida = acceptsHapVida;
        this.acceptsUnimed = acceptsUnimed;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpeciality() {
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

    public String getPresentation() {
        return presentation;
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
