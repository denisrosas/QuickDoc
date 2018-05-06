package com.example.android.quickdoc.dataClasses;

import android.content.Context;

import com.example.android.quickdoc.R;

/**
 * Created by Denis on 06/05/2018.
 */

public class Months {

    public static String getMonthName(int monthId, Context context){

        switch (monthId){
            case 0:
                return context.getString(R.string.january);

            case 1:
                return context.getString(R.string.february);

            case 2:
                return context.getString(R.string.march);

            case 3:
                return context.getString(R.string.april);

            case 4:
                return context.getString(R.string.may);

            case 5:
                return context.getString(R.string.june);

            case 6:
                return context.getString(R.string.july);

            case 7:
                return context.getString(R.string.august);

            case 8:
                return context.getString(R.string.september);

            case 9:
                return context.getString(R.string.october);

            case 10:
                return context.getString(R.string.november);

            case 11:
                return context.getString(R.string.december);

        }
        return "";
    }
}
