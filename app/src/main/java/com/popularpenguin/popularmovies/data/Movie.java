package com.popularpenguin.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("unused")
public class Movie implements Parcelable {
    private int mId;
    private String mTitle;
    private String mPosterPath;
    private String mReleaseDate;
    private String mOverview;
    private double mAverage;

    // Movie trailer data for the detail activity
    private String[] mTrailerKeys;
    private String[] mTrailerNames;

    // Movie review data for the detail activity
    private String[] mReviewAuthors;
    private String[] mReviewContent;
    private String[] mReviewUrls;

    public Movie(int id, String title, String posterPath, String releaseDate,
                 String overview, double average) {

        mId = id;
        mTitle = title;
        mPosterPath = posterPath;
        mReleaseDate = releaseDate;
        mOverview = overview;
        mAverage = average;
    }

    private Movie(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mPosterPath = in.readString();
        mReleaseDate = in.readString();
        mOverview = in.readString();
        mAverage = in.readDouble();

        mTrailerKeys = in.createStringArray();
        mTrailerNames = in.createStringArray();

        mReviewAuthors = in.createStringArray();
        mReviewContent = in.createStringArray();
        mReviewUrls = in.createStringArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mReleaseDate);
        dest.writeString(mOverview);
        dest.writeDouble(mAverage);

        dest.writeStringArray(mTrailerKeys);
        dest.writeStringArray(mTrailerNames);

        dest.writeStringArray(mReviewAuthors);
        dest.writeStringArray(mReviewContent);
        dest.writeStringArray(mReviewUrls);
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
    public int getId() { return mId; }
    public void setId(int id) { mId = id; }

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

    // Trailer data
    public String[] getTrailerKeys() { return mTrailerKeys; }
    public void setTrailerKeys(String[] keys) { mTrailerKeys = keys; }

    public String[] getTrailerNames() { return mTrailerNames; }
    public void setTrailerNames(String[] names) { mTrailerNames = names; }

    // Review data
    public String[] getReviewAuthors() { return mReviewAuthors; }
    public void setReviewAuthors(String[] authors) { mReviewAuthors = authors; }

    public String[] getReviewContent() { return mReviewContent; }
    public void setReviewContent(String[] content) { mReviewContent = content; }

    public String[] getReviewUrls() { return mReviewUrls; }
    public void setReviewUrls(String[] urls) { mReviewUrls = urls; }
}
