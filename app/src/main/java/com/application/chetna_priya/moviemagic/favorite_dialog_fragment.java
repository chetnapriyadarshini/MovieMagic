package com.application.chetna_priya.moviemagic;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.application.chetna_priya.moviemagic.data.FavMovieContract;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Favorite_dialog_fragment extends DialogFragment implements View.OnClickListener {

    private static final String MOVIE = "movie_obj";
    private static final String LOG_TAG = Favorite_dialog_fragment.class.getSimpleName();
    private static final String DIALOG_TYPE = "dialog_type";

    @Bind(R.id.tv_dialog_title)
    TextView mDialogTitleView;

    @Bind(R.id.tv_dialog_content)
    TextView mDialogContent;

    @Bind(R.id.tv_dialog_yes)
    TextView mYesView;

    @Bind(R.id.tv_dialog_back)
    TextView mBackView;

    private static Movie movieObj;
    private static final String BACKGR_BITMAP = "backgr_bitmap";
    private static byte[] mPosterBitmapByteArr;
    private DialogInterface mCallBack;
    public static final int DIALOG_NO_INTERNET = 0;
    public static final int DIALOG_DATABASE = 1;
    public static final int DIALOG_DATABSE_EMPTY = 2;


    static Favorite_dialog_fragment newInstance(Movie movie, Bitmap backgrBitmap, int dialog_type) {
        Favorite_dialog_fragment f = new Favorite_dialog_fragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putParcelable(MOVIE, movie);
        args.putParcelable(BACKGR_BITMAP, backgrBitmap);
        args.putInt(DIALOG_TYPE, dialog_type);
        f.setArguments(args);

        return f;
    }

    static Favorite_dialog_fragment newInstance(int dialog_type) {
        Favorite_dialog_fragment f = new Favorite_dialog_fragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt(DIALOG_TYPE, dialog_type);
        f.setArguments(args);

        return f;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallBack = (DialogInterface) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement setMovieAsFav");
        }
    }

    public interface DialogInterface
    {
        void setMovieAsFav(boolean isFav);
        void makeText(int stringRes);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieObj = getArguments().getParcelable(MOVIE);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorite_dialog, container, false);
        ButterKnife.bind(this, rootView);

        switch (getDialogType())
        {
            case DIALOG_NO_INTERNET:
                setCancelable(false);
                mYesView.setText(R.string.ok);
                mBackView.setVisibility(View.GONE);
                mDialogTitleView.setVisibility(View.GONE);
                mDialogContent.setText(R.string.not_connected);
                mYesView.setOnClickListener(this);
                break;

            case DIALOG_DATABASE:
                mDialogTitleView.setText(movieObj.getTitle());
                if (movieObj.isMarkedFav())
                    mDialogContent.setText(R.string.remove_from_fav);
                else
                    mDialogContent.setText(R.string.add_to_fav);
                mYesView.setOnClickListener(this);
                mBackView.setOnClickListener(this);
                if(!movieObj.isMarkedFav())
                    downloadMoviePoster();//keep the poster downloaded and ready in case user wants to save the mov in favorites
                break;

            case DIALOG_DATABSE_EMPTY:
                setCancelable(false);
                mDialogTitleView.setVisibility(View.GONE);
                mDialogContent.setText(R.string.database_empty);
                mBackView.setVisibility(View.GONE);
                mYesView.setOnClickListener(this);
                break;
        }
        return rootView;
    }


    @Override
    public void onDetach() {
        mPosterBitmapByteArr = null;
        mCallBack = null;
        super.onDetach();
    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tv_dialog_yes:
                switch (getDialogType())
                {
                    case DIALOG_NO_INTERNET:
                        dismiss();
                        getActivity().finish();
                        System.exit(0);
                        break;

                    case DIALOG_DATABASE:
                        if(!movieObj.isMarkedFav()) {
                            mCallBack.setMovieAsFav(true);
                            insertInDb();
                        }else {
                            mCallBack.setMovieAsFav(false);
                            removeFromDb();
                        }
                        break;
                    case DIALOG_DATABSE_EMPTY:
                        dismiss();
                        Intent settingsIntent = new Intent(getActivity(), SettingsAcitvity.class);
                        startActivity(settingsIntent);
                        break;
                }
                break;

            case R.id.tv_dialog_back:
                dismiss();
                break;
        }
    }

    private int getDialogType() {
        return getArguments().getInt(DIALOG_TYPE);
    }

    private Bitmap getBackgrBitmap() {
        return ((Bitmap)getArguments().getParcelable(BACKGR_BITMAP));
    }

    private void removeFromDb() {

        String[] selectionArgs = new String[]{String.valueOf(movieObj.getMovie_id())};
        String condition = FavMovieContract.MovieEntry.COLUMN_MOVIE_ID+" =?";
        Uri movie_by_id_uri = FavMovieContract.MovieEntry.CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(movieObj.getMovie_id())).build();

        int deletedRows = getActivity().getContentResolver().delete(movie_by_id_uri,condition, selectionArgs);
        Log.d(LOG_TAG, "ROW DELETED SUCCESSFULLY " + deletedRows);
        if(deletedRows != 0)
            movieObj.notFav();
        mCallBack.makeText(R.string.movie_removed_successfully);
        dismiss();
    }


    private void downloadMoviePoster() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(String.valueOf(movieObj.getMovie_uri()))
                .build();

        client.newCall(request).enqueue(new Callback() {


            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                InputStream input = response.body().byteStream();
                byte[] buffer = new byte[8192];
                int bytesRead;
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                mPosterBitmapByteArr = output.toByteArray();
            }
        });
    }


    private void insertInDb()
    {
        if(mPosterBitmapByteArr == null) {//The poster could not be downloaded due to some error
            mCallBack.makeText(R.string.movie_could_not_added);
            dismiss();
            return;
        }
        try{
            ContentValues values = new ContentValues();
            values.put(FavMovieContract.MovieEntry.COLUMN_MOVIE_ID, movieObj.getMovie_id());
            values.put(FavMovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movieObj.getTitle());

            values.put(FavMovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP,
                    Utility.getBytesFromBitmap(getBackgrBitmap()));
            values.put(FavMovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
                    mPosterBitmapByteArr);
            values.put(FavMovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movieObj.getReleaseDate());
            values.put(FavMovieContract.MovieEntry.COLUMN_MOVIE_RUNTIME, movieObj.getDuration());
            values.put(FavMovieContract.MovieEntry.COLUMN_MOVIE_RATING, movieObj.getVoteAverage());
            values.put(FavMovieContract.MovieEntry.COLUMN_MOVIE_PLOT, movieObj.getPlotSynopsis());
            values.put(FavMovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, movieObj.getPoster_path());
            values.put(FavMovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH, movieObj.getBackdrop_path());

            Uri insertUri = getActivity().getContentResolver().insert(FavMovieContract.MovieEntry.CONTENT_URI, values);
            Log.d(LOG_TAG, "INSERTDDDDDD URI IDDDD:  " + ContentUris.parseId(insertUri));
            mCallBack.makeText(R.string.added_to_fav_succesfully);

            movieObj.markFav();
        } catch (Exception e) {
            e.printStackTrace();
            mCallBack.makeText(R.string.movie_could_not_added);
        }finally {
            dismiss();
        }
    }

}
