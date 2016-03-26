package com.application.chetna_priya.moviemagic.TaskLoaders;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import com.application.chetna_priya.moviemagic.FetchDataOverNetwork;
import com.application.chetna_priya.moviemagic.Movie;
import com.application.chetna_priya.moviemagic.Utility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chetna_priya on 3/7/2016.
 */
public class FetchMovieDetailsTaskLoader extends AsyncTaskLoader<Movie> {

    private static final String LOG_TAG = FetchMovieDetailsTaskLoader.class.getSimpleName();
    private Context mContext;
    private String mMovieId;

    public FetchMovieDetailsTaskLoader(Context context, String movieId) {
        super(context);
        this.mContext = context;
        this.mMovieId = movieId;
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
            Uri movieUri = Utility.buildUriWithMovieId(mMovieId);
            String movieJsonString = FetchDataOverNetwork.fetchMovieData(mContext, movieUri);
        try {
            return getMovieDataFromJson(movieJsonString);
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

        JSONObject movieInfo = new JSONObject(movieJsonStr);

        Movie movie = new Movie(movieInfo.getString(OWM_MOVIE_ID),
                movieInfo.getString(OWM_POSTER_PATH),
                movieInfo.getString(OWM_MOVIE_BACKDROP_PATH),
                movieInfo.getString(OWM_TITLE),
                movieInfo.getString(OWM_RELEASE_DATE),
                movieInfo.getString(OWM_VOTE_AVERAGE),
                movieInfo.getString(OWM_PLOT_SYNOPSIS),
                movieInfo.getString(OWM_MOVIE_RUNTIME));
        return movie;

    }

}
