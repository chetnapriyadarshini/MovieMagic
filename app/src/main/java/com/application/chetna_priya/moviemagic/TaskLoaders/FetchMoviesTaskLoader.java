package com.application.chetna_priya.moviemagic.TaskLoaders;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.application.chetna_priya.moviemagic.BuildConfig;
import com.application.chetna_priya.moviemagic.Constants;
import com.application.chetna_priya.moviemagic.FetchDataOverNetwork;
import com.application.chetna_priya.moviemagic.Movie;
import com.application.chetna_priya.moviemagic.R;
import com.application.chetna_priya.moviemagic.Utility;
import com.application.chetna_priya.moviemagic.data.FavMovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by chetna_priya on 3/7/2016.
 */
public class FetchMoviesTaskLoader extends AsyncTaskLoader<ArrayList<Movie>> {
    private static final String LOG_TAG = FetchMoviesTaskLoader.class.getSimpleName();
    private Context mContext;
    private static int CURRENT_PAGE_NO = 1;
    private static int total_pages = 1;//set the default value of pages to 1
    final String OWM_MOVIE_PAGE = "page";

    public FetchMoviesTaskLoader(Context context, int pageNo)
    {
        super(context);
        mContext = context;
        CURRENT_PAGE_NO = pageNo;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
    }

    @Override
    public ArrayList<Movie> loadInBackground() {
        final String sort_order = Utility.getSortingChoice(mContext);
        Log.d(LOG_TAG, "Sort Orderrrrrrrr " + sort_order);
        if(sort_order.equals(mContext.getResources().getString(R.string.pref_favorites)))
        {
            try {
                Cursor cursor = mContext.getContentResolver().query(FavMovieContract.MovieEntry.CONTENT_URI,
                        new String[]{FavMovieContract.MovieEntry.COLUMN_MOVIE_ID,
                                FavMovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH},
                        null,
                        null,
                        null);
                cursor.moveToFirst();
                ArrayList<Movie> movieList = new ArrayList<>(cursor.getCount());
                while(!cursor.isAfterLast())
                {
                    Movie movie = new Movie(cursor.getInt(FavMovieContract.MovieEntry.COL_INDEX_MOVIE_ID),
                            cursor.getString(FavMovieContract.MovieEntry.COL_INDEX_MOVIE_POSTER_PATH));
                    movieList.add(movie);
                    cursor.moveToNext();
                }
                cursor.close();
                return movieList;
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }
        else
        {
            if(CURRENT_PAGE_NO > total_pages)
                CURRENT_PAGE_NO = total_pages;//If we have reached the bottom of all the results we don't fetch anymore
            Log.d(LOG_TAG, "CURRENT PAGE ATTTTT: "+CURRENT_PAGE_NO+" TOTAL PAGESSSSS AT: "+total_pages);
            Uri builtUri = Uri.parse(Constants.MOVIE_BASE_URL).buildUpon()
                    .appendPath(sort_order)
                    .appendQueryParameter(OWM_MOVIE_PAGE, String.valueOf(CURRENT_PAGE_NO))
                    .appendQueryParameter(Constants.APP_ID_PARAM, BuildConfig.MOVIE_API_KEY)
                    .build();
            Log.d(LOG_TAG, builtUri.toString());
            String movieList = FetchDataOverNetwork.fetchMovieData(mContext, builtUri);
            if(movieList ==null)
                return null;
            try {
                return getMovieDataFromJson(movieList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private ArrayList<Movie> getMovieDataFromJson(String movieJsonStr)
            throws JSONException {
        final String OWM_RESULTS = "results";
        final String OWM_POSTER_PATH = "poster_path";
        final String OWM_MOVIE_ID = "id";
        final String OWM_MOVIE_LIST_TOTAL_PAGES = "total_pages";


        JSONObject movieJsonObject = new JSONObject(movieJsonStr);
        total_pages = movieJsonObject.getInt(OWM_MOVIE_LIST_TOTAL_PAGES);
        saveTotalPages();

        /*
        CURRENT_PAGE_NO = movieJsonObject.getInt(OWM_MOVIE_PAGE);*/
        JSONArray movieArray = movieJsonObject.getJSONArray(OWM_RESULTS);


        ArrayList<Movie> movieList = new ArrayList<>(movieArray.length());


        for(int i=0; i<movieArray.length();i++)
        {
            JSONObject movieObj = movieArray.getJSONObject(i);
            Movie movie = new Movie(movieObj.getInt(OWM_MOVIE_ID),
                    movieObj.getString(OWM_POSTER_PATH));
            movieList.add(movie);

        }

        return movieList;

    }

    public void saveTotalPages() {
        SharedPreferences sharedPref = ((Activity)mContext).getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(mContext.getResources().getString(R.string.pref_total_pages), total_pages);
        editor.commit();
    }
}
