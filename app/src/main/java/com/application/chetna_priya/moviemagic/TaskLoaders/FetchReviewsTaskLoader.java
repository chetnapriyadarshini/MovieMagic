package com.application.chetna_priya.moviemagic.TaskLoaders;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.application.chetna_priya.moviemagic.FetchDataOverNetwork;
import com.application.chetna_priya.moviemagic.Review;
import com.application.chetna_priya.moviemagic.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by chetna_priya on 3/21/2016.
 */
public class FetchReviewsTaskLoader extends AsyncTaskLoader<ArrayList<Review>> {

    private static final String LOG_TAG = FetchReviewsTaskLoader.class.getSimpleName();
    private int mMovieId ;
    private Context mContext;

    public FetchReviewsTaskLoader(Context context, int movieId) {
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
    public ArrayList<Review> loadInBackground() {
        Uri movieUri = Utility.buildReviewsUriWithMovieId(mMovieId);
        String movieJsonString = FetchDataOverNetwork.fetchMovieData(mContext, movieUri);
        if(movieJsonString ==null)
            return null;
        try {
            return getMovieReviewFromJson(movieJsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<Review> getMovieReviewFromJson(String movieJsonStr) throws JSONException {

        final String OWM_RESULTS = "results";
        final String OWM_REVIEW_AUTHOR = "author";
        final String OWM_REVIEW_URL = "url";
        final String OWM_REVIEW_CONTENT = "content";


        ArrayList<Review> reviews = new ArrayList<>();
        JSONObject movieInfo = new JSONObject(movieJsonStr);
        JSONArray resultArray = movieInfo.getJSONArray(OWM_RESULTS);
        for(int i = 0; i< resultArray.length(); i++)
        {
            JSONObject jsonObject = resultArray.getJSONObject(i);
            String content =formatReviewContent(jsonObject.getString(OWM_REVIEW_CONTENT));
            Review review = new Review(jsonObject.getString(OWM_REVIEW_AUTHOR),
                    content,
                    jsonObject.getString(OWM_REVIEW_URL));
            reviews.add(review);
        }
        return reviews;
    }

    private String formatReviewContent(String review_content)
    {
        /* We only display small review content with a link to complete content */
      //  Log.d(LOG_TAG, "REVIEW: "+review_content);
        int num_words = 200;
        if(review_content.length()-1 < num_words)
            num_words = review_content.length()-1;

        String content = review_content.substring(0, num_words);
        content = content.substring(0, content.lastIndexOf(" "));
        content = content.concat("..");
    //    Log.d(LOG_TAG, "REVIEW AFTER FORMATTING: "+content);
        return content;
    }
}
