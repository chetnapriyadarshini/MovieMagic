package com.application.chetna_priya.moviemagic;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;

import java.util.Calendar;

/**
 * Created by chetna_priya on 3/7/2016.
 */
public class Utility
{
    public static String getSortingChoice(Context context)
    {
        final String order_val = ".desc";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.pref_sort_by_key),
                context.getString(R.string.pref_default_sorting_option))+order_val;
    }

    /* Return release date in format "month day, year"*/
    public static String getFormattedDate(String date)
    {
        final int YEAR = 0;
        final int MONTH = 1;
        final int DAY = 2;
        String[] year_month_day = date.split("-");
        String year, month, day;
    //    Calendar calendar = Calendar.getInstance();

       // int current_year = calendar.get(Calendar.YEAR);
        /*if(!(String.valueOf(current_year).equals(year_month_day[YEAR])))
        {*/
         year = ", "+year_month_day[YEAR];
       // }

        month = getMonthInString(year_month_day[MONTH]);

        day = year_month_day[DAY];
        if(day.charAt(0) == '0')
        {
           day =  day.substring(1);
        }


        return month +" "+ day + year;
    }

    public static String getFormattedVoteAve(String voteAve)
    {
        return voteAve+" / 10";
    }

    private static String getMonthInString(String monthIndex) {

        String[] month_list = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug",
                                "Sept", "Oct", "Nov", "Dec"};
        return month_list[Integer.valueOf(monthIndex)-1];
    }

}
