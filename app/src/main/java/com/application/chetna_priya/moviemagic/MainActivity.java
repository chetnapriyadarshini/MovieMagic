package com.application.chetna_priya.moviemagic;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements MovieRecyclerGridAdapter.AdapterCallback,
    Favorite_dialog_fragment.DialogInterface, MovieDetailFragment.CallBack{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DFTAG = "detail_fragment_tag";
    private static final String FDTAG = "favorite_dialog_tag";
    private static boolean mTwoPane = false;
    private static boolean inflateFragment = false;
    private Bundle savedInstanceState;
    private static View prevView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(checkForInternetAndDatabase()) {
            this.savedInstanceState = savedInstanceState;
            initializeMovieFrgamentAndVars();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getSupportFragmentManager().findFragmentByTag(FDTAG) == null)
        {
            checkForInternetAndDatabase();
        }else if(this.findViewById(android.R.id.content) == null)
            initializeMovieFrgamentAndVars();
    }

    public void initializeMovieFrgamentAndVars() {
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movie_detail_container) != null)/*This will be applicable in cases of tabs 10 inch*/ {
            mTwoPane = true;
            if (savedInstanceState == null) {
                inflateFragment = true;
                Log.d(LOG_TAG, "SAVED INSTANCE NULL INFLATE DETAIL FRAGMENT");
            }
        } else {
            mTwoPane = false;
        }
    }

    public void showMyDialog(int dialog_type) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(FDTAG);
        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        // Create and show the dialog.
        Favorite_dialog_fragment newFragment = Favorite_dialog_fragment.
                newInstance(dialog_type);
        newFragment.show(ft, FDTAG);
    }

    private boolean checkForInternetAndDatabase() {
        if(Utility.getSortingChoice(this).equals(getResources().getString(R.string.pref_favorites)))
        {
            if(Utility.isDatabaseEmpty(this)) {
                showMyDialog(Constants.DIALOG_DATABASE_EMPTY);
                return false;
            }
            return true;
        }else if(!(Utility.isNetworkAvailable(this)))
        {
            showMyDialog(Constants.DIALOG_NO_INTERNET);
            return false;
        }
        return true;
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
    public void onItemSelected(ImageView posterView, int movieId) {
        if(mTwoPane)
        {
            setSelected(posterView);
            /* If a two pane layout simply replace the previous detail movies fragment*/
            MovieDetailFragment movieDetailFragment = MovieDetailFragment.newInstance(movieId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, movieDetailFragment, DFTAG)
                    .commit();
        }else{
            /* If a one pane layout launch Movie Detail Activity*/
            Intent detailIntent;
            detailIntent = new Intent(this, MovieDetailsTabActivity.class);
            detailIntent.putExtra(Constants.MOVIE_ID, movieId);

            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startActivity(detailIntent, bundle);
            }else
                startActivity(detailIntent);


        }
    }

    private void setSelected(ImageView posterView) {
        if(prevView != null)
        {
            prevView.setPadding(0,0,0,0);
        }
        prevView = posterView;
        posterView.setBackgroundColor(getResources().getColor(R.color.dark_green));
        posterView.setPadding(5, 5, 5, 5);
    }

    /*
    * Called when we use a two pane layout and want to
    * launch the detail fragment for first movie by
    * default(activity was just launched and no
    * selection has been made by the user
    * */
    @Override
    public void initMovieDetailFragment(int movieId) {
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

    @Override
    public void setMovieAsFav(boolean isFav) {

    }

    @Override
    public void makeText(int stringRes) {

    }

    @Override
    public void setActionBarTitle(String movieTitle) {
        getSupportActionBar().setTitle(movieTitle);
    }
}
