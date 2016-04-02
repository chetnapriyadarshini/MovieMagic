package com.application.chetna_priya.moviemagic.TaskLoaders;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import com.application.chetna_priya.moviemagic.FetchDataOverNetwork;
import com.application.chetna_priya.moviemagic.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by chetna_priya on 3/21/2016.
 */
public class FetchTrailersTaskLoader extends AsyncTaskLoader<ArrayList<String>> {
    private Context mContext;
    private int mMovieId;

    public FetchTrailersTaskLoader(Context context, int movieId) {
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
    public ArrayList<String> loadInBackground() {
        Uri movieUri = Utility.buildVideoUriWithMovieId(mMovieId);
        String movieJsonString = FetchDataOverNetwork.fetchMovieData(mContext, movieUri);
        if(movieJsonString == null)
            return null;
        try {
            return getMovieTrailerFromJson(movieJsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<String> getMovieTrailerFromJson(String movieJsonStr) throws JSONException {

        final String OWM_RESULTS = "results";
        final String OWM_TRAILER_KEY = "key";

        JSONObject movieInfo = new JSONObject(movieJsonStr);
        JSONArray resultArray = movieInfo.getJSONArray(OWM_RESULTS);

        ArrayList<String> trailer_keys = new ArrayList<>();
        for(int i = 0; i< resultArray.length(); i++)
        {
            JSONObject jsonObject = resultArray.getJSONObject(i);
            trailer_keys.add(jsonObject.getString(OWM_TRAILER_KEY));
        }
        return trailer_keys;
    }
}
