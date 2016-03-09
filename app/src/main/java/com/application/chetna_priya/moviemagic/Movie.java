package com.application.chetna_priya.moviemagic;

import android.net.Uri;

/**
 * Created by chetna_priya on 3/7/2016.
 */
public class Movie
{

    private final String BASE_EXT = "http://image.tmdb.org/t/p";
    private String movie_id;
    private String poster_path;
    private String title;
    private String releaseDate;
    private String voteAverage;
    private String plotSynopsis;
    private String backdrop_path;
    private String duration;

    public Movie(String movie_id, String poster_path)
    {
        this.movie_id = movie_id;
        this.poster_path = poster_path;
    }

    public Movie(String movie_id, String poster_path, String backdrop_path, String title, String releaseDate,
                 String voteAverage, String plotSynopsis, String duration)
    {
        this.movie_id = movie_id;
        this.poster_path = poster_path;
        this.title = title;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.plotSynopsis = plotSynopsis;
        this.backdrop_path = backdrop_path;
        this.duration = duration;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public Uri getMovie_uri() {
        return Uri.parse(BASE_EXT).buildUpon()
                .appendEncodedPath(getPrefferedSize())
                .appendEncodedPath(poster_path).build();
    }

    public Uri get_movie_backdrop_path_Uri() {
        return Uri.parse(BASE_EXT).buildUpon()
                .appendEncodedPath(getPrefferedSize())
                .appendEncodedPath(backdrop_path).build();
    }

    private String getPrefferedSize() {
        //Possible Values : "w92", "w154", "w185", "w342", "w500", "w780", or "original"
        return "w185";
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public String getDuration() {
        return duration;
    }
}
