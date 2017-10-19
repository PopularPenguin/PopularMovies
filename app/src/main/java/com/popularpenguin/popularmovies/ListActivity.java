package com.popularpenguin.popularmovies;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.widget.Toast;

import com.popularpenguin.popularmovies.data.Movie;
import com.popularpenguin.popularmovies.utils.FavoritesLoader;
import com.popularpenguin.popularmovies.utils.MovieAdapter;
import com.popularpenguin.popularmovies.utils.MovieLoader;
import com.popularpenguin.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;

@SuppressWarnings("FieldCanBeLocal")
public class ListActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private static final String TAG = ListActivity.class.getSimpleName();

    private static final String MOVIE_LIST_KEY = "popular_list";
    private static final String MENU_INDEX_KEY = "menu_index";

    private final String INTENT_EXTRA_MOVIE = "movie";

    public static final int MOVIE_LOADER_ID = 0;
    public static final int FAVORITES_LOADER_ID = 1;

    private MovieAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Movie> mMovieList;

    private int mMenuItemIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        mMovieList = new ArrayList<>();

        // Check if there is data in savedInstanceState, if not download the movie data
        if (savedInstanceState != null) {
            mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_KEY);
            mMenuItemIndex = savedInstanceState.getInt(MENU_INDEX_KEY);
            setUpRecyclerView();
        }
        else {
            // if there is no saved state, initialize the loader
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        }
    }

    /** Save the entire movie array so you don't have to fetch it if the view is recreated
     * and save the position of the menu checked item */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_LIST_KEY, mMovieList);
        outState.putInt(MENU_INDEX_KEY, mMenuItemIndex);

        super.onSaveInstanceState(outState);
    }

    /** Create the menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_list, menu);

        // set the menu item checked
        menu.getItem(mMenuItemIndex).setChecked(true);

        return true;
    }

    /** Menu options to sort by popularity or rating, fetch new data when changing options */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!NetworkUtils.isConnected(this)) {
            Toast.makeText(this, R.string.con_error, Toast.LENGTH_SHORT).show();
            return false;
        }

        int itemId = item.getItemId();

        switch (itemId) {

            case R.id.action_sort_popular:
                item.setChecked(true);

                mMenuItemIndex = 0;
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);

                break;

            case R.id.action_sort_rating:
                item.setChecked(true);

                mMenuItemIndex = 1;
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);

                break;

            case R.id.action_sort_favorites:
                item.setChecked(true);

                mMenuItemIndex = 2;
                getSupportLoaderManager().restartLoader(FAVORITES_LOADER_ID, null, this);

                break;

            default:
                throw new UnsupportedOperationException("Invalid menu id");
        }

        return super.onOptionsItemSelected(item);
    }

    /** Set up the RecyclerView, setting the LayoutManager to a grid if the orientation is
     * in portrait and a horizontal LinearLayout if the orientation is in landscape */
    private void setUpRecyclerView() {
        mAdapter = new MovieAdapter(this, mMovieList, this);
        mRecyclerView.setAdapter(mAdapter);

        // Set up a different layout manager according to screen orientation
        int orientation = getWindowManager().getDefaultDisplay().getRotation();

        Log.d(TAG, "orientation = " + orientation);

        switch (orientation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                mLayoutManager = new GridLayoutManager(this, 2 /* columns */,
                        LinearLayoutManager.VERTICAL, false /* reversed? */);
                break;

            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                mLayoutManager = new GridLayoutManager(this, 1 /* columns */,
                        LinearLayoutManager.HORIZONTAL, false);
                break;

            default:
                mLayoutManager = new GridLayoutManager(this, 2 /* columns */,
                        LinearLayoutManager.VERTICAL, false /* reversed? */);
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
    }

    /** Start the DetailsActivity when a movie is clicked */
    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(INTENT_EXTRA_MOVIE, movie);

        startActivity(intent);
    }

    /** Loader callbacks */
    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle args) {
        switch (id) {
            case MOVIE_LOADER_ID:
                boolean popularIsSelected = mMenuItemIndex == 0;

                return new MovieLoader(this, popularIsSelected);

            case FAVORITES_LOADER_ID:
                return new FavoritesLoader(this);

            default:
                throw new UnsupportedOperationException("Invalid loader id");
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> result) {
        if (result == null || result.isEmpty()) {
            // Error loading data from network
            if (loader.getId() == MOVIE_LOADER_ID) {
                Toast.makeText(ListActivity.this, R.string.con_error,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // Favorites database is empty
            else {
                Toast.makeText(ListActivity.this, R.string.fav_empty, Toast.LENGTH_SHORT)
                        .show();
            }
        }

        mMovieList = result;

        setUpRecyclerView();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        // Not implemented
    }
}
