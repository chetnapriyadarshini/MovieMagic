package com.application.chetna_priya.moviemagic;

import android.content.res.Configuration;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;

public class MovieDetailsTabActivity extends AppCompatActivity implements ActionBar.TabListener,
            ViewPager.OnPageChangeListener, MovieDetailFragment.CallBack, Favorite_dialog_fragment.DialogInterface{

    private static final String LOG_TAG = MovieDetailsTabActivity.class.getSimpleName();
    private static final String SELECTED_TAB = "selected_tab";

    MovieDetailsPagerAdapter mMovieDetailsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_movie_details);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.movie_details_enter);
            slide.setSlideEdge(Gravity.TOP);
            slide.addTarget(R.id.movie_detail_layout);
            getWindow().setEnterTransition(slide);
        }*/

        // Create the adapter that will return a fragment for each primary section of the activity.
        mMovieDetailsPagerAdapter = new MovieDetailsPagerAdapter(getSupportFragmentManager(),
                this,getIntent().getIntExtra(Constants.MOVIE_ID, -1));

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mMovieDetailsPagerAdapter);
        mViewPager.setOnPageChangeListener(this);


        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        for(int i=0; i< Constants.TOTAL_TABS; i++)
        {
            actionBar.addTab(actionBar.newTab().
                    setText(mMovieDetailsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }

        if (savedInstanceState != null) {
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt(SELECTED_TAB, 0));
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_TAB, getSupportActionBar().getSelectedNavigationIndex());
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
        getSupportActionBar().setSelectedNavigationItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mViewPager.setCurrentItem(position);
        getSupportActionBar().setSelectedNavigationItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void setActionBarTitle(String movieTitle) {
        getSupportActionBar().setTitle(movieTitle);
    }

    @Override
    public void setMovieAsFav(boolean isFav) {
        mMovieDetailsPagerAdapter.setMovieAsFav(isFav);
    }

    @Override
    public void makeText(int stringRes) {
        mMovieDetailsPagerAdapter.makeText(stringRes);
    }



}
