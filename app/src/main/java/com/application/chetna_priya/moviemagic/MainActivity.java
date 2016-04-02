package com.application.chetna_priya.moviemagic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MovieRecyclerGridAdapter.AdapterCallback {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DFTAG = "dftag";
    private static boolean mTwoPane = false;
    private static boolean inflateFragment = false;
    private static int mDefaultId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.movie_detail_container) != null)/*This will be applicable in cases of tabs 10 inch and 7inch landscape*/
        {
            mTwoPane = true;
            if(savedInstanceState == null)
            {
                inflateFragment = true;
                Log.d(LOG_TAG, "SAVED INSTANCE NULL INFLATE DETAIL FRAGMENT");
            }
            else if(getSupportFragmentManager().findFragmentByTag(DFTAG) == null)
            {
                /*This condition applies in case this activity is launched in 7inch tab in portrait
                * mode and switched to landscape mode before the user makes any selection
                * We are doing this because we want to use the one pane layout for one
                * pane 7 inch tabs*/
                Log.d(LOG_TAG, "Device was rotated to landscape mode for 7 inch tab");
                MovieDetailFragment movieDetailFragment = MovieDetailFragment.newInstance(mDefaultId);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, movieDetailFragment, DFTAG)
                        .commit();
            }
        }else {
            mTwoPane = false;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId)
        {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsAcitvity.class);
                startActivity(settingsIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * Called when a specific movie is selected by the user
    * */
    @Override
    public void onItemSelected(int movieId) {
        if(mTwoPane)
        {
            /* If a two pane layout simply replace the previous detail movies fragment*/
            MovieDetailFragment movieDetailFragment = MovieDetailFragment.newInstance(movieId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, movieDetailFragment, DFTAG)
                    .commit();
        }else{
            /* If a one pane layout launch Movie Detail Activity*/
            Intent detailIntent;
            detailIntent = new Intent(this, MovieDetailsTab.class);
            detailIntent.putExtra(Constants.MOVIE_ID, movieId);
            startActivity(detailIntent);
        }
    }

    /*
    * Called when we use a two pane layout and want to
    * launch the detail fragment for first movie by
    * default(activity was just launched and no
    * selection has been made by the user
    * */
    @Override
    public void initMovieDetailFragment(int movieId) {
        mDefaultId = movieId;//save this uri will be used in case this is a 7 inch tab(one pane in portrait two pane in landscape)
        if(mTwoPane && inflateFragment)
        {
            Log.d(LOG_TAG, "Inflate detail fragment here for tablet");
            inflateFragment = false;
            MovieDetailFragment movieDetailFragment = MovieDetailFragment.
                    newInstance(movieId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, movieDetailFragment, DFTAG)
                    .commitAllowingStateLoss();
        }
    }
}
