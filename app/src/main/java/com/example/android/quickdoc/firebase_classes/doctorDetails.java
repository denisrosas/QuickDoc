package com.example.android.quickdoc;

import android.location.Location;

/**
 * Created by Denis on 22/04/2018.
 */

public class doctorDetails {

    //data used for qui search
    int id;
    private String name;
    private String speciality;
    private float avaregeReviews;
    private Location addressLatLng;

    //data used for showing details
    private String addressExtended;
    private String phoneNumber;
    private String presentation;

    //accepted health care planes
    private boolean acceptsSulamerica;
    private boolean acceptsAmil;
    private boolean acceptsPreventSenior;
    private boolean acceptsBradescoSaude;
    private boolean acceptsHapVida;
    private boolean acceptsUnimed;

    public doctorDetails(int id, String name, String speciality, float avaregeReviews, Location addressLatLng, String addressExtended, String phoneNumber, String presentation, boolean acceptsSulamerica, boolean acceptsAmil, boolean acceptsPreventSenior, boolean acceptsBradescoSaude, boolean acceptsHapVida, boolean acceptsUnimed) {
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
}
