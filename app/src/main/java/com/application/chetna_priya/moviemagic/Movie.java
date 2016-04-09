package com.application.chetna_priya.moviemagic;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chetna_priya on 3/7/2016.
 */
public class Movie implements Parcelable
{
    private int movie_id;
    private String poster_path;
    private String title;
    private String releaseDate;
    private long voteAverage;
    private String plotSynopsis;
    private String backdrop_path;
    private int duration;
    private boolean isMarkedFav = false;
    private final String BASE_EXT = "http://image.tmdb.org/t/p";

    public Movie(int movie_id, String poster_path)
    {
        this.movie_id = movie_id;
        this.poster_path = poster_path;
    }

    public Movie(int movie_id, String poster_path, String backdrop_path, String title, String releaseDate,
                 long voteAverage, String plotSynopsis, int duration)
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

    protected Movie(Parcel in) {
        movie_id = in.readInt();
        poster_path = in.readString();
        title = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readLong();
        plotSynopsis = in.readString();
        backdrop_path = in.readString();
        duration = in.readInt();
        isMarkedFav = in.readByte() != 0;

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movie_id);
        dest.writeString(poster_path);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeLong(voteAverage);
        dest.writeString(plotSynopsis);
        dest.writeString(backdrop_path);
        dest.writeInt(duration);
        dest.writeByte((byte) (isMarkedFav ? 1 : 0));
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getMovie_id() {
        return movie_id;
    }

    public Uri getMovie_uri() {
        return Uri.parse(BASE_EXT).buildUpon()
                .appendEncodedPath(getPrefferedSize(Constants.MOVIE_POSTER))
                .appendEncodedPath(poster_path).build();
    }

    public Uri get_movie_backdrop_path_Uri() {
        return Uri.parse(BASE_EXT).buildUpon()
                .appendEncodedPath(getPrefferedSize(Constants.MOVIE_BACKDROP))
                .appendEncodedPath(backdrop_path).build();
    }

    public String getPrefferedSize(int imgType) {
        //Possible Values : "w92", "w154", "w185", "w342", "w500", "w780", or "original"
        if(imgType == Constants.MOVIE_BACKDROP)
            return  "original";
        return "w"+Constants.PREFERRED_POSTER_SIZE;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public long getVoteAverage() {
        return voteAverage;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public int getDuration() {
        return duration;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public boolean isMarkedFav() {
        return isMarkedFav;
    }

    public void markFav() {
        isMarkedFav = true;
    }

    public void notFav() {
        isMarkedFav = false;
    }

  }
