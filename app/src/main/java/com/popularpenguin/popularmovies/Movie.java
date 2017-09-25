package com.popularpenguin.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("unused")
public class Movie implements Parcelable {
    private String mTitle;
    private String mPosterPath;
    private String mReleaseDate;
    private String mOverview;
    private double mAverage;

    public Movie(String title, String posterPath, String releaseDate,
                 String overview, double average) {

        mTitle = title;
        mPosterPath = posterPath;
        mReleaseDate = releaseDate;
        mOverview = overview;
        mAverage = average;
    }

    private Movie(Parcel in) {
        mTitle = in.readString();
        mPosterPath = in.readString();
        mReleaseDate = in.readString();
        mOverview = in.readString();
        mAverage = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mReleaseDate);
        dest.writeString(mOverview);
        dest.writeDouble(mAverage);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    // Properties
    public String getTitle() { return mTitle; }
    public void setTitle(String title) { mTitle = title; }

    public String getPosterPath() { return mPosterPath; }
    public void setPosterPath(String path) { mPosterPath = path; }

    public String getReleaseDate() { return mReleaseDate.substring(0, 4); }
    public void setReleaseDate(String date) { mReleaseDate = date; }

    public String getOverview() { return mOverview; }
    public void setOverview(String overview) { mOverview = overview; }

    public String getAverage() { return mAverage + "/10"; }
    public void setAverage(double average) { mAverage = average; }
}
