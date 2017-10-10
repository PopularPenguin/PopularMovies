package com.popularpenguin.popularmovies.utils;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.popularpenguin.popularmovies.Movie;

import java.util.ArrayList;

import static com.popularpenguin.popularmovies.utils.NetworkUtils.getMovies;

public class MovieLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    private static final String TAG = MovieLoader.class.getSimpleName();

    private Context mContext;
    private boolean mPopularSelected;

    public MovieLoader(Context context, boolean popularSelected) {
        super(context);

        mContext = context;
        mPopularSelected = popularSelected;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Movie> loadInBackground() {
        return getMovies(mContext, mPopularSelected);
    }

    @Override
    public void deliverResult(ArrayList<Movie> data) {
        super.deliverResult(data);
    }
}
