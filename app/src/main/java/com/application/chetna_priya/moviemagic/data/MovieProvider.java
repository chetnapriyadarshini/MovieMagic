package com.application.chetna_priya.moviemagic.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import static com.application.chetna_priya.moviemagic.data.FavMovieContract.*;


/**
 * Created by chetna_priya on 3/27/2016.
 */
public final class MovieProvider extends ContentProvider
{
    public static final String CONTENT_AUTHORITY = "com.application.chetna_priya.moviemagic";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String LOG_TAG = MovieProvider.class.getSimpleName();
    private FavMovieDbHelper mMovieDbHelper;
    static final int MOVIE = 100;
    static final int MOVIE_BY_ID = 101;

    private static final SQLiteQueryBuilder sMovieByIdQueryBuilder;

    static {
        sMovieByIdQueryBuilder = new SQLiteQueryBuilder();
        sMovieByIdQueryBuilder.setTables(MovieEntry.TABLE_NAME);
    }

    private static final String sMovieIdSelection = MovieEntry.TABLE_NAME
            +"."+MovieEntry.COLUMN_MOVIE_ID +" =? ";

    private Cursor getMovieByMovieId(Uri uri, String[] projection,  String[] selectionArgs,String sortOrder)
    {
        String  selection = sMovieIdSelection;

        return sMovieByIdQueryBuilder.query(mMovieDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getFavMovies(String sortOrder)
    {
        return  sMovieByIdQueryBuilder.query(mMovieDbHelper.getReadableDatabase(),
                null,
                null,
                null,
                null,
                null,
                sortOrder);
    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY,FavMovieContract.PATH_MOVIE, MOVIE);
        uriMatcher.addURI(CONTENT_AUTHORITY, FavMovieContract.PATH_MOVIE+"/*", MOVIE_BY_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mMovieDbHelper = FavMovieDbHelper.getInstance(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case MOVIE:
                return MovieEntry.CONTENT_TYPE;
            case MOVIE_BY_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri);
        }
    }

    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri))
        {
            case MOVIE:
            {
                retCursor = getFavMovies(sortOrder);
                Log.d(LOG_TAG, "URI MATCHED FOR MOVIE"+retCursor);
                break;
            }

            case MOVIE_BY_ID:
            {
                retCursor = getMovieByMovieId(uri,projection, selectionArgs,sortOrder);
                Log.d(LOG_TAG, "URI MATCHED FOR MOVIE_BY_ID "+retCursor);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match)
        {
            case MOVIE:
                long _id = db.insert(MovieEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failer to insert into "+MovieEntry.TABLE_NAME);

                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsDeleted = 0;
        if(null == selection)
            selection = "1";

        switch (match)
        {
            case MOVIE_BY_ID:
                rowsDeleted = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
        }

        if(rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {

        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;

        switch (match)
        {
            case MOVIE_BY_ID:
                rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
        }

        if(rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match)
        {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try
                {
                    for(ContentValues value : values) {
                        long _id = db.insert(MovieEntry.TABLE_NAME, null, value);
                        if(_id  != -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri,values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mMovieDbHelper.close();
        super.shutdown();
    }
}
