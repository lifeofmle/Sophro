package com.lifeofmle.drinksy.app.utilities;

/**
 * Created by mle on 2014-05-08.
 */
public class DateUtility {

    public static String TIME_FORMAT = "HH:mm";
    public static String DAY_FORMAT = "EEEE";
    public static String DATE_SHORT = "MMM dd, yyyy";

    public static String GetDay(int dayOfWeek){
        switch (dayOfWeek){
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                break;
        }

        return null;
    }

    public static String GetTimeOfDay(int hour){
        // before noon
        if (hour < 12){
            return "morning";
        } else if (hour >= 12 && hour <= 18){
            // after noon before evening
            return "afternoon";
        }

        return "evening";
    }
}
