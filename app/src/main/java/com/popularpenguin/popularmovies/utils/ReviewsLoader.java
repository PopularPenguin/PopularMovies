package com.popularpenguin.popularmovies.utils;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

import static com.popularpenguin.popularmovies.utils.NetworkUtils.getReviews;

public class ReviewsLoader extends AsyncTaskLoader<ArrayList<String[]>> {

    private static final String TAG = MovieLoader.class.getSimpleName();

    private int mId;

    public ReviewsLoader(Context context, int id) {
        super(context);

        mId = id;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<String[]> loadInBackground() {
        return getReviews(getContext(), mId);
    }

    @Override
    public void deliverResult(ArrayList<String[]> data) {
        super.deliverResult(data);
    }
}
