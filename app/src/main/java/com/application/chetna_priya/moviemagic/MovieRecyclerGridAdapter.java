package com.application.chetna_priya.moviemagic;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
    private final GridLayoutManager mGridLayoutManager;
    private AdapterCallback mCallBack;
    private int mPos = 0;
    private static int itemWidth = 0;
    private ArrayList<Movie> mMovieArrayList;
    private static boolean isFavoritesView;

    public MovieRecyclerGridAdapter(Context context, GridLayoutManager gridLayoutManager, Cursor cursor)
    {
        super(context, cursor);
        mContext = context;
        mCursor = cursor;
        mGridLayoutManager = gridLayoutManager;
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
        void onItemSelected(int movieId);
        void initMovieDetailFragment(int movieid);
    }

    public int getSelectedPos() {
        return mPos;
    }


    public void setSelectedPos(int mPos) {
        this.mPos = mPos;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView posterView;


        public ViewHolder(View itemView) {
            super(itemView);
            posterView = (ImageView) itemView.findViewById(R.id.img_movie_image);
            posterView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getLayoutPosition();
            mPos = pos;

            //Movie movie =  mCursor.getInt(mCursor.getColumnIndex(FavMovieContract.COLUMN_MOVIE_ID));
            //TODO Add animation on touch
            //TODO Scroll to view to the selected position
            /*if(mCursor != null && mCursor.getCount()> 0)
                mCallBack.onItemSelected(mCursor.getInt
                        (FavMovieContract.MovieEntry.COL_INDEX_MOVIE_ID));
            else {*/
                mCallBack.onItemSelected(getItem(getLayoutPosition()).getMovie_id());
           // }

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
                        if(itemWidth == 0)//We only need to calculate it once as all posters are to be of same size
                        {
                            itemWidth = viewHolder.posterView.getMeasuredWidth();
                            mGridLayoutManager.setSpanCount(getSpanCount());
                        }
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



    public int getSpanCount() {
        DisplayMetrics  displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        Log.d(LOG_TAG, "SCREEN WIDTH: " + displayWidth + " IMAGE WIDTH: " + itemWidth);
        if(itemWidth == 0)
        {
            Log.d(LOG_TAG, "Images are still not loaded");
            return Constants.DEFAULT_SPAN_COUNT;//return default span count
        }
        return (int) Math.floor(displayWidth/itemWidth);
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
    public int getItemCount()
    {
        if(mMovieArrayList != null)
            return mMovieArrayList.size();
        else
            return super.getItemCount();//Return the item count from cursor
    }


    public void setData(ArrayList<Movie> data)
    {
        mMovieArrayList = data;
        /* Inflate the detail fragment for first movie if no selction has been made for a two pane layout*/
        if(data != null)
            mCallBack.initMovieDetailFragment(data.get(0).getMovie_id());
    }
}
