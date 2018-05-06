package com.example.android.quickdoc.dataClasses;

import android.content.Context;

import com.example.android.quickdoc.R;

import java.util.ArrayList;

/**
 * This Class has a method that returns the Specialty Name, based on the specialty Key
 */

public class SpecialtyNames {

    private ArrayList<String> specialtyList;

    public SpecialtyNames(Context context) {
        specialtyList = new ArrayList<>();
        specialtyList.add(context.getString(R.string.cardiologist));
        specialtyList.add(context.getString(R.string.dermatologist));
        specialtyList.add(context.getString(R.string.geriatric));
        specialtyList.add(context.getString(R.string.gynecologist));
        specialtyList.add(context.getString(R.string.nephrologist));
        specialtyList.add(context.getString(R.string.orthopaedist));
        specialtyList.add(context.getString(R.string.ophthalmologist));
        specialtyList.add(context.getString(R.string.oncologist));
        specialtyList.add(context.getString(R.string.pediatrician));
        specialtyList.add(context.getString(R.string.psychiatrist));
        specialtyList.add(context.getString(R.string.urologist));
    }


    public static String getSpecialtyName(Context context, String specialtyKey){

        switch (specialtyKey){
            case "cardiologist":
                return context.getString(R.string.cardiologist);

            case "dermatologist":
                return context.getString(R.string.dermatologist);

            case "geriatric":
                return context.getString(R.string.geriatric);

            case "gynecologist":
                return context.getString(R.string.gynecologist);

            case "nephrologist":
                return context.getString(R.string.nephrologist);

            case "orthopaedist":
                return context.getString(R.string.orthopaedist);

            case "ophthalmologist":
                return context.getString(R.string.ophthalmologist);

            case "oncologist":
                return context.getString(R.string.oncologist);

            case "pediatrician":
                return context.getString(R.string.pediatrician);

            case "psychiatrist":
                return context.getString(R.string.psychiatrist);

            case "urologist":
                return context.getString(R.string.urologist);

        }
        return "";
    }

    public ArrayList<String> getSpecialtyList() {
        return specialtyList;
    }

}
