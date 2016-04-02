package com.application.chetna_priya.moviemagic;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;

public class MovieDetailsTab extends AppCompatActivity implements ActionBar.TabListener,
            ViewPager.OnPageChangeListener, MovieDetailFragment.CallBack{

    private static final String LOG_TAG = MovieDetailsTab.class.getSimpleName();
    private static final String SELECTED_TAB = "selected_tab";

    MovieDetailsPagerAdapter mMovieDetailsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_movie_details);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);
        return true;
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
}
