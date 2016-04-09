package com.application.chetna_priya.moviemagic.TaskLoaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import com.application.chetna_priya.moviemagic.FetchDataOverNetwork;
import com.application.chetna_priya.moviemagic.Movie;
import com.application.chetna_priya.moviemagic.Utility;
import com.application.chetna_priya.moviemagic.data.FavMovieContract.MovieEntry;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chetna_priya on 3/7/2016.
 */
public class FetchMovieDetailsTaskLoader extends AsyncTaskLoader<Movie> {

    private static final String LOG_TAG = FetchMovieDetailsTaskLoader.class.getSimpleName();
    private Context mContext;
    private int mMovieId;

    public FetchMovieDetailsTaskLoader(Context context, int movieId) {
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
            if(movieJsonString == null)//No data fetched check if the movie exists in database
            {
                Cursor cursor = Utility.queryByMovieId(mContext, mMovieId);
                if(cursor == null || cursor.getCount() < 1)
                    return null;
                cursor.moveToFirst();
                return new Movie(cursor.getInt(MovieEntry.COL_INDEX_MOVIE_ID),
                        cursor.getString(MovieEntry.COL_INDEX_MOVIE_POSTER_PATH),
                        cursor.getString(MovieEntry.COL_INDEX_BACKDROP_PATH),
                        cursor.getString(MovieEntry.COL_INDEX_MOVIE_TITLE),
                        cursor.getString(MovieEntry.COL_INDEX_MOVIE_RELEASE_DATE),
                        cursor.getLong(MovieEntry.COL_INDEX_MOVIE_RATING),
                        cursor.getString(MovieEntry.COL_INDEX_MOVIE_PLOT),
                        cursor.getInt(MovieEntry.COL_INDEX_MOVIE_RUNTIME));
            }
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

        return new Movie(movieInfo.getInt(OWM_MOVIE_ID),
                movieInfo.getString(OWM_POSTER_PATH),
                movieInfo.getString(OWM_MOVIE_BACKDROP_PATH),
                movieInfo.getString(OWM_TITLE),
                movieInfo.getString(OWM_RELEASE_DATE),
                movieInfo.getLong(OWM_VOTE_AVERAGE),
                movieInfo.getString(OWM_PLOT_SYNOPSIS),
                movieInfo.getInt(OWM_MOVIE_RUNTIME));

    }

}
