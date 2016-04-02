package com.application.chetna_priya.moviemagic;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.chetna_priya.moviemagic.TaskLoaders.FetchMovieDetailsTaskLoader;
import com.application.chetna_priya.moviemagic.TaskLoaders.FetchTrailersTaskLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

import static com.application.chetna_priya.moviemagic.data.FavMovieContract.*;

public class MovieDetailFragment extends Fragment implements View.OnClickListener, DialogInterface.OnDismissListener {

    private static final int MOVIE_DETAIL_LOADER = 0;
    private static final int MOVIE_TRAILER_LOADER = 1;
    private static final String MOVIE_OBJ = "movie_obj";
    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private static final String YOU_TUBE_BASE_URL = "https://www.youtube.com/watch";
    private static final String TRAILER_LIST = "trailer_list";
    private static final String DFTAG = "dialog_frgament_tag";

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

    @Bind(R.id.tv_trailer_title)
    TextView mTrailerTitle;

    @Bind(R.id.tv_movie_trailer_not_available)
    TextView mTrailerNotAvailable;

    @Bind(R.id.tv_markAsFav)
    FloatingActionButton markAsFav;

    @Bind(R.id.linear_view_trailer)
    LinearLayout mTrailerLayout;
/*
    @Bind(R.id.img_movie_image)
    ImageView mMovieImg;*/

    @Bind(R.id.img_movie_backgr)
    ImageView mMovieBackgrImg;

    @BindString(R.string.trailer)
    String trailer;

    private CallBack mCallBack;

    private static ArrayList<String> mTrailer_keys;

    private static Bitmap mMovieBackgrBitmap;
    private static Bitmap mMoviePosterBitmap;

