package com.application.chetna_priya.moviemagic;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by chetna_priya on 3/18/2016.
 */
public class FetchDataOverNetwork
{
    private static final String LOG_TAG = FetchDataOverNetwork.class.getSimpleName();

    public static String fetchMovieData(Context context, Uri uri)
    {
        //TODO use retrofit lib ti fetch
        HttpURLConnection httpURLConnection;
        BufferedReader bufferedReader;

        String movieList = null;
        try {
            URL url = new URL(uri.toString());

            Log.d(LOG_TAG, url.toString());

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null)
                return null;

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0)
                return null;

            movieList = buffer.toString();
            Log.d(LOG_TAG, movieList);
            return movieList;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
