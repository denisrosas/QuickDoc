package com.example.android.quickdoc.dataClasses;

import java.util.ArrayList;

/**
 * Created by Denis on 05/05/2018.
 */

public class SpecialtyKeys {

    private ArrayList<String> keysList;

    public SpecialtyKeys() {
        keysList = new ArrayList<>();
        keysList.add("cardiologist");
        keysList.add("cermatologist");
        keysList.add("ceriatric");
        keysList.add("cynecologist");
        keysList.add("cephrologist");
        keysList.add("orthopaedist");
        keysList.add("ophthalmologist");
        keysList.add("oncologist");
        keysList.add("pediatrician");
        keysList.add("psychiatrist");
        keysList.add("urologist");
    }

    public String getKeysByPosition(int position) {
        return keysList.get(position);
    }

}
