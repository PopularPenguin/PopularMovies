package com.popularpenguin.popularmovies.utils;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.popularpenguin.popularmovies.data.Movie;

import com.popularpenguin.popularmovies.data.FavoritesContract.FavoritesEntry;

import java.util.ArrayList;

public class FavoritesLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    private static final String TAG = FavoritesLoader.class.getSimpleName();

    private Context ctx;

    public FavoritesLoader(Context ctx) {
        super(ctx);

        this.ctx = ctx;
    }

    @Override
    protected void onStartLoading() {
        // force load every time you navigate back as a movie could have been removed from this list
        forceLoad();
    }

    @Override
    public ArrayList<Movie> loadInBackground() {
        Cursor cursor  = ctx.getContentResolver().query(FavoritesEntry.CONTENT_URI,
                null, null, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            return new ArrayList<>();
        }

        ArrayList<Movie> movies = new ArrayList<>();

        cursor.moveToFirst();

        do {
            int id = cursor.getInt(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_ID));
            String title = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_TITLE));
            String posterPath = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_POSTER_PATH));
            String date = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE));
            String overview = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_OVERVIEW));
            double rating = cursor.getDouble(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_RATING));

            Movie movie = new Movie(id, title, posterPath, date, overview, rating);
            movie.setFavorite(true);
            movies.add(movie);
        } while (cursor.moveToNext());

        return movies;
    }
}
