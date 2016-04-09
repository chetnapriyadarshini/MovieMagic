package com.application.chetna_priya.moviemagic;


import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.chetna_priya.moviemagic.TaskLoaders.FetchReviewsTaskLoader;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieReviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Review>> {

    private static final int MOVIE_REVIEW_LOADER = 0;
    private static final String REVIEW_LIST = "review_list";
    private static final String LOG_TAG = MovieReviewFragment.class.getSimpleName();

    @Bind(R.id.view_reviews)
    RecyclerView mRecyclerView;

    @Bind(R.id.tv_movie_review_not_available)
    TextView mReviewNotAvailable;

    private static ArrayList<Review> mReviewArrayList;

    public MovieReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState == null || !(savedInstanceState.containsKey(REVIEW_LIST)))
        {
            getLoaderManager().initLoader(MOVIE_REVIEW_LOADER, null, this);
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        if(savedInstanceState != null && (savedInstanceState.containsKey(REVIEW_LIST)))
        {
            mReviewArrayList = savedInstanceState.getParcelableArrayList(REVIEW_LIST);
            mRecyclerView.setAdapter(new InnerReviewsAdapter());
        }
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
         if(data == null || data.size() < 1)
             mReviewNotAvailable.setVisibility(View.VISIBLE);
         else {
             mReviewNotAvailable.setVisibility(View.GONE);
             mRecyclerView.setAdapter(new InnerReviewsAdapter());
         }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Review>> loader) {

    }

    public class InnerReviewsAdapter extends RecyclerView.Adapter<InnerReviewsAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, MyColorAnimator.AnimationEndCallback
        {
            @Bind(R.id.tv_author_name)
            TextView author_name;

            @Bind(R.id.tv_review_content)
            TextView review_content;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {

                final int colorFrom = getResources().getColor(android.support.v7.appcompat.R.color.background_material_light);
                final int colorTo = getResources().getColor(android.support.v7.appcompat.R.color.material_grey_300);
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                colorAnimation.setDuration(100); // milliseconds
                MyColorAnimator myColorAnimator = new MyColorAnimator(getActivity(), colorAnimation, itemView, this);
                colorAnimation.addUpdateListener(myColorAnimator);
                colorAnimation.addListener(myColorAnimator);
                colorAnimation.setRepeatCount(1);
                colorAnimation.setRepeatMode(ValueAnimator.REVERSE);
                colorAnimation.start();
            }

            @Override
            public void onAnimationEnd() {
                String url = mReviewArrayList.get(getLayoutPosition()).getUrl();
                Uri review_uri = Uri.parse(url).buildUpon().build();
                Intent review_intent = new Intent(Intent.ACTION_VIEW, review_uri);
                review_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                startActivity(review_intent);
            }
        }
        @Override
        public InnerReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item_view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_review, parent, false);

            return new ViewHolder(item_view);
        }

        @Override
        public void onBindViewHolder(InnerReviewsAdapter.ViewHolder holder, int position) {
            Review review = mReviewArrayList.get(position);
            holder.author_name.setText(review.getAuthor());
            holder.review_content.setText(review.getReview_content());
        }

        @Override
        public int getItemCount() {
            if(mReviewArrayList != null)
                return mReviewArrayList.size();
            return 0;
        }
    }
}
