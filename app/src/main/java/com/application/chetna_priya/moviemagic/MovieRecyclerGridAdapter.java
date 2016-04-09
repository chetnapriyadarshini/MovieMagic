package com.application.chetna_priya.moviemagic;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.application.chetna_priya.moviemagic.data.FavMovieContract;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

/**
 * Created by chetna_priya on 3/24/2016.
 */
public class MovieRecyclerGridAdapter extends CursorRecyclerViewAdapter<MovieRecyclerGridAdapter.ViewHolder> {

    private static final String LOG_TAG = MovieRecyclerGridAdapter.class.getSimpleName();
    private static Context mContext;
    private Cursor mCursor;
    private AdapterCallback mCallBack;
    private ArrayList<Movie> mMovieArrayList;
    private static boolean isFavoritesView;

    public MovieRecyclerGridAdapter(Context context, Cursor cursor)
    {
        super(context, cursor);
        mContext = context;
        mCursor = cursor;
        mMovieArrayList = new ArrayList<>();
        isFavoritesView = Utility.getSortingChoice(mContext).equals(mContext.getResources().getString(R.string.pref_favorites));
        try {
            mCallBack = (AdapterCallback) mContext;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(mContext.toString()
                    + " must implement OnItemSelectedListener");
        }
    }

    /*
    * A public interface should be implemented by the
    * activity that hosts this fragment corresponding to this adapter and will be
    * used to communicate with the activity
    * */
    public interface AdapterCallback
    {
        void onItemSelected(ImageView posterView, int movieId);
        void initMovieDetailFragment(int movieid);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, MyColorAnimator.AnimationEndCallback {

        public ImageView posterView;


        public ViewHolder(View itemView) {
            super(itemView);
            posterView = (ImageView) itemView.findViewById(R.id.img_movie_image);
            posterView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View view) {
            final int colorFrom = mContext.getResources().getColor(android.support.v7.appcompat.R.color.background_material_dark);
            final int colorTo = mContext.getColor(android.support.v7.appcompat.R.color.background_material_light);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(100); // milliseconds
            MyColorAnimator myColorAnimator = new MyColorAnimator(mContext, colorAnimation, itemView, this);
            colorAnimation.addUpdateListener(myColorAnimator);
            colorAnimation.addListener(myColorAnimator);
            colorAnimation.start();

        }

        @Override
        public void onAnimationEnd() {
            mCallBack.onItemSelected(posterView,getItem(getLayoutPosition()).getMovie_id());
        }
    }


    @Override
    public MovieRecyclerGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        View item_view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_movie, parent, false);

        return new ViewHolder(item_view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        viewHolder.posterView.setScaleType(CENTER_CROP);
        final Movie movie = getItem(position);

        Picasso.with(mContext) //
                .load(movie.getMovie_uri())  //
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .fit() /*
                .tag(view.getTag()) */
                .into(viewHolder.posterView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        Log.d(LOG_TAG, "Could not load movie from databse, will try loading from cursor");
                        if(isFavoritesView)//movie exists in the database we inflate it from there
                        {
                            viewHolder.posterView.setImageBitmap(Utility.getMoviePosterById(
                                    FavMovieContract.MovieEntry.COL_INDEX_MOVIE_POSTER, mCursor));
                        }
                    }
                });
    }

    public Movie getItem(int position)
    {
        if(mMovieArrayList != null)
            return mMovieArrayList.get(position);
        else if(isFavoritesView && mCursor.moveToPosition(position))
        {
            //This is a favMoviesSelection, fetch from database
            int id =  mCursor.getInt(FavMovieContract.MovieEntry.COL_INDEX_MOVIE_ID);
            String path = mCursor.getString(FavMovieContract.MovieEntry.COL_INDEX_MOVIE_POSTER_PATH);
            return new Movie(id, path);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if(mMovieArrayList != null)
            return mMovieArrayList.get(position).getMovie_id();
        return super.getItemId(position);
    }

    @Override
    public int getItemCount()
    {
        if(mMovieArrayList != null)
            return mMovieArrayList.size();
        else
            return super.getItemCount();//Return the item count from cursor
    }


    public void addData(ArrayList<Movie> data)
    {
        if(data == null)
            mMovieArrayList = null;
        else if(data.size() > 0) {
            mMovieArrayList.addAll(data);
        /* Inflate the detail fragment for first movie if no selction has been made for a two pane layout*/
            mCallBack.initMovieDetailFragment(data.get(0).getMovie_id());
        }
    }
}
