package com.application.chetna_priya.moviemagic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.chetna_priya.moviemagic.TaskLoaders.FetchTrailersTaskLoader;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;


/**
 * Created by chetna_priya on 4/6/2016.
 */
public class MovieTrailerFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<String>>
{


    private static final String YOU_TUBE_BASE_URL = "https://www.youtube.com/watch";
    private static final String TRAILER_LIST = "trailer_list";
    private static final int MOVIE_TRAILER_LOADER = 1;
    private static final String LOG_TAG = MovieTrailerFragment.class.getSimpleName();

    private static ArrayList<String> mTrailer_keys;

    @Bind(R.id.tv_trailer_title)
    TextView mTrailerTitle;

    @Bind(R.id.tv_movie_trailer_not_available)
    TextView mTrailerNotAvailable;

    @BindString(R.string.trailer)
    String trailer;

    @Bind(R.id.view_trailer)
    RecyclerView mRecyclerView;

    private ShareActionProvider mShareActionProvider;



    public static Fragment newInstance(int movieId) {
        MovieTrailerFragment movieTrailerFragment = new MovieTrailerFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.MOVIE_ID, movieId);
        movieTrailerFragment.setArguments(args);
        return movieTrailerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_movie_trailer, container, false);
        ButterKnife.bind(this, rootView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        if(savedInstanceState != null && savedInstanceState.containsKey(TRAILER_LIST))
        {
            mTrailer_keys = savedInstanceState.getStringArrayList(TRAILER_LIST);
            mRecyclerView.setAdapter(new InnerTrailerAdapter());
        }
        return  rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null || !(savedInstanceState.containsKey(TRAILER_LIST))) {
            getLoaderManager().initLoader(MOVIE_TRAILER_LOADER, null, this);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_detail_frgament, menu);

        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList(TRAILER_LIST, mTrailer_keys);
        super.onSaveInstanceState(outState);
    }

    private int getMovieId()
    {
        return getArguments().getInt(Constants.MOVIE_ID);
    }

    @Override
    public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {
        return new FetchTrailersTaskLoader(getActivity(), getMovieId());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
        mTrailer_keys = data;
        if(data == null || data.size() < 1)// NO trailers are available so we do not inflate the layout and and set visibility of the title to gone
        {
            mTrailerTitle.setVisibility(View.GONE);
            mTrailerNotAvailable.setVisibility(View.VISIBLE);
        }else {
            mTrailerNotAvailable.setVisibility(View.GONE);
            mShareActionProvider.setShareIntent(getShareIntent(0));
            mRecyclerView.setAdapter(new InnerTrailerAdapter());
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String>> loader) {

    }

    private class InnerTrailerAdapter extends RecyclerView.Adapter<InnerTrailerAdapter.ViewHolder> {
        @Override
        public InnerTrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item_view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_trailer, parent, false);

            return new ViewHolder(item_view);
        }

        @Override
        public void onBindViewHolder(InnerTrailerAdapter.ViewHolder holder, int position) {

            holder.trailer_view.setText(trailer + " " + (position+1));
        }

        @Override
        public int getItemCount() {
            if(mTrailer_keys != null) {
                return mTrailer_keys.size();
            }
            return 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView trailer_view;

            public ViewHolder(View itemView) {
                super(itemView);
                trailer_view = (TextView) itemView.findViewById(R.id.tv_trailer);
                trailer_view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Intent you_tube_intent = new Intent(Intent.ACTION_VIEW, buildYouTubeUri(getLayoutPosition()));
                startActivity(you_tube_intent);
            }
        }
    }

    private Uri buildYouTubeUri(int position) {
        final String VIDEOS = "v";
        return Uri.parse(YOU_TUBE_BASE_URL).buildUpon()
                .appendQueryParameter(VIDEOS, mTrailer_keys.get(position))
                .build();
    }

    private Intent getShareIntent(int position) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.putExtra(Intent.EXTRA_TEXT, String.valueOf(buildYouTubeUri(position)));
        shareIntent.setType("text/plain");
        return shareIntent;
    }
}