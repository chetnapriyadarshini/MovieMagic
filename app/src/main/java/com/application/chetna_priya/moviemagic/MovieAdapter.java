package com.application.chetna_priya.moviemagic;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;
/**
 * Created by chetna_priya on 3/1/2016.
 */
public class MovieAdapter extends BaseAdapter{

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private Context mContext;
    private static ArrayList<Movie> mMovieArrayList;
    private static int mSelected = 0;

    public MovieAdapter(Context context) {
        mContext = context;
        mMovieArrayList = new ArrayList<>();
    }



    @Override
    public int getCount() {
        return mMovieArrayList.size();
    }

    @Override
    public Movie getItem(int position) {
        return mMovieArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SquaredImageView view = (SquaredImageView) convertView;
        if (view == null) {
            view = new SquaredImageView(mContext);
            view.setScaleType(CENTER_CROP);
        }
            final Movie movie = getItem(position);
            view.setTag(movie);
            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelected = mMovieArrayList.indexOf(movie);
                    Log.d(LOG_TAG, "SELECTED ITEM INDEXXXXXXXX: " + mSelected);
                }
            });*/

        Picasso.with(mContext) //
                .load(movie.getMovie_uri())  //
                .placeholder(R.drawable.placeholder)
                .fit() //
                .tag(view.getTag()) //
                .into(view);

        /*if(position == mMovieArrayList.indexOf(movie)) {
            Log.d(LOG_TAG, "SET IMAGE RESOURCE FOR SELECTED VIEW AT "+position);
            *//*view.setBackgroundResource(R.drawable.placeholder);*//*
            view.setBackgroundColor(mContext.getResources().getColor(R.color.dark_green));       // Define the border color
            view.setPadding(5, 5, 5, 5);
        }*/
        return view;
    }


    public void setData(ArrayList<Movie> data)
    {
        mMovieArrayList = data;
    }
}
