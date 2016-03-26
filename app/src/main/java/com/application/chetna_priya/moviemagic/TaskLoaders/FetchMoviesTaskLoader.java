package com.application.chetna_priya.moviemagic.TaskLoaders;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.application.chetna_priya.moviemagic.BuildConfig;
import com.application.chetna_priya.moviemagic.Constants;
import com.application.chetna_priya.moviemagic.FetchDataOverNetwork;
import com.application.chetna_priya.moviemagic.Movie;
import com.application.chetna_priya.moviemagic.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by chetna_priya on 3/7/2016.
 */
public class FetchMoviesTaskLoader extends AsyncTaskLoader<ArrayList<Movie>> {
    private static final String LOG_TAG = FetchMoviesTaskLoader.class.getSimpleName();
    private final static String SORT_PARAM = "sort_by";
    private Context mContext;

    public FetchMoviesTaskLoader(Context context)
    {
        super(context);
        mContext = context;
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

        Uri builtUri = Uri.parse(Constants.MOVIE_BASE_URL).buildUpon()
                .appendQueryParameter(SORT_PARAM, sort_order)
                .appendQueryParameter(Constants.APP_ID_PARAM, BuildConfig.MOVIE_API_KEY)
                .build();
        Log.d(LOG_TAG, builtUri.toString());
        String movieList = FetchDataOverNetwork.fetchMovieData(mContext, builtUri);
        try {
            return getMovieDataFromJson(movieList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<Movie> getMovieDataFromJson(String movieJsonStr)
            throws JSONException {
        final String OWM_RESULTS = "results";
        final String OWM_POSTER_PATH = "poster_path";
        final String OWM_MOVIE_ID = "id";
        final String OWM_MOVIE_RELEASE_DATE = "release_date";


        JSONObject movieJsonObject = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJsonObject.getJSONArray(OWM_RESULTS);


        ArrayList<Movie> movieList = new ArrayList<>(movieArray.length());


        for(int i=0; i<movieArray.length();i++)
        {
            JSONObject movieObj = movieArray.getJSONObject(i);
            Movie movie = new Movie(movieObj.getString(OWM_MOVIE_ID),
                    movieObj.getString(OWM_POSTER_PATH),
                    movieObj.getString(OWM_MOVIE_RELEASE_DATE));
            movieList.add(movie);

        }

        return movieList;

    }
}
