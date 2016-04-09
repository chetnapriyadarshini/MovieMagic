package com.application.chetna_priya.moviemagic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.application.chetna_priya.moviemagic.TaskLoaders.FetchMovieDetailsTaskLoader;
import com.application.chetna_priya.moviemagic.TaskLoaders.FetchTrailersTaskLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

import static com.application.chetna_priya.moviemagic.data.FavMovieContract.*;

public class MovieDetailFragment extends Fragment{

    private static final int MOVIE_DETAIL_LOADER = 0;
    private static final String MOVIE_OBJ = "movie_obj";
    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private static final String DFTAG = "dialog_frgament_tag";
    private static final String TRAILER_TAG = "trailer_tag";
    private static final String REVIEWS_TAG = "reviews_tag";

    private static Movie movieObj;

    @Bind(R.id.tv_movie_title)
    TextView mTitleView;

    @Bind(R.id.tv_movie_rating)
    TextView mRatingView;

    @Bind(R.id.tv_movie_plot)
    TextView mMoviePlot;

    @Bind(R.id.tv_movie_release_date)
    TextView mReleaseDate;

    @Bind(R.id.tv_movie_runtime)
    TextView mMovieDuration;

    @Bind(R.id.tv_markAsFav)
    FloatingActionButton markAsFav;

    @Bind(R.id.img_movie_backgr)
    ImageView mMovieBackgrImg;


    private CallBack mCallBack;


    private static Bitmap mMovieBackgrBitmap;

    private View rootView;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*
        * A public interface should be implemented by the
        * activity that hosts this fragment and will be
        * used to communicate with the activity
        * */
    public interface CallBack {
        void setActionBarTitle(String movieTitle);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallBack = (CallBack) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement setActionBarTitle");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBack = null;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);
        markAsFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        markAsFav.setVisibility(View.GONE);//We set the favorites to be visible once all the images are loaded
        return rootView;
    }

    public void makeText(int stringRes) {
        Snackbar.make(rootView, stringRes, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


    private void showDialog()
    {
        FragmentTransaction fragmentTransaction =
                getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(DFTAG);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }

        fragmentTransaction.addToBackStack(null);

        // Create and show the dialog.
        Favorite_dialog_fragment newFragment = Favorite_dialog_fragment.
                newInstance(movieObj, mMovieBackgrBitmap, Constants.DIALOG_DATABASE);
        newFragment.show(fragmentTransaction, DFTAG);
    }
    private int getMovieId() {
        return getArguments().getInt(Constants.MOVIE_ID);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

            if(getActivity().getSupportFragmentManager().findFragmentByTag(REVIEWS_TAG) == null)
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.review_fragment,
                            MovieReviewFragment.newInstance(getMovieId()), REVIEWS_TAG)
                    .commit();

            if(getActivity().getSupportFragmentManager().findFragmentByTag(TRAILER_TAG) == null)
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.trailer_fragment, MovieTrailerFragment.newInstance(getMovieId()), TRAILER_TAG)
                        .commit();

        if (savedInstanceState == null || !(savedInstanceState.containsKey(MOVIE_OBJ))) {
            getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, new CallMovieDetailLoader());
            Log.d(LOG_TAG, "SAVED INSTANCE STATE NULL INIT LOADER");
        } else {
            movieObj = savedInstanceState.getParcelable(MOVIE_OBJ);
            populateFields(movieObj);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_OBJ, movieObj);
    }

    /*Start the new instance of detail movie with the given id */
    public static MovieDetailFragment newInstance(int movieId) {
        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.MOVIE_ID, movieId);
        movieDetailFragment.setArguments(args);

        return movieDetailFragment;
    }

    private class CallMovieDetailLoader implements LoaderManager.LoaderCallbacks<Movie> {
        @Override
        public Loader<Movie> onCreateLoader(int id, Bundle args) {
            return new FetchMovieDetailsTaskLoader(getActivity(),
                    getMovieId());
        }

        @Override
        public void onLoadFinished(Loader<Movie> loader, Movie movie) {
            movieObj = movie;
            populateFields(movieObj);
        }

        @Override
        public void onLoaderReset(Loader<Movie> loader) {

        }
    }

    private void populateFields(final Movie movie) {
        final Cursor cursor = Utility.queryByMovieId(getActivity(), movie.getMovie_id());
        if (cursor != null && cursor.getCount() > 0) {
            //If the movie exists in the fav database add it to list, we use it later
            movie.markFav();
        } else
            movie.notFav();
        setFavoriteResource(movie.isMarkedFav());
        mTitleView.setText(movie.getTitle());
        mReleaseDate.setText(Utility.getFormattedDate(movie.getReleaseDate()));
        mMoviePlot.setText(movie.getPlotSynopsis());
        mRatingView.setText(Utility.getFormattedVoteAve(String.valueOf((movie.getVoteAverage()))));
        mMovieDuration.setText(Utility.getFormattedDuration(movie.getDuration()));

        Picasso.with(getActivity())
                .load(movie.get_movie_backdrop_path_Uri())
                .placeholder(R.drawable.placeholder)
                .fit()
                .into(mMovieBackgrImg, new Callback() {
                    @Override
                    public void onSuccess() {
                        mMovieBackgrBitmap = ((BitmapDrawable) mMovieBackgrImg.getDrawable()).getBitmap();
                        markAsFav.setVisibility(View.VISIBLE);//Image loaded set visibility
                    }

                    @Override
                    public void onError() {
                        Log.e(LOG_TAG, "Movie background loading failed");
                        if (cursor != null && cursor.getCount() > 0)//movie exists in the database we inflate it from there
                        {
                            mMovieBackgrImg.setImageBitmap(Utility.getMoviePosterById(
                                    MovieEntry.COL_INDEX_MOVIE_BACKDROP,
                                    cursor));
                            markAsFav.setVisibility(View.VISIBLE);//Image loaded set visibility
                        }
                    }
                });
        mCallBack.setActionBarTitle(movie.getTitle());
        try {
            cursor.close();
        }catch (Exception e){}
    }

    private void setFavoriteResource(boolean isFav) {
        if (isFav)
            markAsFav.setImageResource(R.drawable.heart_filled_red);
        else
            markAsFav.setImageResource(R.drawable.heart_outline_red);
    }

    public void setMovieAsFav(boolean isFav) {
        if(isFav)
            movieObj.markFav();
        else
            movieObj.notFav();
        setFavoriteResource(isFav);
    }
}