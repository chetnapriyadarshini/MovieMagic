package com.application.chetna_priya.moviemagic;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;

import com.application.chetna_priya.moviemagic.TaskLoaders.FetchMoviesTaskLoader;
import com.application.chetna_priya.moviemagic.data.FavMovieContract;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private static final String LOG_TAG = MovieFragment.class.getSimpleName();
    private static final int MOVIE_LOADER = 0;
    private static final String PAGE_NO = "current_page_no";
    private static final String MOVIE_LIST = "movie_list";
    private static final String SORTING_CHOICE = "sorting_choice";
    private static int page_no = 1;

    private MovieRecyclerGridAdapter mRecyclerViewGridAdapter;
    private GridLayoutManager mGridLayoutManager;
    private ArrayList<Movie> mMovieArrayList;

    @BindString(R.string.pref_favorites)
    String favorites;

    @Bind(R.id.grid_movies)
    RecyclerView mRecyclerView;

    private static Bundle saveBundle;
    /*We have kept this bundle because recycler view does not call
    * onCreateView and OnActivityCreatedAfter resuming, so after resuming
    * we want to read the last loaded page and start loading after that
    * if required*/

    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if(savedInstanceState == null || !(savedInstanceState.containsKey(MOVIE_LIST))) {
            getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(saveBundle != null)
        {
            restoreState(saveBundle);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.movie_fragment, container, false);
            ButterKnife.bind(this, rootView);
            restoreState(savedInstanceState);
            mRecyclerView.setHasFixedSize(false);
            mGridLayoutManager = new GridLayoutManager(getActivity(),Utility.getSpanCount(getActivity()));
            mRecyclerView.setLayoutManager(mGridLayoutManager);
            initializeAdapter();
            if (!(Utility.getSortingChoice(getActivity()).equals(favorites)))//In case of favorites we fetch all the data from databse at once
                mRecyclerView.setOnScrollListener(new EndlessRecyclerViewScrollListener(mGridLayoutManager, getActivity()) {

                    @Override
                    public void onLoadMore(int page, int totalItemsCount) {
                        page_no = page;
                        Log.d(LOG_TAG, "FETCH PAGE NOOOO: " + page);
                        getLoaderManager().restartLoader(MOVIE_LOADER, null, MovieFragment.this);
                    }
                });
            return rootView;
    }

    private void initializeAdapter()
    {
        Cursor cursor = getActivity().getContentResolver().query(FavMovieContract.MovieEntry.CONTENT_URI,
                null, null, null, null);
            mRecyclerViewGridAdapter = new MovieRecyclerGridAdapter(getActivity(), cursor);
            mRecyclerViewGridAdapter.addData(mMovieArrayList);
            cursor.close();
            mRecyclerView.setAdapter(mRecyclerViewGridAdapter);
    }

    private void restoreState(Bundle savedInstanceState) {
        if(savedInstanceState != null && savedInstanceState.containsKey(SORTING_CHOICE))
        {
            /*
            In case sorting choice changed or it is a favorites
             */
            if(needRestart())
            {
                clearData();
                getLoaderManager().restartLoader(MOVIE_LOADER, null, MovieFragment.this);
                return;
            }
        }
        if(savedInstanceState != null && savedInstanceState.containsKey(MOVIE_LIST)) {
            mMovieArrayList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
        }
        else
            mMovieArrayList = new ArrayList<Movie>();
        if(savedInstanceState != null && savedInstanceState.containsKey(PAGE_NO))
        {
            page_no = savedInstanceState.getInt(PAGE_NO);
        }
    }

    private boolean needRestart() {
        if(Utility.getSortingChoice(getActivity()).equals(favorites))
            return true;
        if(!(Utility.isNetworkAvailable(getActivity())))
            return false;
        if(saveBundle != null &&
                (!(Utility.getSortingChoice(getActivity()).equals(saveBundle.getString(SORTING_CHOICE)))))
            return true;
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(PAGE_NO, page_no);
        if(mMovieArrayList != null)
            outState.putParcelableArrayList(MOVIE_LIST, mMovieArrayList);
        /*
        We save this value so that we can restart this loader in case
        user changes the sorting choice and returns to this fragment
         */
        outState.putString(SORTING_CHOICE, Utility.getSortingChoice(getActivity()));
        saveBundle = outState;
        super.onSaveInstanceState(outState);
    }



    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "Fetching page no: " + page_no);
        return new FetchMoviesTaskLoader(getActivity(),page_no);
    }


    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        // update the adapter, saving the last known size
        int curSize = mRecyclerViewGridAdapter.getItemCount();
        if(mMovieArrayList == null)
            mMovieArrayList = new ArrayList<>();
        mMovieArrayList.addAll(data);
        mRecyclerViewGridAdapter.addData(data);

        // for efficiency purposes, only notify the adapter of what elements that got changed
        // curSize will equal to the index of the first element inserted because the list is 0-indexed
        mRecyclerViewGridAdapter.notifyItemRangeInserted(curSize, mMovieArrayList.size() - 1);
         /*We are destroying this loader as we do not want to restart it on resume
         * BugFix: AsyncTaskLoader restarts on resume and loads the same page again*/
        getLoaderManager().destroyLoader(MOVIE_LOADER);
    }


    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        mMovieArrayList = null;
        mRecyclerViewGridAdapter.addData(null);
        getLoaderManager().destroyLoader(MOVIE_LOADER);
    }

    public void clearData() {
        saveBundle = null;
        mMovieArrayList = null;
        mMovieArrayList = new ArrayList<>();
        mRecyclerViewGridAdapter.addData(null);
        mRecyclerViewGridAdapter = null;
        initializeAdapter();

    }
}
