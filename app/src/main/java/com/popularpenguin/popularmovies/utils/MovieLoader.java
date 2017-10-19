package com.popularpenguin.popularmovies.utils;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.popularpenguin.popularmovies.data.FavoritesContract.FavoritesEntry;
import com.popularpenguin.popularmovies.data.Movie;

import java.util.ArrayList;

import static com.popularpenguin.popularmovies.utils.NetworkUtils.getMovies;

public class MovieLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    private static final String TAG = MovieLoader.class.getSimpleName();

    private Context ctx;
    private boolean mPopularSelected;

    public MovieLoader(Context ctx, boolean popularSelected) {
        super(ctx);

        this.ctx = ctx;
        mPopularSelected = popularSelected;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Movie> loadInBackground() {
        ArrayList<Movie> movies = getMovies(ctx, mPopularSelected);

        Cursor cursor = ctx.getContentResolver().query(FavoritesEntry.CONTENT_URI,
                new String[] {FavoritesEntry.COLUMN_MOVIE_ID}, null, null, null, null);

        if (!(cursor == null || cursor.getCount() == 0)) {
            int[] ids = new int[cursor.getCount()]; // the ids of all the favorite movies

            cursor.moveToFirst();

            Log.d(TAG, "Cursor count = " + cursor.getCount());

            // build the ids array
            for (int i = 0; i < cursor.getCount(); i++) {
                ids[i] = cursor.getInt(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_ID));
                cursor.moveToNext();
                Log.d(TAG, "ids[" + i + "] = " + ids[i]);
            }

            // match favorite ids to movie ids
            for (Movie movie : movies) {
                for (int id : ids) {
                    if (movie.getId() == id) {
                        movie.setFavorite(true);
                        Log.d(TAG, "Favorite set for " + movie.getTitle() + "(" +
                        movie.isFavorite() + ")");
                        break;
                    }
                }
            }
        }

        return movies;
    }

    @Override
    public void deliverResult(ArrayList<Movie> data) {
        super.deliverResult(data);
    }
}
