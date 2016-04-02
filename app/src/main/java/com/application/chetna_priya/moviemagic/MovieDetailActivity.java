package com.application.chetna_priya.moviemagic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MovieDetailActivity extends AppCompatActivity implements MovieDetailFragment.CallBack{

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if(savedInstanceState == null) {
            int movieId = getIntent().getIntExtra(Constants.MOVIE_ID,0);
            /*
            We are using this activity instead of tabbed activity
            this means reviews not available, movie not released yet
             */
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_fragment, MovieDetailFragment.newInstance(movieId))
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
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

    @Override
    public void setActionBarTitle(String movieTitle) {
        getSupportActionBar().setTitle(movieTitle);
    }
}
