package com.application.chetna_priya.moviemagic;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.application.chetna_priya.moviemagic.data.FavMovieContract.MovieEntry;

import java.io.ByteArrayOutputStream;

/**
 * Created by chetna_priya on 3/7/2016.
 */
public class Utility
{
    private static final String LOG_TAG = Utility.class.getSimpleName();

    public static String getSortingChoice(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.pref_sort_by_key),
                context.getString(R.string.pref_default_sorting_option));
    }

    /* Return release date in format "month day, year"*/
    public static String getFormattedDate(String date)
    {
        final int YEAR = 0;
        final int MONTH = 1;
        final int DAY = 2;
        String[] year_month_day = date.split("-");
        String year, month, day;

        year = ", "+year_month_day[YEAR];

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
        return " . "+voteAve+" / 10";
    }

    private static String getMonthInString(String monthIndex) {

        String[] month_list = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug",
                                "Sept", "Oct", "Nov", "Dec"};
        return month_list[Integer.valueOf(monthIndex)-1];
    }

    public static String getFormattedDuration(int duration) {
        int minutes = duration % 60;
        int hours = duration / 60;
            return " . "+getHoursString(hours)+ getMinutesString(minutes);
    }

    private static String getMinutesString(int minutes) {
        if(minutes < 1)
            return "";
        return " " + minutes + "m";
    }

    private static String getHoursString(int hours) {
        if(hours < 1)
            return "";
        return hours + "h";
    }

    public static Uri buildUriWithMovieId(int movieId) {
        return Uri.parse(Constants.MOVIE_DETAIL_BASE_URL).buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendQueryParameter(Constants.APP_ID_PARAM, BuildConfig.MOVIE_API_KEY)
                .build();
    }

    public static Uri buildVideoUriWithMovieId(int movieId)
    {
        final String VIDEOS = "videos";
        return Uri.parse(Constants.MOVIE_DETAIL_BASE_URL).buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendPath(VIDEOS)
                .appendQueryParameter(Constants.APP_ID_PARAM, BuildConfig.MOVIE_API_KEY)
                .build();
    }

    public static Uri buildReviewsUriWithMovieId(int movieId)
    {
        final String REVIEWS = "reviews";
        return Uri.parse(Constants.MOVIE_DETAIL_BASE_URL).buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendPath(REVIEWS)
                .appendQueryParameter(Constants.APP_ID_PARAM, BuildConfig.MOVIE_API_KEY)
                .build();
    }

    /*public static boolean isMovieReleased(String releaseDate) {
        Log.d(LOG_TAG, ""+releaseDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(releaseDate);
            Log.d(LOG_TAG, "Formatted Release DATEEEEEE"+date);
            Time time = new Time();
            time.setToNow();
            long currentTime = System.currentTimeMillis();
            int julianDay = Time.getJulianDay(date.getTime(), time.gmtoff);
            int currentJulianDay = Time.getJulianDay(currentTime, time.gmtoff);
            if(julianDay > currentJulianDay)
                return false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }*/

    public static Cursor queryByMovieId(Context activity, int movieId) {


        Log.d(LOG_TAG, "QUERYYYINGGGGGGGGGG MOVIE BY ID");
        String[] selectionArgs = new String[]{String.valueOf(movieId)};
        Uri movie_by_id_uri =
                MovieEntry.CONTENT_URI.buildUpon()
                        .appendPath(String.valueOf(movieId)).build();
        return activity.getContentResolver().query(movie_by_id_uri,
                null,
                null,
                selectionArgs, null);
    }

    public static Bitmap getMoviePosterById(int columnMoviePoster, Cursor cursor) {

        byte[] bitmapArr = cursor.getBlob(columnMoviePoster);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapArr, 0, bitmapArr.length);
        return bitmap;
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
