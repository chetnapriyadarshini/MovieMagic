package com.application.chetna_priya.moviemagic;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

/**
 * Created by chetna_priya on 3/18/2016.
 */
public class MovieDetailsPagerAdapter extends FragmentPagerAdapter {

    private static final String LOG_TAG = MovieDetailsPagerAdapter.class.getSimpleName();
    private static Context mContext;
    int movieId;
    MovieDetailFragment movieDetailFragment;
    MovieReviewFragment movieReviewFragment;

    public MovieDetailsPagerAdapter(FragmentManager fm, Context context, int movieID) {
        super(fm);
        this.movieId = movieID;
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case Constants.TAB_OVERVIEW:
                if(movieDetailFragment == null)
                    movieDetailFragment = MovieDetailFragment.newInstance(movieId);
                return movieDetailFragment;
            case Constants.TAB_REVIEWS:
                if(movieReviewFragment == null)
                    movieReviewFragment = MovieReviewFragment.newInstance(movieId);
                return movieReviewFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return Constants.TOTAL_TABS;
    }



    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case Constants.TAB_OVERVIEW:
                return mContext.getResources().getString(R.string.tab_overview);
            case Constants.TAB_REVIEWS:
                return mContext.getResources().getString(R.string.tab_reviews);
        }
        return null;
    }

    public void setMovieAsFav(boolean isFav)
    {
        movieDetailFragment.setMovieAsFav(isFav);
    }

    public void makeText(int stringRes) {
        movieDetailFragment.makeText(stringRes);
    }
}
