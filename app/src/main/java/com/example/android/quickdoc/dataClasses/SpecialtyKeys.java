package com.example.android.quickdoc.dataClasses;

import android.content.Context;

import com.example.android.quickdoc.R;

import java.util.ArrayList;

public class SpecialtyKeys {

    private ArrayList<String> keysList;

    public SpecialtyKeys(Context context) {
        keysList = new ArrayList<>();
        keysList.add(context.getString(R.string.cardiologist_key));
        keysList.add(context.getString(R.string.dermatologist_key));
        keysList.add(context.getString(R.string.geriatric_key));
        keysList.add(context.getString(R.string.gynecologist_key));
        keysList.add(context.getString(R.string.nephrologist_key));
        keysList.add(context.getString(R.string.orthopaedist_key));
        keysList.add(context.getString(R.string.ophthalmologist_key));
        keysList.add(context.getString(R.string.oncologist_key));
        keysList.add(context.getString(R.string.pediatrician_key));
        keysList.add(context.getString(R.string.psychiatrist_key));
        keysList.add(context.getString(R.string.urologist_key));
    }

    public String getKeysByPosition(int position) {
        return keysList.get(position);
    }

}
