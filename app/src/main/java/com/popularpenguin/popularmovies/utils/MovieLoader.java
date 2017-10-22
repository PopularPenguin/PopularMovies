package com.popularpenguin.popularmovies.utils;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.popularpenguin.popularmovies.ListActivity;
import com.popularpenguin.popularmovies.data.FavoritesContract.FavoritesEntry;
import com.popularpenguin.popularmovies.data.Movie;

import java.util.ArrayList;

import static com.popularpenguin.popularmovies.utils.NetworkUtils.getMovies;

public class MovieLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    private static final String TAG = MovieLoader.class.getSimpleName();

    private Context ctx;
    private ArrayList<Movie> mMovieList;
    private boolean mPopularSelected;
    private int mMenuPosition;

    public MovieLoader(Context ctx, int position) {
        super(ctx);

        this.ctx = ctx;
        mMenuPosition = position;
        mPopularSelected = position == 0;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Movie> loadInBackground() {
        ArrayList<Movie> movies;

        switch (mMenuPosition) {
            case ListActivity.MENU_POPULAR:
            case ListActivity.MENU_RATING:
                movies = loadMoviesFromNetwork();
                break;

            case ListActivity.MENU_FAVORITE:
                movies = loadMoviesFromFavorites();
                break;

            default:
                throw new UnsupportedOperationException("Invalid menu position");
        }

        return movies;
    }

    private ArrayList<Movie> loadMoviesFromNetwork() {
        ArrayList<Movie> movies = getMovies(ctx, mPopularSelected);

        Cursor cursor = ctx.getContentResolver().query(FavoritesEntry.CONTENT_URI,
                new String[] {FavoritesEntry.COLUMN_MOVIE_ID}, null, null, null, null);

        if (!(cursor == null || cursor.getCount() == 0)) {
            int[] ids = new int[cursor.getCount()]; // the ids of all the favorite movies

            cursor.moveToFirst();

            // build the ids array
            for (int i = 0; i < cursor.getCount(); i++) {
                ids[i] = cursor.getInt(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_ID));
                cursor.moveToNext();

            }

            // match favorite ids to movie ids
            for (Movie movie : movies) {
                for (int id : ids) {
                    if (movie.getId() == id) {
                        movie.setFavorite(true);

                        break;
                    }
                }
            }
        }

        return movies;
    }

    private ArrayList<Movie> loadMoviesFromFavorites() {
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
