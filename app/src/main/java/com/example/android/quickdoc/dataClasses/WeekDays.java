package com.example.android.quickdoc.dataClasses;

import android.content.Context;

import com.example.android.quickdoc.R;

/**
 * Created by Denis on 06/05/2018.
 */

public class WeekDays {

    public static String getMonthName(int weekDayId, Context context){

        switch (weekDayId){
            case 1:
                return context.getString(R.string.sunday);

            case 2:
                return context.getString(R.string.monday);

            case 3:
                return context.getString(R.string.tuesday);

            case 4:
                return context.getString(R.string.wednesday);

            case 5:
                return context.getString(R.string.thursday);

            case 6:
                return context.getString(R.string.friday);

            case 7:
                return context.getString(R.string.saturday);
        }
        return "";
    }
}
