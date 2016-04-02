package com.application.chetna_priya.moviemagic;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chetna_priya on 3/21/2016.
 */
public class Review implements Parcelable{


    private String author;
    private String review_content;
    private String url;
    private final int num_words = 200;

    public Review(String author, String content, String url)
    {
        this.author = author;
        this.review_content = content;
        this.url = url;
    }

    protected Review(Parcel in) {
        author = in.readString();
        review_content = in.readString();
        url = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }

    public String getReview_content() {
            return review_content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(review_content);
        dest.writeString(url);
    }
}