    public MovieDetailFragment() {
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        insertInDb();
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
                    + " must implement OnItemSelectedListener");
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
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);
        markAsFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                /*if (movieObj.isMarkedFav()) {

                    //makeText(R.string.movie_already_marked_as_fav);
                }else {
                    //insertInDb();
                }*/

            }
        });
        markAsFav.setVisibility(View.GONE);//We set the favorites to be visible once all the images are loaded
        return rootView;
    }

    private void showDialog() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(DFTAG);
        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = Favorite_dialog_fragment.newInstance(movieObj.isMarkedFav(), movieObj.getTitle());
        newFragment.show(ft, DFTAG);
        newFragment.getDialog().setOnDismissListener(this);
    }

    private int getMovieId() {
        return getArguments().getInt(Constants.MOVIE_ID);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null || !(savedInstanceState.containsKey(MOVIE_OBJ))) {
            getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, new CallMovieDetailLoader());
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.review_fragment,
                            MovieReviewFragment.newInstance(getMovieId()))
                    .commit();
            Log.d(LOG_TAG, "SAVED INSTANCE STATE NULL INIT LOADER");
        } else {
            movieObj = savedInstanceState.getParcelable(MOVIE_OBJ);
            populateFields(movieObj);
            Log.d(LOG_TAG, "Restore movie from savedInstancestate " + movieObj);
        }
        if (savedInstanceState == null || !(savedInstanceState.containsKey(TRAILER_LIST))) {
            getLoaderManager().initLoader(MOVIE_TRAILER_LOADER, null, new CallMovieTrailerLoader());
        } else {
            mTrailer_keys = savedInstanceState.getStringArrayList(TRAILER_LIST);
        }
    }


    @Override
    public void onClick(View view) {
        final String VIDEOS = "v";
        Uri you_tube_uri = Uri.parse(YOU_TUBE_BASE_URL).buildUpon()
                .appendQueryParameter(VIDEOS, mTrailer_keys.get(mTrailerLayout.indexOfChild(view)))
                .build();
        Intent you_tube_intent = new Intent(Intent.ACTION_VIEW, you_tube_uri);
        startActivity(you_tube_intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_OBJ, movieObj);
        outState.putStringArrayList(TRAILER_LIST, mTrailer_keys);
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

    private class CallMovieTrailerLoader implements LoaderManager.LoaderCallbacks<ArrayList<String>> {
        @Override
        public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {
            return new FetchTrailersTaskLoader(getActivity(), getMovieId());
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
            mTrailer_keys = data;
            if (data != null)
                inflateTrailerLayout();
            else// NO trailers are available so we do not inflate the layout and and set visibility of the title to gone
                mTrailerTitle.setVisibility(View.GONE);

        }

        @Override
        public void onLoaderReset(Loader<ArrayList<String>> loader) {

        }
    }

    private void inflateTrailerLayout() {
        if (mTrailer_keys.size() == 0)//Handle scenario when no trailer links are returned from movie db api
        {
            mTrailerNotAvailable.setVisibility(View.VISIBLE);
            return;
        }
        if (mTrailerLayout.getChildCount() != 0)//Layout is already inflated
            return;
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < mTrailer_keys.size(); i++) {
            View trailerView = inflater.inflate(R.layout.list_item_trailer, null);
            TextView trailerText = (TextView) trailerView.findViewById(R.id.tv_trailer);
            trailerText.setOnClickListener(this);
            trailerText.setText(trailer + " " + (i + 1));
            mTrailerLayout.addView(trailerView, i);
        }
    }


    private void populateFields(final Movie movie) {
        if (movieObj.isMarkedFav())
            markAsFav.setImageResource(R.drawable.heart_filled_red);
        else
            markAsFav.setImageResource(R.drawable.heart_outline_red);
        final Cursor cursor = Utility.queryByMovieId(getActivity(), movie.getMovie_id());
        if (cursor != null && cursor.getCount() > 0) {
            //If the movie exists in the fav database add it to list, we use it later
            movieObj.markFav();
        } else
            movieObj.notFav();
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
                        markAsFav.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        Log.e(LOG_TAG, "Movie background loading failed");
                        if (cursor != null && cursor.getCount() > 0)//movie exists in the database we inflate it from there
                        {
                            mMovieBackgrImg.setImageBitmap(Utility.getMoviePosterById(
                                    MovieEntry.COL_INDEX_MOVIE_BACKDROP,
                                    cursor));
                        }
                    }
                });
        final ImageView imgview = new ImageView(getActivity());
        Picasso.with(getActivity())
                .load(movie.getMovie_uri())
                .placeholder(R.drawable.placeholder)
                .fit()
                .into(imgview, new Callback() {
                    @Override
                    public void onSuccess() {
                        mMoviePosterBitmap = ((BitmapDrawable) imgview.getDrawable()).getBitmap();
                    }

                    @Override
                    public void onError() {
                        Log.e(LOG_TAG, "Movie poster loading failed");

                    }
                });
        mCallBack.setActionBarTitle(movie.getTitle());
        cursor.close();
    }


    private void insertInDb() {

        try {
            ContentValues values = new ContentValues();
            values.put(MovieEntry.COLUMN_MOVIE_ID, movieObj.getMovie_id());
            values.put(MovieEntry.COLUMN_MOVIE_TITLE, movieObj.getTitle());
            values.put(MovieEntry.COLUMN_MOVIE_POSTER, Utility.getBytesFromBitmap(mMoviePosterBitmap));
            values.put(MovieEntry.COLUMN_MOVIE_BACKDROP, Utility.getBytesFromBitmap(mMovieBackgrBitmap));
            values.put(MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movieObj.getReleaseDate());
            values.put(MovieEntry.COLUMN_MOVIE_RUNTIME, movieObj.getDuration());
            values.put(MovieEntry.COLUMN_MOVIE_RATING, movieObj.getVoteAverage());
            values.put(MovieEntry.COLUMN_MOVIE_PLOT, movieObj.getPlotSynopsis());
            values.put(MovieEntry.COLUMN_MOVIE_POSTER_PATH, movieObj.getPoster_path());
            values.put(MovieEntry.COLUMN_MOVIE_BACKDROP_PATH, movieObj.getBackdrop_path());

            Uri insertUri = getActivity().getContentResolver().insert(MovieEntry.CONTENT_URI, values);
            Log.d(LOG_TAG, "INSERTDDDDDD URI IDDDD:  " + ContentUris.parseId(insertUri));
            makeText(R.string.added_to_fav_succesfully);

            movieObj.markFav();
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
            e.printStackTrace();
            makeText(R.string.movie_could_not_added);
        }
    }


    private void makeText(int stringRes) {
        Toast.makeText(getActivity(), stringRes, Toast.LENGTH_LONG).show();
    }
}