package com.example.android.quickdoc.dataClasses;

import android.content.Context;

import com.example.android.quickdoc.R;

/**
 * This Class has a method that returns the Specialty Name, based on the specialty Key
 */

public class SpecialtyName {

    public static String getSpecialtyName(Context context, String specialtyKey){

        switch (specialtyKey){
            case "cardiologist":
                return context.getString(R.string.cardiologist);

            case "dermatologist":
                return context.getString(R.string.cardiologist);

            case "geriatric":
                return context.getString(R.string.cardiologist);

            case "gynecologist":
                return context.getString(R.string.cardiologist);

            case "nephrologist":
                return context.getString(R.string.cardiologist);

            case "orthopaedist":
                return context.getString(R.string.cardiologist);

            case "ophthalmologist":
                return context.getString(R.string.cardiologist);

            case "oncologist":
                return context.getString(R.string.cardiologist);

            case "pediatrician":
                return context.getString(R.string.cardiologist);

            case "psychiatrist":
                return context.getString(R.string.cardiologist);

            case "urologist":
                return context.getString(R.string.cardiologist);

        }
        return "";
    }
}
