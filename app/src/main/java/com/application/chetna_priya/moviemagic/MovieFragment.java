package com.application.chetna_priya.moviemagic;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<ArrayList<Movie>>, AdapterView.OnItemClickListener {

    private static final String LOG_TAG = MovieFragment.class.getSimpleName();
    private static final int MOVIE_LOADER = 0;
    private static final String POSITION = "selected_position";
    private GridView mGridView;
    private CallBack mCallBack;
    private MovieAdapter mGridViewAdapter;
    private int mPos = 0;

    private final static String MOVIE_DETAIL_BASE_URL ="http://api.themoviedb.org/3/movie";
    private final static String APP_ID_PARAM = "api_key";

    public MovieFragment() {
    }

    /*
    * A public interface should be implemented by the
    * activity that hosts this fragment and will be
    * used to communicate with the activity
    * */
    public interface CallBack
    {
        void onItemSelected(Uri movieUri);
        void initMovieDetailFragment(Uri movieUri);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallBack = (CallBack) getActivity();
        }catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnItemSelectedListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_fragment, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.grid_movies);
        mGridViewAdapter = new MovieAdapter(getActivity());
        mGridView.setOnItemClickListener(this);
        if(savedInstanceState != null && savedInstanceState.containsKey(POSITION))
        {
            /*Get the selected position from bundle */
            mPos = savedInstanceState.getInt(POSITION);
        }
        mGridView.smoothScrollToPosition(mPos);//scroll to selected pos
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPos != GridView.INVALID_POSITION)
        {
            /* Save the selected position in bundle
            * will be used later to scroll to the
            * given pos so that the view remains in focus*/
            outState.putInt(POSITION, mPos);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPos = position;
        Movie movie = (Movie) parent.getItemAtPosition(position);
        mGridView.setSelection(mPos);
        mCallBack.onItemSelected(buildUriWithMovieId(movie.getMovie_id()));
        view.setBackgroundColor(getActivity().getResources().getColor(R.color.dark_green));
        int pad = (int) getActivity().getResources().getDimension(R.dimen.image_view_padding);
        view.setPadding(pad, pad, pad, pad);
    }




    private Uri buildUriWithMovieId(String movieId) {
        return Uri.parse(MOVIE_DETAIL_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendQueryParameter(APP_ID_PARAM, BuildConfig.MOVIE_API_KEY)
                .build();
    }


    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        return new FetchMoviesTaskLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        mGridView.setAdapter(mGridViewAdapter);
        mGridViewAdapter.setData(data);
        /* Inflate the detail fragment for first movie if no selction has been made*/
        mCallBack.initMovieDetailFragment(buildUriWithMovieId(data.get(0).getMovie_id()));

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        mGridViewAdapter.setData(null);
    }

}
