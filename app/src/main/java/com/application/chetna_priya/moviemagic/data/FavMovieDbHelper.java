package com.application.chetna_priya.moviemagic.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.application.chetna_priya.moviemagic.data.FavMovieContract.MovieEntry;
/**
 * Created by chetna_priya on 3/27/2016.
 */
public class FavMovieDbHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 1;
    public static FavMovieDbHelper sInstance;
    static final String DATABASE_NAME = "movie.db";
    final String LOG_TAG = FavMovieDbHelper.class.getSimpleName();

    public static synchronized FavMovieDbHelper getInstance(Context context)
    {
        if(sInstance == null)
            sInstance = new FavMovieDbHelper(context.getApplicationContext());
        return sInstance;
    }

    public FavMovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_FAV_MOVIE_TABLE = "CREATE TABLE "+MovieEntry.TABLE_NAME
                +" (" +MovieEntry._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                MovieEntry.COLUMN_MOVIE_ID +" INTEGER NOT NULL UNIQUE, "+
                MovieEntry.COLUMN_MOVIE_TITLE+" TEXT NOT NULL UNIQUE, "+
                MovieEntry.COLUMN_MOVIE_POSTER+" BLOB NOT NULL, "+
                MovieEntry.COLUMN_MOVIE_BACKDROP+" BLOB NOT NULL, "+
                MovieEntry.COLUMN_MOVIE_RELEASE_DATE+" TEXT NOT NULL, "+
                MovieEntry.COLUMN_MOVIE_RUNTIME+" TEXT NOT NULL, "+
                MovieEntry.COLUMN_MOVIE_RATING+" REAL NOT NULL, "+
                MovieEntry.COLUMN_MOVIE_PLOT+" TEXT NOT NULL, "+
                MovieEntry.COLUMN_MOVIE_POSTER_PATH+" TEXT NOT NULL, "+
                MovieEntry.COLUMN_MOVIE_BACKDROP_PATH+" TEXT NOT NULL"+" ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_FAV_MOVIE_TABLE);
        Log.d(LOG_TAG, SQL_CREATE_FAV_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
