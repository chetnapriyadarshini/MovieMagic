package com.application.chetna_priya.moviemagic;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Movie>{

    private static final String MOVIE_URI = "movie_uri";
    private static final int MOVIE_DETAIL_LOADER = 0;
    private TextView mTitleView, mRatingView, mMoviePlot, mReleaseDate, mMovieDuration;
    private ImageView mMovieImg, mMovieBackgrImg;

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_movie_detail, container, false);
        mTitleView = (TextView) rootView.findViewById(R.id.tv_movie_title);
        mRatingView = (TextView) rootView.findViewById(R.id.tv_movie_rating);
        mMoviePlot = (TextView) rootView.findViewById(R.id.tv_movie_plot);
        mReleaseDate = (TextView) rootView.findViewById(R.id.tv_movie_release_date);
        mMovieDuration = (TextView) rootView.findViewById(R.id.tv_movie_runtime);
        mMovieImg = (ImageView) rootView.findViewById(R.id.img_movie_image);
        mMovieBackgrImg = (ImageView) rootView.findViewById(R.id.img_movie_backgr);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
    }

    /*Start the new instance of detail movie with the given uri */
    public static MovieDetailFragment newInstance(Uri movie_uri)
    {
        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(MOVIE_URI, movie_uri);
        movieDetailFragment.setArguments(args);

        return movieDetailFragment;
    }

    public Uri getSelectedUri()
    {
        return getArguments().getParcelable(MOVIE_URI);
    }


    @Override
    public Loader<Movie> onCreateLoader(int id, Bundle args) {
        return new FetchMovieDetailsTaskLoader(getActivity(), getSelectedUri());
    }

    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie movie) {
        mTitleView.setText(movie.getTitle());
        mReleaseDate.setText(Utility.getFormattedDate(movie.getReleaseDate()));
        mMoviePlot.setText(movie.getPlotSynopsis());
        mRatingView.setText(Utility.getFormattedVoteAve(movie.getVoteAverage()));
        mMovieDuration.setText(movie.getDuration()+" mins");

        Picasso.with(getActivity())
                .load(movie.get_movie_backdrop_path_Uri())
                .placeholder(R.drawable.placeholder)
                .fit()
                .into(mMovieBackgrImg);
        Picasso.with(getActivity())
                .load(movie.getMovie_uri())
                .placeholder(R.drawable.placeholder)
                .fit()
                .into(mMovieImg);

    }

    @Override
    public void onLoaderReset(Loader<Movie> loader) {

    }
}
