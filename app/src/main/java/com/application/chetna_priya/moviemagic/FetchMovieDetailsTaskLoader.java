package com.application.chetna_priya.moviemagic;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by chetna_priya on 3/7/2016.
 */
public class FetchMovieDetailsTaskLoader extends AsyncTaskLoader<Movie> {

    private static final String LOG_TAG = FetchMovieDetailsTaskLoader.class.getSimpleName();
    private static Uri mMovieUri;
    private Context mContext;

    public FetchMovieDetailsTaskLoader(Context context, Uri movieUri) {
        super(context);
        this.mContext = context;
        this.mMovieUri = movieUri;
        Log.d(LOG_TAG, "RECEIVED URI: "+mMovieUri);
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
    public Movie loadInBackground() {
        HttpURLConnection httpURLConnection;
        BufferedReader bufferedReader;

        String movie = null;
        try {
            URL url = new URL(mMovieUri.toString());

         //   Log.d(LOG_TAG, url.toString());

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null)
                return null;

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0)
                return null;

            movie = buffer.toString();
            Log.d(LOG_TAG, movie);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return getMovieDataFromJson(movie);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Movie getMovieDataFromJson(String movieJsonStr) throws JSONException {
        final String OWM_TITLE = "title";
        final String OWM_RELEASE_DATE = "release_date";
        final String OWM_VOTE_AVERAGE = "vote_average";
        final String OWM_PLOT_SYNOPSIS = "overview";
        final String OWM_POSTER_PATH = "poster_path";
        final String OWM_MOVIE_ID = "id";
        final String OWM_MOVIE_BACKDROP_PATH = "backdrop_path";
        final String OWM_MOVIE_RUNTIME = "runtime";


        JSONObject movieObj = new JSONObject(movieJsonStr);


        Movie movie = new Movie(movieObj.getString(OWM_MOVIE_ID),
                movieObj.getString(OWM_POSTER_PATH),
                movieObj.getString(OWM_MOVIE_BACKDROP_PATH),
                movieObj.getString(OWM_TITLE),
                movieObj.getString(OWM_RELEASE_DATE),
                movieObj.getString(OWM_VOTE_AVERAGE),
                movieObj.getString(OWM_PLOT_SYNOPSIS),
                movieObj.getString(OWM_MOVIE_RUNTIME));

        return movie;
    }

}
