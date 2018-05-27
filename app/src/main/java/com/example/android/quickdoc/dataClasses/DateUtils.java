package com.example.android.quickdoc.dataClasses;

import android.content.Context;

import com.example.android.quickdoc.R;

import java.util.Locale;

/**
 * Created by Denis on 26/05/2018.
 */

public class DateUtils {

    public static String getAdaptedDate(String date, Context context) {

        if(Locale.getDefault().getLanguage().matches("pt")){
            String[] dateVector = date.split(context.getString(R.string.date_separator));
            String year = dateVector[0];
            String month = dateVector[1];
            String day = dateVector[2];
            return day+context.getString(R.string.slash)+month+context.getString(R.string.slash)+year;
        }

        return date;
    }
}
