package com.example.android.quickdoc.dataClasses;

public class AppointmentTime {

    public static String getTimeFromIndex(int horaryId){

        switch (horaryId){
            case 0:
                return "8:00";

            case 1:
                return "8:30";

            case 2:
                return "9:00";

            case 3:
                return "9:30";

            case 4:
                return "10:00";

            case 5:
                return "10:30";

            case 6:
                return "11:00";

            case 7:
                return "11:30";

            case 8:
                return "13:00";

            case 9:
                return "13:30";

            case 10:
                return "14:00";

            case 11:
                return "14:30";

            case 12:
                return "15:00";

            case 13:
                return "15:30";

            case 14:
                return "16:00";

            case 15:
                return "16:30";

        }
        return "";
    }
}
