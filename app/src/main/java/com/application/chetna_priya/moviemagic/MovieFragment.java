package com.application.chetna_priya.moviemagic;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.application.chetna_priya.moviemagic.TaskLoaders.FetchMoviesTaskLoader;
import com.application.chetna_priya.moviemagic.data.FavMovieContract;

import java.util.ArrayList;

public class MovieFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private static final String LOG_TAG = MovieFragment.class.getSimpleName();
    private static final int MOVIE_LOADER = 0;
    private static final String POSITION = "selected_position";

    RecyclerView mRecyclerView;

    private MovieRecyclerGridAdapter mRecyclerViewGridAdapter;
    private GridLayoutManager mGridLayoutManager;

    public MovieFragment() {
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_fragment, container, false);
//        ButterKnife.bind(this, rootView);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.grid_movies);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new MovieItemAnimator());
        mGridLayoutManager = new GridLayoutManager(getActivity(), Constants.DEFAULT_SPAN_COUNT);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        Cursor cursor = getActivity().getContentResolver().query(FavMovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        mRecyclerViewGridAdapter = new MovieRecyclerGridAdapter(getActivity(),mGridLayoutManager, cursor);
        cursor.close();
        mRecyclerView.setAdapter(mRecyclerViewGridAdapter);
        if(savedInstanceState != null && savedInstanceState.containsKey(POSITION))
        {
            /*Get the selected position from bundle */
            mRecyclerViewGridAdapter.setSelectedPos(savedInstanceState.getInt(POSITION));
        }
        mRecyclerView.smoothScrollToPosition(mRecyclerViewGridAdapter.getSelectedPos());//scroll to selected pos
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mRecyclerViewGridAdapter.getSelectedPos() != GridView.INVALID_POSITION)
        {
            /* Save the selected position in bundle
            * will be used later to scroll to the
            * given pos so that the view remains in focus*/
            outState.putInt(POSITION, mRecyclerViewGridAdapter.getSelectedPos());
        }
        super.onSaveInstanceState(outState);
    }



    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        return new FetchMoviesTaskLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        mRecyclerViewGridAdapter.setData(data);
        mRecyclerView.setAdapter(mRecyclerViewGridAdapter);
        mGridLayoutManager.setSpanCount(mRecyclerViewGridAdapter.getSpanCount());
    }
/*
    private void applyPaddingToBackground(View view) {
        view.setBackgroundColor(darkGreen);
        view.setPadding(padding, padding, padding, padding);
    }*/

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        mRecyclerViewGridAdapter.setData(null);
    }

}
