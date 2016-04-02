package com.application.chetna_priya.moviemagic;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.chetna_priya.moviemagic.TaskLoaders.FetchReviewsTaskLoader;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieReviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Review>>, View.OnClickListener {

    private static final int MOVIE_REVIEW_LOADER = 0;
    private static final String REVIEW_LIST = "review_list";
    private static final String LOG_TAG = MovieReviewFragment.class.getSimpleName();

    @Bind(R.id.linear_view_reviews)
    LinearLayout mReviewsLayout;

    @Bind(R.id.tv_movie_review_not_available)
    TextView mReviewNotAvailable;

    private static ArrayList<Review> mReviewArrayList;

    public MovieReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState == null || !(savedInstanceState.containsKey(REVIEW_LIST)))
        {
            getLoaderManager().initLoader(MOVIE_REVIEW_LOADER, null, this);
        }else{
            mReviewArrayList = savedInstanceState.getParcelableArrayList(REVIEW_LIST);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(REVIEW_LIST, mReviewArrayList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_movie_review, container, false);
        ButterKnife.bind(this, rootView);
        return  rootView;
    }


    /*Start the new instance of detail movie with the given uri */
    public static MovieReviewFragment newInstance(int movieId)
    {
        MovieReviewFragment movieReviewFragment = new MovieReviewFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.MOVIE_ID, movieId);
        movieReviewFragment.setArguments(args);
        return movieReviewFragment;
    }

    private int getMovieId()
    {
        return getArguments().getInt(Constants.MOVIE_ID);
    }

    @Override
    public Loader<ArrayList<Review>> onCreateLoader(int id, Bundle args) {
        return new FetchReviewsTaskLoader(getActivity(), getMovieId());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Review>> loader, ArrayList<Review> data) {
         mReviewArrayList = data;
         inflateReviewLayout();
    }

    private void inflateReviewLayout() {
        if(mReviewArrayList.size() == 0) {
            mReviewNotAvailable.setVisibility(View.VISIBLE);
            return;
        }
        if(mReviewsLayout.getChildCount() != 0)//Layout is already inflated
        {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for(int i = 0; i< mReviewArrayList.size();i++) {
            View reviewLayout = inflater.inflate(R.layout.list_item_review, null);
            ViewHolder holder = new ViewHolder(reviewLayout);
            Review review = mReviewArrayList.get(i);
            holder.author_name.setText(review.getAuthor());
            holder.review_content.setText(review.getReview_content());
            reviewLayout.setOnClickListener(this);
            mReviewsLayout.addView(reviewLayout,i);
        }
    }

    @Override
    public void onClick(View view) {
        //TODO add touch feedback
        String url =mReviewArrayList.get(mReviewsLayout.indexOfChild(view)).getUrl();
        Uri review_uri = Uri.parse(url).buildUpon().build();
        Intent you_tube_intent = new Intent(Intent.ACTION_VIEW,review_uri);
        startActivity(you_tube_intent);
    }

    static class ViewHolder
    {
        @Bind(R.id.tv_author_name)
        TextView author_name;

        @Bind(R.id.tv_review_content)
        TextView review_content;


        ViewHolder(View view)
        {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Review>> loader) {

    }

}
