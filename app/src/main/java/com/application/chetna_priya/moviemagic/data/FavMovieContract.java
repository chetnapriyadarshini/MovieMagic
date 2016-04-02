package com.application.chetna_priya.moviemagic.data;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by chetna_priya on 3/27/2016.
 */
public class FavMovieContract {

    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = MovieProvider.BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+MovieProvider.CONTENT_AUTHORITY+"/"+PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+MovieProvider.CONTENT_AUTHORITY+"/"+PATH_MOVIE;
        public static final String TABLE_NAME = "favMovies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_POSTER = "movie_poster";
        public static final String COLUMN_MOVIE_BACKDROP = "movie_backdrop";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movie_release_date";
        public static final String COLUMN_MOVIE_RUNTIME = "movie_runtime";
        public static final String COLUMN_MOVIE_RATING = "movie_rating";
        public static final String COLUMN_MOVIE_PLOT = "movie_plot";
        public static final String COLUMN_MOVIE_POSTER_PATH = "movie_poster_path";
        public static final String COLUMN_MOVIE_BACKDROP_PATH = "movie_backdrop_path";


        public static final int COL_INDEX_MOVIE_ID = 1;
        public static final int COL_INDEX_MOVIE_TITLE = 2;
        public static final int COL_INDEX_MOVIE_POSTER = 3;
        public static final int COL_INDEX_MOVIE_BACKDROP = 4;
        public static final int COL_INDEX_MOVIE_RELEASE_DATE = 5;
        public static final int COL_INDEX_MOVIE_RUNTIME = 6;
        public static final int COL_INDEX_MOVIE_RATING = 7;
        public static final int COL_INDEX_MOVIE_PLOT = 8;
        public static final int COL_INDEX_MOVIE_POSTER_PATH = 9;
        public static final int COL_INDEX_BACKDROP_PATH = 10;

    //    public static final String COLUMN_MOVIE_GENRE = "movie_genre";

        public static Uri buildMovieUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
