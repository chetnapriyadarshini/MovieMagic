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
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by chetna_priya on 3/7/2016.
 */
public class FetchMoviesTaskLoader extends AsyncTaskLoader<ArrayList<Movie>> {
    private static final String LOG_TAG = FetchMoviesTaskLoader.class.getSimpleName();
    final static String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
    final static String SORT_PARAM = "sort_by";
    final static String APP_ID_PARAM = "api_key";
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
        HttpURLConnection httpURLConnection;
        BufferedReader bufferedReader;

        String movieList = null;
        try {

            final String sort_order = Utility.getSortingChoice(mContext);

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, sort_order)
                    .appendQueryParameter(APP_ID_PARAM, BuildConfig.MOVIE_API_KEY)
                    .build();
            URL url = new URL(builtUri.toString());

            Log.d(LOG_TAG, url.toString());

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

            movieList = buffer.toString();
            Log.d(LOG_TAG, movieList);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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


        JSONObject movieJsonObject = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJsonObject.getJSONArray(OWM_RESULTS);


        ArrayList<Movie> movieList = new ArrayList<>(movieArray.length());


        for(int i=0; i<movieArray.length();i++)
        {
            JSONObject movieObj = movieArray.getJSONObject(i);
            Movie movie = new Movie(movieObj.getString(OWM_MOVIE_ID),
                    movieObj.getString(OWM_POSTER_PATH));
            movieList.add(movie);

        }

        return movieList;

    }
}
