package com.application.chetna_priya.moviemagic;
import android.content.Context;
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
                /*We are using a tabbed activity the reviews must be available
                *This means movie is released
                * We pass true as the second arguement
                 */
                return MovieDetailFragment.newInstance(movieId);
            case Constants.TAB_REVIEWS:
                return MovieReviewFragment.newInstance(movieId);
        }
        return null;
    }

    @Override
    public int getCount() {
        return Constants.TOTAL_TABS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case Constants.TAB_OVERVIEW:
                return mContext.getResources().getString(R.string.tab_overview);
            case Constants.TAB_REVIEWS:
                return mContext.getResources().getString(R.string.tab_reviews);
        }
        return null;
    }
}
